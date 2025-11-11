package com.stivenva.contentsharingtest.infrastructure.persistence.jpa.repository;

import com.stivenva.contentsharingtest.domain.model.User;
import com.stivenva.contentsharingtest.infrastructure.persistence.jpa.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findByEmail(String username);

    Optional<User> findByUsername(String username);
}
