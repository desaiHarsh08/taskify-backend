package com.taskify.stakeholders.controllers;

import com.taskify.common.utils.PageResponse;
import com.taskify.stakeholders.dtos.ParentCompanyDto;
import com.taskify.stakeholders.services.ParentCompanyServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parent-companies")
public class ParentCompanyController {

    private final ParentCompanyServices parentCompanyServices;

    @Autowired
    public ParentCompanyController(ParentCompanyServices parentCompanyServices) {
        this.parentCompanyServices = parentCompanyServices;
    }

    @PostMapping
    public ResponseEntity<ParentCompanyDto> createParentCompany(@RequestBody ParentCompanyDto parentCompanyDto) {
        ParentCompanyDto createdCompany = parentCompanyServices.createParentCompany(parentCompanyDto);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PageResponse<ParentCompanyDto>> getAllParentCompanies(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize) {
        PageResponse<ParentCompanyDto> response = this.parentCompanyServices.getAllParentCompanies(pageNumber, pageSize);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<ParentCompanyDto>> getParentCompaniesByNameStateCityAndPincode(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @RequestParam String searchTxt
    ) {
        PageResponse<ParentCompanyDto> response = parentCompanyServices.getParentCompaniesByNameStateCityAndPincode(pageNumber, pageSize, searchTxt);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParentCompanyDto> getParentCompanyById(@PathVariable Long id) {
        ParentCompanyDto parentCompanyDto = parentCompanyServices.getParentCompanyById(id);
        return new ResponseEntity<>(parentCompanyDto, HttpStatus.OK);
    }

    @GetMapping("/email")
    public ResponseEntity<?> getParentCompanyByEmail(
            @RequestParam(name = "page", defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "100") Integer pageSize,
            @RequestParam String email
    ) {
        return new ResponseEntity<>(
                this.parentCompanyServices.getParentCompanyByEmail(pageNumber, pageSize, email),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParentCompanyDto> updateParentCompany(
            @PathVariable Long id,
            @RequestBody ParentCompanyDto parentCompanyDto
    ) {
        if (!parentCompanyDto.getId().equals(id)) {
            throw new IllegalArgumentException("Please provide the valid parent_company_id!");
        }

        return new ResponseEntity<>(this.parentCompanyServices.updateParentCompany(parentCompanyDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteParentCompany(@PathVariable Long id) {
        return new ResponseEntity<>(this.parentCompanyServices.deleteParentCompany(id), HttpStatus.NO_CONTENT);
    }
}
