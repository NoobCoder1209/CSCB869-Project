package bg.medicalrecord.dto.report;

// DTO за справка: месец с най-много болнични
public class MonthSickLeaveDto {
    private int month;
    private int year;
    private long count;

    public MonthSickLeaveDto(int month, int year, long count) {
        this.month = month;
        this.year = year;
        this.count = count;
    }

    public int getMonth() { return month; }
    public int getYear() { return year; }
    public long getCount() { return count; }
}
