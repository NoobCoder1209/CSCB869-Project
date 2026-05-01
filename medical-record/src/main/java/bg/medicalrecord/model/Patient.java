package bg.medicalrecord.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

// Пациент с ЕГН и личен лекар
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String fullName;              // Пълно име на пациента

    @Column(nullable = false, unique = true, length = 10)
    private String egn;                   // Единен граждански номер

    // Личен лекар
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personal_doctor_id")
    private Doctor personalDoctor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;                    // Потребителски акаунт

    // Здравноосигурителни вноски
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InsurancePayment> insurancePayments = new ArrayList<>();

    // Прегледи
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Examination> examinations = new ArrayList<>();

    public Patient() {}

    public Patient(String fullName, String egn) {
        this.fullName = fullName;
        this.egn = egn;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEgn() { return egn; }
    public void setEgn(String egn) { this.egn = egn; }
    public Doctor getPersonalDoctor() { return personalDoctor; }
    public void setPersonalDoctor(Doctor personalDoctor) { this.personalDoctor = personalDoctor; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<InsurancePayment> getInsurancePayments() { return insurancePayments; }
    public void setInsurancePayments(List<InsurancePayment> insurancePayments) { this.insurancePayments = insurancePayments; }
    public List<Examination> getExaminations() { return examinations; }
    public void setExaminations(List<Examination> examinations) { this.examinations = examinations; }
}
