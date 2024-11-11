package com.taskify.task.instances.repositories;

import com.taskify.task.instances.models.ColTableInstanceModel;
import com.taskify.task.instances.models.ColumnVariantInstanceModel;
import com.taskify.task.instances.models.RowTableInstanceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColTableInstanceRepository extends JpaRepository<ColTableInstanceModel, Long> {

    List<ColTableInstanceModel> findByRowTableInstanceId(RowTableInstanceModel rowTableInstance);

}
