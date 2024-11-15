package com.taskify.user.repositories;

import com.taskify.common.constants.CacheNames;
import com.taskify.user.models.UserModel;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {

    Optional<UserModel> findByEmail(String email);

    @Override
    @Cacheable(value = CacheNames.USER, key = "#id", condition = "#result != null && #result.isPresent()")
    Optional<UserModel> findById(Long id);

    @Query("SELECT u FROM UserModel u JOIN DepartmentModel d WHERE u.id = d.id AND d.name = :departmentName")
    List<UserModel> findUsersByDepartment(@Param("departmentName") String departmentName);

}
