package bg.medicalrecord.service.impl;

import bg.medicalrecord.dto.report.*;
import bg.medicalrecord.model.*;
import bg.medicalrecord.repository.*;
import bg.medicalrecord.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Сервиз за генериране на 11-те справки
@Service
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final PatientRepository patientRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final ExaminationRepository examinationRepository;
    private final DoctorRepository doctorRepository;
    private final SickLeaveRepository sickLeaveRepository;

    public ReportServiceImpl(PatientRepository patientRepository,
                             DiagnosisRepository diagnosisRepository,
                             ExaminationRepository examinationRepository,
                             DoctorRepository doctorRepository,
                             SickLeaveRepository sickLeaveRepository) {
        this.patientRepository = patientRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.examinationRepository = examinationRepository;
        this.doctorRepository = doctorRepository;
        this.sickLeaveRepository = sickLeaveRepository;
    }

    // Справка 1: Пациенти с дадена диагноза
    @Override
    public List<Patient> getPatientsWithDiagnosis(Long diagnosisId) {
        return patientRepository.findByDiagnosis(diagnosisId);
    }

    // Справка 2: Най-честа диагноза
    @Override
    public Optional<Diagnosis> getMostCommonDiagnosis() {
        return diagnosisRepository.findMostCommon();
    }

    // Справка 3: Пациенти с даден личен лекар
    @Override
    public List<Patient> getPatientsByGp(Long doctorId) {
        return patientRepository.findByPersonalDoctorId(doctorId);
    }

    // Справка 4: Обща стойност на прегледи платени от пациенти
    @Override
    public BigDecimal getTotalPaidByPatients() {
        return examinationRepository.sumPaidByPatients();
    }

    // Справка 5: Стойност на прегледи платени от пациенти по лекар
    @Override
    public List<DoctorPaymentSummaryDto> getPaymentsPerDoctor() {
        return examinationRepository.sumPaidByPatientsPerDoctor().stream()
                .map(row -> new DoctorPaymentSummaryDto((Doctor) row[0], (BigDecimal) row[1]))
                .collect(Collectors.toList());
    }

    // Справка 6: Брой пациенти при всеки личен лекар
    @Override
    public List<GpPatientCountDto> getPatientCountPerGp() {
        return doctorRepository.countPatientsByGp().stream()
                .map(row -> new GpPatientCountDto((Doctor) row[0], (Long) row[1]))
                .collect(Collectors.toList());
    }

    // Справка 7: Брой посещения при всеки лекар
    @Override
    public List<DoctorVisitCountDto> getVisitCountPerDoctor() {
        return doctorRepository.countVisitsByDoctor().stream()
                .map(row -> new DoctorVisitCountDto((Doctor) row[0], (Long) row[1]))
                .collect(Collectors.toList());
    }

    // Справка 8: История на посещенията на пациент
    @Override
    public List<Examination> getPatientHistory(Long patientId) {
        return examinationRepository.findByPatientIdOrderByExaminationDateDesc(patientId);
    }

    // Справка 9: Прегледи по лекар и/или период
    @Override
    public List<Examination> getExaminationsByDoctorAndPeriod(Long doctorId, LocalDate from, LocalDate to) {
        return examinationRepository.findByDoctorAndPeriod(doctorId, from, to);
    }

    // Справка 10: Месец с най-много болнични
    @Override
    public Optional<MonthSickLeaveDto> getMonthWithMostSickLeaves() {
        List<Object[]> results = sickLeaveRepository.findMonthWithMostSickLeaves();
        if (results.isEmpty()) {
            return Optional.empty();
        }
        Object[] first = results.get(0);
        return Optional.of(new MonthSickLeaveDto(
                ((Number) first[0]).intValue(),
                ((Number) first[1]).intValue(),
                ((Number) first[2]).longValue()
        ));
    }

    // Справка 11: Лекар(и) с най-много болнични
    @Override
    public List<Doctor> getDoctorsWithMostSickLeaves() {
        List<Object[]> results = sickLeaveRepository.countSickLeavesPerDoctor();
        if (results.isEmpty()) {
            return List.of();
        }
        long maxCount = ((Number) results.get(0)[1]).longValue();
        return results.stream()
                .filter(row -> ((Number) row[1]).longValue() == maxCount)
                .map(row -> (Doctor) row[0])
                .collect(Collectors.toList());
    }
}
