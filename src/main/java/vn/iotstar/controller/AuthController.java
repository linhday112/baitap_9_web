package vn.iotstar.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.iotstar.dto.RegisterDTO;
import vn.iotstar.dto.ResetPasswordDTO;
import vn.iotstar.service.AuthService;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(
            @Valid @ModelAttribute("registerDTO") RegisterDTO registerDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            authService.register(registerDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công! Vui lòng kiểm tra OTP để xác thực tài khoản.");
            return "redirect:/verify-otp?email=" + registerDTO.getEmail();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/verify-otp")
    public String verifyOtpPage(@RequestParam(value = "email", required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String processVerifyOtp(
            @RequestParam("email") String email,
            @RequestParam("otp") String otp,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        boolean isVerified = authService.verifyRegisterOtp(email, otp);
        if (isVerified) {
            redirectAttributes.addFlashAttribute("successMessage", "Tài khoản của bạn đã được xác thực thành công! Hãy đăng nhập.");
            return "redirect:/login";
        } else {
            model.addAttribute("errorMessage", "Mã OTP không chính xác hoặc đã hết hạn!");
            model.addAttribute("email", email);
            return "auth/verify-otp";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            authService.sendForgotPasswordOtp(email);
            redirectAttributes.addFlashAttribute("successMessage", "Mã OTP khôi phục mật khẩu đã được gửi đến email của bạn.");
            return "redirect:/reset-password?email=" + email;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/forgot-password";
        }
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(value = "email", required = false) String email, Model model) {
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setEmail(email);
        model.addAttribute("resetPasswordDTO", dto);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(
            @Valid @ModelAttribute("resetPasswordDTO") ResetPasswordDTO resetPasswordDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "auth/reset-password";
        }

        boolean success = authService.resetPassword(resetPasswordDTO);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập bằng mật khẩu mới.");
            return "redirect:/login";
        } else {
            model.addAttribute("errorMessage", "Mã OTP không đúng hoặc đã hết hạn!");
            return "auth/reset-password";
        }
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "403";
    }
}
