package bg.medicalrecord.service;

import bg.medicalrecord.model.InsurancePayment;
import bg.medicalrecord.model.Patient;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

// Интерфейс за управление на здравноосигурителни вноски
public interface InsuranceService {

    // Връща всички вноски на пациент
    List<InsurancePayment> findByPatient(Long patientId);

    // Добавя вноска за даден месец
    InsurancePayment addPayment(Patient patient, YearMonth month);

    // Изтрива вноска
    void deletePayment(Long id);

    // Проверява дали пациентът е здравноосигурен към дадена дата
    boolean isInsuredAt(Long patientId, LocalDate date);
}
