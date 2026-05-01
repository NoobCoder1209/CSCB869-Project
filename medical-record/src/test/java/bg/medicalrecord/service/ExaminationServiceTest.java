package bg.medicalrecord.service;

import bg.medicalrecord.dto.ExaminationDto;
import bg.medicalrecord.exception.ResourceNotFoundException;
import bg.medicalrecord.model.*;
import bg.medicalrecord.model.enums.Specialty;
import bg.medicalrecord.repository.DiagnosisRepository;
import bg.medicalrecord.repository.DoctorRepository;
import bg.medicalrecord.repository.ExaminationRepository;
import bg.medicalrecord.repository.PatientRepository;
import bg.medicalrecord.service.impl.ExaminationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Модулни тестове за ExaminationService
@ExtendWith(MockitoExtension.class)
class ExaminationServiceTest {

    @Mock
    private ExaminationRepository examinationRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DiagnosisRepository diagnosisRepository;

    @Mock
    private InsuranceService insuranceService;

    @Mock
    private UserService userService;

    @InjectMocks
    private ExaminationServiceImpl examinationService;

    private Doctor testDoctor;
    private Patient testPatient;
    private Diagnosis testDiagnosis;
    private ExaminationDto testDto;

    @BeforeEach
    void setUp() {
        // Подготовка на общи тестови данни
        testDoctor = new Doctor("1234567890", "Д-р Иванов", Specialty.GENERAL_PRACTICE, true);
        testDoctor.setId(1L);

        testPatient = new Patient("Петър Георгиев", "9001015678");
        testPatient.setId(2L);

        testDiagnosis = new Diagnosis("J06", "Остра инфекция на горните дихателни пътища");
        testDiagnosis.setId(3L);

        testDto = new ExaminationDto();
        testDto.setDoctorId(1L);
        testDto.setPatientId(2L);
        testDto.setDiagnosisId(3L);
        testDto.setExaminationDate(LocalDate.of(2025, 3, 15));
        testDto.setTreatment("Антибиотик, почивка");
        testDto.setPrice(new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("create - задава paidByNhif=true когато пациентът е осигурен")
    void create_insuredPatient_setsNhifTrue() {
        // Подготовка - пациентът е здравноосигурен
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(testPatient));
        when(diagnosisRepository.findById(3L)).thenReturn(Optional.of(testDiagnosis));
        when(insuranceService.isInsuredAt(2L, LocalDate.of(2025, 3, 15))).thenReturn(true);
        when(examinationRepository.save(any(Examination.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Изпълнение
        Examination result = examinationService.create(testDto);

        // Проверка - прегледът се плаща от НЗОК
        assertTrue(result.isPaidByNhif());
        assertEquals(testDoctor, result.getDoctor());
        assertEquals(testPatient, result.getPatient());
        assertEquals(testDiagnosis, result.getDiagnosis());
        assertEquals(new BigDecimal("50.00"), result.getPrice());
        verify(insuranceService).isInsuredAt(2L, LocalDate.of(2025, 3, 15));
    }

    @Test
    @DisplayName("create - задава paidByNhif=false когато пациентът не е осигурен")
    void create_uninsuredPatient_setsNhifFalse() {
        // Подготовка - пациентът НЕ е здравноосигурен
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.of(testPatient));
        when(diagnosisRepository.findById(3L)).thenReturn(Optional.of(testDiagnosis));
        when(insuranceService.isInsuredAt(2L, LocalDate.of(2025, 3, 15))).thenReturn(false);
        when(examinationRepository.save(any(Examination.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Изпълнение
        Examination result = examinationService.create(testDto);

        // Проверка - прегледът се плаща от пациента
        assertFalse(result.isPaidByNhif());
    }

    @Test
    @DisplayName("create - хвърля ResourceNotFoundException при несъществуващ лекар")
    void create_nonExistingDoctor_throwsException() {
        // Подготовка
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        // Изпълнение и проверка
        assertThrows(ResourceNotFoundException.class, () -> examinationService.create(testDto));
        verify(examinationRepository, never()).save(any(Examination.class));
    }

    @Test
    @DisplayName("create - хвърля ResourceNotFoundException при несъществуващ пациент")
    void create_nonExistingPatient_throwsException() {
        // Подготовка
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(patientRepository.findById(2L)).thenReturn(Optional.empty());

        // Изпълнение и проверка
        assertThrows(ResourceNotFoundException.class, () -> examinationService.create(testDto));
        verify(examinationRepository, never()).save(any(Examination.class));
    }

    @Test
    @DisplayName("findByPatient - връща прегледите на конкретен пациент")
    void findByPatient_returnsExaminations() {
        // Подготовка
        Examination exam1 = new Examination();
        exam1.setId(1L);
        exam1.setExaminationDate(LocalDate.of(2025, 3, 15));

        Examination exam2 = new Examination();
        exam2.setId(2L);
        exam2.setExaminationDate(LocalDate.of(2025, 2, 10));

        when(examinationRepository.findByPatientIdOrderByExaminationDateDesc(2L))
                .thenReturn(Arrays.asList(exam1, exam2));

        // Изпълнение
        List<Examination> result = examinationService.findByPatient(2L);

        // Проверка - прегледите са подредени по дата (низходящо)
        assertEquals(2, result.size());
        assertEquals(LocalDate.of(2025, 3, 15), result.get(0).getExaminationDate());
        assertEquals(LocalDate.of(2025, 2, 10), result.get(1).getExaminationDate());
        verify(examinationRepository).findByPatientIdOrderByExaminationDateDesc(2L);
    }

    @Test
    @DisplayName("findByDoctor - връща прегледите на конкретен лекар")
    void findByDoctor_returnsExaminations() {
        // Подготовка
        Examination exam1 = new Examination();
        exam1.setId(1L);
        exam1.setDoctor(testDoctor);

        when(examinationRepository.findByDoctorIdOrderByExaminationDateDesc(1L))
                .thenReturn(List.of(exam1));

        // Изпълнение
        List<Examination> result = examinationService.findByDoctor(1L);

        // Проверка
        assertEquals(1, result.size());
        verify(examinationRepository).findByDoctorIdOrderByExaminationDateDesc(1L);
    }

    @Test
    @DisplayName("findById - връща преглед при съществуващ ID")
    void findById_existingId_returnsExamination() {
        // Подготовка
        Examination exam = new Examination();
        exam.setId(1L);
        exam.setTreatment("Лечение");
        when(examinationRepository.findById(1L)).thenReturn(Optional.of(exam));

        // Изпълнение
        Examination result = examinationService.findById(1L);

        // Проверка
        assertNotNull(result);
        assertEquals("Лечение", result.getTreatment());
    }

    @Test
    @DisplayName("findById - хвърля ResourceNotFoundException при несъществуващ ID")
    void findById_nonExistingId_throwsException() {
        // Подготовка
        when(examinationRepository.findById(99L)).thenReturn(Optional.empty());

        // Изпълнение и проверка
        assertThrows(ResourceNotFoundException.class, () -> examinationService.findById(99L));
    }
}
