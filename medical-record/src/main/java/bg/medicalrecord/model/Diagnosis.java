package bg.medicalrecord.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

// Медицинска диагноза по МКБ-10 класификация
@Entity
@Table(name = "diagnoses")
public class Diagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String code;                  // МКБ-10 код

    @Column(nullable = false, length = 255)
    private String description;           // Описание на диагнозата

    @OneToMany(mappedBy = "diagnosis", fetch = FetchType.LAZY)
    private List<Examination> examinations = new ArrayList<>();

    public Diagnosis() {}

    public Diagnosis(String code, String description) {
        this.code = code;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Examination> getExaminations() { return examinations; }
    public void setExaminations(List<Examination> examinations) { this.examinations = examinations; }
}
