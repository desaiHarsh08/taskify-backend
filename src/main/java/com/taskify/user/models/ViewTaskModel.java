package com.taskify.user.models;

import com.taskify.common.constants.DepartmentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "view_tasks")
public class ViewTaskModel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private DepartmentType taskType;

    @ManyToOne(targetEntity = UserModel.class)
    @JoinColumn(name = "user_id_fk")
    private UserModel user;

}
