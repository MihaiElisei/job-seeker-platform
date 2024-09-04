package com.itschool.job_seeker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersDTO {

    private Long userId;
    private String email;
    private String password;
    private boolean isActive;
    private Date registrationDate;
    private Long userTypeId;
}
