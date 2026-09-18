package com.ali.worksphere.repository;

import com.ali.worksphere.entity.Project;
import com.ali.worksphere.myEnum.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByNameAndIdNot(String name, Long id);

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByDepartmentIdAndStatus(Long departmentId, ProjectStatus status);
}
