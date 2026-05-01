package bg.medicalrecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO за създаване и редактиране на диагноза
public class DiagnosisDto {

    @NotBlank(message = "МКБ-10 кодът е задължителен")
    @Size(max = 10, message = "Кодът не може да надвишава 10 символа")
    private String code;

    @NotBlank(message = "Описанието е задължително")
    @Size(max = 255, message = "Описанието не може да надвишава 255 символа")
    private String description;

    // Getters and Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
