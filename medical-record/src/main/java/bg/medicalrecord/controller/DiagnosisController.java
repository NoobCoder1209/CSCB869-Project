package bg.medicalrecord.controller;

import bg.medicalrecord.dto.DiagnosisDto;
import bg.medicalrecord.model.Diagnosis;
import bg.medicalrecord.service.DiagnosisService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// Контролер за управление на МКБ-10 диагнози
@Controller
@RequestMapping("/diagnoses")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    public DiagnosisController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    // Списък на всички диагнози (администратор и лекар)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public String list(Model model) {
        model.addAttribute("diagnoses", diagnosisService.findAll());
        return "diagnoses/list";
    }

    // Форма за нова диагноза (само за администратор)
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newForm(Model model) {
        model.addAttribute("diagnosisDto", new DiagnosisDto());
        return "diagnoses/form";
    }

    // Създаване на нова диагноза (само за администратор)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@Valid @ModelAttribute("diagnosisDto") DiagnosisDto diagnosisDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "diagnoses/form";
        }
        try {
            Diagnosis diagnosis = diagnosisService.create(diagnosisDto);
            redirectAttributes.addFlashAttribute("successMessage", "Диагнозата беше успешно създадена.");
            return "redirect:/diagnoses";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/diagnoses/new";
        }
    }

    // Форма за редактиране на диагноза (само за администратор)
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Long id, Model model) {
        Diagnosis diagnosis = diagnosisService.findById(id);
        DiagnosisDto dto = new DiagnosisDto();
        dto.setCode(diagnosis.getCode());
        dto.setDescription(diagnosis.getDescription());
        model.addAttribute("diagnosisDto", dto);
        model.addAttribute("diagnosisId", id);
        return "diagnoses/form";
    }

    // Обновяване на диагноза (само за администратор)
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("diagnosisDto") DiagnosisDto diagnosisDto,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("diagnosisId", id);
            return "diagnoses/form";
        }
        try {
            diagnosisService.update(id, diagnosisDto);
            redirectAttributes.addFlashAttribute("successMessage", "Диагнозата беше успешно обновена.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/diagnoses";
    }

    // Изтриване на диагноза (само за администратор)
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            diagnosisService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Диагнозата беше успешно изтрита.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/diagnoses";
    }
}
