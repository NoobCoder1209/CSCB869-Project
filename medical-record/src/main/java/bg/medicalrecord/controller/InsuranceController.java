package bg.medicalrecord.controller;

import bg.medicalrecord.model.InsurancePayment;
import bg.medicalrecord.model.Patient;
import bg.medicalrecord.service.InsuranceService;
import bg.medicalrecord.service.PatientService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.YearMonth;
import java.util.List;

// Контролер за управление на здравноосигурителни вноски (само за администратор)
@Controller
@RequestMapping("/insurance")
@PreAuthorize("hasRole('ADMIN')")
public class InsuranceController {

    private final InsuranceService insuranceService;
    private final PatientService patientService;

    public InsuranceController(InsuranceService insuranceService, PatientService patientService) {
        this.insuranceService = insuranceService;
        this.patientService = patientService;
    }

    // Списък с вноски по пациент
    @GetMapping
    public String list(@RequestParam(required = false) Long patientId, Model model) {
        model.addAttribute("patients", patientService.findAll());
        if (patientId != null) {
            Patient patient = patientService.findById(patientId);
            List<InsurancePayment> payments = insuranceService.findByPatient(patientId);
            model.addAttribute("selectedPatient", patient);
            model.addAttribute("payments", payments);
            model.addAttribute("selectedPatientId", patientId);
        }
        return "insurance/list";
    }

    // Добавяне на вноска
    @PostMapping("/add")
    public String addPayment(@RequestParam Long patientId,
                             @RequestParam String month,
                             RedirectAttributes redirectAttributes) {
        try {
            Patient patient = patientService.findById(patientId);
            YearMonth yearMonth = YearMonth.parse(month);
            insuranceService.addPayment(patient, yearMonth);
            redirectAttributes.addFlashAttribute("successMessage", "Вноската беше успешно добавена.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/insurance?patientId=" + patientId;
    }

    // Изтриване на вноска
    @PostMapping("/{id}/delete")
    public String deletePayment(@PathVariable Long id,
                                @RequestParam Long patientId,
                                RedirectAttributes redirectAttributes) {
        try {
            insuranceService.deletePayment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Вноската беше успешно изтрита.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/insurance?patientId=" + patientId;
    }
}
