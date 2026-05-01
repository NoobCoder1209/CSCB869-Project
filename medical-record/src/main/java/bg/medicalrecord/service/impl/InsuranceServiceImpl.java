package bg.medicalrecord.service.impl;

import bg.medicalrecord.model.InsurancePayment;
import bg.medicalrecord.model.Patient;
import bg.medicalrecord.repository.InsurancePaymentRepository;
import bg.medicalrecord.service.InsuranceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

// Сервиз за управление на здравноосигурителни вноски
@Service
public class InsuranceServiceImpl implements InsuranceService {

    private final InsurancePaymentRepository insurancePaymentRepository;

    public InsuranceServiceImpl(InsurancePaymentRepository insurancePaymentRepository) {
        this.insurancePaymentRepository = insurancePaymentRepository;
    }

    // Връща всички вноски на пациент
    @Override
    public List<InsurancePayment> findByPatient(Long patientId) {
        return insurancePaymentRepository.findByPatientId(patientId);
    }

    // Добавя вноска за даден месец
    @Override
    @Transactional
    public InsurancePayment addPayment(Patient patient, YearMonth month) {
        InsurancePayment payment = new InsurancePayment(patient, month);
        return insurancePaymentRepository.save(payment);
    }

    // Изтрива вноска
    @Override
    @Transactional
    public void deletePayment(Long id) {
        insurancePaymentRepository.deleteById(id);
    }

    // Проверява дали пациентът е здравноосигурен към дадена дата
    // Осигурен е ако има вноски за последните 6 месеца преди датата
    @Override
    public boolean isInsuredAt(Long patientId, LocalDate date) {
        YearMonth examMonth = YearMonth.from(date);
        List<InsurancePayment> payments = insurancePaymentRepository.findByPatientId(patientId);

        for (int i = 1; i <= 6; i++) {
            YearMonth requiredMonth = examMonth.minusMonths(i);
            boolean found = payments.stream()
                    .anyMatch(p -> p.getPaymentMonth().equals(requiredMonth));
            if (!found) {
                return false;
            }
        }
        return true;
    }
}
