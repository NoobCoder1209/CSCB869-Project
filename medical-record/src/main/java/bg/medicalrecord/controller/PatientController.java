package bg.medicalrecord.controller;

import bg.medicalrecord.dto.PatientDto;
import bg.medicalrecord.exception.AccessForbiddenException;
import bg.medicalrecord.model.Patient;
import bg.medicalrecord.model.User;
import bg.medicalrecord.model.enums.Role;
import bg.medicalrecord.service.DoctorService;
import bg.medicalrecord.service.PatientService;
import bg.medicalrecord.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Контролер за управление на пациенти
@Controller
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final UserService userService;

    public PatientController(PatientService patientService,
                             DoctorService doctorService,
                             UserService userService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.userService = userService;
    }

    // Списък на всички пациенти (администратор и лекар)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String list(Model model) {
        model.addAttribute("patients", patientService.findAll());
        return "patients/list";
    }

    // Преглед на единичен пациент (администратор, лекар или самият пациент)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String view(@PathVariable Long id, Model model) {
        User currentUser = userService.getCurrentUser();
        Patient patient = patientService.findById(id);

        // Пациент може да вижда само собствения си профил
        if (currentUser.getRole() == Role.ROLE_PATIENT) {
            Patient ownProfile = patientService.findByCurrentUser();
            if (!ownProfile.getId().equals(id)) {
                throw new AccessForbiddenException("Нямате право да виждате данните на друг пациент");
            }
        }

        model.addAttribute("patient", patient);
        return "patients/view";
    }

    // Форма за нов пациент (само за администратор)
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newForm(Model model) {
        model.addAttribute("patientDto", new PatientDto());
        model.addAttribute("doctors", doctorService.findAllGps());
        model.addAttribute("users", userService.findAll());
        return "patients/form";
    }

    // Създаване на нов пациент (само за администратор)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@Valid @ModelAttribute("patientDto") PatientDto patientDto,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("doctors", doctorService.findAllGps());
            model.addAttribute("users", userService.findAll());
            return "patients/form";
        }
        try {
            Patient patient = patientService.create(patientDto);
            redirectAttributes.addFlashAttribute("successMessage", "Пациентът беше успешно създаден.");
            return "redirect:/patients/" + patient.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("doctors", doctorService.findAllGps());
            model.addAttribute("users", userService.findAll());
            return "patients/form";
        }
    }

    // Форма за редактиране на пациент (само за администратор)
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        Patient patient = patientService.findById(id);
        PatientDto dto = new PatientDto();
        dto.setFullName(patient.getFullName());
        dto.setEgn(patient.getEgn());
        if (patient.getPersonalDoctor() != null) {
            dto.setPersonalDoctorId(patient.getPersonalDoctor().getId());
        }
        if (patient.getUser() != null) {
            dto.setUserId(patient.getUser().getId());
        }
        model.addAttribute("patientDto", dto);
        model.addAttribute("patientId", id);
        model.addAttribute("doctors", doctorService.findAllGps());
        model.addAttribute("users", userService.findAll());
        return "patients/form";
    }

    // Обновяване на пациент (само за администратор)
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("patientDto") PatientDto patientDto,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("patientId", id);
            model.addAttribute("doctors", doctorService.findAllGps());
            model.addAttribute("users", userService.findAll());
            return "patients/form";
        }
        try {
            patientService.update(id, patientDto);
            redirectAttributes.addFlashAttribute("successMessage", "Данните на пациента бяха успешно обновени.");
            return "redirect:/patients/" + id;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("patientId", id);
            model.addAttribute("doctors", doctorService.findAllGps());
            model.addAttribute("users", userService.findAll());
            return "patients/form";
        }
    }

    // Изтриване на пациент (само за администратор)
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            patientService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Пациентът беше успешно изтрит.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/patients";
    }
}
