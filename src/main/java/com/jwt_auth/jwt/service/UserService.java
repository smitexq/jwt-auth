package com.jwt_auth.jwt.service;

import com.jwt_auth.jwt.dto.UserDto;
import org.springframework.data.crossstore.ChangeSetPersister;

public interface UserService {
    UserDto getUserById(String id) throws ChangeSetPersister.NotFoundException;
    UserDto getUserByEmail(String email) throws ChangeSetPersister.NotFoundException;
    String addUser(UserDto user);
}
