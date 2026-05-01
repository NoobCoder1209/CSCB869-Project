package bg.medicalrecord.controller;

import bg.medicalrecord.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Интеграционни тестове за HomeController - проверка на маршрутизация и автентикация
@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("GET /login - връща страницата за вход с код 200")
    void login_returnsOk() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @DisplayName("GET / - пренасочва неавтентикиран потребител към /login")
    void home_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("GET / - пренасочва администратор към /admin")
    @WithMockUser(roles = "ADMIN")
    void home_adminUser_redirectsToAdmin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    @DisplayName("GET / - пренасочва лекар към /doctor")
    @WithMockUser(roles = "DOCTOR")
    void home_doctorUser_redirectsToDoctor() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/doctor"));
    }

    @Test
    @DisplayName("GET / - пренасочва пациент към /patient")
    @WithMockUser(roles = "PATIENT")
    void home_patientUser_redirectsToPatient() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patient"));
    }

    @Test
    @DisplayName("GET /access-denied - връща страницата за отказан достъп")
    @WithMockUser
    void accessDenied_returnsPage() throws Exception {
        mockMvc.perform(get("/access-denied"))
                .andExpect(status().isOk())
                .andExpect(view().name("access-denied"));
    }
}
