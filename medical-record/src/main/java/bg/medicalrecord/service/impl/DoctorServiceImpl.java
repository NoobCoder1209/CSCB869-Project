package bg.medicalrecord.service.impl;

import bg.medicalrecord.dto.DoctorDto;
import bg.medicalrecord.exception.DuplicateIdentifierException;
import bg.medicalrecord.exception.ResourceNotFoundException;
import bg.medicalrecord.model.Doctor;
import bg.medicalrecord.model.User;
import bg.medicalrecord.repository.DoctorRepository;
import bg.medicalrecord.repository.UserRepository;
import bg.medicalrecord.service.DoctorService;
import bg.medicalrecord.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Сервиз за управление на лекари
@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public DoctorServiceImpl(DoctorRepository doctorRepository, UserRepository userRepository, UserService userService) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    // Връща всички лекари
    @Override
    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    // Намира лекар по ID
    @Override
    public Doctor findById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Лекар", id));
    }

    // Намира лекар по УИН
    @Override
    public Doctor findByUin(String uin) {
        return doctorRepository.findByUin(uin)
                .orElseThrow(() -> new ResourceNotFoundException("Лекар с УИН '" + uin + "' не е намерен"));
    }

    // Намира лекарския профил на текущо влезлия потребител
    @Override
    public Doctor findByCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return doctorRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Лекарски профил не е намерен за текущия потребител"));
    }

    // Връща всички лични лекари (ОПЛ)
    @Override
    public List<Doctor> findAllGps() {
        return doctorRepository.findByIsGpTrue();
    }

    // Създава нов лекар
    @Override
    @Transactional
    public Doctor create(DoctorDto dto) {
        if (doctorRepository.existsByUin(dto.getUin())) {
            throw new DuplicateIdentifierException("Лекар с този УИН вече съществува");
        }
        Doctor doctor = new Doctor(dto.getUin(), dto.getFullName(), dto.getSpecialty(), dto.isGp());
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Потребител", dto.getUserId()));
            doctor.setUser(user);
        }
        return doctorRepository.save(doctor);
    }

    // Обновява лекар
    @Override
    @Transactional
    public Doctor update(Long id, DoctorDto dto) {
        Doctor doctor = findById(id);
        if (!doctor.getUin().equals(dto.getUin()) && doctorRepository.existsByUin(dto.getUin())) {
            throw new DuplicateIdentifierException("Лекар с този УИН вече съществува");
        }
        doctor.setUin(dto.getUin());
        doctor.setFullName(dto.getFullName());
        doctor.setSpecialty(dto.getSpecialty());
        doctor.setGp(dto.isGp());
        return doctorRepository.save(doctor);
    }

    // Изтрива лекар
    @Override
    @Transactional
    public void delete(Long id) {
        Doctor doctor = findById(id);
        doctorRepository.delete(doctor);
    }
}
