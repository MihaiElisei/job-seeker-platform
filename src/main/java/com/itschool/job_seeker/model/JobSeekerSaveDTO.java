package com.itschool.job_seeker.model;

import com.itschool.job_seeker.entity.JobPostActivity;
import com.itschool.job_seeker.entity.JobSeekerProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerSaveDTO {

    private Long id;
    private JobSeekerProfile userId;
    private JobPostActivity job;
}
