package bg.medicalrecord.service.impl;

import bg.medicalrecord.dto.DiagnosisDto;
import bg.medicalrecord.exception.DuplicateIdentifierException;
import bg.medicalrecord.exception.ResourceNotFoundException;
import bg.medicalrecord.model.Diagnosis;
import bg.medicalrecord.repository.DiagnosisRepository;
import bg.medicalrecord.service.DiagnosisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Сервиз за управление на диагнози (МКБ-10)
@Service
public class DiagnosisServiceImpl implements DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;

    public DiagnosisServiceImpl(DiagnosisRepository diagnosisRepository) {
        this.diagnosisRepository = diagnosisRepository;
    }

    // Връща всички диагнози
    @Override
    public List<Diagnosis> findAll() {
        return diagnosisRepository.findAll();
    }

    // Намира диагноза по ID
    @Override
    public Diagnosis findById(Long id) {
        return diagnosisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Диагноза", id));
    }

    // Създава нова диагноза
    @Override
    @Transactional
    public Diagnosis create(DiagnosisDto dto) {
        if (diagnosisRepository.existsByCode(dto.getCode())) {
            throw new DuplicateIdentifierException("Диагноза с този код вече съществува");
        }
        Diagnosis diagnosis = new Diagnosis(dto.getCode(), dto.getDescription());
        return diagnosisRepository.save(diagnosis);
    }

    // Обновява диагноза
    @Override
    @Transactional
    public Diagnosis update(Long id, DiagnosisDto dto) {
        Diagnosis diagnosis = findById(id);
        if (!diagnosis.getCode().equals(dto.getCode()) && diagnosisRepository.existsByCode(dto.getCode())) {
            throw new DuplicateIdentifierException("Диагноза с този код вече съществува");
        }
        diagnosis.setCode(dto.getCode());
        diagnosis.setDescription(dto.getDescription());
        return diagnosisRepository.save(diagnosis);
    }

    // Изтрива диагноза
    @Override
    @Transactional
    public void delete(Long id) {
        Diagnosis diagnosis = findById(id);
        diagnosisRepository.delete(diagnosis);
    }
}
