package bg.medicalrecord.service.impl;

import bg.medicalrecord.dto.ExaminationDto;
import bg.medicalrecord.exception.AccessForbiddenException;
import bg.medicalrecord.exception.ResourceNotFoundException;
import bg.medicalrecord.model.*;
import bg.medicalrecord.model.enums.Role;
import bg.medicalrecord.repository.DiagnosisRepository;
import bg.medicalrecord.repository.DoctorRepository;
import bg.medicalrecord.repository.ExaminationRepository;
import bg.medicalrecord.repository.PatientRepository;
import bg.medicalrecord.service.ExaminationService;
import bg.medicalrecord.service.InsuranceService;
import bg.medicalrecord.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Сервиз за управление на прегледи
@Service
public class ExaminationServiceImpl implements ExaminationService {

    private final ExaminationRepository examinationRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final InsuranceService insuranceService;
    private final UserService userService;

    public ExaminationServiceImpl(ExaminationRepository examinationRepository,
                                  DoctorRepository doctorRepository,
                                  PatientRepository patientRepository,
                                  DiagnosisRepository diagnosisRepository,
                                  InsuranceService insuranceService,
                                  UserService userService) {
        this.examinationRepository = examinationRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.insuranceService = insuranceService;
        this.userService = userService;
    }

    // Връща всички прегледи
    @Override
    public List<Examination> findAll() {
        return examinationRepository.findAll();
    }

    // Намира преглед по ID
    @Override
    public Examination findById(Long id) {
        return examinationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Преглед", id));
    }

    // Връща прегледите на пациент
    @Override
    public List<Examination> findByPatient(Long patientId) {
        return examinationRepository.findByPatientIdOrderByExaminationDateDesc(patientId);
    }

    // Връща прегледите на лекар
    @Override
    public List<Examination> findByDoctor(Long doctorId) {
        return examinationRepository.findByDoctorIdOrderByExaminationDateDesc(doctorId);
    }

    // Създава преглед и автоматично определя дали се плаща от НЗОК
    @Override
    @Transactional
    public Examination create(ExaminationDto dto) {
        Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Лекар", dto.getDoctorId()));
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Пациент", dto.getPatientId()));
        Diagnosis diagnosis = diagnosisRepository.findById(dto.getDiagnosisId())
                .orElseThrow(() -> new ResourceNotFoundException("Диагноза", dto.getDiagnosisId()));

        Examination examination = new Examination();
        examination.setExaminationDate(dto.getExaminationDate());
        examination.setDoctor(doctor);
        examination.setPatient(patient);
        examination.setDiagnosis(diagnosis);
        examination.setTreatment(dto.getTreatment());
        examination.setPrice(dto.getPrice());

        // Определя дали прегледът се плаща от НЗОК спрямо осигурителния статус
        boolean insured = insuranceService.isInsuredAt(patient.getId(), dto.getExaminationDate());
        examination.setPaidByNhif(insured);

        return examinationRepository.save(examination);
    }

    // Обновява преглед (само собственикът лекар или администратор)
    @Override
    @Transactional
    public Examination update(Long id, ExaminationDto dto) {
        Examination examination = findById(id);
        assertCurrentUserCanEdit(examination);

        Diagnosis diagnosis = diagnosisRepository.findById(dto.getDiagnosisId())
                .orElseThrow(() -> new ResourceNotFoundException("Диагноза", dto.getDiagnosisId()));

        examination.setExaminationDate(dto.getExaminationDate());
        examination.setDiagnosis(diagnosis);
        examination.setTreatment(dto.getTreatment());
        examination.setPrice(dto.getPrice());

        return examinationRepository.save(examination);
    }

    // Изтрива преглед
    @Override
    @Transactional
    public void delete(Long id) {
        Examination examination = findById(id);
        assertCurrentUserCanEdit(examination);
        examinationRepository.delete(examination);
    }

    // Проверява дали текущият потребител може да редактира прегледа
    private void assertCurrentUserCanEdit(Examination examination) {
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() == Role.ROLE_ADMIN) {
            return;
        }
        if (currentUser.getRole() == Role.ROLE_DOCTOR) {
            if (examination.getDoctor().getUser() == null ||
                !examination.getDoctor().getUser().getId().equals(currentUser.getId())) {
                throw new AccessForbiddenException("Нямате право да редактирате чужд преглед");
            }
        }
    }
}
