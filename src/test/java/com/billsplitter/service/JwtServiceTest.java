package com.billsplitter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtServiceTest {

    private JwtService jwtService;
    private final String SECRET =
            "mysecretkeymysecretkeymysecretkeymysecretkey";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret", SECRET);
    }

    private UserDetails buildUser() {
        return User.builder()
                .username("usertest@gmail.com")
                .password("password123")
                .roles("USER")
                .build();
    }

    @Test
    void shouldGenerateToken() {
        UserDetails user = buildUser();

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }
}
