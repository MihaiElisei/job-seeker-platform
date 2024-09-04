package com.itschool.job_seeker.model;

import com.itschool.job_seeker.entity.Skills;
import com.itschool.job_seeker.entity.Users;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerProfileDTO {

    private Long userAccountId;
    private Users userId;
    private String firstName;
    private String lastName;
    private String city;
    private String county;
    private String country;
    private String workAuthorization;
    private String employmentType;
    private String resume;
    private String profilePhoto;
    private List<Skills> skills;

}
