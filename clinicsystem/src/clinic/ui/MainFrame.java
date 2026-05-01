package clinic.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Main application window.
 * Sidebar navigation + content area using CardLayout.
 */
public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     contentArea = new JPanel(cardLayout);

    private DashboardPanel    dashPanel;
    private DoctorPanel       doctorPanel;
    private PatientPanel      patientPanel;
    private SchedulePanel     schedulePanel;
    private AppointmentPanel  apptPanel;
    private HistoryPanel      historyPanel;

    private JButton lastActive = null;

    public MainFrame() {
        setTitle("Clinic Appointment System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setMinimumSize(new Dimension(900, 560));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);
        add(buildContent(), BorderLayout.CENTER);

        // Start on dashboard
        navigate("dashboard");
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(Theme.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Logo / clinic name
        JPanel logoArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        logoArea.setOpaque(false);
        logoArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        JLabel logo = new JLabel("🏥 ClinicApp");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logo.setForeground(Color.WHITE);
        logoArea.add(logo);

        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(logoArea);
        sidebar.add(Box.createVerticalStrut(8));

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x2D3D50));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(12));

        // Nav items
        JButton btnDash      = navButton("📊  Dashboard",    "dashboard");
        JButton btnDoctors   = navButton("👨‍⚕️  Doctors",      "doctors");
        JButton btnPatients  = navButton("🧑  Patients",      "patients");
        JButton btnSchedules = navButton("📆  Schedules",     "schedules");
        JButton btnAppt      = navButton("📋  Appointments",  "appointments");
        JButton btnHistory   = navButton("🕘  History",       "history");

        sidebar.add(btnDash);
        sidebar.add(btnDoctors);
        sidebar.add(btnPatients);
        sidebar.add(btnSchedules);
        sidebar.add(btnAppt);
        sidebar.add(btnHistory);

        sidebar.add(Box.createVerticalGlue());

        // Version footer
        JLabel ver = new JLabel("  v1.0  |  Unit 06");
        ver.setFont(Theme.FONT_SMALL);
        ver.setForeground(new Color(0x475569));
        ver.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        sidebar.add(ver);

        return sidebar;
    }

    private JButton navButton(String label, String card) {
        JButton btn = new JButton(label);
        btn.setFont(Theme.FONT_NAV);
        btn.setForeground(Theme.SIDEBAR_TEXT);
        btn.setBackground(Theme.BG_SIDEBAR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setPreferredSize(new Dimension(200, 42));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        btn.addActionListener(e -> {
            navigate(card);
            setActiveButton(btn);
        });
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != lastActive) btn.setBackground(Theme.SIDEBAR_HOVER);
            }
            public void mouseExited(MouseEvent e) {
                if (btn != lastActive) btn.setBackground(Theme.BG_SIDEBAR);
            }
        });
        return btn;
    }

    private void setActiveButton(JButton btn) {
        if (lastActive != null) {
            lastActive.setBackground(Theme.BG_SIDEBAR);
            lastActive.setForeground(Theme.SIDEBAR_TEXT);
        }
        btn.setBackground(Theme.SIDEBAR_ACTIVE);
        btn.setForeground(Color.WHITE);
        lastActive = btn;
    }

    private JPanel buildContent() {
        dashPanel     = new DashboardPanel();
        doctorPanel   = new DoctorPanel();
        patientPanel  = new PatientPanel();
        schedulePanel = new SchedulePanel();
        apptPanel     = new AppointmentPanel();
        historyPanel  = new HistoryPanel();

        contentArea.setBackground(Theme.BG_PRIMARY);
        contentArea.add(dashPanel,     "dashboard");
        contentArea.add(doctorPanel,   "doctors");
        contentArea.add(patientPanel,  "patients");
        contentArea.add(schedulePanel, "schedules");
        contentArea.add(apptPanel,     "appointments");
        contentArea.add(historyPanel,  "history");

        return contentArea;
    }

    private void navigate(String card) {
        cardLayout.show(contentArea, card);
        // Refresh active panel
        switch (card) {
            case "dashboard":    dashPanel.refresh();    break;
            case "doctors":      doctorPanel.refresh();  break;
            case "patients":     patientPanel.refresh(); break;
            case "schedules":    schedulePanel.refresh();break;
            case "appointments": apptPanel.refresh();    break;
            case "history":      historyPanel.refresh(); break;
        }
    }
}
