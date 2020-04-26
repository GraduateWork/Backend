package org.graduatework.backend.controllers;

import org.graduatework.backend.db.DBUser;
import org.graduatework.backend.dto.UserActivation;
import org.graduatework.backend.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class AuthController {

    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public void login(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @RequestMapping(value = "signUp", method = RequestMethod.POST)
    public void registerUser(HttpServletResponse response,
                             @RequestBody DBUser user) {
        try {
            authService.registerUser(user);
        } catch (IllegalArgumentException e) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getLocalizedMessage());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "activation", method = RequestMethod.POST)
    public void activateUser(HttpServletResponse response,
                             @RequestBody UserActivation userActivation) {
        try {
            authService.activateUser(userActivation.getUsername(), userActivation.getCode());
        } catch (IllegalArgumentException e) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getLocalizedMessage());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "user", method = RequestMethod.GET)
    public DBUser getPrincipal() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return authService.getUser(username);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public void home() {
    }
}
