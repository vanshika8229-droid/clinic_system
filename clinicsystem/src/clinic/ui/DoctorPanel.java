package clinic.ui;

import clinic.model.*;
import clinic.service.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DoctorPanel extends JPanel {

    private final DoctorService service = new DoctorService();
    private DefaultTableModel tableModel;
    private JTable table;

    // Form fields
    private JTextField tfName, tfSpec, tfPhone, tfEmail;

    public DoctorPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Doctor Management");
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

        card.add(Theme.boldLabel("Add New Doctor"));
        card.add(Box.createVerticalStrut(14));

        tfName  = Theme.styledField(); tfSpec  = Theme.styledField();
        tfPhone = Theme.styledField(); tfEmail = Theme.styledField();

        card.add(Theme.label("Full Name *")); card.add(Box.createVerticalStrut(4));
        card.add(tfName);  card.add(Box.createVerticalStrut(10));
        card.add(Theme.label("Specialization *")); card.add(Box.createVerticalStrut(4));
        card.add(tfSpec);  card.add(Box.createVerticalStrut(10));
        card.add(Theme.label("Phone (10 digits) *")); card.add(Box.createVerticalStrut(4));
        card.add(tfPhone); card.add(Box.createVerticalStrut(10));
        card.add(Theme.label("Email")); card.add(Box.createVerticalStrut(4));
        card.add(tfEmail); card.add(Box.createVerticalStrut(16));

        JButton btnAdd = Theme.primaryButton("Add Doctor");
        btnAdd.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnAdd.addActionListener(e -> addDoctor());
        card.add(btnAdd);

        return card;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        String[] cols = {"ID", "Name", "Specialization", "Phone", "Email", "Active"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);

        JScrollPane sp = Theme.scrollPane(table);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        JButton btnRemove = Theme.dangerButton("Remove");
        btnRemove.setPreferredSize(new Dimension(100, 32));
        btnRemove.addActionListener(e -> removeDoctor());
        btnRow.add(btnRemove);

        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, 8));
        card.add(Theme.boldLabel("All Doctors"), BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);

        return card;
    }

    private void addDoctor() {
        String name  = tfName.getText().trim();
        String spec  = tfSpec.getText().trim();
        String phone = tfPhone.getText().trim();
        String email = tfEmail.getText().trim();

        String err = service.addDoctor(name, spec, phone, email);
        if (err != null) { Theme.showError(this, err); return; }

        Theme.showSuccess(this, "Doctor added successfully!");
        tfName.setText(""); tfSpec.setText(""); tfPhone.setText(""); tfEmail.setText("");
        refresh();
    }

    private void removeDoctor() {
        int row = table.getSelectedRow();
        if (row < 0) { Theme.showError(this, "Select a doctor to remove."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove this doctor (soft delete)?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            service.removeDoctor(id);
            refresh();
        }
    }

    private void styleTable(JTable t) {
        t.setFont(Theme.FONT_BODY);
        t.setRowHeight(30);
        t.setGridColor(Theme.BORDER);
        t.getTableHeader().setFont(Theme.FONT_BOLD);
        t.getTableHeader().setBackground(Theme.BG_PRIMARY);
        t.getTableHeader().setForeground(Theme.TEXT_SECONDARY);
        t.setSelectionBackground(new Color(0xDBEAFE));
    }

    public void refresh() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        List<Doctor> doctors = service.getAllDoctors();
        for (Doctor d : doctors) {
            tableModel.addRow(new Object[]{
                    d.getId(), d.getName(), d.getSpecialization(),
                    d.getPhone(), d.getEmail(), d.isActive() ? "Yes" : "No"
            });
        }
    }
}
