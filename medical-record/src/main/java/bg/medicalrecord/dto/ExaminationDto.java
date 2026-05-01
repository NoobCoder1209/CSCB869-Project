package bg.medicalrecord.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

// DTO за създаване и редактиране на преглед
public class ExaminationDto {

    @NotNull(message = "Датата е задължителна")
    private LocalDate examinationDate;

    @NotNull(message = "Лекарят е задължителен")
    private Long doctorId;

    @NotNull(message = "Пациентът е задължителен")
    private Long patientId;

    @NotNull(message = "Диагнозата е задължителна")
    private Long diagnosisId;

    @NotBlank(message = "Лечението е задължително")
    @Size(max = 2000, message = "Лечението не може да надвишава 2000 символа")
    private String treatment;

    @NotNull(message = "Цената е задължителна")
    @DecimalMin(value = "0.00", message = "Цената не може да е отрицателна")
    private BigDecimal price;

    // Getters and Setters
    public LocalDate getExaminationDate() { return examinationDate; }
    public void setExaminationDate(LocalDate examinationDate) { this.examinationDate = examinationDate; }
    public Long getDoctorId() { return doctorId; }
    public void setDoctorId(Long doctorId) { this.doctorId = doctorId; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public Long getDiagnosisId() { return diagnosisId; }
    public void setDiagnosisId(Long diagnosisId) { this.diagnosisId = diagnosisId; }
    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
