package clinic.ui;

import clinic.dao.*;
import clinic.model.*;
import clinic.service.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryPanel extends JPanel {

    private final DoctorService  docService = new DoctorService();
    private final PatientService patService = new PatientService();

    private DefaultTableModel tableModel;
    private JTable table;

    private JComboBox<String> cbFilterType, cbFilterValue;
    private JLabel lblCount;

    public HistoryPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Appointment History");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        add(title, BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        refresh();
    }

    private JPanel buildContent() {
        JPanel outer = new JPanel(new BorderLayout(0, 12));
        outer.setOpaque(false);

        // Filter bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterBar.setOpaque(false);

        cbFilterType  = new JComboBox<>(new String[]{"All", "By Doctor", "By Patient"});
        cbFilterValue = new JComboBox<>();
        cbFilterType.setFont(Theme.FONT_BODY);
        cbFilterValue.setFont(Theme.FONT_BODY);
        lblCount = Theme.label("Total: 0 records");

        JButton btnFilter = Theme.primaryButton("Apply Filter");
        btnFilter.setPreferredSize(new Dimension(110, 32));
        btnFilter.addActionListener(e -> applyFilter());

        cbFilterType.addActionListener(e -> updateFilterValues());

        filterBar.add(Theme.boldLabel("Filter:"));
        filterBar.add(cbFilterType);
        filterBar.add(cbFilterValue);
        filterBar.add(btnFilter);
        filterBar.add(lblCount);

        // Table
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

        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, 10));
        card.add(filterBar, BorderLayout.NORTH);
        card.add(Theme.scrollPane(table), BorderLayout.CENTER);

        outer.add(card, BorderLayout.CENTER);
        return outer;
    }

    private void updateFilterValues() {
        cbFilterValue.removeAllItems();
        String type = (String) cbFilterType.getSelectedItem();
        if ("By Doctor".equals(type)) {
            for (Doctor d : docService.getAllDoctors())
                cbFilterValue.addItem(d.getId() + " - Dr. " + d.getName());
        } else if ("By Patient".equals(type)) {
            for (Patient p : patService.getAllPatients())
                cbFilterValue.addItem(p.getId() + " - " + p.getName());
        }
    }

    private void applyFilter() {
        tableModel.setRowCount(0);
        AppointmentDao ad = new AppointmentDao();
        PatientDao pd = new PatientDao();
        DoctorDao  dd = new DoctorDao();

        List<Appointment> list = ad.findAll();
        String type = (String) cbFilterType.getSelectedItem();

        if ("By Doctor".equals(type) && cbFilterValue.getSelectedItem() != null) {
            int did = Integer.parseInt(((String) cbFilterValue.getSelectedItem()).split(" - ")[0]);
            list = list.stream().filter(a -> a.getDoctorId() == did).collect(Collectors.toList());
        } else if ("By Patient".equals(type) && cbFilterValue.getSelectedItem() != null) {
            int pid = Integer.parseInt(((String) cbFilterValue.getSelectedItem()).split(" - ")[0]);
            list = list.stream().filter(a -> a.getPatientId() == pid).collect(Collectors.toList());
        }

        for (Appointment a : list) {
            Patient pat = pd.findById(a.getPatientId());
            Doctor  doc = dd.findById(a.getDoctorId());
            tableModel.addRow(new Object[]{
                    a.getId(),
                    pat != null ? pat.getName() : "?",
                    doc != null ? "Dr. " + doc.getName() : "?",
                    a.getDate(), a.getTimeSlot(), a.getReason(), a.getStatus().name()
            });
        }
        lblCount.setText("Total: " + list.size() + " records");
    }

    public void refresh() {
        updateFilterValues();
        applyFilter();
    }
}
