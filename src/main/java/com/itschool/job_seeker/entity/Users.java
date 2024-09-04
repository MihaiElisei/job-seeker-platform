package com.itschool.job_seeker.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data //Lombok annotations to generate automatically all constructors all getters and all setters
@AllArgsConstructor
@NoArgsConstructor
@Entity  // Annotation to tell Hibernate that this class is an entity and should be persisted in the database
@Table(name="users") // Annotation to tell Hibernate that this entity should be mapped to the 'users' table in the database
public class Users {


    @Id // Annotation to tell Hibernate that this field is the primary key in the table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Annotation to tell Hibernate to generate new ids for us. GenerationType.IDENTITY will use the database's auto-increment feature (will increment the id by 1)
    private Long userId;

    @Column(unique=true)
    private String email;

    @NotEmpty
    private String password;

    private boolean isActive;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date registrationDate;

    @ManyToOne(cascade = CascadeType.ALL) // fetch is used to specify how the posts should be fetched (LAZY will fetch the posts only when they are accessed)
    @JoinColumn(name="userTypeId", referencedColumnName = "userTypeId") // name will be the column name in the 'users' table
    private UsersType userTypeId;
}
