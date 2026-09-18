package com.ali.worksphere.controller;


import com.ali.worksphere.dto.*;
import com.ali.worksphere.service.AuthService;
import com.ali.worksphere.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService,PasswordResetService passwordResetService) {

        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    public ResponseEntity<EmployeeResponse> register(@Valid @RequestBody RegisterRequest request){
        EmployeeResponse response = authService.register(request.getName(),request.getEmail(),request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
       LoginResponse response = authService.login(request.getEmail(), request.getPassword());
       return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request){
        LoginResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request){
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request){
        passwordResetService.generateOtp(request.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Void> verifyOtp(@Valid @RequestBody VerifyOtpRequest verify){
        passwordResetService.verifyOtp(verify.getEmail(),verify.getOtp());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        passwordResetService.resetPassword(request.getEmail(),request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
