package bg.medicalrecord.dto;

import bg.medicalrecord.model.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// DTO за създаване на нов потребител
public class CreateUserDto {

    @NotBlank(message = "Потребителското име е задължително")
    @Size(min = 3, max = 100, message = "Потребителското име трябва да е между 3 и 100 символа")
    private String username;

    @NotBlank(message = "Паролата е задължителна")
    @Size(min = 6, message = "Паролата трябва да е поне 6 символа")
    private String password;

    @NotBlank(message = "Потвърждението на паролата е задължително")
    private String confirmPassword;

    @NotNull(message = "Ролята е задължителна")
    private Role role;

    // Getters and Setters (generate all)
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
