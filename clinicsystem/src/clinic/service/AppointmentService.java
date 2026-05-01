package clinic.service;

import clinic.dao.*;
import clinic.model.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

/**
 * Core business logic for appointment scheduling.
 * Handles conflict checking, slot validation, booking, rescheduling, cancellation.
 */
public class AppointmentService {

    private final AppointmentDao appointmentDao = new AppointmentDao();
    private final ScheduleDao    scheduleDao    = new ScheduleDao();
    private final DoctorDao      doctorDao      = new DoctorDao();
    private final PatientDao     patientDao     = new PatientDao();

    /**
     * Book an appointment. Returns null on success, or error message string.
     */
    public String bookAppointment(int patientId, int doctorId, String date, String timeSlot, String reason) {
        // 1. Validate patient & doctor exist
        if (patientDao.findById(patientId) == null) return "Patient not found.";
        if (doctorDao.findById(doctorId) == null)   return "Doctor not found.";

        // 2. Validate date format
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            return "Invalid date format. Use YYYY-MM-DD.";
        }

        // 3. Date must not be in the past
        if (localDate.isBefore(LocalDate.now())) {
            return "Cannot book an appointment in the past.";
        }

        // 4. Check doctor schedule for that day
        String dayName = localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        List<Schedule> daySchedules = scheduleDao.findByDoctorAndDay(doctorId, dayName);
        if (daySchedules.isEmpty()) {
            return "Doctor is not available on " + dayName + ".";
        }

        // 5. Check time slot falls within schedule
        boolean slotValid = false;
        for (Schedule s : daySchedules) {
            if (isSlotInRange(timeSlot, s.getStartTime(), s.getEndTime(), s.getSlotDurationMinutes())) {
                slotValid = true;
                break;
            }
        }
        if (!slotValid) {
            return "Time slot " + timeSlot + " is outside doctor's schedule for " + dayName + ".";
        }

        // 6. Check for conflict — same doctor, same date, same time
        List<Appointment> existing = appointmentDao.findByDoctorAndDate(doctorId, date);
        for (Appointment a : existing) {
            if (a.getTimeSlot().equals(timeSlot)) {
                return "Slot " + timeSlot + " on " + date + " is already booked for this doctor.";
            }
        }

        // 7. All checks passed — book
        Appointment appt = new Appointment(0, patientId, doctorId, date, timeSlot, reason);
        appointmentDao.save(appt);
        return null; // null = success
    }

    /**
     * Cancel an existing appointment.
     */
    public String cancelAppointment(int appointmentId) {
        Appointment appt = appointmentDao.findById(appointmentId);
        if (appt == null) return "Appointment not found.";
        if (appt.getStatus() == Appointment.Status.CANCELLED) return "Already cancelled.";
        appt.setStatus(Appointment.Status.CANCELLED);
        appointmentDao.update(appt);
        return null;
    }

    /**
     * Reschedule: cancel old slot and book a new one atomically.
     */
    public String rescheduleAppointment(int appointmentId, String newDate, String newTimeSlot) {
        Appointment appt = appointmentDao.findById(appointmentId);
        if (appt == null) return "Appointment not found.";
        if (appt.getStatus() == Appointment.Status.CANCELLED) return "Cannot reschedule a cancelled appointment.";

        // Try booking new slot
        String error = bookAppointment(appt.getPatientId(), appt.getDoctorId(), newDate, newTimeSlot, appt.getReason());
        if (error != null) return error;

        // Cancel old
        appt.setStatus(Appointment.Status.RESCHEDULED);
        appointmentDao.update(appt);
        return null;
    }

    /**
     * Get available time slots for a doctor on a given date.
     */
    public List<String> getAvailableSlots(int doctorId, String date) {
        List<String> available = new ArrayList<>();
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            return available;
        }

        String dayName = localDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        List<Schedule> daySchedules = scheduleDao.findByDoctorAndDay(doctorId, dayName);
        if (daySchedules.isEmpty()) return available;

        // Already booked slots
        Set<String> booked = new HashSet<>();
        for (Appointment a : appointmentDao.findByDoctorAndDate(doctorId, date)) {
            booked.add(a.getTimeSlot());
        }

        for (Schedule s : daySchedules) {
            List<String> slots = generateSlots(s.getStartTime(), s.getEndTime(), s.getSlotDurationMinutes());
            for (String slot : slots) {
                if (!booked.contains(slot)) available.add(slot);
            }
        }
        return available;
    }

    // ── Helpers ──────────────────────────────────────────────────

    private boolean isSlotInRange(String slot, String start, String end, int duration) {
        List<String> slots = generateSlots(start, end, duration);
        return slots.contains(slot);
    }

    public List<String> generateSlots(String start, String end, int durationMinutes) {
        List<String> slots = new ArrayList<>();
        try {
            LocalTime s = LocalTime.parse(start);
            LocalTime e = LocalTime.parse(end);
            while (!s.plusMinutes(durationMinutes).isAfter(e)) {
                slots.add(s.format(DateTimeFormatter.ofPattern("HH:mm")));
                s = s.plusMinutes(durationMinutes);
            }
        } catch (Exception ignored) {}
        return slots;
    }
}
