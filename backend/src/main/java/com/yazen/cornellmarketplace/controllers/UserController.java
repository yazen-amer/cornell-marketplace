package com.yazen.cornellmarketplace.controllers;

import com.yazen.cornellmarketplace.entities.Users;
import java.security.Principal;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import com.yazen.cornellmarketplace.repositories.UserRepository;
import com.yazen.cornellmarketplace.dtos.UserDto;

@Controller
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users/{id}")
    @ResponseBody
    public Users getUser(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/users/me")
    @ResponseBody
    public UserDto getCurrentUser(Principal principal) {
        String email = principal.getName();
        Optional<Users> user = userRepository.findByEmail(email);
        return new UserDto(user.get().getId(), user.get().getUsername());
    }

    @PostMapping("/users")
    @ResponseBody
    public Users createUser(@RequestBody Users user) {
        return userRepository.save(user);
    }

}
