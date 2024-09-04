package com.itschool.job_seeker.services;


import com.itschool.job_seeker.model.UsersTypeDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UsersTypeService {


    /**
     * Get all user types
     *
     * @return all user types
     */
    List<UsersTypeDTO> getAll();

}
