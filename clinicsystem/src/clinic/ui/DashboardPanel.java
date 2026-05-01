package clinic.ui;

import clinic.dao.*;
import clinic.model.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(Theme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        build();
    }

    private void build() {
        removeAll();

        // ── Header ──────────────────────────────────────────────
        JLabel title = new JLabel("Dashboard");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // ── Stat Cards ──────────────────────────────────────────
        JPanel cardsRow = new JPanel(new GridLayout(1, 4, 16, 0));
        cardsRow.setOpaque(false);

        DataStore ds = DataStore.getInstance();
        int totalDoctors   = (int) ds.getDoctors().values().stream().filter(Doctor::isActive).count();
        int totalPatients  = ds.getPatients().size();
        String today       = LocalDate.now().toString();
        int todayAppts     = (int) ds.getAppointments().values().stream()
                .filter(a -> a.getDate().equals(today) && a.getStatus() == Appointment.Status.BOOKED).count();
        int totalAppts     = ds.getAppointments().size();

        cardsRow.add(statCard("Doctors",      String.valueOf(totalDoctors),  Theme.ACCENT,   "👨‍⚕️"));
        cardsRow.add(statCard("Patients",     String.valueOf(totalPatients), Theme.SUCCESS,  "🧑‍🤝‍🧑"));
        cardsRow.add(statCard("Today",        todayAppts + " appts",         Theme.WARNING,  "📅"));
        cardsRow.add(statCard("Total Appts",  String.valueOf(totalAppts),    new Color(0x8B5CF6), "📋"));

        JPanel center = new JPanel(new BorderLayout(0, 16));
        center.setOpaque(false);
        center.add(cardsRow, BorderLayout.NORTH);

        // ── Recent Appointments Table ────────────────────────────
        JLabel recentTitle = Theme.boldLabel("Recent Appointments");
        recentTitle.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        String[] cols = {"#", "Patient", "Doctor", "Date", "Time", "Status"};
        Object[][] rows = buildRecentRows();
        JTable table = new JTable(rows, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        styleTable(table);

        JScrollPane sp = Theme.scrollPane(table);
        sp.setPreferredSize(new Dimension(0, 220));

        JPanel tableCard = Theme.card();
        tableCard.setLayout(new BorderLayout(0, 8));
        tableCard.add(recentTitle, BorderLayout.NORTH);
        tableCard.add(sp, BorderLayout.CENTER);

        center.add(tableCard, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel statCard(String label, String value, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));

        JLabel val = new JLabel(value);
        val.setFont(new Font("Segoe UI", Font.BOLD, 26));
        val.setForeground(color);

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.FONT_SMALL);
        lbl.setForeground(Theme.TEXT_SECONDARY);

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 2));
        text.setOpaque(false);
        text.add(val);
        text.add(lbl);

        card.add(ico,  BorderLayout.WEST);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private Object[][] buildRecentRows() {
        DataStore ds = DataStore.getInstance();
        List<Appointment> list = ds.getAppointments().values().stream()
                .sorted((a, b) -> b.getId() - a.getId())
                .limit(10)
                .collect(Collectors.toList());

        Object[][] rows = new Object[list.size()][6];
        DoctorDao dd  = new DoctorDao();
        PatientDao pd = new PatientDao();
        for (int i = 0; i < list.size(); i++) {
            Appointment a = list.get(i);
            Patient pat = pd.findById(a.getPatientId());
            Doctor  doc = dd.findById(a.getDoctorId());
            rows[i][0] = a.getId();
            rows[i][1] = pat != null ? pat.getName() : "?";
            rows[i][2] = doc != null ? "Dr. " + doc.getName() : "?";
            rows[i][3] = a.getDate();
            rows[i][4] = a.getTimeSlot();
            rows[i][5] = a.getStatus().name();
        }
        return rows;
    }

    private void styleTable(JTable t) {
        t.setFont(Theme.FONT_BODY);
        t.setRowHeight(30);
        t.setGridColor(Theme.BORDER);
        t.getTableHeader().setFont(Theme.FONT_BOLD);
        t.getTableHeader().setBackground(Theme.BG_PRIMARY);
        t.getTableHeader().setForeground(Theme.TEXT_SECONDARY);
        t.setSelectionBackground(new Color(0xDBEAFE));
        t.setFocusable(false);
    }

    public void refresh() {
        build();
    }
}
