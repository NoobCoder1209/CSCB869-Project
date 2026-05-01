package bg.medicalrecord.service.impl;

import bg.medicalrecord.dto.PatientDto;
import bg.medicalrecord.exception.DuplicateIdentifierException;
import bg.medicalrecord.exception.ResourceNotFoundException;
import bg.medicalrecord.model.Doctor;
import bg.medicalrecord.model.Patient;
import bg.medicalrecord.model.User;
import bg.medicalrecord.repository.DoctorRepository;
import bg.medicalrecord.repository.PatientRepository;
import bg.medicalrecord.repository.UserRepository;
import bg.medicalrecord.service.PatientService;
import bg.medicalrecord.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Сервиз за управление на пациенти
@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public PatientServiceImpl(PatientRepository patientRepository, DoctorRepository doctorRepository,
                              UserRepository userRepository, UserService userService) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    // Връща всички пациенти
    @Override
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    // Намира пациент по ID
    @Override
    public Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пациент", id));
    }

    // Намира пациентския профил на текущо влезлия потребител
    @Override
    public Patient findByCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return patientRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Пациентски профил не е намерен за текущия потребител"));
    }

    // Създава нов пациент
    @Override
    @Transactional
    public Patient create(PatientDto dto) {
        if (patientRepository.existsByEgn(dto.getEgn())) {
            throw new DuplicateIdentifierException("Пациент с това ЕГН вече съществува");
        }
        Patient patient = new Patient(dto.getFullName(), dto.getEgn());
        if (dto.getPersonalDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(dto.getPersonalDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Лекар", dto.getPersonalDoctorId()));
            patient.setPersonalDoctor(doctor);
        }
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Потребител", dto.getUserId()));
            patient.setUser(user);
        }
        return patientRepository.save(patient);
    }

    // Обновява пациент
    @Override
    @Transactional
    public Patient update(Long id, PatientDto dto) {
        Patient patient = findById(id);
        if (!patient.getEgn().equals(dto.getEgn()) && patientRepository.existsByEgn(dto.getEgn())) {
            throw new DuplicateIdentifierException("Пациент с това ЕГН вече съществува");
        }
        patient.setFullName(dto.getFullName());
        patient.setEgn(dto.getEgn());
        if (dto.getPersonalDoctorId() != null) {
            Doctor doctor = doctorRepository.findById(dto.getPersonalDoctorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Лекар", dto.getPersonalDoctorId()));
            patient.setPersonalDoctor(doctor);
        } else {
            patient.setPersonalDoctor(null);
        }
        return patientRepository.save(patient);
    }

    // Изтрива пациент
    @Override
    @Transactional
    public void delete(Long id) {
        Patient patient = findById(id);
        patientRepository.delete(patient);
    }
}
