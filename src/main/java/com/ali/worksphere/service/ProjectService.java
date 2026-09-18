package com.ali.worksphere.service;

import com.ali.worksphere.dto.EmployeeResponse;
import com.ali.worksphere.dto.ProjectResponse;
import com.ali.worksphere.dto.ProjectSummaryResponse;
import com.ali.worksphere.entity.Department;
import com.ali.worksphere.entity.Employee;
import com.ali.worksphere.entity.Project;
import com.ali.worksphere.exception.*;
import com.ali.worksphere.myEnum.DepartmentStatus;
import com.ali.worksphere.myEnum.EmployeeRole;
import com.ali.worksphere.myEnum.EmployeeStatus;
import com.ali.worksphere.myEnum.ProjectStatus;
import com.ali.worksphere.repository.DepartmentRepository;
import com.ali.worksphere.repository.EmployeeRepository;
import com.ali.worksphere.repository.ProjectRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public ProjectService(ProjectRepository projectRepository, DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.projectRepository = projectRepository;
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public ProjectResponse createProject(String name, Long departmentId){
        Employee currentEmployee = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        Department department = departmentRepository.findById(departmentId).orElseThrow(
                () -> new DepartmentNotFoundException("Department with id " + departmentId + " not found")
        );


        if(department.getStatus() == DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Department with id " + departmentId + " is inactive");
        }

        assert currentEmployee != null;
        if(currentEmployee.getDepartment() == null){
            throw new UnauthorizedAccessException("Manager is not assigned to any department");
        }

        if(currentEmployee.getDepartment().getStatus() ==  DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Manager's department is inactive");
        }

        if(!currentEmployee.getDepartment().getId().equals(department.getId())){
            throw new UnauthorizedAccessException("Manager can only create project for their own department");
        }

        Project project = new Project();
        project.setName(name);
        project.setDepartment(department);
        project.setStatus(ProjectStatus.DRAFT);
        projectRepository.save(project);

        return convertToResponse(project);
    }


    @Transactional
    public ProjectResponse assignProjectManager(Long projectId,Long employeeId){

        Project project = findProjectById(projectId);

        if(project.getStatus() == ProjectStatus.INACTIVE){
            throw new  ProjectInactiveException("Project with id " + projectId + " is inactive");
        }

        if (project.getStatus() == ProjectStatus.ACTIVE){
            throw new ProjectAlreadyActiveException("Project with id " + projectId + " is already active");
        }

        Employee currentEmployee = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert currentEmployee != null;
        if(currentEmployee.getDepartment() == null){
            throw new UnauthorizedAccessException("Manager is not assigned to any department");

        }

        if(currentEmployee.getDepartment().getStatus() ==  DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Manager's department is inactive");
        }


        if(!currentEmployee.getDepartment().getId().equals(project.getDepartment().getId())){
            throw new UnauthorizedAccessException("Manager can only assign project manager for their own department");
        }

        Employee employee = findEmployeeById(employeeId);

        validateEmployeeForProject(employee,project,employeeId);

        if(project.getManager() != null){
            throw new ProjectAlreadyHasManagerException("Project with id " + projectId + " has already manager");
        }

        project.setManager(employee);
        project.setStatus(ProjectStatus.ACTIVE);
        project.getEmployees().add(employee);
        projectRepository.save(project);

        return convertToResponse(project);
    }

    @Transactional
    public ProjectResponse changeProjectManager(Long projectId, Long employeeId){
        Project project = findProjectById(projectId);

        if(project.getStatus() == ProjectStatus.INACTIVE){
            throw new  ProjectInactiveException("Project with id " + projectId + " is inactive");
        }

        if (project.getStatus() == ProjectStatus.DRAFT){
            throw new ProjectNotActiveException("Project id " + projectId + " is draft. Can't change manager");
        }

        Employee currentEmployee = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert currentEmployee != null;
        if(currentEmployee.getDepartment() == null){
            throw new UnauthorizedAccessException("Manager is not assigned to any department");

        }

        if(currentEmployee.getDepartment().getStatus() ==  DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Department Manager is inactive");
        }


        if(!currentEmployee.getDepartment().getId().equals(project.getDepartment().getId())){
            throw new UnauthorizedAccessException("Manager can only change project manager for their own department");
        }

        Employee employee = findEmployeeById(employeeId);

        validateEmployeeForProject(employee,project,employeeId);

        project.setManager(employee);

        if(!project.getEmployees().contains(employee)){
            project.getEmployees().add(employee);
        }


        projectRepository.save(project);

        return convertToResponse(project);
    }

    @Transactional
    public void deleteProjectManager(Long projectId){
        Project project = findProjectById(projectId);

        if(project.getStatus() == ProjectStatus.INACTIVE){
            throw new ProjectInactiveException("Project with id " + projectId + " is inactive");
        }

        if(project.getManager() == null){
            throw new ProjectManagerAlreadyNull("Project manager is already null");
        }

        Employee currentEmployee = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert currentEmployee != null;
        if(currentEmployee.getDepartment() == null){
            throw new UnauthorizedAccessException("Manager is not assigned to any department");

        }

        if(currentEmployee.getDepartment().getStatus() ==  DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Manager's department is inactive");
        }


        if(!currentEmployee.getDepartment().getId().equals(project.getDepartment().getId())){
            throw new UnauthorizedAccessException("Manager can only deactivate project manager for their own department");
        }

        project.setManager(null);
        project.setStatus(ProjectStatus.DRAFT);
        projectRepository.save(project);
    }

    @Transactional
    public ProjectResponse assignEmployeeToProject(Long projectId, Long employeeId){


        Project project = findProjectById(projectId);

        if(project.getStatus() == ProjectStatus.INACTIVE){
            throw new  ProjectInactiveException("Project with id " + projectId + " is inactive");
        }

        Employee currentEmployee = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert currentEmployee != null;
        if(currentEmployee.getDepartment() == null){
            throw new UnauthorizedAccessException("Manager is not assigned to any department");

        }

        if(currentEmployee.getDepartment().getStatus() ==  DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Manager's department is inactive");
        }


        if(!currentEmployee.getDepartment().getId().equals(project.getDepartment().getId())){
            throw new UnauthorizedAccessException("Manager can only assign employee to project for their own department");
        }

        Employee employee = findEmployeeById(employeeId);

        validateEmployeeForProject(employee,project,employeeId);

        if(project.getEmployees().contains(employee)){
            throw new EmployeeAlreadyAssign("Employee " + employee.getName() + " has already assigned to employee list.");
        }

        project.getEmployees().add(employee);
        projectRepository.save(project);

        return convertToResponse(project);

    }



    @Transactional
    public ProjectResponse removeEmployeeFromProject(Long projectId, Long employeeId){

        Project project = findProjectById(projectId);

        if(project.getStatus() == ProjectStatus.INACTIVE){
            throw new  ProjectInactiveException("Project with id " + projectId + " is inactive");
        }

        Employee currentEmployee = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert currentEmployee != null;
        if(currentEmployee.getDepartment() == null){
            throw new UnauthorizedAccessException("Manager is not assigned to any department");

        }

        if(currentEmployee.getDepartment().getStatus() ==  DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Manager's department is inactive");
        }


        if(!currentEmployee.getDepartment().getId().equals(project.getDepartment().getId())){
            throw new UnauthorizedAccessException("Manager can only remove projects from their own department");
        }

        Employee employee = findEmployeeById(employeeId);

        validateEmployeeForProject(employee,project,employeeId);

        if(!project.getEmployees().contains(employee)){
            throw new EmployeeNotAssignedException("Employee " + employee.getName() + " has not assigned yet.");
        }

        if(project.getManager() != null){
            if(project.getManager().getId().equals(employeeId)){
                throw new EmployeeManagerCannotBeRemoved("Employee Manager " + employee.getName() + "cannot be removed");
            }
        }

        project.getEmployees().remove(employee);
        projectRepository.save(project);

        return  convertToResponse(project);
    }

    public ProjectResponse getProjectById(Long projectId){
        Project project = findProjectById(projectId);

        Employee currentEmployee = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert currentEmployee != null;
       if(currentEmployee.getRole() == EmployeeRole.MANAGER){
           if(currentEmployee.getDepartment() == null){
               throw new UnauthorizedAccessException("Manager is not assigned to any department");
           }

           if(!currentEmployee.getDepartment().getId().equals(project.getDepartment().getId())){
               throw new UnauthorizedAccessException("Manager can only access their own department projects");
           }
       }

        return convertToResponse(project);
    }


    public List<ProjectResponse> getAllProjects(){
        List<Project> projects;
        List<ProjectResponse> responses = new ArrayList<>();

        Employee currentEmployee = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert currentEmployee != null;
        if(currentEmployee.getRole() == EmployeeRole.MANAGER){
            if(currentEmployee.getDepartment() == null){
                throw new UnauthorizedAccessException("Manager is not assigned to any department");
            }

            if(currentEmployee.getDepartment().getStatus() == DepartmentStatus.INACTIVE){
                throw new DepartmentInactiveException("Manager's department is inactive");
            }

            projects = projectRepository.findByDepartmentIdAndStatus(currentEmployee.getDepartment().getId(), ProjectStatus.ACTIVE);
        } else {
            projects = projectRepository.findByStatus(ProjectStatus.ACTIVE);
        }

        for(Project p : projects){
           responses.add(convertToResponse(p));
        }

        return responses;
    }

    public List<ProjectResponse> getProjectByStatus(ProjectStatus status){
        List<Project> projects;
        List<ProjectResponse> responses = new ArrayList<>();

        Employee currentEmployee = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert currentEmployee != null;
        if(currentEmployee.getRole() == EmployeeRole.MANAGER){
            if(currentEmployee.getDepartment() == null){
                throw new UnauthorizedAccessException("Manager is not assigned to any department");
            }

            if(currentEmployee.getDepartment().getStatus() == DepartmentStatus.INACTIVE){
                throw new DepartmentInactiveException("Manager's department is inactive");
            }

            projects = projectRepository.findByDepartmentIdAndStatus(currentEmployee.getDepartment().getId(), status);
        } else {
            projects = projectRepository.findByStatus(status);
        }

        for(Project p : projects){
            responses.add(convertToResponse(p));
        }
        return responses;
    }

    @Transactional
    public ProjectResponse updateProject(String name, Long projectId){
        Project project = findProjectById(projectId);

        if(project.getStatus() == ProjectStatus.INACTIVE){
            throw new ProjectInactiveException("Project with id " + projectId + " is inactive");
        }

        Employee currentEmployee = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert currentEmployee != null;
        if(currentEmployee.getDepartment() == null){
            throw new UnauthorizedAccessException("Manager is not assigned to any department");

        }

        if(currentEmployee.getDepartment().getStatus() ==  DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Manager's department is inactive");
        }


        if(!currentEmployee.getDepartment().getId().equals(project.getDepartment().getId())){
            throw new UnauthorizedAccessException("Manager can only update projects for their own department");
        }

        if(projectRepository.existsByNameAndIdNot(name, projectId)){
            throw new ProjectNameAlreadyExistsException("Project name " + name + "is already exists");
        }



        project.setName(name);
        projectRepository.save(project);

        return convertToResponse(project);
    }


    @Transactional
    public void deleteProject(Long projectId){
        Project project = findProjectById(projectId);

        if(project.getStatus() == ProjectStatus.INACTIVE){
            throw new ProjectInactiveException("Project with id " + projectId + " is already inactive");
        }

        Employee currentEmployee = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert currentEmployee != null;
        if(currentEmployee.getDepartment() == null){
            throw new UnauthorizedAccessException("Manager is not assigned to any department");

        }

        if(currentEmployee.getDepartment().getStatus() ==  DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Manager's department is inactive");
        }


        if(!currentEmployee.getDepartment().getId().equals(project.getDepartment().getId())){
            throw new UnauthorizedAccessException("Manager can only deactivate projects for their own department");
        }

        project.setStatus(ProjectStatus.INACTIVE);
        projectRepository.save(project);

    }


    private Project findProjectById(Long projectId){
        return projectRepository.findById(projectId).orElseThrow(
                () -> new ProjectNotFoundException("Project with id " + projectId + " not found")
        );
    }

    private Employee findEmployeeById(Long employeeId){
        return employeeRepository.findById(employeeId).orElseThrow(
                () -> new EmployeeNotFoundException("Employee with id " + employeeId + " not found")
        );
    }

    private void validateEmployeeForProject(Employee employee, Project project, Long employeeId){

        if(employee.getStatus() == EmployeeStatus.INACTIVE){
            throw new EmployeeInactiveException("Employee with id " + employeeId + " is inactive");
        }

        if(employee.getDepartment() == null){
            throw new UnauthorizedAccessException("Employee with id " + employeeId + " is not assigned to any department");
        }

        if(!employee.getDepartment().getId().equals(project.getDepartment().getId())){
            throw new DifferentDepartmentException("Employee with id " + employeeId + " has different department");
        }
    }

    private ProjectResponse convertToResponse(Project project){
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setStatus(project.getStatus());
        response.setDepartmentId(project.getDepartment().getId());
        response.setDepartmentName(project.getDepartment().getName());

        if(project.getManager() != null){
            response.setManagerId(project.getManager().getId());
            response.setManagerName(project.getManager().getName());

        }

        List<EmployeeResponse>  employeeResponses = new ArrayList<>();

        for(Employee e : project.getEmployees()){
            EmployeeResponse employeeResponse = new EmployeeResponse();
            employeeResponse.setId(e.getId());
            employeeResponse.setName(e.getName());

            employeeResponses.add(employeeResponse);
        }

        response.setEmployees(employeeResponses);

        return response;
    }


    public List<ProjectSummaryResponse> getMyProjects() {
        Employee currentUser = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert currentUser != null;

        List<Project> projects = currentUser.getProjects();
        List<ProjectSummaryResponse> responses = new ArrayList<>();

        for (Project p : projects){
            ProjectSummaryResponse response = new ProjectSummaryResponse();
            response.setId(p.getId());
            response.setName(p.getName());
            response.setStatus(p.getStatus());

            responses.add(response);
        }

        return responses;
    }

    public List<ProjectSummaryResponse> getDepartmentProjects(){
        Employee currentUser = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert currentUser != null;
        if(currentUser.getDepartment() == null){
            throw new DepartmentNotFoundException("Manager department is not assigned");
        }

        if(currentUser.getStatus() == EmployeeStatus.INACTIVE){
            throw new EmployeeInactiveException("Manager status is inactive so can't view department projects ");
        }

        if(currentUser.getDepartment().getStatus() == DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Manager's department is inactive");
        }

        List<Project> projects = currentUser.getDepartment().getProjects();
        List<ProjectSummaryResponse> responses = new ArrayList<>();

        for(Project p : projects){
            ProjectSummaryResponse response = new ProjectSummaryResponse();
            response.setId(p.getId());
            response.setName(p.getName());
            response.setStatus(p.getStatus());

            responses.add(response);
        }

        return responses;
    }
}

