package com.ali.worksphere.service;

import com.ali.worksphere.entity.Employee;
import com.ali.worksphere.entity.PasswordResetOtp;
import com.ali.worksphere.exception.EmployeeNotFoundException;
import com.ali.worksphere.repository.EmployeeRepository;
import com.ali.worksphere.repository.PasswordResetRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;

import java.time.LocalDateTime;

@Service
public class PasswordResetService {

    private final EmployeeRepository employeeRepository;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final PasswordEncoder encoder;
    private final PasswordResetRepository passwordResetOtpRepository;

    public PasswordResetService(EmployeeRepository employeeRepository, PasswordResetRepository passwordResetOtpRepository,EmailService emailService,PasswordEncoder encoder) {
        this.employeeRepository = employeeRepository;
        this.passwordResetOtpRepository = passwordResetOtpRepository;
        this.emailService = emailService;
        this.encoder = encoder;
    }

    public void generateOtp(String email) {

        // 1. Check whether employee exists
        employeeRepository.findByEmail(email).orElseThrow(
                () -> new EmployeeNotFoundException("Employee not found")
        );

        // 2. Remove previous OTP
        passwordResetOtpRepository.deleteByEmail(email);

        // 3. Generate 6 digit OTP
        String otp = String.format("%06d", secureRandom.nextInt(1_000_000));

        // 4. Create OTP entity
        PasswordResetOtp passwordResetOtp = new PasswordResetOtp();
        passwordResetOtp.setEmail(email);
        passwordResetOtp.setOtp(encoder.encode(otp));
        passwordResetOtp.setExpiryTime(LocalDateTime.now().plusMinutes(2));
        passwordResetOtp.setVerified(false);

        // 5. Save OTP
        passwordResetOtpRepository.save(passwordResetOtp);

        emailService.sendOtp(email,otp);
    }

    public void verifyOtp(String email, String otp){
        PasswordResetOtp passwordResetOtp = passwordResetOtpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid Otp"));

        if(passwordResetOtp.getExpiryTime().isBefore(LocalDateTime.now())){
            throw new RuntimeException(("OTP has expired"));
        }
        passwordResetOtp.setVerified(true);
        passwordResetOtpRepository.save(passwordResetOtp);
    }

    public void resetPassword(String email, String newPassword){
        PasswordResetOtp passwordResetOtp = passwordResetOtpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP verification required"));
        if(!passwordResetOtp.isVerified()){
            throw new RuntimeException("OTP verification required");
        }

        if(passwordResetOtp.getExpiryTime().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Otp has expired");
        }

        Employee employee = employeeRepository.findByEmail(email).
                orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        employee.setPassword(encoder.encode(newPassword));
        employeeRepository.save(employee);
        passwordResetOtpRepository.deleteByEmail(email);
    }
}
