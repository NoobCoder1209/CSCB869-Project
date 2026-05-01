package bg.medicalrecord.repository;

import bg.medicalrecord.model.SickLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

// Хранилище за болнични листове
public interface SickLeaveRepository extends JpaRepository<SickLeave, Long> {

    Optional<SickLeave> findByExaminationId(Long examinationId);

    // Месец с най-много издадени болнични
    @Query("SELECT MONTH(sl.startDate), YEAR(sl.startDate), COUNT(sl) FROM SickLeave sl GROUP BY YEAR(sl.startDate), MONTH(sl.startDate) ORDER BY COUNT(sl) DESC")
    List<Object[]> findMonthWithMostSickLeaves();

    // Лекари с най-много болнични
    @Query("SELECT e.doctor, COUNT(sl) FROM SickLeave sl JOIN sl.examination e GROUP BY e.doctor ORDER BY COUNT(sl) DESC")
    List<Object[]> countSickLeavesPerDoctor();
}
