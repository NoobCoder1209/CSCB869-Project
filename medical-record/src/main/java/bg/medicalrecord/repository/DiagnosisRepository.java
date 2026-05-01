package bg.medicalrecord.repository;

import bg.medicalrecord.model.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

// Хранилище за диагнози
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {
    Optional<Diagnosis> findByCode(String code);
    boolean existsByCode(String code);

    // Най-честа диагноза (по брой прегледи)
    @Query("SELECT d FROM Diagnosis d JOIN d.examinations e GROUP BY d ORDER BY COUNT(e) DESC LIMIT 1")
    Optional<Diagnosis> findMostCommon();
}
