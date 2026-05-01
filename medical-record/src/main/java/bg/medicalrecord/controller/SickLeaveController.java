package bg.medicalrecord.controller;

import bg.medicalrecord.dto.SickLeaveDto;
import bg.medicalrecord.exception.AccessForbiddenException;
import bg.medicalrecord.model.Doctor;
import bg.medicalrecord.model.Examination;
import bg.medicalrecord.model.Patient;
import bg.medicalrecord.model.SickLeave;
import bg.medicalrecord.model.User;
import bg.medicalrecord.model.enums.Role;
import bg.medicalrecord.service.DoctorService;
import bg.medicalrecord.service.ExaminationService;
import bg.medicalrecord.service.PatientService;
import bg.medicalrecord.service.SickLeaveService;
import bg.medicalrecord.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

// Контролер за управление на болнични листове
@Controller
@RequestMapping("/sick-leaves")
public class SickLeaveController {

    private final SickLeaveService sickLeaveService;
    private final ExaminationService examinationService;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final UserService userService;

    public SickLeaveController(SickLeaveService sickLeaveService,
                               ExaminationService examinationService,
                               PatientService patientService,
                               DoctorService doctorService,
                               UserService userService) {
        this.sickLeaveService = sickLeaveService;
        this.examinationService = examinationService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.userService = userService;
    }

    // Списък болнични — администраторът вижда всички; лекарят вижда от свои прегледи;
    // пациентът вижда само своите
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String list(Model model) {
        User currentUser = userService.getCurrentUser();
        List<SickLeave> sickLeaves = new ArrayList<>();

        if (currentUser.getRole() == Role.ROLE_ADMIN) {
            // Всички болнични се получават чрез всички прегледи
            List<Examination> all = examinationService.findAll();
            for (Examination e : all) {
                SickLeave sl = sickLeaveService.findByExaminationId(e.getId());
                if (sl != null) {
                    sickLeaves.add(sl);
                }
            }
        } else if (currentUser.getRole() == Role.ROLE_DOCTOR) {
            Doctor doctor = doctorService.findByCurrentUser();
            List<Examination> doctorExams = examinationService.findByDoctor(doctor.getId());
            for (Examination e : doctorExams) {
                SickLeave sl = sickLeaveService.findByExaminationId(e.getId());
                if (sl != null) {
                    sickLeaves.add(sl);
                }
            }
        } else {
            // ROLE_PATIENT
            Patient patient = patientService.findByCurrentUser();
            List<Examination> patientExams = examinationService.findByPatient(patient.getId());
            for (Examination e : patientExams) {
                SickLeave sl = sickLeaveService.findByExaminationId(e.getId());
                if (sl != null) {
                    sickLeaves.add(sl);
                }
            }
        }

        model.addAttribute("sickLeaves", sickLeaves);
        return "sick-leaves/list";
    }

    // Форма за нов болничен лист (администратор и лекарят-собственик на прегледа)
    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String newForm(@RequestParam Long examinationId, Model model) {
        Examination examination = examinationService.findById(examinationId);
        assertCanManageExamination(examination);

        model.addAttribute("sickLeaveDto", new SickLeaveDto());
        model.addAttribute("examination", examination);
        model.addAttribute("examinationId", examinationId);
        return "sick-leaves/form";
    }

    // Създаване на болничен лист (администратор и лекар)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String create(@RequestParam Long examinationId,
                         @Valid @ModelAttribute("sickLeaveDto") SickLeaveDto sickLeaveDto,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("examination", examinationService.findById(examinationId));
            model.addAttribute("examinationId", examinationId);
            return "sick-leaves/form";
        }
        try {
            SickLeave sickLeave = sickLeaveService.create(examinationId, sickLeaveDto);
            redirectAttributes.addFlashAttribute("successMessage", "Болничният лист беше успешно издаден.");
            return "redirect:/sick-leaves/" + sickLeave.getId() + "/edit";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("examination", examinationService.findById(examinationId));
            model.addAttribute("examinationId", examinationId);
            return "sick-leaves/form";
        }
    }

    // Преглед на болничен лист
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String view(@PathVariable Long id, Model model) {
        SickLeave sickLeave = sickLeaveService.findById(id);
        model.addAttribute("sickLeave", sickLeave);
        return "sick-leaves/view";
    }

    // Форма за редактиране на болничен лист (администратор и лекарят-собственик)
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String editForm(@PathVariable Long id, Model model) {
        SickLeave sickLeave = sickLeaveService.findById(id);
        assertCanManageExamination(sickLeave.getExamination());

        SickLeaveDto dto = new SickLeaveDto();
        dto.setStartDate(sickLeave.getStartDate());
        dto.setNumberOfDays(sickLeave.getNumberOfDays());

        model.addAttribute("sickLeaveDto", dto);
        model.addAttribute("sickLeaveId", id);
        model.addAttribute("examination", sickLeave.getExamination());
        return "sick-leaves/form";
    }

    // Обновяване на болничен лист (администратор и лекарят-собственик)
    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("sickLeaveDto") SickLeaveDto sickLeaveDto,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        SickLeave sickLeave = sickLeaveService.findById(id);
        assertCanManageExamination(sickLeave.getExamination());

        if (bindingResult.hasErrors()) {
            model.addAttribute("sickLeaveId", id);
            model.addAttribute("examination", sickLeave.getExamination());
            return "sick-leaves/form";
        }
        try {
            sickLeaveService.update(id, sickLeaveDto);
            redirectAttributes.addFlashAttribute("successMessage", "Болничният лист беше успешно обновен.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/sick-leaves";
    }

    // Изтриване на болничен лист (само за администратор)
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            sickLeaveService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Болничният лист беше успешно изтрит.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/sick-leaves";
    }

    // Проверява дали текущият потребител може да управлява болничния на дадения преглед
    private void assertCanManageExamination(Examination examination) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() == Role.ROLE_ADMIN) {
            return;
        }
        if (currentUser.getRole() == Role.ROLE_DOCTOR) {
            Doctor doctor = doctorService.findByCurrentUser();
            if (!examination.getDoctor().getId().equals(doctor.getId())) {
                throw new AccessForbiddenException("Нямате право да управлявате болничен лист за чужд преглед");
            }
        }
    }
}
