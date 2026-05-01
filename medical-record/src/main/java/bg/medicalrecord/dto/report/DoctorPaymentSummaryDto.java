package bg.medicalrecord.dto.report;

import bg.medicalrecord.model.Doctor;
import java.math.BigDecimal;

// DTO за справка: стойност на прегледи по лекар
public class DoctorPaymentSummaryDto {
    private Doctor doctor;
    private BigDecimal totalAmount;

    public DoctorPaymentSummaryDto(Doctor doctor, BigDecimal totalAmount) {
        this.doctor = doctor;
        this.totalAmount = totalAmount;
    }

    public Doctor getDoctor() { return doctor; }
    public BigDecimal getTotalAmount() { return totalAmount; }
}
