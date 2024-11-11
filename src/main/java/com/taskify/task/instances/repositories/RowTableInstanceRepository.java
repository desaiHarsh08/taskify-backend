package com.taskify.task.instances.repositories;

import com.taskify.task.instances.models.ColumnInstanceModel;
import com.taskify.task.instances.models.RowTableInstanceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RowTableInstanceRepository extends JpaRepository<RowTableInstanceModel, Long> {

    List<RowTableInstanceModel> findByColumnInstance(ColumnInstanceModel columnInstance);

}
