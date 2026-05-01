package bg.medicalrecord.model;

import bg.medicalrecord.model.enums.Role;
import jakarta.persistence.*;

// Потребителски акаунт за автентикация и оторизация
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;              // Потребителско име за вход

    @Column(nullable = false)
    private String password;              // BCrypt хеш на паролата

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;                    // Роля в системата

    @Column(nullable = false)
    private boolean enabled = true;       // Активен акаунт

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Doctor doctor;                // Профил на лекар (ако ролята е ROLE_DOCTOR)

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Patient patient;              // Профил на пациент (ако ролята е ROLE_PATIENT)

    // Конструктори
    public User() {}

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
}
