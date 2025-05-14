package com.jwt_auth.jwt.service.impl;

import com.jwt_auth.jwt.dto.JwtAuthenticationDto;
import com.jwt_auth.jwt.dto.RefreshTokenDto;
import com.jwt_auth.jwt.dto.UserCredentialsDto;
import com.jwt_auth.jwt.dto.UserDto;
import com.jwt_auth.jwt.entity.User;
import com.jwt_auth.jwt.mapper.UserMapper;
import com.jwt_auth.jwt.repository.UserRepository;
import com.jwt_auth.jwt.security.jwt.JwtService;
import com.jwt_auth.jwt.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public JwtAuthenticationDto singIn(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        User user = findByCredentials(userCredentialsDto);
        return jwtService.generateAuthToken(user.getEmail());
    }

    @Override
    public JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws Exception {
        String refreshToken = refreshTokenDto.getRefreshToken();
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            User user = findByEmail(jwtService.getEmailFromToken(refreshToken));
            return jwtService.refreshBaseToken(user.getEmail(), refreshToken);
        }
        throw new AuthenticationException("Invalid refresh token");
    }

    @Override
    @Transactional
    public UserDto getUserById(String id) {
        Optional<UserDto> fromDB = userRepository.findByUserId(UUID.fromString(id)).map(user -> userMapper.toDto(user));
        if (fromDB.isPresent()) {
            return fromDB.get();
        }
        return null;
    }

    @Override
    @Transactional
    public UserDto getUserByEmail(String email) {
        Optional<UserDto> fromDB = userRepository.findByEmail(email).map(user -> userMapper.toDto(user));
        if (fromDB.isPresent()) {
            return fromDB.get();
        }
        return null;
    }

    @Override
    public String addUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User added";
    }




    private User findByCredentials(UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        Optional<User> optionalUser = userRepository.findByEmail(userCredentialsDto.getEmail());
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            if (passwordEncoder.matches(userCredentialsDto.getPassword(), user.getPassword())){
                return user;
            }
        }
        throw new AuthenticationException("Email or password is not correct");
    }

    private User findByEmail(String email) throws Exception {
        return userRepository.findByEmail(email).orElseThrow(()->
                new Exception(String.format("User with email %s not found", email)));
    }
}
