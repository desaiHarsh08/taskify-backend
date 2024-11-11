    package com.taskify.user.models;

    import com.taskify.task.templates.models.FunctionTemplateModel;
    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;

    @Entity
    @Table(name = "departments")
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public class DepartmentModel {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String name;

        @ManyToOne(targetEntity = UserModel.class)
        @JoinColumn(nullable = false)
        private UserModel user;

        @ManyToMany(targetEntity = RoleModel.class)
        @JoinTable(
                name = "departments_roles",
                joinColumns = @JoinColumn(name = "department_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id"),
                uniqueConstraints = @UniqueConstraint(columnNames = {"department_id", "role_id"})
        )
        private List<RoleModel> roles = new ArrayList<>();

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
            updatedAt = LocalDateTime.now();
        }

        @PreUpdate
        protected void onUpdate() {
            updatedAt = LocalDateTime.now();
        }

        public DepartmentModel(Long id) {
            this.id = id;
        }

        public void removeRole(RoleModel role) {
            roles.remove(role);
            role.getDepartments().remove(this);
        }

    }
