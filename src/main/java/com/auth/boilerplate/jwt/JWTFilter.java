package com.auth.boilerplate.jwt;

import com.auth.boilerplate.auth.RefreshToken;
import com.auth.boilerplate.auth.exception.AuthException;
import com.auth.boilerplate.auth.repository.RefreshTokenRepository;
import com.auth.boilerplate.user.User;
import com.auth.boilerplate.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private static final String ACCESS_TOKEN_KEY = "Authorization";

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    private final List<String> EXCLUDE_URLS = Arrays.asList(
            "/api/v1/auth/login",
            "/api/v1/auth",
            "/api/v1/users",
            "/api/v1/test/unauth",
            "/h2-console"
    );

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        String method = request.getMethod();

        if (EXCLUDE_URLS.stream().anyMatch(requestUri::startsWith)) {
            filterChain.doFilter(request, response);
            System.out.println("?????????");
            return;
        }

        Cookie[] cookies = request.getCookies(); // 어세스 토큰이 들어있는 쿠키가 없다면 아직 로그인하지 않은 상태와 동일
        if (cookies == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String authorization = Arrays.stream(cookies)
                .filter(cookie -> ACCESS_TOKEN_KEY.equals(cookie.getName()))
                .findFirst()
                .map(cookie -> URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8))
                .orElse(null);

        Long userId = Arrays.stream(cookies)
                .filter(cookie -> "user-id".equals(cookie.getName()))
                .findFirst()
                .map(cookie -> Long.parseLong(cookie.getValue()))
                .orElseThrow(() -> new AuthException("", HttpStatus.UNAUTHORIZED));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("", HttpStatus.NOT_FOUND));

        if(!isToken(authorization)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            if(isExpired(authorization)) { // 만료되었다면
                String refreshToken = findRefreshToken(userId).getToken();

                if (isExpired(refreshToken)) { // 리프레쉬도 만료되었다면
                    discardRefreshToken(userId);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                // 리프레쉬 토큰이 살아있다면
                JWTTokens jwtTokens = reissueJwt(userId);
                discardRefreshToken(userId);
                saveRefreshToken(user, jwtTokens.refreshToken());
                response.addCookie(createCookie("Authorization", URLEncoder.encode(jwtTokens.accessToken(), StandardCharsets.UTF_8)));

                Cookie userIdCookie = new Cookie("user-id", String.valueOf(userId));
                userIdCookie.setHttpOnly(false);
                userIdCookie.setSecure(true);
                userIdCookie.setPath("/");
                userIdCookie.setMaxAge(60 * 60 * 24);
                response.addCookie(userIdCookie);
            }

            log.info("{} pass authentication", userId);
            filterChain.doFilter(request, response);

        } catch (EmptyResultDataAccessException e) {
            log.error("Refresh token not found = {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }



    private JWTTokens reissueJwt(Long userId) {
        return jwtUtil.createJwt(userId);
    }

    private void saveRefreshToken(User user, String refreshToken) {
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .user(user)
                        .token(refreshToken)
                        .build()
        );
    }

    private void discardRefreshToken(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    private RefreshToken findRefreshToken(Long userId) {
        return refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthException("", HttpStatus.NOT_FOUND));
    }

    private boolean isExpired(String token) {
        return jwtUtil.isExpired(token);
    }

    private boolean isToken(String authorization) {
        return authorization != null && authorization.startsWith("Bearer ");
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(3600000);
        cookie.setPath("/");
        cookie.setHttpOnly(false);

        return cookie;
    }
}
