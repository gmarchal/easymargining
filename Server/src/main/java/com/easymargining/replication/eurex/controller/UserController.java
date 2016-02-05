package com.easymargining.replication.eurex.controller;

import com.easymargining.replication.eurex.domain.model.User;
import com.easymargining.replication.eurex.domain.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by Gilles Marchal on 21/01/2016.
 */

@Slf4j
@RestController
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @RequestMapping(value = "/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @RequestMapping(value = "/user/save", method= RequestMethod.GET)
    public User saveUser(User user) {
        userRepository.save(user);
        return user;
    }


}
