package clinic.dao;

import clinic.model.VisitRecord;
import java.util.*;
import java.util.stream.Collectors;

public class VisitRecordDao {

    private final DataStore store = DataStore.getInstance();

    public void save(VisitRecord record) {
        if (record.getId() == 0) {
            record.setId(store.nextVisitId());
        }
        store.getVisitRecords().put(record.getId(), record);
        store.save();
    }

    public VisitRecord findById(int id) {
        return store.getVisitRecords().get(id);
    }

    public List<VisitRecord> findAll() {
        return new ArrayList<>(store.getVisitRecords().values());
    }

    public List<VisitRecord> findByPatientId(int patientId) {
        return store.getVisitRecords().values().stream()
                .filter(v -> v.getPatientId() == patientId)
                .collect(Collectors.toList());
    }

    public List<VisitRecord> findByDoctorId(int doctorId) {
        return store.getVisitRecords().values().stream()
                .filter(v -> v.getDoctorId() == doctorId)
                .collect(Collectors.toList());
    }
}
