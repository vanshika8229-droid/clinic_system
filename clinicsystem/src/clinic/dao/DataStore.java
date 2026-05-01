package clinic.dao;

import clinic.model.*;
import java.io.*;
import java.util.*;

/**
 * Central in-memory + file-backed data store.
 * Mimics a database: all DAO classes read/write through here.
 * Data is persisted to a binary file so it survives restarts.
 */
@SuppressWarnings("unchecked")
public class DataStore {

    private static final String DATA_FILE = "clinic_data.dat";
    private static DataStore instance;

    private Map<Integer, Doctor>      doctors      = new LinkedHashMap<>();
    private Map<Integer, Patient>     patients     = new LinkedHashMap<>();
    private Map<Integer, Schedule>    schedules    = new LinkedHashMap<>();
    private Map<Integer, Appointment> appointments = new LinkedHashMap<>();
    private Map<Integer, VisitRecord> visitRecords = new LinkedHashMap<>();

    private int doctorSeq      = 1;
    private int patientSeq     = 1;
    private int scheduleSeq    = 1;
    private int appointmentSeq = 1;
    private int visitSeq       = 1;

    private DataStore() {
        load();
    }

    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    // ── Sequence generators ─────────────────────────────────────
    public int nextDoctorId()      { return doctorSeq++; }
    public int nextPatientId()     { return patientSeq++; }
    public int nextScheduleId()    { return scheduleSeq++; }
    public int nextAppointmentId() { return appointmentSeq++; }
    public int nextVisitId()       { return visitSeq++; }

    // ── Map accessors ────────────────────────────────────────────
    public Map<Integer, Doctor>      getDoctors()      { return doctors; }
    public Map<Integer, Patient>     getPatients()     { return patients; }
    public Map<Integer, Schedule>    getSchedules()    { return schedules; }
    public Map<Integer, Appointment> getAppointments() { return appointments; }
    public Map<Integer, VisitRecord> getVisitRecords() { return visitRecords; }

    // ── Persistence ──────────────────────────────────────────────
    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(doctors);
            oos.writeObject(patients);
            oos.writeObject(schedules);
            oos.writeObject(appointments);
            oos.writeObject(visitRecords);
            oos.writeInt(doctorSeq);
            oos.writeInt(patientSeq);
            oos.writeInt(scheduleSeq);
            oos.writeInt(appointmentSeq);
            oos.writeInt(visitSeq);
        } catch (IOException e) {
            System.err.println("DataStore save error: " + e.getMessage());
        }
    }

    private void load() {
        File f = new File(DATA_FILE);
        if (!f.exists()) {
            seedData();
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            doctors      = (Map<Integer, Doctor>)      ois.readObject();
            patients     = (Map<Integer, Patient>)     ois.readObject();
            schedules    = (Map<Integer, Schedule>)    ois.readObject();
            appointments = (Map<Integer, Appointment>) ois.readObject();
            visitRecords = (Map<Integer, VisitRecord>) ois.readObject();
            doctorSeq      = ois.readInt();
            patientSeq     = ois.readInt();
            scheduleSeq    = ois.readInt();
            appointmentSeq = ois.readInt();
            visitSeq       = ois.readInt();
        } catch (Exception e) {
            System.err.println("DataStore load error: " + e.getMessage() + " — seeding fresh data.");
            seedData();
        }
    }

    private void seedData() {
        // Seed two doctors
        Doctor d1 = new Doctor(nextDoctorId(), "Arun Kumar",    "General Physician", "9841000001", "arun@clinic.com");
        Doctor d2 = new Doctor(nextDoctorId(), "Priya Sharma",  "Cardiologist",      "9841000002", "priya@clinic.com");
        Doctor d3 = new Doctor(nextDoctorId(), "Ramesh Iyer",   "Dermatologist",     "9841000003", "ramesh@clinic.com");
        doctors.put(d1.getId(), d1);
        doctors.put(d2.getId(), d2);
        doctors.put(d3.getId(), d3);

        // Seed schedules
        Schedule s1 = new Schedule(nextScheduleId(), 1, "Monday",    "09:00", "13:00", 30);
        Schedule s2 = new Schedule(nextScheduleId(), 1, "Wednesday", "09:00", "13:00", 30);
        Schedule s3 = new Schedule(nextScheduleId(), 2, "Tuesday",   "10:00", "16:00", 30);
        Schedule s4 = new Schedule(nextScheduleId(), 3, "Thursday",  "09:00", "14:00", 30);
        schedules.put(s1.getId(), s1);
        schedules.put(s2.getId(), s2);
        schedules.put(s3.getId(), s3);
        schedules.put(s4.getId(), s4);

        // Seed one patient
        Patient p1 = new Patient(nextPatientId(), "Karthik Raj", 32, "Male",   "9500000001", "karthik@mail.com", "Chennai");
        Patient p2 = new Patient(nextPatientId(), "Meena Devi",  27, "Female", "9500000002", "meena@mail.com",   "Tambaram");
        patients.put(p1.getId(), p1);
        patients.put(p2.getId(), p2);

        save();
    }
}
