package com.pointers.ices4hu.services;

import com.pointers.ices4hu.models.Department;
import com.pointers.ices4hu.repositories.DepartmentRepository;
import com.pointers.ices4hu.responses.DepartmentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Department getDepartmentByDepartmentId(Long departmentId) {
        return departmentRepository.findById(departmentId).orElse(null);
    }

    public ResponseEntity<Object> getDepartments() {
        List<Department> departments = departmentRepository.findAll();
        List<DepartmentResponse> departmentResponses = new ArrayList<>();

        for (Department department: departments) {
            DepartmentResponse departmentResponse = new DepartmentResponse();
            departmentResponse.buildFrom(department);
            departmentResponses.add(departmentResponse);
        }

        return new ResponseEntity<>(departmentResponses, HttpStatus.OK);

    }

}
