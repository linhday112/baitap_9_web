package vn.iotstar.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vn.iotstar.security.CustomUserDetails;
import vn.iotstar.service.ProductService;
import vn.iotstar.service.UserService;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final ProductService productService;

    @GetMapping("/")
    public String homePage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        long totalUsers = userService.countTotalUsers();
        long totalProducts = productService.countTotalProducts();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalProducts", totalProducts);

        if (userDetails != null) {
            long userProductsCount = productService.countProductsByUserId(userDetails.getId());
            model.addAttribute("userProductsCount", userProductsCount);
        }

        return "home";
    }
}
