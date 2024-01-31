package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.Event;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.EventRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import com.example.eventinformationsystembackend.service.DataValidationService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

@Service
@Transactional
@RequiredArgsConstructor
public class DataValidationServiceImpl implements DataValidationService {

    private final ApplicationContext applicationContext;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findUserByUsername(username).
                orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));
    }

    @Override
    public Event getEventByName(String name) {
        return eventRepository.findEventByName(name).
                orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));
    }

    @Override
    public <T> T getResourceByIdOrThrowException(Long id, Class<T> entityClass, String errorMessage) {
        JpaRepository<T, Long> repository = getRepository(entityClass);
        if (repository != null) {
            return checkIfResourceExistsOrThrowException(repository.findById(id), errorMessage);
        }
        throw new IllegalArgumentException("Unsupported entity class: " + entityClass.getName());
    }

    private <T> JpaRepository<T, Long> getRepository(Class<T> entityClass) {
        String entityClassName = entityClass.getSimpleName();
        String repositoryBeanName = entityClassName.substring(0, 1).toLowerCase() +
                                    entityClassName.substring(1) + "Repository";
        String[] beanNames = applicationContext.getBeanNamesForType(JpaRepository.class);

        for (String beanName : beanNames) {
            if (beanName.endsWith(repositoryBeanName)) {
                return (JpaRepository<T, Long>) applicationContext.getBean(beanName, JpaRepository.class);
            }
        }

        return null;
    }

    private <T> T checkIfResourceExistsOrThrowException(Optional<T> optional, String errorMessage) {
        return optional.orElseThrow(() -> new ResourceNotFoundException(errorMessage));
    }
}
