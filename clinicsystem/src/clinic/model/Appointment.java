package clinic.model;

import java.io.Serializable;

public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status { BOOKED, CANCELLED, COMPLETED, RESCHEDULED }

    private int id;
    private int patientId;
    private int doctorId;
    private String date;        // "YYYY-MM-DD"
    private String timeSlot;    // "HH:MM"
    private String reason;
    private Status status;
    private String notes;

    public Appointment() {}

    public Appointment(int id, int patientId, int doctorId, String date, String timeSlot, String reason) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.timeSlot = timeSlot;
        this.reason = reason;
        this.status = Status.BOOKED;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Appointment[" + id + "] " + date + " " + timeSlot + " - " + status;
    }
}
