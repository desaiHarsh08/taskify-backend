package com.taskify.user.repositories;

import com.taskify.user.models.PermissionModel;
import com.taskify.user.models.ViewTaskModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionModel, Long> {

    List<PermissionModel> findByViewTask(ViewTaskModel viewTask);

}

