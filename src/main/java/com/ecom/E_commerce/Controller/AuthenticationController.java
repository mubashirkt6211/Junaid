package com.ecom.E_commerce.Controller;

import com.ecom.E_commerce.Exception.EmailAlreadyExistsException;
import com.ecom.E_commerce.Request.LoginRequest;
import com.ecom.E_commerce.Request.RegisterRequest;
import com.ecom.E_commerce.Response.AuthenticationResponse;
import com.ecom.E_commerce.Service.UserService;
import com.ecom.E_commerce.Service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {

    private final UserServiceImpl userService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) throws EmailAlreadyExistsException {
        String token = String.valueOf(userService.register(request));
        return ResponseEntity.ok(new AuthenticationResponse("User registered successfully", token));
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = userService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(new AuthenticationResponse("Login successful", token));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthenticationResponse(e.getMessage(), null));
        }
    }
    }
