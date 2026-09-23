package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import vn.iotstar.service.EmailService;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtp(String email, String otp, String subject) {
        log.info("Sending OTP [{}] to email [{}]...", otp, email);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("linhdttv0106@gmail.com");
            message.setTo(email);
            message.setSubject(subject);
            message.setText("""
                Xin chào,

                Mã OTP của bạn là: %s

                OTP có hiệu lực trong 5 phút và chỉ sử dụng một lần.
                Không chia sẻ mã này cho người khác.
                """.formatted(otp));
            mailSender.send(message);
            log.info("OTP Mail sent successfully to {}", email);
        } catch (Exception e) {
            log.error("MailSendError: Không thể gửi mail tới {}: {}. Mã OTP để thử nghiệm là: [{}]", email, e.getMessage(), otp);
        }
    }
}
