package com.nokia.ndac.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.nokia.ndac.bean.SystemParameter;

public interface SystemParmeterRepository
        extends MongoRepository<SystemParameter, String> {

    public Optional<SystemParameter> findByName(String name);
}
