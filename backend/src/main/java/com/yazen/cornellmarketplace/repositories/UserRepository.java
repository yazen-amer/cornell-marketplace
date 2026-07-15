package com.yazen.cornellmarketplace.repositories;

import java.util.List;
import com.yazen.cornellmarketplace.entities.Users;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

public interface UserRepository extends CrudRepository<Users, Long> {
    Users findById(long id);

    Optional<Users> findByEmail(String email);

    Optional<Users> findByUsername(String username);
}
