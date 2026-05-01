package bg.medicalrecord.dto.report;

import bg.medicalrecord.model.Doctor;

// DTO за справка: брой посещения при лекар
public class DoctorVisitCountDto {
    private Doctor doctor;
    private long visitCount;

    public DoctorVisitCountDto(Doctor doctor, long visitCount) {
        this.doctor = doctor;
        this.visitCount = visitCount;
    }

    public Doctor getDoctor() { return doctor; }
    public long getVisitCount() { return visitCount; }
}
