package com.itschool.job_seeker.repository;

import com.itschool.job_seeker.entity.UsersType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersTypeRepository extends JpaRepository<UsersType, Long> {
}
