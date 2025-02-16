package com.ilo.energyallocation.user.repository;

import com.ilo.energyallocation.user.model.IloUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<IloUser, String> {
    Optional<IloUser> findByUsername(String username);

    Optional<IloUser> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
