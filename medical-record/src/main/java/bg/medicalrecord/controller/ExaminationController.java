package bg.medicalrecord.controller;

import bg.medicalrecord.dto.ExaminationDto;
import bg.medicalrecord.model.Examination;
import bg.medicalrecord.model.Patient;
import bg.medicalrecord.model.User;
import bg.medicalrecord.model.enums.Role;
import bg.medicalrecord.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// Контролер за управление на прегледи
@Controller
@RequestMapping("/examinations")
public class ExaminationController {

    private final ExaminationService examinationService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final DiagnosisService diagnosisService;
    private final UserService userService;

    public ExaminationController(ExaminationService examinationService,
                                 DoctorService doctorService,
                                 PatientService patientService,
                                 DiagnosisService diagnosisService,
                                 UserService userService) {
        this.examinationService = examinationService;
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.diagnosisService = diagnosisService;
        this.userService = userService;
    }

    // Списък прегледи - администраторът и лекарят виждат всички, пациентът само своите
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String list(Model model) {
        User currentUser = userService.getCurrentUser();
        List<Examination> examinations;

        if (currentUser.getRole() == Role.ROLE_PATIENT) {
            Patient patient = patientService.findByCurrentUser();
            examinations = examinationService.findByPatient(patient.getId());
        } else {
            examinations = examinationService.findAll();
        }

        model.addAttribute("examinations", examinations);
        return "examinations/list";
    }

    // Преглед на единичен запис
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String view(@PathVariable Long id, Model model) {
        Examination examination = examinationService.findById(id);
        User currentUser = userService.getCurrentUser();

        // Пациент може да вижда само свои прегледи
        if (currentUser.getRole() == Role.ROLE_PATIENT) {
            Patient ownProfile = patientService.findByCurrentUser();
            if (!examination.getPatient().getId().equals(ownProfile.getId())) {
                throw new bg.medicalrecord.exception.AccessForbiddenException(
                        "Нямате право да виждате този преглед");
            }
        }

        model.addAttribute("examination", examination);
        return "examinations/view";
    }

    // Форма за нов преглед (администратор и лекар)
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String newForm(Model model) {
        ExaminationDto dto = new ExaminationDto();

        // При лекар — предварително попълваме собствения лекар
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() == Role.ROLE_DOCTOR) {
            dto.setDoctorId(doctorService.findByCurrentUser().getId());
        }

        model.addAttribute("examinationDto", dto);
        populateFormModel(model);
        return "examinations/form";
    }

    // Създаване на нов преглед (администратор и лекар)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String create(@Valid @ModelAttribute("examinationDto") ExaminationDto examinationDto,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateFormModel(model);
            return "examinations/form";
        }
        try {
            Examination examination = examinationService.create(examinationDto);
            redirectAttributes.addFlashAttribute("successMessage", "Прегледът беше успешно записан.");
            return "redirect:/examinations/" + examination.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            populateFormModel(model);
            return "examinations/form";
        }
    }

    // Форма за редактиране (администратор или лекарят-собственик)
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String editForm(@PathVariable Long id, Model model) {
        Examination examination = examinationService.findById(id);

        ExaminationDto dto = new ExaminationDto();
        dto.setExaminationDate(examination.getExaminationDate());
        dto.setDoctorId(examination.getDoctor().getId());
        dto.setPatientId(examination.getPatient().getId());
        dto.setDiagnosisId(examination.getDiagnosis().getId());
        dto.setTreatment(examination.getTreatment());
        dto.setPrice(examination.getPrice());

        model.addAttribute("examinationDto", dto);
        model.addAttribute("examinationId", id);
        populateFormModel(model);
        return "examinations/form";
    }

    // Обновяване на преглед (администратор или лекарят-собственик)
    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("examinationDto") ExaminationDto examinationDto,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("examinationId", id);
            populateFormModel(model);
            return "examinations/form";
        }
        try {
            examinationService.update(id, examinationDto);
            redirectAttributes.addFlashAttribute("successMessage", "Прегледът беше успешно обновен.");
            return "redirect:/examinations/" + id;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("examinationId", id);
            populateFormModel(model);
            return "examinations/form";
        }
    }

    // Изтриване на преглед (само за администратор)
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            examinationService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Прегледът беше успешно изтрит.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/examinations";
    }

    // Зарежда списъците за падащите менюта във формата
    private void populateFormModel(Model model) {
        model.addAttribute("doctors", doctorService.findAll());
        model.addAttribute("patients", patientService.findAll());
        model.addAttribute("diagnoses", diagnosisService.findAll());
    }
}
