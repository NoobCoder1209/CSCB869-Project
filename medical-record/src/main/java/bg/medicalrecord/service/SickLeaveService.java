package bg.medicalrecord.service;

import bg.medicalrecord.dto.SickLeaveDto;
import bg.medicalrecord.model.SickLeave;

import java.util.List;

// Интерфейс за управление на болнични листове
public interface SickLeaveService {

    // Връща всички болнични листове
    List<SickLeave> findAll();

    // Намира болничен по ID
    SickLeave findById(Long id);

    // Намира болничен по преглед
    SickLeave findByExaminationId(Long examinationId);

    // Издава болничен лист за преглед
    SickLeave create(Long examinationId, SickLeaveDto dto);

    // Обновява болничен лист
    SickLeave update(Long id, SickLeaveDto dto);

    // Изтрива болничен лист
    void delete(Long id);
}
