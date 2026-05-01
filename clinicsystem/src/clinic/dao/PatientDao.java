package clinic.dao;

import clinic.model.Patient;
import java.util.*;

public class PatientDao {

    private final DataStore store = DataStore.getInstance();

    public void save(Patient patient) {
        if (patient.getId() == 0) {
            patient.setId(store.nextPatientId());
        }
        store.getPatients().put(patient.getId(), patient);
        store.save();
    }

    public Patient findById(int id) {
        return store.getPatients().get(id);
    }

    public List<Patient> findAll() {
        return new ArrayList<>(store.getPatients().values());
    }

    public void update(Patient patient) {
        store.getPatients().put(patient.getId(), patient);
        store.save();
    }

    public void delete(int id) {
        store.getPatients().remove(id);
        store.save();
    }
}
