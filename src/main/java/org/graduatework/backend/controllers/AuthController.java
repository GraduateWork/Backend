package org.graduatework.backend.controllers;

import org.graduatework.backend.services.AuthService;
import org.graduatework.dto.DBUser;
import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping(value = "registration", method = RequestMethod.POST)
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

    @RequestMapping(value = "registration", method = RequestMethod.POST)
    public void activateUser(HttpServletResponse response,
                             @RequestParam("username") String username,
                             @RequestParam("code") String code) {
        try {
            authService.activateUser(username, code);
        } catch (IllegalArgumentException e) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getLocalizedMessage());
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}
