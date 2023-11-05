package com.soulrebel.auth.interceptor;

import com.soulrebel.auth.exception.NoBearerTokenError;
import com.soulrebel.auth.service.RegisterService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    private final RegisterService service;

    public AuthorizationInterceptor(RegisterService service) {
        this.service = service;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        var authorizationHeader = request.getHeader ("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith ("Bearer "))
            throw new NoBearerTokenError ();
        request.setAttribute ("user", service.getUserFromToken (authorizationHeader.substring (7)));

        return true;
    }
}
