package com.ali.worksphere.service;

import com.ali.worksphere.dto.EmployeeResponse;
import com.ali.worksphere.dto.LoginResponse;
import com.ali.worksphere.entity.Employee;
import com.ali.worksphere.entity.RefreshToken;
import com.ali.worksphere.exception.*;
import com.ali.worksphere.myEnum.EmployeeRole;
import com.ali.worksphere.myEnum.EmployeeStatus;
import com.ali.worksphere.repository.EmployeeRepository;
import com.ali.worksphere.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(EmployeeRepository employeeRepository, BCryptPasswordEncoder encoder, JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.employeeRepository = employeeRepository;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    public EmployeeResponse register(String name, String email, String password) {
        if(employeeRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException("Email " + email + " already exists");
        }

        Employee employee = new Employee();
        employee.setName(name);
        employee.setEmail(email);
        employee.setPassword(encoder.encode(password));
        employee.setRole(EmployeeRole.EMPLOYEE);
        employee.setStatus(EmployeeStatus.PENDING);
        employeeRepository.save(employee);

        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setEmail(employee.getEmail());
        response.setName(employee.getName());
        response.setRole(employee.getRole());
        response.setStatus(employee.getStatus());
        return response;
    }


    public LoginResponse login(String email, String password){
        Employee employee = employeeRepository.findByEmail(email).orElseThrow(
                () -> new InvalidCredentialsException("Invalid email or password")
        );

        if (!encoder.matches(password, employee.getPassword())){
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if(employee.getStatus() == EmployeeStatus.INACTIVE){
            throw new EmployeeInactiveException("Employee status is inactive");
        }

        if(employee.getStatus() == EmployeeStatus.PENDING){
            throw new EmployeePendingException("Employee status is pending");
        }

        String accessToken = jwtService.generateAccessToken(employee);

        String refreshToken = jwtService.generateRefreshToken();

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setRefreshToken(refreshToken);
        refreshTokenEntity.setEmployee(employee);
        refreshTokenEntity.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshTokenEntity.setRevoked(false);

        refreshTokenRepository.save(refreshTokenEntity);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setId(employee.getId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        response.setRole(employee.getRole());
        return response;

    }


    public LoginResponse refreshToken(String refreshToken){

        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(
                () -> new InvalidRefreshTokenException("Invalid refresh token")
        );

        if(token.isRevoked()){
            throw new InvalidRefreshTokenException("Refresh token is revoked");
        }

        if(token.getExpiryDate().isBefore(LocalDateTime.now())){
            throw new InvalidRefreshTokenException("Refresh token is expired");
        }

        Employee employee = token.getEmployee();
        if(employee.getStatus() != EmployeeStatus.ACTIVE){
            throw new EmployeeInactiveException("Employee is not active");
        }

        String accessToken = jwtService.generateAccessToken(employee);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setId(employee.getId());
        response.setName(employee.getName());
        response.setRole(employee.getRole());

        return response;
    }



    @Transactional
    public void logout(String refreshToken){
        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(
                () -> new InvalidRefreshTokenException("Invalid refresh token")
        );

        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }
}
