package vn.iotstar.service;

import vn.iotstar.dto.RegisterDTO;
import vn.iotstar.dto.ResetPasswordDTO;

import java.io.IOException;

public interface AuthService {

    void register(RegisterDTO registerDTO) throws IOException;

    boolean verifyRegisterOtp(String email, String otp);

    void sendForgotPasswordOtp(String email);

    boolean resetPassword(ResetPasswordDTO resetPasswordDTO);
}
