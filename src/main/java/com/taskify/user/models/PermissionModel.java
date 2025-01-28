package com.taskify.user.models;

import com.taskify.common.constants.PermissionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private PermissionType type;

    @ManyToOne(targetEntity = ViewTaskModel.class)
    @JoinColumn(name = "view_task_id_fk")
    private ViewTaskModel viewTask;

}
