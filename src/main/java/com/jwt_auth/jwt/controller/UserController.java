package com.jwt_auth.jwt.controller;

import com.jwt_auth.jwt.dto.UserDto;
import com.jwt_auth.jwt.service.UserService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public String createUser(@RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable String id) throws ChangeSetPersister.NotFoundException {
        return userService.getUserById(id);
    }

    @GetMapping("/email/{email}")
    public UserDto getUserByEmail(@PathVariable String email) throws ChangeSetPersister.NotFoundException {
        return userService.getUserByEmail(email);
    }
}