package com.itschool.job_seeker.model;

import com.itschool.job_seeker.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsersTypeDTO {

    private Long userTypeId;

    private String userTypeName;

    private List<Users> users;

}
