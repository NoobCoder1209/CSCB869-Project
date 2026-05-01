package bg.medicalrecord.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

// DTO за създаване и редактиране на болничен лист
public class SickLeaveDto {

    @NotNull(message = "Началната дата е задължителна")
    private LocalDate startDate;

    @Min(value = 1, message = "Болничният е минимум 1 ден")
    @Max(value = 365, message = "Болничният е максимум 365 дни")
    private int numberOfDays;

    // Getters and Setters
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public int getNumberOfDays() { return numberOfDays; }
    public void setNumberOfDays(int numberOfDays) { this.numberOfDays = numberOfDays; }
}
