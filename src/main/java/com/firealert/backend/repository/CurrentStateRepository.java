package com.firealert.backend.repository;

import com.firealert.backend.model.CurrentState;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CurrentStateRepository extends MongoRepository<CurrentState, String> {

    Optional<CurrentState> findByDeviceId(String deviceId);
}