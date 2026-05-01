package bg.medicalrecord.repository;

import bg.medicalrecord.model.Examination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// Хранилище за прегледи
public interface ExaminationRepository extends JpaRepository<Examination, Long> {

    List<Examination> findByPatientIdOrderByExaminationDateDesc(Long patientId);
    List<Examination> findByDoctorIdOrderByExaminationDateDesc(Long doctorId);

    // Прегледи по лекар и/или период
    @Query("""
        SELECT e FROM Examination e
        WHERE (:doctorId IS NULL OR e.doctor.id = :doctorId)
          AND (:fromDate IS NULL OR e.examinationDate >= :fromDate)
          AND (:toDate IS NULL OR e.examinationDate <= :toDate)
        ORDER BY e.examinationDate DESC
        """)
    List<Examination> findByDoctorAndPeriod(
        @Param("doctorId") Long doctorId,
        @Param("fromDate") LocalDate fromDate,
        @Param("toDate") LocalDate toDate
    );

    // Обща стойност платена от пациенти (не от НЗОК)
    @Query("SELECT COALESCE(SUM(e.price), 0) FROM Examination e WHERE e.paidByNhif = false")
    BigDecimal sumPaidByPatients();

    // Стойност платена от пациенти групирана по лекар
    @Query("SELECT e.doctor, COALESCE(SUM(e.price), 0) FROM Examination e WHERE e.paidByNhif = false GROUP BY e.doctor")
    List<Object[]> sumPaidByPatientsPerDoctor();
}
