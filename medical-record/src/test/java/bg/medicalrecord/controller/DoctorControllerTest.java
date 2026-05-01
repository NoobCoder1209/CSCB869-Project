package bg.medicalrecord.controller;

import bg.medicalrecord.config.SecurityConfig;
import bg.medicalrecord.model.Doctor;
import bg.medicalrecord.model.enums.Specialty;
import bg.medicalrecord.service.DoctorService;
import bg.medicalrecord.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Интеграционни тестове за DoctorController - проверка на достъп по роли
@WebMvcTest(DoctorController.class)
@Import(SecurityConfig.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService doctorService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsService userDetailsService;

    // === Тестове за достъп на администратор ===

    @Test
    @DisplayName("GET /doctors - администратор вижда списъка с лекари")
    @WithMockUser(roles = "ADMIN")
    void list_adminRole_returnsOk() throws Exception {
        Doctor doctor = new Doctor("123456", "Д-р Иванова", Specialty.CARDIOLOGY, false);
        doctor.setId(1L);
        when(doctorService.findAll()).thenReturn(Arrays.asList(doctor));

        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctors/list"))
                .andExpect(model().attributeExists("doctors"));
    }

    @Test
    @DisplayName("GET /doctors/{id} - администратор вижда данни за лекар")
    @WithMockUser(roles = "ADMIN")
    void view_adminRole_returnsOk() throws Exception {
        Doctor doctor = new Doctor("123456", "Д-р Иванова", Specialty.CARDIOLOGY, false);
        doctor.setId(1L);
        when(doctorService.findById(1L)).thenReturn(doctor);

        mockMvc.perform(get("/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctors/view"))
                .andExpect(model().attributeExists("doctor"));
    }

    @Test
    @DisplayName("GET /doctors/new - администратор вижда формата за нов лекар")
    @WithMockUser(roles = "ADMIN")
    void newForm_adminRole_returnsOk() throws Exception {
        mockMvc.perform(get("/doctors/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctors/form"))
                .andExpect(model().attributeExists("doctorDto"))
                .andExpect(model().attributeExists("specialties"));
    }

    @Test
    @DisplayName("POST /doctors/{id}/delete - администратор може да изтрие лекар")
    @WithMockUser(roles = "ADMIN")
    void delete_adminRole_redirectsToList() throws Exception {
        mockMvc.perform(post("/doctors/1/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/doctors"));
    }

    // === Тестове за ограничен достъп на пациент ===

    @Test
    @DisplayName("GET /doctors - пациент няма достъп до списъка")
    @WithMockUser(roles = "PATIENT")
    void list_patientRole_forbidden() throws Exception {
        mockMvc.perform(get("/doctors"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /doctors/new - пациент няма достъп до формата за създаване")
    @WithMockUser(roles = "PATIENT")
    void newForm_patientRole_forbidden() throws Exception {
        mockMvc.perform(get("/doctors/new"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /doctors/1/delete - пациент не може да изтрие лекар")
    @WithMockUser(roles = "PATIENT")
    void delete_patientRole_forbidden() throws Exception {
        mockMvc.perform(post("/doctors/1/delete").with(csrf()))
                .andExpect(status().isForbidden());
    }

    // === Тестове за достъп на лекар ===

    @Test
    @DisplayName("GET /doctors/{id} - лекар може да види профил на колега")
    @WithMockUser(roles = "DOCTOR")
    void view_doctorRole_returnsOk() throws Exception {
        Doctor doctor = new Doctor("123456", "Д-р Петров", Specialty.NEUROLOGY, false);
        doctor.setId(2L);
        when(doctorService.findById(2L)).thenReturn(doctor);

        mockMvc.perform(get("/doctors/2"))
                .andExpect(status().isOk())
                .andExpect(view().name("doctors/view"));
    }

    @Test
    @DisplayName("GET /doctors - лекар има достъп до списъка с лекари")
    @WithMockUser(roles = "DOCTOR")
    void list_doctorRole_allowed() throws Exception {
        when(doctorService.findAll()).thenReturn(java.util.Collections.emptyList());
        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk());
    }

    // === Тест за неавтентикиран потребител ===

    @Test
    @DisplayName("GET /doctors - неавтентикиран потребител се пренасочва към login")
    void list_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/doctors"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
