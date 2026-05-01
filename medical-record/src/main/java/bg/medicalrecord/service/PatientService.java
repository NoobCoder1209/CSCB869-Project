package bg.medicalrecord.service;

import bg.medicalrecord.dto.PatientDto;
import bg.medicalrecord.model.Patient;

import java.util.List;

// Интерфейс за управление на пациенти
public interface PatientService {

    // Връща всички пациенти
    List<Patient> findAll();

    // Намира пациент по ID
    Patient findById(Long id);

    // Намира пациентския профил на текущо влезлия потребител
    Patient findByCurrentUser();

    // Създава нов пациент
    Patient create(PatientDto dto);

    // Обновява пациент
    Patient update(Long id, PatientDto dto);

    // Изтрива пациент
    void delete(Long id);
}
