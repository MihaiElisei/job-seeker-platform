package com.itschool.job_seeker.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users_type")
public class UsersType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userTypeId;

    private String userTypeName;

    @OneToMany(targetEntity = Users.class,mappedBy = "userTypeId")
    private List<Users> users;

}
