package bg.medicalrecord.service;

import bg.medicalrecord.dto.DiagnosisDto;
import bg.medicalrecord.model.Diagnosis;

import java.util.List;

// Интерфейс за управление на диагнози (МКБ-10)
public interface DiagnosisService {

    // Връща всички диагнози
    List<Diagnosis> findAll();

    // Намира диагноза по ID
    Diagnosis findById(Long id);

    // Създава нова диагноза
    Diagnosis create(DiagnosisDto dto);

    // Обновява диагноза
    Diagnosis update(Long id, DiagnosisDto dto);

    // Изтрива диагноза
    void delete(Long id);
}
