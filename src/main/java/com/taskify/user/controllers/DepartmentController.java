package com.taskify.user.controllers;

import com.taskify.user.dtos.DepartmentDto;
import com.taskify.user.services.DepartmentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentServices departmentServices;

    @PostMapping("")
    public ResponseEntity<DepartmentDto> addDepartment(@RequestBody DepartmentDto departmentDto) {
        return new ResponseEntity<>(this.departmentServices.createDepartment(departmentDto), HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllDepartments(
            @RequestParam("page") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "100", required = false) Integer pageSize
    ) {
        return new ResponseEntity<>(this.departmentServices.getAllDepartments(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getDepartmentsByUserId(@RequestParam("id") Long userId) {
        return new ResponseEntity<>(this.departmentServices.getDepartmentsByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartmentsById(@PathVariable Long id) {
        return new ResponseEntity<>(this.departmentServices.getDepartmentById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        return new ResponseEntity<>(this.departmentServices.deleteDepartment(id), HttpStatus.OK);
    }

}
