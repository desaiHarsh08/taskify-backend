package com.taskify.user.controllers;

import com.taskify.common.constants.CacheNames;
import com.taskify.common.utils.Helper;
import com.taskify.user.dtos.UserDto;
import com.taskify.user.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServices userServices;

    @PostMapping("/add")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        UserDto createdUser = this.userServices.createUser(userDto);
        if (createdUser == null) {
            return new ResponseEntity<>(this.userServices.getUserByEmail(userDto.getEmail()), HttpStatus.OK);
        }

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("")
    @Cacheable(cacheNames = CacheNames.ALL_USERS)
    public ResponseEntity<?> getAllUsers(
            @RequestParam("page") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "100", required = false) Integer pageSize
    ) {
        return new ResponseEntity<>(this.userServices.getAllUsers(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Cacheable(cacheNames = CacheNames.USER, key = "#id")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return new ResponseEntity<>(this.userServices.getUserById(id), HttpStatus.OK);
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<?> getUserById(@PathVariable String department) {
        return new ResponseEntity<>(this.userServices.getUsersByDepartment(department), HttpStatus.OK);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return new ResponseEntity<>(this.userServices.getUserByEmail(email), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @CacheEvict(value =     {CacheNames.ALL_USERS}, allEntries = true)
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        if (!userDto.getId().equals(id)) {
            throw new IllegalArgumentException("Please provide the valid id!");
        }

        return new ResponseEntity<>(this.userServices.updateUser(userDto), HttpStatus.OK);
    }



}
