package com.soulrebel.auth.service;

import com.soulrebel.auth.domain.dto.UserResponse;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public interface UserService {

    UserResponse createUser(HttpServletRequest request);
}
