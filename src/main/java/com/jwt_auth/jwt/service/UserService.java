package com.jwt_auth.jwt.service;

import com.jwt_auth.jwt.dto.JwtAuthenticationDto;
import com.jwt_auth.jwt.dto.RefreshTokenDto;
import com.jwt_auth.jwt.dto.UserCredentialsDto;
import com.jwt_auth.jwt.dto.UserDto;
import org.springframework.data.crossstore.ChangeSetPersister;

import javax.naming.AuthenticationException;

public interface UserService {
    JwtAuthenticationDto singIn(UserCredentialsDto userCredentialsDto) throws AuthenticationException;
    JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception;
    UserDto getUserById(String id) throws ChangeSetPersister.NotFoundException;
    UserDto getUserByEmail(String email) throws ChangeSetPersister.NotFoundException;
    String addUser(UserDto user);
}
