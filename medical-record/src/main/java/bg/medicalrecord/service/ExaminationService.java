package bg.medicalrecord.service;

import bg.medicalrecord.dto.ExaminationDto;
import bg.medicalrecord.model.Examination;

import java.util.List;

// Интерфейс за управление на прегледи
public interface ExaminationService {

    // Връща всички прегледи
    List<Examination> findAll();

    // Намира преглед по ID
    Examination findById(Long id);

    // Връща прегледите на пациент
    List<Examination> findByPatient(Long patientId);

    // Връща прегледите на лекар
    List<Examination> findByDoctor(Long doctorId);

    // Създава преглед и автоматично определя дали се плаща от НЗОК
    Examination create(ExaminationDto dto);

    // Обновява преглед (само собственикът лекар или администратор)
    Examination update(Long id, ExaminationDto dto);

    // Изтрива преглед
    void delete(Long id);
}
