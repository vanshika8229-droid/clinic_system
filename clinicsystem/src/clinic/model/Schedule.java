package clinic.model;

import java.io.Serializable;

public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int doctorId;
    private String dayOfWeek;   // e.g. "Monday"
    private String startTime;   // e.g. "09:00"
    private String endTime;     // e.g. "17:00"
    private int slotDurationMinutes;

    public Schedule() {}

    public Schedule(int id, int doctorId, String dayOfWeek, String startTime, String endTime, int slotDurationMinutes) {
        this.id = id;
        this.doctorId = doctorId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotDurationMinutes = slotDurationMinutes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public int getSlotDurationMinutes() { return slotDurationMinutes; }
    public void setSlotDurationMinutes(int slotDurationMinutes) { this.slotDurationMinutes = slotDurationMinutes; }

    @Override
    public String toString() {
        return dayOfWeek + ": " + startTime + " - " + endTime + " (" + slotDurationMinutes + " min slots)";
    }
}
