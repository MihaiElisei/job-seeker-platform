package com.itschool.job_seeker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data// Lombok annotation to auto-generate getter/setter methods, toString(), equals(), and hashCode() methods.
@AllArgsConstructor // Lombok annotation to generate a constructor with all arguments.
@NoArgsConstructor // Lombok annotation to generate a no-argument constructor.
@Entity // Marks this class as a JPA entity (mapped to a database table).
@Table(name="recruiter_profile") // Specifies the table name in the database.
public class RecruiterProfile {

    @Id // Specifies the primary key of this entity.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userAccountId;

    @OneToOne // Indicates a one-to-one relationship with Users entity.
    @JoinColumn(name = "user_account_id") // Defines the foreign key column in the recruiter_profile table that refers to Users.
    @MapsId // Specifies that the primary key of RecruiterProfile is mapped to the primary key of the associated Users entity.
    private Users userId;

    private String firstName;
    private String lastName;
    private String city;
    private String county;
    private String country;
    private String company;

    @Column(nullable = true, length = 64) // Specifies that this column can be null and has a maximum length of 64 characters.
    private String profilePhoto;

    /**
     * Generates the path to the recruiter's profile photo based on the user's account ID.
     *
     * @return the path to the profile photo, or null if no photo is available.
     */
    @Transient // Indicates that this field is not to be persisted to the database.

    public String getPhotosImagePath(){
        if(profilePhoto == null){
            return null;
        }else {
            return "/photos/recruiter/" + userAccountId + "/" + profilePhoto;
        }
    }

    public RecruiterProfile(Users users){
        this.userId = users;
    }



}
