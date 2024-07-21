package com.auth.boilerplate.user.service;

import com.auth.boilerplate.user.dto.JoinRequest;
import com.auth.boilerplate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void join(JoinRequest joinRequest) {
        joinRequest.setPassword(passwordEncoder.encode(joinRequest.getPassword()));
        userRepository.save(joinRequest.toUser());
    }
}

