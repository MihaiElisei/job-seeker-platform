package com.itschool.job_seeker.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="job_seeker_profile")
public class JobSeekerProfile {

    @Id
    private Long userAccountId;

    @OneToOne
    @JoinColumn(name="user_account_id")
    @MapsId
    private Users userId;

    private String firstName;
    private String lastName;
    private String city;
    private String county;
    private String country;
    private String workAuthorization;
    private String employmentType;
    private String resume;
    @Column(nullable = true, length = 64)
    private String profilePhoto;

    @OneToMany(targetEntity = Skills.class, cascade = CascadeType.ALL, mappedBy = "jobSeekerProfile")
    private List<Skills> skills;

    @Transient
    public String getPhotosImagePath(){
        if(profilePhoto == null || userAccountId == null) return null;
        return "/photos/candidate/"+userAccountId+"/"+profilePhoto;
    }

    public JobSeekerProfile(Users users) {
        this.userId = users;
    }

}
