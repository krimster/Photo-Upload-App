package com.erickrim.springboot.repositories;

import com.erickrim.springboot.entities.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by krime on 1/13/17.
 */
public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
}
