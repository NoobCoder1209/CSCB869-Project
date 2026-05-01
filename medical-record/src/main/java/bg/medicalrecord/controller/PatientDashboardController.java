package bg.medicalrecord.controller;

import bg.medicalrecord.model.Examination;
import bg.medicalrecord.model.Patient;
import bg.medicalrecord.service.ExaminationService;
import bg.medicalrecord.service.InsuranceService;
import bg.medicalrecord.service.PatientService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

// Контролер за дашборда на пациента
@Controller
@RequestMapping("/patient")
@PreAuthorize("hasRole('PATIENT')")
public class PatientDashboardController {

    private final PatientService patientService;
    private final ExaminationService examinationService;
    private final InsuranceService insuranceService;

    public PatientDashboardController(PatientService patientService,
                                      ExaminationService examinationService,
                                      InsuranceService insuranceService) {
        this.patientService = patientService;
        this.examinationService = examinationService;
        this.insuranceService = insuranceService;
    }

    // Дашборд с информация за пациента, последните прегледи и осигурителен статус
    @GetMapping
    public String dashboard(Model model) {
        Patient patient = patientService.findByCurrentUser();
        List<Examination> allExaminations = examinationService.findByPatient(patient.getId());

        // Показваме последните 5 прегледа
        List<Examination> recentExaminations = allExaminations.stream()
                .limit(5)
                .toList();

        // Текущ осигурителен статус към днешна дата
        boolean isInsured = insuranceService.isInsuredAt(patient.getId(), LocalDate.now());

        model.addAttribute("patient", patient);
        model.addAttribute("recentExaminations", recentExaminations);
        model.addAttribute("totalExaminations", allExaminations.size());
        model.addAttribute("isInsured", isInsured);
        return "patient/dashboard";
    }

    // Всички прегледи на пациента
    @GetMapping("/examinations")
    public String examinations(Model model) {
        Patient patient = patientService.findByCurrentUser();
        List<Examination> examinations = examinationService.findByPatient(patient.getId());
        model.addAttribute("examinations", examinations);
        return "patient/examinations";
    }
}
