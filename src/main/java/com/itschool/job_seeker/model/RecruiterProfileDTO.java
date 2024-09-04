package com.itschool.job_seeker.model;

import com.itschool.job_seeker.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterProfileDTO {

    private Long userAccountId;
    private Users userId;
    private String firstName;
    private String lastName;
    private String city;
    private String county;
    private String country;
    private String company;
    private String profilePhoto;
}
