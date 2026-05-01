package bg.medicalrecord.dto.report;

import bg.medicalrecord.model.Doctor;

// DTO за справка: брой пациенти при личен лекар
public class GpPatientCountDto {
    private Doctor doctor;
    private long patientCount;

    public GpPatientCountDto(Doctor doctor, long patientCount) {
        this.doctor = doctor;
        this.patientCount = patientCount;
    }

    public Doctor getDoctor() { return doctor; }
    public long getPatientCount() { return patientCount; }
}
