package com.stivenva.contentsharingtest.infrastructure.mapper;

import com.stivenva.contentsharingtest.domain.model.User;
import com.stivenva.contentsharingtest.infrastructure.persistence.jpa.model.UserEntity;

public final class UserMapper {

    public static User toDomain(UserEntity e){
        return new User(
                e.getId(),
                e.getUsername(),
                e.getFirstname(),
                e.getLastname(),
                e.getEmail(),
                e.getRatingCount(),
                e.getLastLogin(),
                e.getCreatedAt()
        );
    }

    public static UserEntity toEntity(User d){
        UserEntity e = new UserEntity();
        e.setId(d.id());
        e.setUsername(d.username());
        e.setFirstname(d.firstname());
        e.setLastname(d.lastname());
        e.setEmail(d.email());
        e.setRatingCount(d.ratingCount());
        e.setLastLogin(d.lastLogin());
        e.setCreatedAt(d.createdAt());
        return e;
    }

}
