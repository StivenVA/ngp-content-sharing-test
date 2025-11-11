package com.stivenva.contentsharingtest.infrastructure.identity.cognito;

import com.stivenva.contentsharingtest.application.dto.response.AuthResponseDto;
import com.stivenva.contentsharingtest.domain.model.User;
import com.stivenva.contentsharingtest.domain.port.IdentityProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CognitoService implements IdentityProvider {

    private final CognitoIdentityProviderClient cognitoIdentityProviderClient;

    @Value("${aws.cognito.user-pool-id:}")
    private String userPoolId;
    
    @Value("${aws.cognito.app-client-id:}")
    private String appClientId;

    @Value("${aws.cognito.app-client-secret:}")
    private String appClientSecret;

    @Override
    public Optional<AuthResponseDto> login(String email, String password) {
        Map<String, String> authParams = createAuthParams(email, password);

        InitiateAuthRequest request = InitiateAuthRequest.builder()
                .clientId(appClientId)
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(authParams)
                .build();

        InitiateAuthResponse response = cognitoIdentityProviderClient.initiateAuth(request);

        AuthenticationResultType result = response.authenticationResult();
        if (result == null || result.accessToken() == null) {
            throw new RuntimeException("Authentication failed: no tokens returned by Cognito");
        }

        AuthResponseDto authResponse = createAuthResponse(result);
        authResponse.refreshExpiresIn = getRefreshTokenExpirySeconds();
        return Optional.ofNullable(authResponse);
    }

    private HashMap<String,String> createAuthParams(String email, String password){
        HashMap<String,String> authParams = new HashMap<>();
        authParams.put("USERNAME", email);
        authParams.put("PASSWORD", password);
        if (appClientSecret != null && !appClientSecret.isBlank()) {
            authParams.put("SECRET_HASH", calculateSecretHash(email));
        }
        return authParams;
    }

    private AuthResponseDto createAuthResponse(AuthenticationResultType result){

        if(result == null)
            return null;

        AuthResponseDto authResponse = new AuthResponseDto();

        authResponse.accessToken = result.accessToken();
        authResponse.refreshToken = result.refreshToken();
        authResponse.expiresIn = result.expiresIn();
        authResponse.refreshExpiresIn = getRefreshTokenExpirySeconds();

        return authResponse;
    }

    @Override
    public Optional<SignUpResponse> register(String name, String username, String password) {
        SignUpRequest.Builder builder = SignUpRequest.builder()
                .clientId(appClientId)
                .username(username)
                .password(password)
                .userAttributes(
                        AttributeType.builder().name("name").value(username).build(),
                        AttributeType.builder().name("email").value(username).build()
                );
        if (appClientSecret != null && !appClientSecret.isBlank()) {
            builder = builder.secretHash(calculateSecretHash(username));
        }

        SignUpRequest signUpRequest = builder.build();
        SignUpResponse signUpResponse = cognitoIdentityProviderClient.signUp(signUpRequest);

        return Optional.ofNullable(signUpResponse);
    }

    @Override
    public void confirmAccount(String email, String code) {
        ConfirmSignUpRequest confirmSignUpRequest = ConfirmSignUpRequest.builder()
                .clientId(appClientId)
                .username(email)
                .confirmationCode(code)
                .secretHash(calculateSecretHash(email))
                .build();

        try {
            cognitoIdentityProviderClient.confirmSignUp(confirmSignUpRequest);
        }
        catch (ResourceNotFoundException e) {
            throw new RuntimeException("User not found", e);
        }
        catch (InvalidParameterException e) {
            throw new RuntimeException("Invalid confirmation code", e);
        }
    }

    @Override
    public boolean isEmailAvailable(String email) {
        ListUsersResponse list = cognitoIdentityProviderClient.listUsers(
                ListUsersRequest.builder()
                        .userPoolId(userPoolId)
                        .filter("email = \"" + email + "\"")
                        .limit(1)
                        .build()
        );
        return list.users() == null || list.users().isEmpty();
    }

    @Override
    public Optional<AuthResponseDto> refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return Optional.empty();
        }
        Map<String, String> authParams = new HashMap<>();
        authParams.put("REFRESH_TOKEN", refreshToken);

        InitiateAuthRequest request = InitiateAuthRequest.builder()
                .clientId(appClientId)
                .authFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
                .authParameters(authParams)
                .build();

        InitiateAuthResponse response = cognitoIdentityProviderClient.initiateAuth(request);
        AuthenticationResultType result = response.authenticationResult();
        if (result == null || result.accessToken() == null) {
            return Optional.empty();
        }


        AuthResponseDto authResponse = createAuthResponse(result);

        return Optional.of(authResponse);
    }

    public void logout(String accessToken){
        RevokeTokenRequest request = RevokeTokenRequest.builder()
                .clientId(appClientId)
                .token(accessToken)
                .build();
        cognitoIdentityProviderClient.revokeToken(request);
    }


    private String calculateSecretHash(String username) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(appClientSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] rawHmac = mac.doFinal((username + appClientId).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate Cognito SECRET_HASH", e);
        }
    }

    private long getRefreshTokenExpirySeconds() {
        try {
            DescribeUserPoolClientRequest req = DescribeUserPoolClientRequest.builder()
                    .userPoolId(userPoolId)
                    .clientId(appClientId)
                    .build();
            DescribeUserPoolClientResponse resp = cognitoIdentityProviderClient.describeUserPoolClient(req);
            UserPoolClientType client = resp.userPoolClient();
            if (client == null) return 30L * 24 * 3600;
            Integer validity = client.refreshTokenValidity();
            if (validity == null) validity = 30;
            TokenValidityUnitsType units = client.tokenValidityUnits();
            if (units == null || units.refreshToken() == null) {
                return validity.longValue() * 24 * 3600;
            }
            TimeUnitsType unitType = units.refreshToken();

            return switch (unitType) {
                case HOURS -> validity.longValue() * 3600;
                case MINUTES -> validity.longValue() * 60;
                case SECONDS -> validity.longValue();
                default -> validity.longValue() * 24 * 3600;
            };

        } catch (Exception e) {
            return 30L * 24 * 3600;
        }
    }
}
