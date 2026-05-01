package clinic.dao;

import clinic.model.Schedule;
import java.util.*;
import java.util.stream.Collectors;

public class ScheduleDao {

    private final DataStore store = DataStore.getInstance();

    public void save(Schedule schedule) {
        if (schedule.getId() == 0) {
            schedule.setId(store.nextScheduleId());
        }
        store.getSchedules().put(schedule.getId(), schedule);
        store.save();
    }

    public Schedule findById(int id) {
        return store.getSchedules().get(id);
    }

    public List<Schedule> findAll() {
        return new ArrayList<>(store.getSchedules().values());
    }

    public List<Schedule> findByDoctorId(int doctorId) {
        return store.getSchedules().values().stream()
                .filter(s -> s.getDoctorId() == doctorId)
                .collect(Collectors.toList());
    }

    public List<Schedule> findByDoctorAndDay(int doctorId, String day) {
        return store.getSchedules().values().stream()
                .filter(s -> s.getDoctorId() == doctorId && s.getDayOfWeek().equalsIgnoreCase(day))
                .collect(Collectors.toList());
    }

    public void delete(int id) {
        store.getSchedules().remove(id);
        store.save();
    }

    public void update(Schedule schedule) {
        store.getSchedules().put(schedule.getId(), schedule);
        store.save();
    }
}
