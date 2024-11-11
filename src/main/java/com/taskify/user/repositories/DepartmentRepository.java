package com.taskify.user.repositories;

import com.taskify.user.models.DepartmentModel;
import com.taskify.user.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentModel, Long> {

    Optional<DepartmentModel> findByName(String name);

    List<DepartmentModel> findByUser(UserModel user);

}
