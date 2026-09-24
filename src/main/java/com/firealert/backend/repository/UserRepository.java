package com.firealert.backend.repository;

import com.firealert.backend.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,String> {

    boolean existsByDeviceId(String deviceId);

    Optional<User>
    findByDeviceId(String deviceId);

    Optional<User>
    findByEmailAndDeviceId(String email, String deviceId);


}
