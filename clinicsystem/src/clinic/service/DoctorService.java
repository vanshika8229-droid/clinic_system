package clinic.service;

import clinic.dao.DoctorDao;
import clinic.dao.ScheduleDao;
import clinic.model.Doctor;
import clinic.model.Schedule;
import java.util.List;

public class DoctorService {

    private final DoctorDao   doctorDao   = new DoctorDao();
    private final ScheduleDao scheduleDao = new ScheduleDao();

    public String addDoctor(String name, String specialization, String phone, String email) {
        if (name == null || name.trim().isEmpty())           return "Name is required.";
        if (specialization == null || specialization.trim().isEmpty()) return "Specialization is required.";
        if (phone == null || !phone.matches("\\d{10}"))      return "Phone must be 10 digits.";
        Doctor d = new Doctor(0, name.trim(), specialization.trim(), phone.trim(), email == null ? "" : email.trim());
        doctorDao.save(d);
        return null;
    }

    public String addSchedule(int doctorId, String day, String start, String end, int slotMin) {
        if (doctorDao.findById(doctorId) == null) return "Doctor not found.";
        Schedule s = new Schedule(0, doctorId, day, start, end, slotMin);
        scheduleDao.save(s);
        return null;
    }

    public List<Doctor> getAllDoctors() {
        return doctorDao.findActive();
    }

    public List<Schedule> getSchedulesForDoctor(int doctorId) {
        return scheduleDao.findByDoctorId(doctorId);
    }

    public void deleteSchedule(int scheduleId) {
        scheduleDao.delete(scheduleId);
    }

    public Doctor getDoctorById(int id) {
        return doctorDao.findById(id);
    }

    public void updateDoctor(Doctor d) {
        doctorDao.update(d);
    }

    public void removeDoctor(int id) {
        doctorDao.delete(id);
    }
}
