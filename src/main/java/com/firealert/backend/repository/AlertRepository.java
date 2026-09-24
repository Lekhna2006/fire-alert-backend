package com.firealert.backend.repository;

import com.firealert.backend.model.Alert;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AlertRepository extends MongoRepository<Alert, String> {
    List<Alert>findByDeviceIdOrderByTimestampDesc(String deviceId);

    void deleteByDeviceId(String deviceId);


}