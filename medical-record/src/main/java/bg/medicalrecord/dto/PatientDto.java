package bg.medicalrecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// DTO за създаване и редактиране на пациент
public class PatientDto {

    @NotBlank(message = "Името е задължително")
    @Size(max = 150, message = "Името не може да надвишава 150 символа")
    private String fullName;

    @NotBlank(message = "ЕГН е задължително")
    @Pattern(regexp = "\\d{10}", message = "ЕГН трябва да съдържа точно 10 цифри")
    private String egn;

    private Long personalDoctorId;

    private Long userId;

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEgn() { return egn; }
    public void setEgn(String egn) { this.egn = egn; }
    public Long getPersonalDoctorId() { return personalDoctorId; }
    public void setPersonalDoctorId(Long personalDoctorId) { this.personalDoctorId = personalDoctorId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
