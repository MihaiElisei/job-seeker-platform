package com.itschool.job_seeker.services.impl;

import com.itschool.job_seeker.entity.UsersType;
import com.itschool.job_seeker.model.UsersTypeDTO;
import com.itschool.job_seeker.repository.UsersTypeRepository;
import com.itschool.job_seeker.services.UsersTypeService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsersTypeServiceImpl implements UsersTypeService {

    private final UsersTypeRepository usersTypeRepository;
    private final ModelMapper modelMapper;

    public UsersTypeServiceImpl(UsersTypeRepository usersTypeRepository, ModelMapper modelMapper) {
        this.usersTypeRepository = usersTypeRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Get All User Types
     *
     * @return a list with all types
     */
    @Override
    public List<UsersTypeDTO> getAll() {

        List<UsersType> usersTypes = usersTypeRepository.findAll();

        return usersTypes.stream()
                .map(this::mapToUsersTypeDTO)
                .toList();
    }


    //Convert entity into DTO
    private UsersTypeDTO mapToUsersTypeDTO(UsersType usersType) {
        return modelMapper.map(usersType, UsersTypeDTO.class);
    }

    //Convert DTO into entity
    private UsersType mapToUsersType(UsersTypeDTO usersTypeDTO) {
        return modelMapper.map(usersTypeDTO, UsersType.class);
    }


}
