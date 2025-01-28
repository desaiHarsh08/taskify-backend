package com.taskify.user.repositories;

import com.taskify.user.models.UserModel;
import com.taskify.user.models.ViewTaskModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewTaskRepository extends JpaRepository<ViewTaskModel, Long> {

    List<ViewTaskModel> findByUser(UserModel user);

}
