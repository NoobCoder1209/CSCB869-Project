package bg.medicalrecord.service;

import bg.medicalrecord.dto.DoctorDto;
import bg.medicalrecord.model.Doctor;

import java.util.List;

// Интерфейс за управление на лекари
public interface DoctorService {

    // Връща всички лекари
    List<Doctor> findAll();

    // Намира лекар по ID
    Doctor findById(Long id);

    // Намира лекар по УИН
    Doctor findByUin(String uin);

    // Намира лекарския профил на текущо влезлия потребител
    Doctor findByCurrentUser();

    // Връща всички лични лекари (ОПЛ)
    List<Doctor> findAllGps();

    // Създава нов лекар
    Doctor create(DoctorDto dto);

    // Обновява лекар
    Doctor update(Long id, DoctorDto dto);

    // Изтрива лекар
    void delete(Long id);
}
