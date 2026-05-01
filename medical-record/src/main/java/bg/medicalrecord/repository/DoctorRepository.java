package bg.medicalrecord.repository;

import bg.medicalrecord.model.Doctor;
import bg.medicalrecord.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

// Хранилище за лекари
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUin(String uin);
    boolean existsByUin(String uin);
    Optional<Doctor> findByUser(User user);
    List<Doctor> findByIsGpTrue();

    // Брой пациенти при всеки личен лекар
    @Query("SELECT d, COUNT(p) FROM Doctor d LEFT JOIN d.patients p WHERE d.isGp = true GROUP BY d")
    List<Object[]> countPatientsByGp();

    // Брой прегледи при всеки лекар
    @Query("SELECT d, COUNT(e) FROM Doctor d LEFT JOIN d.examinations e GROUP BY d")
    List<Object[]> countVisitsByDoctor();
}
