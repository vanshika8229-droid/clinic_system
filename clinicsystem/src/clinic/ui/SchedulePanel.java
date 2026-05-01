package clinic.ui;

import clinic.model.*;
import clinic.service.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SchedulePanel extends JPanel {

    private final DoctorService service = new DoctorService();
    private DefaultTableModel tableModel;
    private JTable table;

    private JComboBox<String> cbDoctor, cbDay, cbStart, cbEnd, cbSlot;

    public SchedulePanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Schedule Management");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        add(title, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildForm(), buildTablePanel());
        split.setDividerLocation(280);
        split.setBorder(null);
        split.setOpaque(false);
        add(split, BorderLayout.CENTER);

        refresh();
    }

    private JPanel buildForm() {
        JPanel card = Theme.card();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(260, 0));

        card.add(Theme.boldLabel("Add Doctor Schedule"));
        card.add(Box.createVerticalStrut(14));

        String[] days  = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
        String[] times = {"08:00","08:30","09:00","09:30","10:00","10:30","11:00","11:30",
                          "12:00","12:30","13:00","13:30","14:00","14:30","15:00","15:30",
                          "16:00","16:30","17:00","17:30","18:00"};
        String[] slots = {"15","20","30","45","60"};

        cbDoctor = new JComboBox<>();
        cbDoctor.setFont(Theme.FONT_BODY);
        populateDoctors();

        cbDay   = Theme.styledCombo(days);
        cbStart = Theme.styledCombo(times);
        cbEnd   = Theme.styledCombo(times);
        cbSlot  = Theme.styledCombo(slots);

        // Default end to later time
        cbEnd.setSelectedIndex(8);

        card.add(Theme.label("Doctor *"));          card.add(Box.createVerticalStrut(4)); card.add(cbDoctor); card.add(Box.createVerticalStrut(8));
        card.add(Theme.label("Day of Week *"));     card.add(Box.createVerticalStrut(4)); card.add(cbDay);    card.add(Box.createVerticalStrut(8));
        card.add(Theme.label("Start Time *"));      card.add(Box.createVerticalStrut(4)); card.add(cbStart);  card.add(Box.createVerticalStrut(8));
        card.add(Theme.label("End Time *"));        card.add(Box.createVerticalStrut(4)); card.add(cbEnd);    card.add(Box.createVerticalStrut(8));
        card.add(Theme.label("Slot (minutes) *"));  card.add(Box.createVerticalStrut(4)); card.add(cbSlot);   card.add(Box.createVerticalStrut(16));

        JButton btnAdd = Theme.primaryButton("Add Schedule");
        btnAdd.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnAdd.addActionListener(e -> addSchedule());
        card.add(btnAdd);

        return card;
    }

    private JPanel buildTablePanel() {
        String[] cols = {"ID", "Doctor", "Day", "Start", "End", "Slot (min)"};
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

        JButton btnDel = Theme.dangerButton("Remove");
        btnDel.setPreferredSize(new Dimension(100, 32));
        btnDel.addActionListener(e -> removeSchedule());

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnDel);

        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, 8));
        card.add(Theme.boldLabel("All Schedules"), BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private void populateDoctors() {
        cbDoctor.removeAllItems();
        for (Doctor d : service.getAllDoctors()) {
            cbDoctor.addItem(d.getId() + " - Dr. " + d.getName());
        }
    }

    private void addSchedule() {
        if (cbDoctor.getSelectedItem() == null) { Theme.showError(this, "No doctors available."); return; }
        String doctorEntry = (String) cbDoctor.getSelectedItem();
        int doctorId = Integer.parseInt(doctorEntry.split(" - ")[0]);
        String day   = (String) cbDay.getSelectedItem();
        String start = (String) cbStart.getSelectedItem();
        String end   = (String) cbEnd.getSelectedItem();
        int slotMin  = Integer.parseInt((String) cbSlot.getSelectedItem());

        if (start.compareTo(end) >= 0) {
            Theme.showError(this, "End time must be after start time."); return;
        }

        String err = service.addSchedule(doctorId, day, start, end, slotMin);
        if (err != null) { Theme.showError(this, err); return; }

        Theme.showSuccess(this, "Schedule added!");
        refresh();
    }

    private void removeSchedule() {
        int row = table.getSelectedRow();
        if (row < 0) { Theme.showError(this, "Select a schedule to remove."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        service.deleteSchedule(id);
        refresh();
    }

    public void refresh() {
        if (tableModel == null) return;
        populateDoctors();
        tableModel.setRowCount(0);
        DoctorService ds = new DoctorService();
        for (Doctor d : ds.getAllDoctors()) {
            for (Schedule s : ds.getSchedulesForDoctor(d.getId())) {
                tableModel.addRow(new Object[]{
                        s.getId(), "Dr. " + d.getName(), s.getDayOfWeek(),
                        s.getStartTime(), s.getEndTime(), s.getSlotDurationMinutes()
                });
            }
        }
    }
}
