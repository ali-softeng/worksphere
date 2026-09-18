package com.ali.worksphere.repository;

import com.ali.worksphere.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    void deleteByEmployeeId(Long employeeId);
}
