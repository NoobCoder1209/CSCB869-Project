package bg.medicalrecord.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

// Медицински преглед свързващ лекар, пациент и диагноза
@Entity
@Table(name = "examinations")
public class Examination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate examinationDate;    // Дата на прегледа

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;                // Лекар провел прегледа

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;              // Пациент

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "diagnosis_id", nullable = false)
    private Diagnosis diagnosis;          // Поставена диагноза

    @Column(nullable = false, length = 2000)
    private String treatment;             // Предписано лечение

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;             // Цена на прегледа

    @Column(nullable = false)
    private boolean paidByNhif;           // Платено от НЗОК (true) или пациент (false)

    // Болничен лист (може да няма)
    @OneToOne(mappedBy = "examination", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SickLeave sickLeave;

    public Examination() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getExaminationDate() { return examinationDate; }
    public void setExaminationDate(LocalDate examinationDate) { this.examinationDate = examinationDate; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public Diagnosis getDiagnosis() { return diagnosis; }
    public void setDiagnosis(Diagnosis diagnosis) { this.diagnosis = diagnosis; }
    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public boolean isPaidByNhif() { return paidByNhif; }
    public void setPaidByNhif(boolean paidByNhif) { this.paidByNhif = paidByNhif; }
    public SickLeave getSickLeave() { return sickLeave; }
    public void setSickLeave(SickLeave sickLeave) { this.sickLeave = sickLeave; }
}
