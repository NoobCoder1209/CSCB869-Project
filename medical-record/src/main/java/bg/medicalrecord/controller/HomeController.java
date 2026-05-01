package bg.medicalrecord.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Контролер за начална страница и управление на вход/изход
@Controller
public class HomeController {

    // Пренасочва към дашборда според ролята на потребителя
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin";
        }
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DOCTOR"))) {
            return "redirect:/doctor";
        }
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PATIENT"))) {
            return "redirect:/patient";
        }
        return "redirect:/login";
    }

    // Страница за вход
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Страница за отказан достъп
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}
