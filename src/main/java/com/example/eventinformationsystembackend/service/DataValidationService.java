package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.model.*;

public interface DataValidationService {
    <T> T getResourceByIdOrThrowException(Long id, Class<T> entityClass, String errorMessage);
    User getUserByUsername(String username);
    Event getEventByName(String name);
}
