package bg.medicalrecord.controller;

import bg.medicalrecord.service.DiagnosisService;
import bg.medicalrecord.service.DoctorService;
import bg.medicalrecord.service.ExaminationService;
import bg.medicalrecord.service.PatientService;
import bg.medicalrecord.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// Контролер за администраторски дашборд
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final DoctorService doctorService;
    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    private final ExaminationService examinationService;
    private final UserService userService;

    public AdminController(DoctorService doctorService,
                           PatientService patientService,
                           DiagnosisService diagnosisService,
                           ExaminationService examinationService,
                           UserService userService) {
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.diagnosisService = diagnosisService;
        this.examinationService = examinationService;
        this.userService = userService;
    }

    // Административен дашборд с обобщени статистики
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("doctorCount", doctorService.findAll().size());
        model.addAttribute("patientCount", patientService.findAll().size());
        model.addAttribute("diagnosisCount", diagnosisService.findAll().size());
        model.addAttribute("examinationCount", examinationService.findAll().size());
        return "admin/dashboard";
    }

    // Списък с потребители
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }
}
