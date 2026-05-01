package bg.medicalrecord.controller;

import bg.medicalrecord.dto.DoctorDto;
import bg.medicalrecord.model.Doctor;
import bg.medicalrecord.model.enums.Specialty;
import bg.medicalrecord.service.DoctorService;
import bg.medicalrecord.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Контролер за управление на лекари
@Controller
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final UserService userService;

    public DoctorController(DoctorService doctorService, UserService userService) {
        this.doctorService = doctorService;
        this.userService = userService;
    }

    // Списък на всички лекари (администратор и лекар)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String list(Model model) {
        model.addAttribute("doctors", doctorService.findAll());
        return "doctors/list";
    }

    // Преглед на единичен лекар (администратор, лекар, пациент)
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("doctor", doctorService.findById(id));
        return "doctors/view";
    }

    // Форма за нов лекар (само за администратор)
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newForm(Model model) {
        model.addAttribute("doctorDto", new DoctorDto());
        model.addAttribute("specialties", Specialty.values());
        model.addAttribute("users", userService.findAll());
        return "doctors/form";
    }

    // Създаване на нов лекар (само за администратор)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@Valid @ModelAttribute("doctorDto") DoctorDto doctorDto,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("specialties", Specialty.values());
            model.addAttribute("users", userService.findAll());
            return "doctors/form";
        }
        try {
            Doctor doctor = doctorService.create(doctorDto);
            redirectAttributes.addFlashAttribute("successMessage", "Лекарят беше успешно създаден.");
            return "redirect:/doctors/" + doctor.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("specialties", Specialty.values());
            model.addAttribute("users", userService.findAll());
            return "doctors/form";
        }
    }

    // Форма за редактиране на лекар (само за администратор)
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        Doctor doctor = doctorService.findById(id);
        DoctorDto dto = new DoctorDto();
        dto.setUin(doctor.getUin());
        dto.setFullName(doctor.getFullName());
        dto.setSpecialty(doctor.getSpecialty());
        dto.setGp(doctor.isGp());
        if (doctor.getUser() != null) {
            dto.setUserId(doctor.getUser().getId());
        }
        model.addAttribute("doctorDto", dto);
        model.addAttribute("doctorId", id);
        model.addAttribute("specialties", Specialty.values());
        model.addAttribute("users", userService.findAll());
        return "doctors/form";
    }

    // Обновяване на лекар (само за администратор)
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("doctorDto") DoctorDto doctorDto,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("doctorId", id);
            model.addAttribute("specialties", Specialty.values());
            model.addAttribute("users", userService.findAll());
            return "doctors/form";
        }
        try {
            doctorService.update(id, doctorDto);
            redirectAttributes.addFlashAttribute("successMessage", "Данните на лекаря бяха успешно обновени.");
            return "redirect:/doctors/" + id;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("doctorId", id);
            model.addAttribute("specialties", Specialty.values());
            model.addAttribute("users", userService.findAll());
            return "doctors/form";
        }
    }

    // Изтриване на лекар (само за администратор)
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            doctorService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Лекарят беше успешно изтрит.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/doctors";
    }
}
