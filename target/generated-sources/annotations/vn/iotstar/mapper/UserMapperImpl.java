package vn.iotstar.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import vn.iotstar.dto.UserDTO;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-23T17:23:40+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 22 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDTO(User entity) {
        if ( entity == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setRoleName( entityRoleName( entity ) );
        userDTO.setId( entity.getId() );
        userDTO.setUsername( entity.getUsername() );
        userDTO.setEmail( entity.getEmail() );
        userDTO.setFullName( entity.getFullName() );
        userDTO.setEnabled( entity.isEnabled() );

        return userDTO;
    }

    @Override
    public User toEntity(UserDTO dto) {
        if ( dto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( dto.getId() );
        user.username( dto.getUsername() );
        user.email( dto.getEmail() );
        user.fullName( dto.getFullName() );
        user.enabled( dto.isEnabled() );

        return user.build();
    }

    private String entityRoleName(User user) {
        Role role = user.getRole();
        if ( role == null ) {
            return null;
        }
        return role.getName();
    }
}
