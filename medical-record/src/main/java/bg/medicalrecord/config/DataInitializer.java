package bg.medicalrecord.config;

import bg.medicalrecord.model.*;
import bg.medicalrecord.model.enums.Role;
import bg.medicalrecord.model.enums.Specialty;
import bg.medicalrecord.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final InsurancePaymentRepository insurancePaymentRepository;
    private final ExaminationRepository examinationRepository;
    private final SickLeaveRepository sickLeaveRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           DoctorRepository doctorRepository,
                           PatientRepository patientRepository,
                           DiagnosisRepository diagnosisRepository,
                           InsurancePaymentRepository insurancePaymentRepository,
                           ExaminationRepository examinationRepository,
                           SickLeaveRepository sickLeaveRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.insurancePaymentRepository = insurancePaymentRepository;
        this.examinationRepository = examinationRepository;
        this.sickLeaveRepository = sickLeaveRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isPresent()) {
            return;
        }

        // ---- Users ----
        User adminUser   = userRepository.save(new User("admin",      passwordEncoder.encode("admin123"),   Role.ROLE_ADMIN));
        User userPetrov  = userRepository.save(new User("dr.petrov",  passwordEncoder.encode("doctor123"),  Role.ROLE_DOCTOR));
        User userDimitrov= userRepository.save(new User("dr.dimitrov",passwordEncoder.encode("doctor123"),  Role.ROLE_DOCTOR));
        User userTodorov = userRepository.save(new User("dr.todorov", passwordEncoder.encode("doctor123"),  Role.ROLE_DOCTOR));
        User userPat1    = userRepository.save(new User("patient1",   passwordEncoder.encode("patient123"), Role.ROLE_PATIENT));
        User userPat2    = userRepository.save(new User("patient2",   passwordEncoder.encode("patient123"), Role.ROLE_PATIENT));
        User userPat3    = userRepository.save(new User("patient3",   passwordEncoder.encode("patient123"), Role.ROLE_PATIENT));
        User userPat4    = userRepository.save(new User("patient4",   passwordEncoder.encode("patient123"), Role.ROLE_PATIENT));
        User userPat5    = userRepository.save(new User("patient5",   passwordEncoder.encode("patient123"), Role.ROLE_PATIENT));

        // ---- Doctors ----
        Doctor drPetrov   = new Doctor("1234567890", "Д-р Иван Петров",    Specialty.GENERAL_PRACTICE, true);
        drPetrov.setUser(userPetrov);
        drPetrov = doctorRepository.save(drPetrov);

        Doctor drDimitrov = new Doctor("2345678901", "Д-р Георги Димитров", Specialty.CARDIOLOGY,       false);
        drDimitrov.setUser(userDimitrov);
        drDimitrov = doctorRepository.save(drDimitrov);

        Doctor drTodorov  = new Doctor("3456789012", "Д-р Николай Тодоров", Specialty.NEUROLOGY,        true);
        drTodorov.setUser(userTodorov);
        drTodorov = doctorRepository.save(drTodorov);

        // ---- Patients ----
        Patient pat1 = new Patient("Петър Колев",      "8501011234");
        pat1.setUser(userPat1);
        pat1.setPersonalDoctor(drPetrov);
        pat1 = patientRepository.save(pat1);

        Patient pat2 = new Patient("Борис Стоянов",    "9002021345");
        pat2.setUser(userPat2);
        pat2.setPersonalDoctor(drPetrov);
        pat2 = patientRepository.save(pat2);

        Patient pat3 = new Patient("Димитър Начев",    "8803031456");
        pat3.setUser(userPat3);
        pat3.setPersonalDoctor(drTodorov);
        pat3 = patientRepository.save(pat3);

        Patient pat4 = new Patient("Стефан Попов",     "9504041567");
        pat4.setUser(userPat4);
        pat4.setPersonalDoctor(drTodorov);
        pat4 = patientRepository.save(pat4);

        Patient pat5 = new Patient("Красимир Василев", "9205051678");
        pat5.setUser(userPat5);
        pat5.setPersonalDoctor(drPetrov);
        pat5 = patientRepository.save(pat5);

        // ---- Diagnoses ----
        Diagnosis dJ069  = diagnosisRepository.save(new Diagnosis("J06.9", "Остра инфекция на горните дихателни пътища"));
        Diagnosis dI10   = diagnosisRepository.save(new Diagnosis("I10",   "Есенциална хипертония"));
        Diagnosis dE11   = diagnosisRepository.save(new Diagnosis("E11",   "Захарен диабет тип 2"));
        Diagnosis dM545  = diagnosisRepository.save(new Diagnosis("M54.5", "Болка в кръста"));
        Diagnosis dK297  = diagnosisRepository.save(new Diagnosis("K29.7", "Гастрит, неуточнен"));
        Diagnosis dJ45   = diagnosisRepository.save(new Diagnosis("J45",   "Астма"));
        Diagnosis dN390  = diagnosisRepository.save(new Diagnosis("N39.0", "Инфекция на пикочните пътища"));
        Diagnosis dG43   = diagnosisRepository.save(new Diagnosis("G43",   "Мигрена"));
        Diagnosis dF32   = diagnosisRepository.save(new Diagnosis("F32",   "Депресивен епизод"));
        Diagnosis dL309  = diagnosisRepository.save(new Diagnosis("L30.9", "Дерматит, неуточнен"));

        // ---- Insurance Payments ----
        // Patients 1, 2, 5: fully insured — all 12 months from 2024-07 to 2025-06
        for (Patient fullyInsured : List.of(pat1, pat2, pat5)) {
            YearMonth month = YearMonth.of(2024, 7);
            while (!month.isAfter(YearMonth.of(2025, 6))) {
                insurancePaymentRepository.save(new InsurancePayment(fullyInsured, month));
                month = month.plusMonths(1);
            }
        }

        // Patient 3: partially insured — 3 months only: 2025-04, 2025-05, 2025-06
        for (YearMonth month : List.of(YearMonth.of(2025, 4), YearMonth.of(2025, 5), YearMonth.of(2025, 6))) {
            insurancePaymentRepository.save(new InsurancePayment(pat3, month));
        }

        // Patient 4: no payments

        // ---- Examinations ----
        // Patients 1, 2, 5 are fully insured for all exams in Jan–Jun 2025  → paidByNhif = true
        // Patients 3, 4 are not insured                                      → paidByNhif = false

        Examination exam1  = saveExam(LocalDate.of(2025, 1, 15),  drPetrov,   pat1, dJ069, "Предписани антибиотици",                              new BigDecimal("40.00"), true);
        Examination exam2  = saveExam(LocalDate.of(2025, 2, 10),  drDimitrov, pat2, dI10,  "Контролен преглед, корекция на терапия",              new BigDecimal("60.00"), true);
        Examination exam3  = saveExam(LocalDate.of(2025, 2, 20),  drPetrov,   pat3, dK297, "Предписана диета и медикаменти",                      new BigDecimal("45.00"), false);
        Examination exam4  = saveExam(LocalDate.of(2025, 3, 5),   drTodorov,  pat4, dG43,  "Неврологичен преглед, предписан суматриптан",        new BigDecimal("55.00"), false);
        Examination exam5  = saveExam(LocalDate.of(2025, 3, 15),  drPetrov,   pat5, dE11,  "Проследяване на кръвна захар",                        new BigDecimal("40.00"), true);
        Examination exam6  = saveExam(LocalDate.of(2025, 4, 1),   drDimitrov, pat1, dI10,  "ЕКГ и ехокардиография",                               new BigDecimal("80.00"), true);
        Examination exam7  = saveExam(LocalDate.of(2025, 4, 10),  drTodorov,  pat2, dF32,  "Консултация, насочване към психолог",                 new BigDecimal("50.00"), true);
        Examination exam8  = saveExam(LocalDate.of(2025, 4, 20),  drPetrov,   pat3, dJ069, "Грипоподобно състояние, симптоматично лечение",       new BigDecimal("35.00"), false);
        Examination exam9  = saveExam(LocalDate.of(2025, 5, 5),   drPetrov,   pat4, dM545, "Физиотерапия и обезболяващи",                         new BigDecimal("45.00"), false);
        Examination exam10 = saveExam(LocalDate.of(2025, 5, 15),  drDimitrov, pat5, dI10,  "Холтер мониториране",                                  new BigDecimal("90.00"), true);
        Examination exam11 = saveExam(LocalDate.of(2025, 6, 1),   drTodorov,  pat1, dN390, "Антибиотична терапия",                                 new BigDecimal("40.00"), true);
        Examination exam12 = saveExam(LocalDate.of(2025, 6, 10),  drPetrov,   pat2, dL309, "Предписан крем и антихистамини",                       new BigDecimal("35.00"), true);

        // ---- Sick Leaves ----
        sickLeaveRepository.save(new SickLeave(exam1,  LocalDate.of(2025, 1, 15), 7));
        sickLeaveRepository.save(new SickLeave(exam4,  LocalDate.of(2025, 3, 5),  14));
        sickLeaveRepository.save(new SickLeave(exam8,  LocalDate.of(2025, 4, 20), 5));
        sickLeaveRepository.save(new SickLeave(exam9,  LocalDate.of(2025, 5, 5),  10));

        log.info("Seed data initialized");
    }

    private Examination saveExam(LocalDate date, Doctor doctor, Patient patient,
                                  Diagnosis diagnosis, String treatment,
                                  BigDecimal price, boolean paidByNhif) {
        Examination exam = new Examination();
        exam.setExaminationDate(date);
        exam.setDoctor(doctor);
        exam.setPatient(patient);
        exam.setDiagnosis(diagnosis);
        exam.setTreatment(treatment);
        exam.setPrice(price);
        exam.setPaidByNhif(paidByNhif);
        return examinationRepository.save(exam);
    }
}
