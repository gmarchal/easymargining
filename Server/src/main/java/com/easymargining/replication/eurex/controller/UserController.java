package com.easymargining.replication.eurex.controller;

import com.easymargining.replication.eurex.domain.model.User;
import com.easymargining.replication.eurex.domain.model.exception.UserConflictException;
import com.easymargining.replication.eurex.domain.repository.IUserRepository;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;

/**
 * Created by Gilles Marchal on 21/01/2016.
 */

@Slf4j
@RestController
@RequestMapping(value = "/api/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @RequestMapping(value = "/list", method= RequestMethod.GET)
    public List<User> getUsers() {
        return userRepository.findAll();
    }


    @RequestMapping(value = "/current", method= RequestMethod.GET)
    public Principal user(Principal user) {
        log.info("Request asked with user : " + user);
        return user;
    }

    /*
    @RequestMapping(value = "/current", method= RequestMethod.GET)
    @ResponseBody
    public UserDetails getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if("anonymousUser".equals(principal)) {
            return null;
        } else if (principal instanceof UserDetails) {
            return (UserDetails)principal;
        } else {
            throw new IllegalStateException("Unknown principal : " + principal);
        }
    }
    */

    @RequestMapping(value = "/signup", method= RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestBody User user, HttpServletResponse response) {

        // Validate first that the user doesn't exist.
        User existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser != null) {
            throw new UserConflictException(user.getEmail());
        }

        User savedUser = userRepository.save(user);
        response.setHeader(HttpHeaders.LOCATION, "/user/" + savedUser.getEmail());
    }

    @ExceptionHandler(UserConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String userConflictException(UserConflictException ex) {
        return ex.getMessage();
    }

}
