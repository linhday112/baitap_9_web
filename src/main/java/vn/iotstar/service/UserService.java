package vn.iotstar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.iotstar.dto.UserDTO;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    Page<UserDTO> findAll(String keyword, Pageable pageable);

    UserDTO findById(Long id);

    UserDTO updateUser(Long id, UserDTO userDTO, MultipartFile imageFile) throws IOException;

    void deleteUser(Long id);

    long countTotalUsers();
}
