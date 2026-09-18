package com.ali.worksphere.repository;

import com.ali.worksphere.entity.Department;
import com.ali.worksphere.myEnum.DepartmentStatus;
import com.ali.worksphere.myEnum.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department,Long> {
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    List<Department> findByStatus(DepartmentStatus status);

    boolean existsByManagerId(Long id);

}
