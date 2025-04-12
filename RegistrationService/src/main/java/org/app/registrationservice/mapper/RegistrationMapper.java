package org.app.registrationservice.mapper;

import org.app.registrationservice.dto.RegistrationDTO;
import org.app.registrationservice.entity.Registration;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;

@MapperConfig
@Mapper(componentModel = "spring")
public interface RegistrationMapper {
    RegistrationDTO toDto(Registration registration);
    Registration toEntity(RegistrationDTO dto);
}
