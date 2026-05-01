package bg.medicalrecord.controller;

import bg.medicalrecord.service.DiagnosisService;
import bg.medicalrecord.service.DoctorService;
import bg.medicalrecord.service.PatientService;
import bg.medicalrecord.service.ReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

// Контролер за генериране на справки
@Controller
@RequestMapping("/reports")
@PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
public class ReportController {

    private final ReportService reportService;
    private final DiagnosisService diagnosisService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public ReportController(ReportService reportService,
                            DiagnosisService diagnosisService,
                            DoctorService doctorService,
                            PatientService patientService) {
        this.reportService = reportService;
        this.diagnosisService = diagnosisService;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // Индексна страница с всички 11 справки
    @GetMapping
    public String index() {
        return "reports/index";
    }

    // Справка 1: Пациенти с дадена диагноза
    @GetMapping("/patients-by-diagnosis")
    public String patientsByDiagnosis(@RequestParam(required = false) Long diagnosisId, Model model) {
        model.addAttribute("diagnoses", diagnosisService.findAll());
        if (diagnosisId != null) {
            model.addAttribute("patients", reportService.getPatientsWithDiagnosis(diagnosisId));
            model.addAttribute("selectedDiagnosisId", diagnosisId);
        }
        return "reports/patients-by-diagnosis";
    }

    // Справка 2: Най-честа диагноза
    @GetMapping("/most-common-diagnosis")
    public String mostCommonDiagnosis(Model model) {
        model.addAttribute("diagnosis", reportService.getMostCommonDiagnosis().orElse(null));
        return "reports/most-common-diagnosis";
    }

    // Справка 3: Пациенти с даден личен лекар
    @GetMapping("/patients-by-gp")
    public String patientsByGp(@RequestParam(required = false) Long doctorId, Model model) {
        model.addAttribute("doctors", doctorService.findAllGps());
        if (doctorId != null) {
            model.addAttribute("patients", reportService.getPatientsByGp(doctorId));
            model.addAttribute("selectedDoctorId", doctorId);
        }
        return "reports/patients-by-gp";
    }

    // Справка 4: Обща стойност на прегледи платени от пациенти
    @GetMapping("/patient-payments")
    public String patientPayments(Model model) {
        model.addAttribute("totalAmount", reportService.getTotalPaidByPatients());
        return "reports/patient-payments";
    }

    // Справка 5: Стойност на прегледи платени от пациенти по лекар
    @GetMapping("/payments-by-doctor")
    public String paymentsByDoctor(Model model) {
        model.addAttribute("paymentSummaries", reportService.getPaymentsPerDoctor());
        return "reports/payments-by-doctor";
    }

    // Справка 6: Брой пациенти при всеки личен лекар
    @GetMapping("/patients-per-gp")
    public String patientsPerGp(Model model) {
        model.addAttribute("gpPatientCounts", reportService.getPatientCountPerGp());
        return "reports/patients-per-gp";
    }

    // Справка 7: Брой посещения при всеки лекар
    @GetMapping("/visits-per-doctor")
    public String visitsPerDoctor(Model model) {
        model.addAttribute("visitCounts", reportService.getVisitCountPerDoctor());
        return "reports/visits-per-doctor";
    }

    // Справка 8: История на посещенията на пациент
    @GetMapping("/patient-history")
    public String patientHistory(@RequestParam(required = false) Long patientId, Model model) {
        model.addAttribute("patients", patientService.findAll());
        if (patientId != null) {
            model.addAttribute("examinations", reportService.getPatientHistory(patientId));
            model.addAttribute("selectedPatientId", patientId);
        }
        return "reports/patient-history";
    }

    // Справка 9: Прегледи по лекар и/или период
    @GetMapping("/examinations-filter")
    public String examinationsFilter(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            Model model) {
        model.addAttribute("doctors", doctorService.findAll());
        if (doctorId != null || dateFrom != null || dateTo != null) {
            model.addAttribute("examinations",
                    reportService.getExaminationsByDoctorAndPeriod(doctorId, dateFrom, dateTo));
            model.addAttribute("selectedDoctorId", doctorId);
            model.addAttribute("dateFrom", dateFrom);
            model.addAttribute("dateTo", dateTo);
        }
        return "reports/examinations-filter";
    }

    // Справка 10: Месец с най-много болнични
    @GetMapping("/month-most-sick-leaves")
    public String monthMostSickLeaves(Model model) {
        model.addAttribute("result", reportService.getMonthWithMostSickLeaves().orElse(null));
        return "reports/month-most-sick-leaves";
    }

    // Справка 11: Лекар(и) с най-много болнични
    @GetMapping("/doctor-most-sick-leaves")
    public String doctorMostSickLeaves(Model model) {
        model.addAttribute("doctors", reportService.getDoctorsWithMostSickLeaves());
        return "reports/doctor-most-sick-leaves";
    }
}
