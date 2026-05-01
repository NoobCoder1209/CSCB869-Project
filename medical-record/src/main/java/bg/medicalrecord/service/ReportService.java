package bg.medicalrecord.service;

import bg.medicalrecord.dto.report.*;
import bg.medicalrecord.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// Интерфейс за генериране на 11-те справки
public interface ReportService {

    // Справка 1: Пациенти с дадена диагноза
    List<Patient> getPatientsWithDiagnosis(Long diagnosisId);

    // Справка 2: Най-честа диагноза
    Optional<Diagnosis> getMostCommonDiagnosis();

    // Справка 3: Пациенти с даден личен лекар
    List<Patient> getPatientsByGp(Long doctorId);

    // Справка 4: Обща стойност на прегледи платени от пациенти
    BigDecimal getTotalPaidByPatients();

    // Справка 5: Стойност на прегледи платени от пациенти по лекар
    List<DoctorPaymentSummaryDto> getPaymentsPerDoctor();

    // Справка 6: Брой пациенти при всеки личен лекар
    List<GpPatientCountDto> getPatientCountPerGp();

    // Справка 7: Брой посещения при всеки лекар
    List<DoctorVisitCountDto> getVisitCountPerDoctor();

    // Справка 8: История на посещенията на пациент
    List<Examination> getPatientHistory(Long patientId);

    // Справка 9: Прегледи по лекар и/или период
    List<Examination> getExaminationsByDoctorAndPeriod(Long doctorId, LocalDate from, LocalDate to);

    // Справка 10: Месец с най-много болнични
    Optional<MonthSickLeaveDto> getMonthWithMostSickLeaves();

    // Справка 11: Лекар(и) с най-много болнични
    List<Doctor> getDoctorsWithMostSickLeaves();
}
