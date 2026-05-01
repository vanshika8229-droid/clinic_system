package clinic.dao;

import clinic.model.Doctor;
import java.util.*;
import java.util.stream.Collectors;

public class DoctorDao {

    private final DataStore store = DataStore.getInstance();

    public void save(Doctor doctor) {
        if (doctor.getId() == 0) {
            doctor.setId(store.nextDoctorId());
        }
        store.getDoctors().put(doctor.getId(), doctor);
        store.save();
    }

    public Doctor findById(int id) {
        return store.getDoctors().get(id);
    }

    public List<Doctor> findAll() {
        return new ArrayList<>(store.getDoctors().values());
    }

    public List<Doctor> findActive() {
        return store.getDoctors().values().stream()
                .filter(Doctor::isActive)
                .collect(Collectors.toList());
    }

    public void delete(int id) {
        Doctor d = store.getDoctors().get(id);
        if (d != null) {
            d.setActive(false);   // soft delete
            store.save();
        }
    }

    public void update(Doctor doctor) {
        store.getDoctors().put(doctor.getId(), doctor);
        store.save();
    }
}
