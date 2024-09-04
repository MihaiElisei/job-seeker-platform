package com.itschool.job_seeker.model;

import com.itschool.job_seeker.entity.JobCompany;
import com.itschool.job_seeker.entity.JobLocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterJobsDTO {

    private Long totalCandidates;
    private Long jobPostId;
    private String jobTitle;
    private JobLocation jobLocationId;
    private JobCompany jobCompanyId;


}
