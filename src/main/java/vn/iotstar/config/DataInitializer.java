package vn.iotstar.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            log.info("Khởi tạo dữ liệu mẫu cho hệ thống...");

            // 1. Roles
            Role adminRole = roleRepository.findByNameIgnoreCase("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));
            Role userRole = roleRepository.findByNameIgnoreCase("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

            // 2. Admin User
            User admin = userRepository.findByUsername("admin")
                    .map(existingAdmin -> {
                        existingAdmin.setFullName("Nguyễn Đình Lĩnh (Admin)");
                        return userRepository.save(existingAdmin);
                    })
                    .orElseGet(() -> {
                        User newAdmin = User.builder()
                                .username("admin")
                                .email("admin@gmail.com")
                                .fullName("Nguyễn Đình Lĩnh (Admin)")
                                .password(passwordEncoder.encode("123456"))
                                .images("/images/avatar-default.png")
                                .role(adminRole)
                                .enabled(true)
                                .createdAt(LocalDateTime.now())
                                .build();
                        return userRepository.save(newAdmin);
                    });

            // 3. Normal User
            User normalUser = userRepository.findByUsername("user01")
                    .orElseGet(() -> {
                        User newUser = User.builder()
                                .username("user01")
                                .email("user01@gmail.com")
                                .fullName("Nguyễn Văn A (User)")
                                .password(passwordEncoder.encode("123456"))
                                .images("/images/avatar-default.png")
                                .role(userRole)
                                .enabled(true)
                                .createdAt(LocalDateTime.now())
                                .build();
                        return userRepository.save(newUser);
                    });

            // 4. Sample Products
            if (productRepository.count() == 0) {
                productRepository.save(Product.builder()
                        .name("Laptop Dell XPS 15")
                        .brand("Dell")
                        .madein("Mỹ")
                        .price(new BigDecimal("35000000.00"))
                        .images("/images/laptop.png")
                        .createdAt(LocalDateTime.now())
                        .user(admin)
                        .build());

                productRepository.save(Product.builder()
                        .name("iPhone 15 Pro Max")
                        .brand("Apple")
                        .madein("Mỹ")
                        .price(new BigDecimal("32000000.00"))
                        .images("/images/phone.png")
                        .createdAt(LocalDateTime.now())
                        .user(admin)
                        .build());

                productRepository.save(Product.builder()
                        .name("Samsung Galaxy S24 Ultra")
                        .brand("Samsung")
                        .madein("Hàn Quốc")
                        .price(new BigDecimal("29000000.00"))
                        .images("/images/phone.png")
                        .createdAt(LocalDateTime.now())
                        .user(normalUser)
                        .build());

                productRepository.save(Product.builder()
                        .name("Bàn phím cơ Logitech MX Keys")
                        .brand("Logitech")
                        .madein("Thụy Sĩ")
                        .price(new BigDecimal("2800000.00"))
                        .images("/images/keyboard.png")
                        .createdAt(LocalDateTime.now())
                        .user(normalUser)
                        .build());
            }

            log.info("Khởi tạo dữ liệu mẫu hoàn tất!");
            log.info("--> Tài khoản Admin: admin / 123456 hoặc admin@gmail.com / 123456");
            log.info("--> Tài khoản User:  user01 / 123456 hoặc user01@gmail.com / 123456");
        };
    }
}
