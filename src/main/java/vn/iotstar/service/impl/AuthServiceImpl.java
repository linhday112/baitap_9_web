package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.dto.RegisterDTO;
import vn.iotstar.dto.ResetPasswordDTO;
import vn.iotstar.entity.OtpToken;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.repository.OtpTokenRepository;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.service.AuthService;
import vn.iotstar.service.FileUploadService;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileUploadService fileUploadService;
    private final JavaMailSender mailSender;

    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) throws IOException {
        if (userRepository.existsByUsername(registerDTO.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại!");
        }
        if (userRepository.existsByEmailIgnoreCase(registerDTO.getEmail())) {
            throw new IllegalArgumentException("Email đã được đăng ký!");
        }

        Role userRole = roleRepository.findByNameIgnoreCase("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        String imagePath = null;
        if (registerDTO.getImageFile() != null && !registerDTO.getImageFile().isEmpty()) {
            imagePath = fileUploadService.saveFile(registerDTO.getImageFile());
        }

        User user = User.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .fullName(registerDTO.getFullName())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .images(imagePath)
                .role(userRole)
                .enabled(false) // Required OTP verification
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        // Generate OTP
        String otpCode = String.format("%06d", (int) (Math.random() * 1000000));
        OtpToken otpToken = OtpToken.builder()
                .email(user.getEmail())
                .token(otpCode)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .type(OtpToken.OtpType.REGISTER)
                .build();
        otpTokenRepository.save(otpToken);

        sendOtpMail(user.getEmail(), "Mã OTP xác thực tài khoản đăng ký", "Mã OTP của bạn là: " + otpCode);
    }

    @Override
    @Transactional
    public boolean verifyRegisterOtp(String email, String otp) {
        OtpToken token = otpTokenRepository.findByTokenAndEmailAndTypeAndUsedFalse(otp, email, OtpToken.OtpType.REGISTER)
                .orElse(null);

        if (token == null || token.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        token.setUsed(true);
        otpTokenRepository.save(token);

        User user = userRepository.findByEmailIgnoreCase(email).orElse(null);
        if (user != null) {
            user.setEnabled(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void sendForgotPasswordOtp(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản với email này!"));

        String otpCode = String.format("%06d", (int) (Math.random() * 1000000));
        OtpToken otpToken = OtpToken.builder()
                .email(user.getEmail())
                .token(otpCode)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .type(OtpToken.OtpType.FORGOT_PASSWORD)
                .build();
        otpTokenRepository.save(otpToken);

        sendOtpMail(user.getEmail(), "Mã OTP khôi phục mật khẩu", "Mã OTP đặt lại mật khẩu của bạn là: " + otpCode);
    }

    @Override
    @Transactional
    public boolean resetPassword(ResetPasswordDTO resetPasswordDTO) {
        OtpToken token = otpTokenRepository.findByTokenAndEmailAndTypeAndUsedFalse(
                        resetPasswordDTO.getOtp(), resetPasswordDTO.getEmail(), OtpToken.OtpType.FORGOT_PASSWORD)
                .orElse(null);

        if (token == null || token.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        token.setUsed(true);
        otpTokenRepository.save(token);

        User user = userRepository.findByEmailIgnoreCase(resetPasswordDTO.getEmail()).orElse(null);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    private void sendOtpMail(String toEmail, String subject, String content) {
        log.info("[OTP MAIL SIMULATION] To: {} | Subject: {} | Body: {}", toEmail, subject, content);
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(toEmail);
            mailMessage.setSubject(subject);
            mailMessage.setText(content);
            mailSender.send(mailMessage);
        } catch (Exception e) {
            log.warn("Lưu ý: Không gửi được mail qua SMTP server thực tế (chế độ Dev), OTP được ghi log thành công: {}", content);
        }
    }
}
