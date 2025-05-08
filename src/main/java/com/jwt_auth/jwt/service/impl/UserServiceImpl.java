package com.jwt_auth.jwt.service.impl;

import com.jwt_auth.jwt.dto.UserDto;
import com.jwt_auth.jwt.mapper.UserMapper;
import com.jwt_auth.jwt.repository.UserRepository;
import com.jwt_auth.jwt.service.UserService;
import org.springframework.data.crossstore.ChangeSetPersister;

public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public UserDto getUserById(String id) throws ChangeSetPersister.NotFoundException {
        return null;
    }

    @Override
    public UserDto getUserByEmail(String email) throws ChangeSetPersister.NotFoundException {
        return null;
    }

    @Override
    public String addUser(UserDto user) {
        return "";
    }
}
