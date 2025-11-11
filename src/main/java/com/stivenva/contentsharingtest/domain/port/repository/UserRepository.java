package com.stivenva.contentsharingtest.domain.port.repository;

import com.stivenva.contentsharingtest.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(long id);
    Optional<User> findByEmail(String username);
    Optional<User> findByUsername(String username);
    User save(User user);
    void delete(User user);
    void updateRateCount(long id);
    void deleteById(long id);

}
