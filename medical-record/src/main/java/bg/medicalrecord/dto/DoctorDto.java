package bg.medicalrecord.dto;

import bg.medicalrecord.model.enums.Specialty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO за създаване и редактиране на лекар
public class DoctorDto {

    @NotBlank(message = "УИН е задължителен")
    @Size(min = 10, max = 10, message = "УИН трябва да е точно 10 цифри")
    private String uin;

    @NotBlank(message = "Името е задължително")
    @Size(max = 150, message = "Името не може да надвишава 150 символа")
    private String fullName;

    @NotNull(message = "Специалността е задължителна")
    private Specialty specialty;

    private boolean isGp;

    private Long userId;

    // Getters and Setters
    public String getUin() { return uin; }
    public void setUin(String uin) { this.uin = uin; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Specialty getSpecialty() { return specialty; }
    public void setSpecialty(Specialty specialty) { this.specialty = specialty; }
    public boolean isGp() { return isGp; }
    public void setGp(boolean gp) { isGp = gp; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
