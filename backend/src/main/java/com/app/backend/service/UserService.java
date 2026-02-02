package com.app.backend.service;

import com.app.backend.dto.RegisterRequest;
import com.app.backend.entity.User;
import com.app.backend.enums.Role;
import com.app.backend.exception.BadRequestException;
import com.app.backend.exception.NotFoundException;
import com.app.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User create(RegisterRequest request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new BadRequestException("User already exist.");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        return saveUser(user);
    }
    public User findUser(String email){
        return userRepository.findByEmail(email).orElseThrow(()->new NotFoundException("User not found."));
    }
    public User saveUser(User user){
         User saved = userRepository.save(user);
         userRepository.flush();
         return saved;
    }
    public void edit(RegisterRequest request){
        User user = findUser(request.getEmail());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        saveUser(user);
    }
}
