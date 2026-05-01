package bg.medicalrecord.repository;

import bg.medicalrecord.model.InsurancePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Хранилище за здравноосигурителни вноски
public interface InsurancePaymentRepository extends JpaRepository<InsurancePayment, Long> {
    List<InsurancePayment> findByPatientId(Long patientId);
}
