package clinic.dao;

import clinic.model.Appointment;
import java.util.*;
import java.util.stream.Collectors;

public class AppointmentDao {

    private final DataStore store = DataStore.getInstance();

    public void save(Appointment appt) {
        if (appt.getId() == 0) {
            appt.setId(store.nextAppointmentId());
        }
        store.getAppointments().put(appt.getId(), appt);
        store.save();
    }

    public Appointment findById(int id) {
        return store.getAppointments().get(id);
    }

    public List<Appointment> findAll() {
        return new ArrayList<>(store.getAppointments().values());
    }

    public List<Appointment> findByDoctorId(int doctorId) {
        return store.getAppointments().values().stream()
                .filter(a -> a.getDoctorId() == doctorId)
                .collect(Collectors.toList());
    }

    public List<Appointment> findByPatientId(int patientId) {
        return store.getAppointments().values().stream()
                .filter(a -> a.getPatientId() == patientId)
                .collect(Collectors.toList());
    }

    public List<Appointment> findByDoctorAndDate(int doctorId, String date) {
        return store.getAppointments().values().stream()
                .filter(a -> a.getDoctorId() == doctorId
                        && a.getDate().equals(date)
                        && a.getStatus() == Appointment.Status.BOOKED)
                .collect(Collectors.toList());
    }

    public void update(Appointment appt) {
        store.getAppointments().put(appt.getId(), appt);
        store.save();
    }

    public void delete(int id) {
        store.getAppointments().remove(id);
        store.save();
    }
}
