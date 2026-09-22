package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.iotstar.dto.UserDTO;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.mapper.UserMapper;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.service.FileUploadService;
import vn.iotstar.service.UserService;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProductRepository productRepository;
    private final UserMapper userMapper;
    private final FileUploadService fileUploadService;

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(String keyword, Pageable pageable) {
        Page<User> users;
        if (keyword != null && !keyword.trim().isEmpty()) {
            users = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
                    keyword.trim(), keyword.trim(), keyword.trim(), pageable);
        } else {
            users = userRepository.findAll(pageable);
        }

        return users.map(user -> {
            UserDTO dto = userMapper.toDto(user);
            dto.setProductCount(productRepository.countByUserId(user.getId()));
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id));
        UserDTO dto = userMapper.toDto(user);
        dto.setProductCount(productRepository.countByUserId(user.getId()));
        return dto;
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO, MultipartFile imageFile) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng với ID: " + id));

        user.setFullName(userDTO.getFullName());
        user.setEnabled(userDTO.isEnabled());

        if (userDTO.getRoleId() != null) {
            Role role = roleRepository.findById(userDTO.getRoleId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Role!"));
            user.setRole(role);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = fileUploadService.saveFile(imageFile);
            user.setImages(imagePath);
        }

        User updatedUser = userRepository.save(user);
        UserDTO dto = userMapper.toDto(updatedUser);
        dto.setProductCount(productRepository.countByUserId(updatedUser.getId()));
        return dto;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalUsers() {
        return userRepository.count();
    }
}
