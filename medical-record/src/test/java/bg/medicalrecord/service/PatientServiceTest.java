package bg.medicalrecord.service;

import bg.medicalrecord.dto.PatientDto;
import bg.medicalrecord.exception.DuplicateIdentifierException;
import bg.medicalrecord.exception.ResourceNotFoundException;
import bg.medicalrecord.model.Doctor;
import bg.medicalrecord.model.Patient;
import bg.medicalrecord.model.User;
import bg.medicalrecord.repository.DoctorRepository;
import bg.medicalrecord.repository.PatientRepository;
import bg.medicalrecord.repository.UserRepository;
import bg.medicalrecord.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Модулни тестове за PatientService
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient testPatient;
    private PatientDto testDto;

    @BeforeEach
    void setUp() {
        // Подготовка на тестови данни
        testPatient = new Patient("Иван Петров", "8501011234");
        testPatient.setId(1L);

        testDto = new PatientDto();
        testDto.setFullName("Иван Петров");
        testDto.setEgn("8501011234");
    }

    @Test
    @DisplayName("findAll - връща списък с всички пациенти")
    void findAll_returnsAllPatients() {
        // Подготовка
        Patient secondPatient = new Patient("Мария Иванова", "9002023456");
        secondPatient.setId(2L);
        when(patientRepository.findAll()).thenReturn(Arrays.asList(testPatient, secondPatient));

        // Изпълнение
        List<Patient> result = patientService.findAll();

        // Проверка
        assertEquals(2, result.size());
        assertEquals("Иван Петров", result.get(0).getFullName());
        assertEquals("Мария Иванова", result.get(1).getFullName());
        verify(patientRepository).findAll();
    }

    @Test
    @DisplayName("findById - връща пациент при съществуващ ID")
    void findById_existingId_returnsPatient() {
        // Подготовка
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));

        // Изпълнение
        Patient result = patientService.findById(1L);

        // Проверка
        assertNotNull(result);
        assertEquals("Иван Петров", result.getFullName());
        assertEquals("8501011234", result.getEgn());
        verify(patientRepository).findById(1L);
    }

    @Test
    @DisplayName("findById - хвърля ResourceNotFoundException при несъществуващ ID")
    void findById_nonExistingId_throwsException() {
        // Подготовка
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        // Изпълнение и проверка
        assertThrows(ResourceNotFoundException.class, () -> patientService.findById(99L));
        verify(patientRepository).findById(99L);
    }

    @Test
    @DisplayName("create - успешно създава пациент")
    void create_validDto_createsPatient() {
        // Подготовка
        when(patientRepository.existsByEgn("8501011234")).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);

        // Изпълнение
        Patient result = patientService.create(testDto);

        // Проверка
        assertNotNull(result);
        assertEquals("Иван Петров", result.getFullName());
        verify(patientRepository).existsByEgn("8501011234");
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    @DisplayName("create - хвърля DuplicateIdentifierException при дублирано ЕГН")
    void create_duplicateEgn_throwsException() {
        // Подготовка
        when(patientRepository.existsByEgn("8501011234")).thenReturn(true);

        // Изпълнение и проверка
        assertThrows(DuplicateIdentifierException.class, () -> patientService.create(testDto));
        verify(patientRepository).existsByEgn("8501011234");
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    @DisplayName("create - задава личен лекар ако е подаден personalDoctorId")
    void create_withDoctorId_setsPersonalDoctor() {
        // Подготовка
        testDto.setPersonalDoctorId(5L);
        Doctor doctor = new Doctor();
        doctor.setId(5L);
        doctor.setFullName("Д-р Георгиев");

        when(patientRepository.existsByEgn("8501011234")).thenReturn(false);
        when(doctorRepository.findById(5L)).thenReturn(Optional.of(doctor));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Изпълнение
        Patient result = patientService.create(testDto);

        // Проверка
        assertNotNull(result.getPersonalDoctor());
        assertEquals("Д-р Георгиев", result.getPersonalDoctor().getFullName());
    }

    @Test
    @DisplayName("update - успешно обновява данните на пациент")
    void update_validDto_updatesPatient() {
        // Подготовка
        PatientDto updateDto = new PatientDto();
        updateDto.setFullName("Иван Петров-Новия");
        updateDto.setEgn("8501011234");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Изпълнение
        Patient result = patientService.update(1L, updateDto);

        // Проверка
        assertEquals("Иван Петров-Новия", result.getFullName());
        verify(patientRepository).save(testPatient);
    }

    @Test
    @DisplayName("delete - успешно изтрива съществуващ пациент")
    void delete_existingId_deletesPatient() {
        // Подготовка
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));

        // Изпълнение
        patientService.delete(1L);

        // Проверка
        verify(patientRepository).delete(testPatient);
    }

    @Test
    @DisplayName("delete - хвърля ResourceNotFoundException при несъществуващ ID")
    void delete_nonExistingId_throwsException() {
        // Подготовка
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        // Изпълнение и проверка
        assertThrows(ResourceNotFoundException.class, () -> patientService.delete(99L));
        verify(patientRepository, never()).delete(any(Patient.class));
    }
}
