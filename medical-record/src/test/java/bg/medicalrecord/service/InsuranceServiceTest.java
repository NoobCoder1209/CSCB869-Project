package bg.medicalrecord.service;

import bg.medicalrecord.model.InsurancePayment;
import bg.medicalrecord.model.Patient;
import bg.medicalrecord.repository.InsurancePaymentRepository;
import bg.medicalrecord.service.impl.InsuranceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Модулни тестове за InsuranceService - проверка на осигурителен статус
@ExtendWith(MockitoExtension.class)
class InsuranceServiceTest {

    @Mock
    private InsurancePaymentRepository insurancePaymentRepository;

    @InjectMocks
    private InsuranceServiceImpl insuranceService;

    private Patient testPatient;
    private static final Long PATIENT_ID = 1L;

    @BeforeEach
    void setUp() {
        testPatient = new Patient("Иван Петров", "8501011234");
        testPatient.setId(PATIENT_ID);
    }

    @Test
    @DisplayName("isInsuredAt - връща true когато пациентът има вноски за последните 6 месеца")
    void isInsuredAt_allSixMonthsPaid_returnsTrue() {
        // Подготовка - пациентът е платил последните 6 месеца преди януари 2025
        LocalDate examDate = LocalDate.of(2025, 1, 15);
        List<InsurancePayment> payments = Arrays.asList(
                createPayment(YearMonth.of(2024, 12)),  // 1 месец назад
                createPayment(YearMonth.of(2024, 11)),  // 2 месеца назад
                createPayment(YearMonth.of(2024, 10)),  // 3 месеца назад
                createPayment(YearMonth.of(2024, 9)),   // 4 месеца назад
                createPayment(YearMonth.of(2024, 8)),   // 5 месеца назад
                createPayment(YearMonth.of(2024, 7))    // 6 месеца назад
        );
        when(insurancePaymentRepository.findByPatientId(PATIENT_ID)).thenReturn(payments);

        // Изпълнение
        boolean result = insuranceService.isInsuredAt(PATIENT_ID, examDate);

        // Проверка - пациентът е здравноосигурен
        assertTrue(result);
        verify(insurancePaymentRepository).findByPatientId(PATIENT_ID);
    }

    @Test
    @DisplayName("isInsuredAt - връща false когато липсва вноска за един от последните 6 месеца")
    void isInsuredAt_missingOneMonth_returnsFalse() {
        // Подготовка - липсва вноска за октомври 2024
        LocalDate examDate = LocalDate.of(2025, 1, 15);
        List<InsurancePayment> payments = Arrays.asList(
                createPayment(YearMonth.of(2024, 12)),
                createPayment(YearMonth.of(2024, 11)),
                // Липсва октомври 2024
                createPayment(YearMonth.of(2024, 9)),
                createPayment(YearMonth.of(2024, 8)),
                createPayment(YearMonth.of(2024, 7))
        );
        when(insurancePaymentRepository.findByPatientId(PATIENT_ID)).thenReturn(payments);

        // Изпълнение
        boolean result = insuranceService.isInsuredAt(PATIENT_ID, examDate);

        // Проверка - пациентът НЕ е здравноосигурен
        assertFalse(result);
    }

    @Test
    @DisplayName("isInsuredAt - връща false когато няма никакви вноски")
    void isInsuredAt_noPayments_returnsFalse() {
        // Подготовка - пациентът няма нито една вноска
        LocalDate examDate = LocalDate.of(2025, 1, 15);
        when(insurancePaymentRepository.findByPatientId(PATIENT_ID)).thenReturn(Collections.emptyList());

        // Изпълнение
        boolean result = insuranceService.isInsuredAt(PATIENT_ID, examDate);

        // Проверка
        assertFalse(result);
    }

    @Test
    @DisplayName("isInsuredAt - връща false когато вноските са от друг период")
    void isInsuredAt_paymentsFromDifferentPeriod_returnsFalse() {
        // Подготовка - вноските са от 2023 г., а прегледът е през 2025 г.
        LocalDate examDate = LocalDate.of(2025, 1, 15);
        List<InsurancePayment> payments = Arrays.asList(
                createPayment(YearMonth.of(2023, 6)),
                createPayment(YearMonth.of(2023, 5)),
                createPayment(YearMonth.of(2023, 4)),
                createPayment(YearMonth.of(2023, 3)),
                createPayment(YearMonth.of(2023, 2)),
                createPayment(YearMonth.of(2023, 1))
        );
        when(insurancePaymentRepository.findByPatientId(PATIENT_ID)).thenReturn(payments);

        // Изпълнение
        boolean result = insuranceService.isInsuredAt(PATIENT_ID, examDate);

        // Проверка
        assertFalse(result);
    }

    @Test
    @DisplayName("findByPatient - връща вноските на конкретен пациент")
    void findByPatient_returnsPaymentsForPatient() {
        // Подготовка
        List<InsurancePayment> expectedPayments = Arrays.asList(
                createPayment(YearMonth.of(2024, 12)),
                createPayment(YearMonth.of(2024, 11))
        );
        when(insurancePaymentRepository.findByPatientId(PATIENT_ID)).thenReturn(expectedPayments);

        // Изпълнение
        List<InsurancePayment> result = insuranceService.findByPatient(PATIENT_ID);

        // Проверка
        assertEquals(2, result.size());
        verify(insurancePaymentRepository).findByPatientId(PATIENT_ID);
    }

    // Помощен метод за създаване на тестова вноска
    private InsurancePayment createPayment(YearMonth month) {
        InsurancePayment payment = new InsurancePayment(testPatient, month);
        return payment;
    }
}
