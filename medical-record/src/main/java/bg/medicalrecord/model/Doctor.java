package bg.medicalrecord.model;

import bg.medicalrecord.model.enums.Specialty;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

// Лекар с уникален идентификационен номер (УИН) и специалност
@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String uin;                   // Уникален идентификационен номер

    @Column(nullable = false, length = 150)
    private String fullName;              // Пълно име на лекаря

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Specialty specialty;          // Медицинска специалност

    @Column(nullable = false)
    private boolean isGp = false;         // Общопрактикуващ лекар (ОПЛ)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;                    // Потребителски акаунт

    // Пациенти, на които е личен лекар
    @OneToMany(mappedBy = "personalDoctor", fetch = FetchType.LAZY)
    private List<Patient> patients = new ArrayList<>();

    // Проведени прегледи
    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<Examination> examinations = new ArrayList<>();

    public Doctor() {}

    public Doctor(String uin, String fullName, Specialty specialty, boolean isGp) {
        this.uin = uin;
        this.fullName = fullName;
        this.specialty = specialty;
        this.isGp = isGp;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUin() { return uin; }
    public void setUin(String uin) { this.uin = uin; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public Specialty getSpecialty() { return specialty; }
    public void setSpecialty(Specialty specialty) { this.specialty = specialty; }
    public boolean isGp() { return isGp; }
    public void setGp(boolean gp) { isGp = gp; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Patient> getPatients() { return patients; }
    public void setPatients(List<Patient> patients) { this.patients = patients; }
    public List<Examination> getExaminations() { return examinations; }
    public void setExaminations(List<Examination> examinations) { this.examinations = examinations; }
}
