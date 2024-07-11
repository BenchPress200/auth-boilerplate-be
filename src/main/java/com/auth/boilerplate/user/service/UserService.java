package com.auth.boilerplate.user.service;

import com.auth.boilerplate.user.dto.JoinRequest;
import com.auth.boilerplate.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private static UserRepository userRepository;

    public void join(JoinRequest joinRequest) {
        userRepository.save(joinRequest.toUser());
    }
}
