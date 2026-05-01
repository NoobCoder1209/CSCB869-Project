package bg.medicalrecord.service.impl;

import bg.medicalrecord.dto.SickLeaveDto;
import bg.medicalrecord.exception.ResourceNotFoundException;
import bg.medicalrecord.model.Examination;
import bg.medicalrecord.model.SickLeave;
import bg.medicalrecord.repository.SickLeaveRepository;
import bg.medicalrecord.service.ExaminationService;
import bg.medicalrecord.service.SickLeaveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Сервиз за управление на болнични листове
@Service
public class SickLeaveServiceImpl implements SickLeaveService {

    private final SickLeaveRepository sickLeaveRepository;
    private final ExaminationService examinationService;

    public SickLeaveServiceImpl(SickLeaveRepository sickLeaveRepository, ExaminationService examinationService) {
        this.sickLeaveRepository = sickLeaveRepository;
        this.examinationService = examinationService;
    }

    // Връща всички болнични листове
    @Override
    public List<SickLeave> findAll() {
        return sickLeaveRepository.findAll();
    }

    // Намира болничен по ID
    @Override
    public SickLeave findById(Long id) {
        return sickLeaveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Болничен лист", id));
    }

    // Намира болничен по преглед
    @Override
    public SickLeave findByExaminationId(Long examinationId) {
        return sickLeaveRepository.findByExaminationId(examinationId).orElse(null);
    }

    // Издава болничен лист за преглед
    @Override
    @Transactional
    public SickLeave create(Long examinationId, SickLeaveDto dto) {
        Examination examination = examinationService.findById(examinationId);
        SickLeave sickLeave = new SickLeave(examination, dto.getStartDate(), dto.getNumberOfDays());
        return sickLeaveRepository.save(sickLeave);
    }

    // Обновява болничен лист
    @Override
    @Transactional
    public SickLeave update(Long id, SickLeaveDto dto) {
        SickLeave sickLeave = findById(id);
        sickLeave.setStartDate(dto.getStartDate());
        sickLeave.setNumberOfDays(dto.getNumberOfDays());
        return sickLeaveRepository.save(sickLeave);
    }

    // Изтрива болничен лист
    @Override
    @Transactional
    public void delete(Long id) {
        SickLeave sickLeave = findById(id);
        sickLeaveRepository.delete(sickLeave);
    }
}
