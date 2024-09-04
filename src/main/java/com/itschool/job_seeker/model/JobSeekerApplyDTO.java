package com.itschool.job_seeker.model;

import com.itschool.job_seeker.entity.JobPostActivity;
import com.itschool.job_seeker.entity.JobSeekerProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerApplyDTO {

    private Long id;
    private JobSeekerProfile userId;
    private JobPostActivity job;
    private Date applyDate;
    private String coverLetter;
}
