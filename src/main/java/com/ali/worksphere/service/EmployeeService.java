package com.ali.worksphere.service;


import com.ali.worksphere.dto.EmployeeResponse;
import com.ali.worksphere.dto.EmployeeSummaryResponse;
import com.ali.worksphere.entity.Department;
import com.ali.worksphere.entity.Employee;
import com.ali.worksphere.exception.*;
import com.ali.worksphere.myEnum.DepartmentStatus;
import com.ali.worksphere.myEnum.EmployeeRole;
import com.ali.worksphere.myEnum.EmployeeStatus;
import com.ali.worksphere.repository.DepartmentRepository;
import com.ali.worksphere.repository.EmployeeRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class EmployeeService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    public EmployeeService(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
    }

    public EmployeeResponse createEmployee(String name, Long departmentId){
        Department department = departmentRepository.findById(departmentId).orElseThrow(
                () -> new DepartmentNotFoundException("Department with id " + departmentId + " not found")
        );

        if(department.getStatus() == DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Department with id " + departmentId + " is inactive");
        }

        Employee employee = new Employee();
        employee.setName(name);
        employee.setDepartment(department);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employeeRepository.save(employee);

        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setName(employee.getName());
        response.setDepartmentId(employee.getDepartment().getId());
        response.setDepartmentName(employee.getDepartment().getName());
        return response;
    }


    public EmployeeResponse getEmployeeById(Long id){
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new EmployeeNotFoundException("Employee with id " + id + " not found")
        );

        Employee currentEmployee = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        assert currentEmployee != null;

        if(currentEmployee.getRole() == EmployeeRole.EMPLOYEE && !currentEmployee.getId().equals(employee.getId())){
            throw new UnauthorizedAccessException("Employee can only access their own profile");
        }

       if(currentEmployee.getRole() == EmployeeRole.MANAGER){
           if(currentEmployee.getDepartment() == null){
               throw new UnauthorizedAccessException("Manager is not assigned to any department");
           }

           if(employee.getDepartment() == null){
               throw new UnauthorizedAccessException("Employee is not assigned to any department");
           }

           if(!currentEmployee.getDepartment().getId().equals(employee.getDepartment().getId())){
               throw new UnauthorizedAccessException("Manager can only access employees from their own department");
           }
       }



        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setName(employee.getName());
        if(employee.getDepartment()!= null){
            response.setDepartmentId(employee.getDepartment().getId());
            response.setDepartmentName(employee.getDepartment().getName());
        }

        return response;
    }

    public List<EmployeeResponse> getAllEmployees(){
        Employee currentEmployee = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        List<Employee> employees;

        assert currentEmployee != null;
        if(currentEmployee.getRole() == EmployeeRole.MANAGER){

            if(currentEmployee.getDepartment() == null){
                throw new UnauthorizedAccessException("Manager is not assigned to any department");
            }

             employees = employeeRepository.findByDepartmentIdAndStatus(currentEmployee.getDepartment().getId(), EmployeeStatus.ACTIVE);

        } else {
            employees = employeeRepository.findByStatus(EmployeeStatus.ACTIVE);
        }

        List<EmployeeResponse> responses = new ArrayList<>();

        for(Employee employee : employees){
            EmployeeResponse response = new EmployeeResponse();
            response.setId(employee.getId());
            response.setName(employee.getName());

            if(employee.getDepartment()!= null){
                response.setDepartmentId(employee.getDepartment().getId());
                response.setDepartmentName(employee.getDepartment().getName());
            }

            response.setStatus(employee.getStatus());

            responses.add(response);
        }

        return responses;
    }

    public EmployeeResponse updateEmployee(Long id, Long departmentId, String name){
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new EmployeeNotFoundException("Employee with id " + id + " not found")
        );

        Department department = departmentRepository.findById(departmentId).orElseThrow(
                () -> new DepartmentNotFoundException("Department with id " + departmentId + " not found")
        );

        employee.setName(name);


        if(employee.getDepartment() == null || !employee.getDepartment().getId().equals(departmentId)){

            if(department.getStatus() == DepartmentStatus.INACTIVE){
                throw new DepartmentInactiveException("Department with id " + departmentId + " is inactive");
            }
            employee.setDepartment(department);
        }


        employeeRepository.save(employee);
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setName(employee.getName());
        if(employee.getDepartment()!= null){
            response.setDepartmentId(employee.getDepartment().getId());
            response.setDepartmentName(employee.getDepartment().getName());
        }

        return response;
    }

    public List<EmployeeResponse> getPendingEmployees(){
        List<Employee> employees = employeeRepository.findByStatus(EmployeeStatus.PENDING);
        List<EmployeeResponse> responses = new ArrayList<>();
        for (Employee employee : employees) {
            EmployeeResponse response = new EmployeeResponse();
            response.setId(employee.getId());
            response.setName(employee.getName());
            response.setEmail(employee.getEmail());
            response.setStatus(employee.getStatus());
            response.setRole(employee.getRole());

            if(employee.getDepartment()!= null){
                response.setDepartmentId(employee.getDepartment().getId());
                response.setDepartmentName(employee.getDepartment().getName());
            }

            responses.add(response);
        }

        return responses;
    }


    public EmployeeResponse approveEmployee(Long id, Long departmentId, EmployeeRole role){
        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new EmployeeNotFoundException("Employee with id " + id + " not found")
        );

        if(employee.getStatus() != EmployeeStatus.PENDING){
            throw new EmployeeNotPendingException("Employee with id " + id + " is not in PENDING status and cannot be approved");
        }

        Employee currentEmployee = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        assert currentEmployee != null;
        if(currentEmployee.getRole() == EmployeeRole.HR && role ==EmployeeRole.ADMIN){
            throw new UnauthorizedRoleAssignmentException("HR cannot assign ADMIN role");
        }

        if(currentEmployee.getRole() == EmployeeRole.HR && role == EmployeeRole.HR){
            throw new UnauthorizedRoleAssignmentException("HR cannot assign HR role");
        }

        Department department = departmentRepository.findById(departmentId).orElseThrow(
                () -> new DepartmentNotFoundException("Department with id " + departmentId + " not found")
        );

        if(department.getStatus() == DepartmentStatus.INACTIVE){
            throw new DepartmentInactiveException("Department with id " + departmentId + " is inactive");
        }



        employee.setDepartment(department);
        employee.setRole(role);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employeeRepository.save(employee);

        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setName(employee.getName());
        response.setEmail(employee.getEmail());
        if(employee.getDepartment()!= null){
            response.setDepartmentId(employee.getDepartment().getId());
            response.setDepartmentName(employee.getDepartment().getName());
        }
        response.setStatus(employee.getStatus());
        response.setRole(employee.getRole());

        return response;
    }

    public EmployeeResponse deleteEmployee(Long id){

        Employee currentEmployee = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();


        Employee employee = employeeRepository.findById(id).orElseThrow(
                () -> new EmployeeNotFoundException("Employee with id " + id + " not found")
        );

        if(employee.getStatus() ==  EmployeeStatus.INACTIVE){
            throw new EmployeeAlreadyInactiveException("Employee is already INACTIVE");
        }

        assert currentEmployee != null;
        if(currentEmployee.getId().equals(employee.getId())){
            throw new UnauthorizedAccessException("You can't deactivate yourself");
        }

        if(employee.getRole() == EmployeeRole.ADMIN){
            long count = employeeRepository.countByRoleAndStatus(employee.getRole(), EmployeeStatus.ACTIVE);
            if(count ==1 ){
                throw new UnauthorizedAccessException("Cannot deactivate last Admin");
            }


        }

        employee.setStatus(EmployeeStatus.INACTIVE);
        employeeRepository.save(employee);
        EmployeeResponse response = new EmployeeResponse();
        response.setId(employee.getId());
        response.setName(employee.getName());
        response.setStatus(employee.getStatus());
        return response;
    }


    public EmployeeResponse getMyProfile(){
        Employee currentUser = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        assert currentUser != null;
        return getEmployeeById(currentUser.getId());
    }

    public List<EmployeeSummaryResponse> getMyTeam(){
        Employee currentUser = (Employee) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        assert currentUser != null;

        if(currentUser.getDepartment() == null){
            throw new DepartmentNotFoundException("Department is not assigned yet");
        }

        List<Employee> employees = currentUser.getDepartment().getEmployees();
        List<EmployeeSummaryResponse> responses = new ArrayList<>();

        for (Employee e : employees){

            if(e.getId().equals(currentUser.getId())){
                continue;
            }

            EmployeeSummaryResponse response = new EmployeeSummaryResponse();
            response.setId(e.getId());
            response.setName(e.getName());
            response.setRole(e.getRole());

            responses.add(response);
        }

        return responses;
    }

}
