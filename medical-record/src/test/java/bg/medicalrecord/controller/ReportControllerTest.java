package bg.medicalrecord.controller;

import bg.medicalrecord.config.SecurityConfig;
import bg.medicalrecord.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Интеграционни тестове за ReportController - проверка на всички 11 справки
@WebMvcTest(ReportController.class)
@Import(SecurityConfig.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;

    @MockBean
    private DiagnosisService diagnosisService;

    @MockBean
    private DoctorService doctorService;

    @MockBean
    private PatientService patientService;

    @MockBean
    private UserDetailsService userDetailsService;

    // === Справка 1: Пациенти по диагноза ===

    @Test
    @DisplayName("GET /reports/patients-by-diagnosis - достъпна за администратор")
    @WithMockUser(roles = "ADMIN")
    void patientsByDiagnosis_adminRole_returnsOk() throws Exception {
        when(diagnosisService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reports/patients-by-diagnosis"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/patients-by-diagnosis"));
    }

    // === Справка 2: Най-честа диагноза ===

    @Test
    @DisplayName("GET /reports/most-common-diagnosis - достъпна за администратор")
    @WithMockUser(roles = "ADMIN")
    void mostCommonDiagnosis_adminRole_returnsOk() throws Exception {
        when(reportService.getMostCommonDiagnosis()).thenReturn(Optional.empty());

        mockMvc.perform(get("/reports/most-common-diagnosis"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/most-common-diagnosis"));
    }

    // === Справка 3: Пациенти по личен лекар ===

    @Test
    @DisplayName("GET /reports/patients-by-gp - достъпна за администратор")
    @WithMockUser(roles = "ADMIN")
    void patientsByGp_adminRole_returnsOk() throws Exception {
        when(doctorService.findAllGps()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reports/patients-by-gp"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/patients-by-gp"));
    }

    // === Справка 4: Обща стойност платена от пациенти ===

    @Test
    @DisplayName("GET /reports/patient-payments - достъпна за администратор")
    @WithMockUser(roles = "ADMIN")
    void patientPayments_adminRole_returnsOk() throws Exception {
        when(reportService.getTotalPaidByPatients()).thenReturn(BigDecimal.ZERO);

        mockMvc.perform(get("/reports/patient-payments"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/patient-payments"));
    }

    // === Справка 5: Плащания по лекар ===

    @Test
    @DisplayName("GET /reports/payments-by-doctor - достъпна за администратор")
    @WithMockUser(roles = "ADMIN")
    void paymentsByDoctor_adminRole_returnsOk() throws Exception {
        when(reportService.getPaymentsPerDoctor()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reports/payments-by-doctor"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/payments-by-doctor"));
    }

    // === Справка 6: Брой пациенти по ОПЛ ===

    @Test
    @DisplayName("GET /reports/patients-per-gp - достъпна за администратор")
    @WithMockUser(roles = "ADMIN")
    void patientsPerGp_adminRole_returnsOk() throws Exception {
        when(reportService.getPatientCountPerGp()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reports/patients-per-gp"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/patients-per-gp"));
    }

    // === Справка 7: Брой посещения по лекар ===

    @Test
    @DisplayName("GET /reports/visits-per-doctor - достъпна за администратор")
    @WithMockUser(roles = "ADMIN")
    void visitsPerDoctor_adminRole_returnsOk() throws Exception {
        when(reportService.getVisitCountPerDoctor()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reports/visits-per-doctor"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/visits-per-doctor"));
    }

    // === Справка 8: История на пациент ===

    @Test
    @DisplayName("GET /reports/patient-history - достъпна за администратор")
    @WithMockUser(roles = "ADMIN")
    void patientHistory_adminRole_returnsOk() throws Exception {
        when(patientService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reports/patient-history"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/patient-history"));
    }

    // === Справка 9: Прегледи по лекар и период ===

    @Test
    @DisplayName("GET /reports/examinations-filter - достъпна за администратор")
    @WithMockUser(roles = "ADMIN")
    void examinationsFilter_adminRole_returnsOk() throws Exception {
        when(doctorService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reports/examinations-filter"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/examinations-filter"));
    }

    // === Справка 10: Месец с най-много болнични ===

    @Test
    @DisplayName("GET /reports/month-most-sick-leaves - достъпна за администратор")
    @WithMockUser(roles = "ADMIN")
    void monthMostSickLeaves_adminRole_returnsOk() throws Exception {
        when(reportService.getMonthWithMostSickLeaves()).thenReturn(Optional.empty());

        mockMvc.perform(get("/reports/month-most-sick-leaves"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/month-most-sick-leaves"));
    }

    // === Справка 11: Лекар с най-много болнични ===

    @Test
    @DisplayName("GET /reports/doctor-most-sick-leaves - достъпна за администратор")
    @WithMockUser(roles = "ADMIN")
    void doctorMostSickLeaves_adminRole_returnsOk() throws Exception {
        when(reportService.getDoctorsWithMostSickLeaves()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reports/doctor-most-sick-leaves"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/doctor-most-sick-leaves"));
    }

    // === Индексна страница ===

    @Test
    @DisplayName("GET /reports - индексна страница достъпна за администратор")
    @WithMockUser(roles = "ADMIN")
    void index_adminRole_returnsOk() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/index"));
    }

    // === Тест за достъп от лекар (разрешено по @PreAuthorize) ===

    @Test
    @DisplayName("GET /reports - достъпна и за лекар")
    @WithMockUser(roles = "DOCTOR")
    void index_doctorRole_returnsOk() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().isOk())
                .andExpect(view().name("reports/index"));
    }

    // === Тест за отказан достъп на пациент ===

    @Test
    @DisplayName("GET /reports - пациент няма достъп до справките")
    @WithMockUser(roles = "PATIENT")
    void index_patientRole_forbidden() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().isForbidden());
    }

    // === Тест за неавтентикиран потребител ===

    @Test
    @DisplayName("GET /reports - неавтентикиран потребител се пренасочва")
    void index_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/reports"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}
