package com.stivenva.contentsharingtest.infrastructure.persistence.jpa.adapter;

import com.stivenva.contentsharingtest.domain.model.User;
import com.stivenva.contentsharingtest.infrastructure.mapper.UserMapper;
import com.stivenva.contentsharingtest.infrastructure.persistence.jpa.model.UserEntity;
import com.stivenva.contentsharingtest.infrastructure.persistence.jpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryJpaAdapter implements com.stivenva.contentsharingtest.domain.port.repository.UserRepository {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findById(long id) {
        return userRepository.findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail((email))
                .map(UserMapper::toDomain);

    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User save(User user) {

        UserEntity userEntity = userRepository.save(UserMapper.toEntity(user));
        return UserMapper.toDomain(userEntity);
    }

    @Override
    public void delete(User user) {

        userRepository.delete(UserMapper.toEntity(user));
    }

    @Override
    public void updateRateCount(long id) {
        userRepository.findById(id).ifPresent(userEntity->{
            userEntity.setRatingCount(userEntity.getRatingCount()+1);

            userRepository.save(userEntity);
        });
    }

    @Override
    public void deleteById(long id) {
        userRepository.deleteById(id);
    }

}
