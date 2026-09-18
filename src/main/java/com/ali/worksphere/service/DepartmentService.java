package com.ali.worksphere.service;


import com.ali.worksphere.dto.*;
import com.ali.worksphere.entity.Department;
import com.ali.worksphere.entity.Employee;
import com.ali.worksphere.entity.Project;
import com.ali.worksphere.exception.*;
import com.ali.worksphere.myEnum.DepartmentStatus;
import com.ali.worksphere.myEnum.EmployeeRole;
import com.ali.worksphere.myEnum.EmployeeStatus;
import com.ali.worksphere.repository.DepartmentRepository;
import com.ali.worksphere.repository.EmployeeRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public DepartmentService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public DepartmentResponse createDepartment(String name){
        if(departmentRepository.existsByName(name)){
            throw new DepartmentAlreadyExistsException("Department already exists");
        }

        Department department = new Department();
        department.setName(name);
        department.setStatus(DepartmentStatus.ACTIVE);
        departmentRepository.save(department);

        DepartmentResponse response = new DepartmentResponse();
        response.setId(department.getId());
        response.setName(department.getName());
        response.setStatus(department.getStatus());
        return response;
    }


    public DepartmentDetailResponse getDepartmentById(Long id){
        Department department = departmentRepository.findById(id).orElseThrow(
                () -> new DepartmentNotFoundException("Department with id " + id + " not found")
        );

        Employee currentEmployee = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        assert currentEmployee != null;

        if(currentEmployee.getRole() == EmployeeRole.MANAGER){
            if(currentEmployee.getDepartment() == null){
                throw new UnauthorizedAccessException("Employee is not assigned to any department");
            }

            if (currentEmployee.getDepartment().getStatus() == DepartmentStatus.INACTIVE){
                throw new DepartmentInactiveException("Manager's department is inactive");
            }

            if(!currentEmployee.getDepartment().getId().equals(department.getId())){
                throw new UnauthorizedAccessException("Manager can only access their own department");
            }
        }


        List<EmployeeSummaryResponse> responses = new ArrayList<>();
        List<Employee> employees = department.getEmployees();
        for(Employee e : employees){
            EmployeeSummaryResponse response = new EmployeeSummaryResponse();
            response.setId(e.getId());
            response.setName(e.getName());
            response.setRole(e.getRole());

            responses.add(response);
        }

        DepartmentDetailResponse response = new DepartmentDetailResponse();
        response.setId(department.getId());
        response.setName(department.getName());
        response.setStatus(department.getStatus());
        if(department.getManager() != null){
            response.setManagerName(department.getManager().getName());
        }
        response.setEmployees(responses);
        return response;
    }


    public List<DepartmentResponse> getAllDepartments(){
        Employee currentEmployee = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        List<DepartmentResponse> responses = new ArrayList<>();
        List<Department> departments;

        assert currentEmployee != null;

        if(currentEmployee.getRole() == EmployeeRole.MANAGER){

            if(currentEmployee.getDepartment() == null){
                throw new UnauthorizedAccessException("Manager is not assigned to any department");
            }

            if(currentEmployee.getDepartment().getStatus() == DepartmentStatus.INACTIVE){
                throw new DepartmentInactiveException("Manager's department is inactive");
            }



            departments = List.of(currentEmployee.getDepartment());

        } else {
            departments = departmentRepository.findByStatus(DepartmentStatus.ACTIVE);
        }

        for(Department department : departments){
            DepartmentResponse response = new DepartmentResponse();
            response.setId(department.getId());
            response.setName(department.getName());
            response.setStatus(department.getStatus());

            responses.add(response);
        }

        return responses;
    }

    public DepartmentResponse updateDepartment(Long id, String name){
        Department department = departmentRepository.findById(id).orElseThrow(
                () -> new DepartmentNotFoundException("Department with id " + id + " not found")
        );

        if(department.getStatus() == DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Department is inactive so can't updated");
        }


        if(departmentRepository.existsByNameAndIdNot(name, id)){
            throw new DepartmentAlreadyExistsException("Department name " + name + "already exists");
        }


        department.setName(name);
        departmentRepository.save(department);
        DepartmentResponse response = new DepartmentResponse();
        response.setId(department.getId());
        response.setName(department.getName());
        response.setStatus(department.getStatus());
        return response;
    }

    @Transactional
    public void deleteDepartment(Long id){
        Department department = departmentRepository.findById(id).orElseThrow(
                () -> new DepartmentNotFoundException("Department with id " + id + " not found"));

        if(department.getStatus() ==  DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Department is already inactive");
        }

        if(employeeRepository.existsByDepartmentIdAndStatus(id, EmployeeStatus.ACTIVE)){
            throw new DepartmentHasActiveEmployeesException(
                    " Department cannot be deactivated while active employees exist");
        }

        department.setStatus(DepartmentStatus.INACTIVE);
        department.setManager(null);
        departmentRepository.save(department);
    }

    @Transactional
    public ManagerResponse assignManager(Long departmentId, Long employeeId){
        Department department = departmentRepository.findById(departmentId).orElseThrow(
                () -> new DepartmentNotFoundException("Department with id " + departmentId + " not found")
        );


        if(department.getStatus() == DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Department with id " + departmentId +  " is inactive so can't be assigned");
        }

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new EmployeeNotFoundException("Employee with id " + employeeId + " not found")
        );


        if(employee.getStatus() == EmployeeStatus.INACTIVE){
            throw new EmployeeInactiveException("Employee with id " + employeeId + " is inactive so can't be assigned");
        }

        if(employee.getDepartment() == null){
            throw new UnauthorizedAccessException("Employee is not assigned to any department");
        }

        if(!employee.getDepartment().getId().equals(departmentId)){
            throw new DifferentDepartmentException("Employee department and target department is different.");
        }

        if(departmentRepository.existsByManagerId(employeeId)){
            throw new EmployeeAlreadyManager("Employee " + employee.getName() + "is already a manager");
        }

        if(department.getManager() != null){
            throw new DepartmentAlreadyHasManagerException("Department already has a manager");
        }

        department.setManager(employee);
        employee.setRole(EmployeeRole.MANAGER);
        departmentRepository.save(department);
        ManagerResponse response = new ManagerResponse();
        response.setDepartmentId(department.getId());
        response.setDepartmentName(department.getName());
        response.setManagerId(employee.getId());
        response.setManagerName(employee.getName());

        return response;
    }



    @Transactional
    public ManagerResponse changeManager(Long departmentId, Long employeeId){
        Department department = departmentRepository.findById(departmentId).orElseThrow(
                () -> new DepartmentNotFoundException("Department with id " + departmentId + " not found")
        );

        if(department.getStatus() == DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Department is inactive. Can't change manager");
        }

        Employee employee = employeeRepository.findById(employeeId).orElseThrow(
                () -> new EmployeeNotFoundException("Employee with id " + employeeId + " not found")
        );

        if (employee.getStatus() == EmployeeStatus.INACTIVE){
            throw new EmployeeInactiveException("Employee is inactive. Can't change manager");
        }

        if(!employee.getDepartment().getId().equals(departmentId)){
            throw new DifferentDepartmentException("Employee department and target department is different.");
        }

        if(departmentRepository.existsByManagerId(employeeId)){
            throw new EmployeeAlreadyManager("Employee " + employee.getName() + "is already a manager");
        }

        if(department.getManager()  == null){
            throw new DepartmentManagerAlreadyNull( "Department has no manager to change");
        }

        Employee oldManager = department.getManager();
        department.setManager(employee);
        oldManager.setRole(EmployeeRole.EMPLOYEE);
        employee.setRole(EmployeeRole.MANAGER);
        departmentRepository.save(department);

        ManagerResponse response = new ManagerResponse();
        response.setDepartmentId(department.getId());
        response.setDepartmentName(department.getName());
        response.setManagerId(employee.getId());
        response.setManagerName(employee.getName());
        return response;
    }



    @Transactional
    public void deleteManager(Long departmentId){
        Department department = departmentRepository.findById(departmentId).orElseThrow(
                () -> new DepartmentNotFoundException("Department with id " + departmentId + " not found")
        );

        if(department.getStatus() == DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Department is inactive. Can't delete manager");
        }

        if(department.getManager() == null){
            throw new DepartmentManagerAlreadyNull("No department manager assigned yet");
        }

        Employee manager = department.getManager();

        department.setManager(null);
        manager.setRole(EmployeeRole.EMPLOYEE);
        departmentRepository.save(department);
    }

    public MyDepartmentResponse getMyDepartment(){
        Employee currentUser = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert currentUser != null;

        if(currentUser.getDepartment() == null){
            throw new UnauthorizedAccessException("Employee : " + currentUser.getName() + " is not assigned to any department");
        }


        Department department = currentUser.getDepartment();
        if(department.getStatus() == DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Department is inactive ");
        }

        List<Employee> employees = department.getEmployees();
        List<EmployeeSummaryResponse> myEmployees = new ArrayList<>();

        for (Employee e : employees){
            EmployeeSummaryResponse summaryResponse = new EmployeeSummaryResponse();
            summaryResponse.setId(e.getId());
            summaryResponse.setName(e.getName());
            summaryResponse.setRole(e.getRole());

            myEmployees.add(summaryResponse);
        }

        List<Project> project = department.getProjects();
        List<ProjectSummaryResponse> projects = new ArrayList<>();

        for (Project p : project){
            ProjectSummaryResponse summaryResponse = new ProjectSummaryResponse();
            summaryResponse.setId(p.getId());
            summaryResponse.setName(p.getName());
            summaryResponse.setStatus(p.getStatus());

            projects.add(summaryResponse);
        }

        MyDepartmentResponse response = new MyDepartmentResponse();
        response.setId(department.getId());
        response.setName(department.getName());
        response.setStatus(department.getStatus());
        if(department.getManager() != null){
            response.setManager(department.getManager().getName());
        }
        response.setEmployees(myEmployees);
        response.setProjects(projects);

        return response;
    }

}
