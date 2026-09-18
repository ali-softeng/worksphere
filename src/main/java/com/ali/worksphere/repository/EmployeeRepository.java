package com.ali.worksphere.repository;

import com.ali.worksphere.entity.Employee;
import com.ali.worksphere.myEnum.EmployeeRole;
import com.ali.worksphere.myEnum.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByDepartmentIdAndStatus(Long id, EmployeeStatus status);

    List<Employee> findByStatus(EmployeeStatus status);

    boolean existsByEmail(String email);

    Optional<Employee>  findByEmail(String email);;

    List<Employee> findByDepartmentIdAndStatus(Long id, EmployeeStatus status);

    long countByRoleAndStatus(EmployeeRole role, EmployeeStatus status);
}
