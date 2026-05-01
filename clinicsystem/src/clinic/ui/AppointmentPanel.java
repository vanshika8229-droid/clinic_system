package clinic.ui;

import clinic.dao.*;
import clinic.model.*;
import clinic.service.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class AppointmentPanel extends JPanel {

    private final AppointmentService apptService = new AppointmentService();
    private final DoctorService      docService  = new DoctorService();
    private final PatientService     patService  = new PatientService();

    private DefaultTableModel tableModel;
    private JTable table;

    // Booking form
    private JComboBox<String> cbPatient, cbDoctor, cbSlot;
    private JTextField tfDate, tfReason;

    public AppointmentPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Appointment Booking");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        add(title, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildForm(), buildTablePanel());
        split.setDividerLocation(300);
        split.setBorder(null);
        split.setOpaque(false);
        add(split, BorderLayout.CENTER);

        refresh();
    }

    private JPanel buildForm() {
        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(280, 0));

        card.add(Theme.boldLabel("Book Appointment"));
        card.add(Box.createVerticalStrut(14));

        cbPatient = new JComboBox<>();  cbPatient.setFont(Theme.FONT_BODY);
        cbDoctor  = new JComboBox<>();  cbDoctor.setFont(Theme.FONT_BODY);
        cbSlot    = new JComboBox<>();  cbSlot.setFont(Theme.FONT_BODY);
        tfDate    = Theme.styledField();
        tfReason  = Theme.styledField();

        tfDate.setText(LocalDate.now().plusDays(1).toString());

        // Populate dropdowns
        for (Patient p : patService.getAllPatients())
            cbPatient.addItem(p.getId() + " - " + p.getName());
        for (Doctor d : docService.getAllDoctors())
            cbDoctor.addItem(d.getId() + " - Dr. " + d.getName());

        card.add(Theme.label("Patient *"));         card.add(Box.createVerticalStrut(4)); card.add(cbPatient); card.add(Box.createVerticalStrut(8));
        card.add(Theme.label("Doctor *"));          card.add(Box.createVerticalStrut(4)); card.add(cbDoctor);  card.add(Box.createVerticalStrut(8));
        card.add(Theme.label("Date (YYYY-MM-DD) *"));card.add(Box.createVerticalStrut(4)); card.add(tfDate);   card.add(Box.createVerticalStrut(8));

        JButton btnLoad = Theme.primaryButton("Load Slots");
        btnLoad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btnLoad.addActionListener(e -> loadSlots());
        card.add(btnLoad); card.add(Box.createVerticalStrut(8));

        card.add(Theme.label("Available Slot *"));  card.add(Box.createVerticalStrut(4)); card.add(cbSlot);    card.add(Box.createVerticalStrut(8));
        card.add(Theme.label("Reason / Notes"));    card.add(Box.createVerticalStrut(4)); card.add(tfReason);  card.add(Box.createVerticalStrut(16));

        JButton btnBook = Theme.successButton("Book Now");
        btnBook.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnBook.addActionListener(e -> bookAppointment());
        card.add(btnBook);

        return card;
    }

    private JPanel buildTablePanel() {
        String[] cols = {"ID", "Patient", "Doctor", "Date", "Time", "Reason", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(Theme.FONT_BODY);
        table.setRowHeight(30);
        table.setGridColor(Theme.BORDER);
        table.getTableHeader().setFont(Theme.FONT_BOLD);
        table.getTableHeader().setBackground(Theme.BG_PRIMARY);
        table.getTableHeader().setForeground(Theme.TEXT_SECONDARY);
        table.setSelectionBackground(new Color(0xDBEAFE));

        JScrollPane sp = Theme.scrollPane(table);

        // Action buttons
        JButton btnCancel     = Theme.dangerButton("Cancel");
        JButton btnReschedule = Theme.primaryButton("Reschedule");
        btnCancel.setPreferredSize(new Dimension(90, 32));
        btnReschedule.setPreferredSize(new Dimension(110, 32));
        btnCancel.addActionListener(e     -> cancelAppointment());
        btnReschedule.addActionListener(e -> rescheduleAppointment());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnCancel);
        btnRow.add(btnReschedule);

        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, 8));
        card.add(Theme.boldLabel("All Appointments"), BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private void loadSlots() {
        if (cbDoctor.getSelectedItem() == null) return;
        String date    = tfDate.getText().trim();
        int doctorId   = Integer.parseInt(((String) cbDoctor.getSelectedItem()).split(" - ")[0]);
        List<String> slots = apptService.getAvailableSlots(doctorId, date);
        cbSlot.removeAllItems();
        if (slots.isEmpty()) {
            cbSlot.addItem("-- No slots available --");
        } else {
            for (String s : slots) cbSlot.addItem(s);
        }
    }

    private void bookAppointment() {
        if (cbPatient.getSelectedItem() == null || cbDoctor.getSelectedItem() == null) {
            Theme.showError(this, "Select patient and doctor."); return;
        }
        if (cbSlot.getSelectedItem() == null || cbSlot.getSelectedItem().toString().startsWith("--")) {
            Theme.showError(this, "Load and select a valid slot."); return;
        }

        int patientId = Integer.parseInt(((String) cbPatient.getSelectedItem()).split(" - ")[0]);
        int doctorId  = Integer.parseInt(((String) cbDoctor.getSelectedItem()).split(" - ")[0]);
        String date   = tfDate.getText().trim();
        String slot   = (String) cbSlot.getSelectedItem();
        String reason = tfReason.getText().trim();

        String err = apptService.bookAppointment(patientId, doctorId, date, slot, reason);
        if (err != null) { Theme.showError(this, err); return; }

        Theme.showSuccess(this, "Appointment booked successfully!");
        tfReason.setText("");
        refresh();
    }

    private void cancelAppointment() {
        int row = table.getSelectedRow();
        if (row < 0) { Theme.showError(this, "Select an appointment to cancel."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Cancel this appointment?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String err = apptService.cancelAppointment(id);
            if (err != null) { Theme.showError(this, err); return; }
            Theme.showSuccess(this, "Appointment cancelled.");
            refresh();
        }
    }

    private void rescheduleAppointment() {
        int row = table.getSelectedRow();
        if (row < 0) { Theme.showError(this, "Select an appointment to reschedule."); return; }
        int id = (int) tableModel.getValueAt(row, 0);

        JTextField newDate = Theme.styledField();
        JTextField newTime = Theme.styledField();
        newDate.setText(LocalDate.now().plusDays(1).toString());
        newTime.setText("09:00");

        Object[] fields = {
                Theme.label("New Date (YYYY-MM-DD):"), newDate,
                Theme.label("New Time (HH:MM):"), newTime
        };
        int result = JOptionPane.showConfirmDialog(this, fields, "Reschedule Appointment", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String err = apptService.rescheduleAppointment(id, newDate.getText().trim(), newTime.getText().trim());
            if (err != null) { Theme.showError(this, err); return; }
            Theme.showSuccess(this, "Appointment rescheduled!");
            refresh();
        }
    }

    public void refresh() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);

        // Repopulate combos
        if (cbPatient != null) {
            cbPatient.removeAllItems();
            for (Patient p : patService.getAllPatients())
                cbPatient.addItem(p.getId() + " - " + p.getName());
        }
        if (cbDoctor != null) {
            cbDoctor.removeAllItems();
            for (Doctor d : docService.getAllDoctors())
                cbDoctor.addItem(d.getId() + " - Dr. " + d.getName());
        }

        PatientDao pd = new PatientDao();
        DoctorDao  dd = new DoctorDao();
        AppointmentDao ad = new AppointmentDao();

        for (Appointment a : ad.findAll()) {
            Patient pat = pd.findById(a.getPatientId());
            Doctor  doc = dd.findById(a.getDoctorId());
            tableModel.addRow(new Object[]{
                    a.getId(),
                    pat != null ? pat.getName() : "?",
                    doc != null ? "Dr. " + doc.getName() : "?",
                    a.getDate(), a.getTimeSlot(), a.getReason(), a.getStatus().name()
            });
        }
    }
}
