package bg.medicalrecord.controller;

import bg.medicalrecord.model.Doctor;
import bg.medicalrecord.model.Examination;
import bg.medicalrecord.model.SickLeave;
import bg.medicalrecord.service.DoctorService;
import bg.medicalrecord.service.ExaminationService;
import bg.medicalrecord.service.PatientService;
import bg.medicalrecord.service.SickLeaveService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

// Контролер за дашборда на лекаря
@Controller
@RequestMapping("/doctor")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorDashboardController {

    private final DoctorService doctorService;
    private final ExaminationService examinationService;
    private final PatientService patientService;
    private final SickLeaveService sickLeaveService;

    public DoctorDashboardController(DoctorService doctorService,
                                     ExaminationService examinationService,
                                     PatientService patientService,
                                     SickLeaveService sickLeaveService) {
        this.doctorService = doctorService;
        this.examinationService = examinationService;
        this.patientService = patientService;
        this.sickLeaveService = sickLeaveService;
    }

    // Дашборд с информация за лекаря и последните му прегледи
    @GetMapping
    public String dashboard(Model model) {
        Doctor doctor = doctorService.findByCurrentUser();
        List<Examination> allExaminations = examinationService.findByDoctor(doctor.getId());

        // Последните 10 прегледа
        List<Examination> latestExaminations = allExaminations.stream()
                .limit(10)
                .toList();

        // Брой пациенти при този личен лекар
        long totalPatients = doctor.isGp()
                ? patientService.findAll().stream()
                    .filter(p -> p.getPersonalDoctor() != null && p.getPersonalDoctor().getId().equals(doctor.getId()))
                    .count()
                : 0;

        // Брой болнични от прегледите на лекаря
        long totalSickLeaves = allExaminations.stream()
                .map(e -> sickLeaveService.findByExaminationId(e.getId()))
                .filter(sl -> sl != null)
                .count();

        model.addAttribute("doctor", doctor);
        model.addAttribute("recentExaminations", latestExaminations);
        model.addAttribute("totalExaminations", allExaminations.size());
        model.addAttribute("totalPatients", totalPatients);
        model.addAttribute("totalSickLeaves", totalSickLeaves);
        return "doctor/dashboard";
    }
}
