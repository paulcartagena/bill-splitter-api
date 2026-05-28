package com.billsplitter.controller;

import com.billsplitter.dto.auth.LoginRequestDTO;
import com.billsplitter.dto.auth.RegisterRequestDTO;
import com.billsplitter.dto.auth.TokenResponseDTO;
import com.billsplitter.model.User;
import com.billsplitter.service.AuthService;
import com.billsplitter.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public TokenResponseDTO register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        User user = authService.registerUser(registerRequestDTO);
        String token = jwtService.generateToken(user);

        return new TokenResponseDTO(token);
    }

    @PostMapping("/login")
    public TokenResponseDTO login(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(),
                                                        loginRequestDTO.getPassword())
        );

        UserDetails userDetails = (User) auth.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        return new TokenResponseDTO(token);
    }
}
