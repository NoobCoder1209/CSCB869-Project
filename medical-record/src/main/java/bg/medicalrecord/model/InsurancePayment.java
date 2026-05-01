package bg.medicalrecord.model;

import jakarta.persistence.*;
import java.time.YearMonth;

// Здравноосигурителна вноска за определен месец
@Entity
@Table(name = "insurance_payments")
public class InsurancePayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;              // Пациент

    @Column(nullable = false, length = 7)
    private YearMonth paymentMonth;       // Месец на вноската (YYYY-MM)

    public InsurancePayment() {}

    public InsurancePayment(Patient patient, YearMonth paymentMonth) {
        this.patient = patient;
        this.paymentMonth = paymentMonth;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public YearMonth getPaymentMonth() { return paymentMonth; }
    public void setPaymentMonth(YearMonth paymentMonth) { this.paymentMonth = paymentMonth; }
}
