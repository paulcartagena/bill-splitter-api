package com.billsplitter.service;

import com.billsplitter.dto.auth.RegisterRequestDTO;
import com.billsplitter.model.User;
import com.billsplitter.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO(
                "usertest@gmail.com",
                "User Test",
                "password123");

        User savedUser = new User(
                1L,
                "User Test",
                "usertest@gmail.com",
                "hashedPassword");

        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString()))
                .thenReturn("hashedPassword");
        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        User result = authService.registerUser(registerRequestDTO);

        assertNotNull(result);
        assertEquals(registerRequestDTO.getEmail(), result.getEmail());
    }
}
