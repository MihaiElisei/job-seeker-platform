package com.itschool.job_seeker.model;

import com.itschool.job_seeker.entity.JobCompany;
import com.itschool.job_seeker.entity.JobLocation;
import com.itschool.job_seeker.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobPostActivityDTO {

    private Long jobPostId;
    private Users postedById;
    private JobLocation jobLocationId;
    private JobCompany jobCompanyId;
    private Boolean isActive;
    private Boolean isSaved;
    private String descriptionOfJob;
    private String jobTitle;
    private String jobType;
    private String salary;
    private String remote;
    private Date postedDate;
}
