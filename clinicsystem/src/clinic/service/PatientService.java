package clinic.service;

import clinic.dao.PatientDao;
import clinic.model.Patient;
import java.util.List;

public class PatientService {

    private final PatientDao patientDao = new PatientDao();

    public String registerPatient(String name, int age, String gender, String phone, String email, String address) {
        if (name == null || name.trim().isEmpty())       return "Name is required.";
        if (age <= 0 || age > 150)                       return "Enter a valid age (1-150).";
        if (gender == null || gender.trim().isEmpty())   return "Gender is required.";
        if (phone == null || !phone.matches("\\d{10}"))  return "Phone must be 10 digits.";
        Patient p = new Patient(0, name.trim(), age, gender.trim(), phone.trim(),
                email == null ? "" : email.trim(), address == null ? "" : address.trim());
        patientDao.save(p);
        return null;
    }

    public List<Patient> getAllPatients() {
        return patientDao.findAll();
    }

    public Patient getPatientById(int id) {
        return patientDao.findById(id);
    }

    public void updatePatient(Patient p) {
        patientDao.update(p);
    }

    public void deletePatient(int id) {
        patientDao.delete(id);
    }
}
