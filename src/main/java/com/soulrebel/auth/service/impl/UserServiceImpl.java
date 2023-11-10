package com.soulrebel.auth.service.impl;

import com.soulrebel.auth.domain.User;
import com.soulrebel.auth.domain.dto.UserResponse;
import com.soulrebel.auth.service.UserService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public UserResponse createUser(HttpServletRequest request) {

        var user = (User) request.getAttribute ("user");
        return new UserResponse (user.getId (), user.getFirstName (), user.getLastName (), user.getEmail ());
    }
}
