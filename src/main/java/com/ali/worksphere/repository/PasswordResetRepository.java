package com.ali.worksphere.repository;

import com.ali.worksphere.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordResetOtp, Long> {
    Optional<PasswordResetOtp> findByEmail(String email);
    void deleteByEmail(String email);
}
