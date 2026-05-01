package bg.medicalrecord.model;

import jakarta.persistence.*;
import java.time.LocalDate;

// Болничен лист издаден при преглед
@Entity
@Table(name = "sick_leaves")
public class SickLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "examination_id", nullable = false, unique = true)
    private Examination examination;      // Прегледът, при който е издаден

    @Column(nullable = false)
    private LocalDate startDate;          // Начална дата

    @Column(nullable = false)
    private int numberOfDays;             // Брой дни

    public SickLeave() {}

    public SickLeave(Examination examination, LocalDate startDate, int numberOfDays) {
        this.examination = examination;
        this.startDate = startDate;
        this.numberOfDays = numberOfDays;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Examination getExamination() { return examination; }
    public void setExamination(Examination examination) { this.examination = examination; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public int getNumberOfDays() { return numberOfDays; }
    public void setNumberOfDays(int numberOfDays) { this.numberOfDays = numberOfDays; }
}
