package com.auth.boilerplate.auth.service;

import static java.lang.Boolean.TRUE;

import com.auth.boilerplate.auth.RefreshToken;
import com.auth.boilerplate.auth.dto.LoginRequest;
import com.auth.boilerplate.auth.dto.LoginSuccessResult;
import com.auth.boilerplate.auth.exception.AuthException;
import com.auth.boilerplate.auth.repository.RefreshTokenRepository;
import com.auth.boilerplate.auth.service.EmailCodeTable.EmailCode;
import com.auth.boilerplate.jwt.JWTTokens;
import com.auth.boilerplate.jwt.JWTUtil;
import com.auth.boilerplate.user.User;
import com.auth.boilerplate.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JWTUtil jwtUtil;
    private final JavaMailSender javaMailSender;
    private final EmailCodeTable emailCodeTable;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public void sendMail(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new AuthException(email, HttpStatus.CONFLICT);
        }

        try {
            String code = createCode(email);
            emailCodeTable.add(email, new EmailCode(code, LocalDateTime.now()));
        } catch (MessagingException messagingException) {
            throw new AuthException(
                    "",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public void authenticateCode(String email, String code) {

        log.info("code: => {}", code);
        if (!emailCodeTable.contains(email)) {
            throw new AuthException(
                    "",
                    HttpStatus.NOT_FOUND
            );
        }

        if (emailCodeTable.isTimeout(email)) {
            throw new AuthException(
                    "",
                    HttpStatus.REQUEST_TIMEOUT
            );
        }

        if (!emailCodeTable.isValidCode(email, code)) {
            throw new AuthException("", HttpStatus.UNAUTHORIZED);
        }
    }

    public LoginSuccessResult login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AuthException("", HttpStatus.NOT_FOUND));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthException("", HttpStatus.UNAUTHORIZED);
        }

        JWTTokens jwtTokens = jwtUtil.createJwt(user.getId());

        refreshTokenRepository.save(
                RefreshToken.builder()
                .user(user)
                .token(jwtTokens.refreshToken())
                .build()
        );

        return LoginSuccessResult.builder()
                .userId(user.getId())
                .accessToken(jwtTokens.accessToken())
                .build();
    }

    private String createCode(String email) throws MessagingException {
        String code = generateRandomCode();
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, TRUE, "UTF-8");

        helper.setTo(email);
        helper.setSubject("메일인증");
        helper.setText(code, TRUE);
        javaMailSender.send(message);

        return code;
    }

    private String generateRandomCode() {
        Random random = new SecureRandom();
        int randomNumber = 100000 + random.nextInt(99999);
        return String.valueOf(randomNumber);
    }
}
