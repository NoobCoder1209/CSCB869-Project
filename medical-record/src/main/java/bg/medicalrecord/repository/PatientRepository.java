package bg.medicalrecord.repository;

import bg.medicalrecord.model.Patient;
import bg.medicalrecord.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

// Хранилище за пациенти
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByEgn(String egn);
    boolean existsByEgn(String egn);
    Optional<Patient> findByUser(User user);

    // Пациенти с дадена диагноза
    @Query("SELECT DISTINCT p FROM Patient p JOIN p.examinations e WHERE e.diagnosis.id = :diagnosisId")
    List<Patient> findByDiagnosis(@Param("diagnosisId") Long diagnosisId);

    // Пациенти с даден личен лекар
    List<Patient> findByPersonalDoctorId(Long doctorId);
}
