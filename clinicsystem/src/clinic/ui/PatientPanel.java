package clinic.ui;

import clinic.model.*;
import clinic.service.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientPanel extends JPanel {

    private final PatientService service = new PatientService();
    private DefaultTableModel tableModel;
    private JTable table;

    private JTextField tfName, tfAge, tfPhone, tfEmail, tfAddress;
    private JComboBox<String> cbGender;

    public PatientPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Theme.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JLabel title = new JLabel("Patient Management");
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

        card.add(Theme.boldLabel("Register Patient"));
        card.add(Box.createVerticalStrut(14));

        tfName    = Theme.styledField();
        tfAge     = Theme.styledField();
        tfPhone   = Theme.styledField();
        tfEmail   = Theme.styledField();
        tfAddress = Theme.styledField();
        cbGender  = Theme.styledCombo(new String[]{"Male", "Female", "Other"});

        card.add(Theme.label("Full Name *"));     card.add(Box.createVerticalStrut(4)); card.add(tfName);    card.add(Box.createVerticalStrut(8));
        card.add(Theme.label("Age *"));           card.add(Box.createVerticalStrut(4)); card.add(tfAge);     card.add(Box.createVerticalStrut(8));
        card.add(Theme.label("Gender *"));        card.add(Box.createVerticalStrut(4)); card.add(cbGender);  card.add(Box.createVerticalStrut(8));
        card.add(Theme.label("Phone (10 digits) *")); card.add(Box.createVerticalStrut(4)); card.add(tfPhone);   card.add(Box.createVerticalStrut(8));
        card.add(Theme.label("Email"));           card.add(Box.createVerticalStrut(4)); card.add(tfEmail);   card.add(Box.createVerticalStrut(8));
        card.add(Theme.label("Address"));         card.add(Box.createVerticalStrut(4)); card.add(tfAddress); card.add(Box.createVerticalStrut(16));

        JButton btnReg = Theme.primaryButton("Register");
        btnReg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnReg.addActionListener(e -> registerPatient());
        card.add(btnReg);

        return card;
    }

    private JPanel buildTablePanel() {
        String[] cols = {"ID", "Name", "Age", "Gender", "Phone", "Email", "Address"};
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

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        JButton btnDel = Theme.dangerButton("Delete");
        btnDel.setPreferredSize(new Dimension(100, 32));
        btnDel.addActionListener(e -> deletePatient());
        btnRow.add(btnDel);

        JPanel card = Theme.card();
        card.setLayout(new BorderLayout(0, 8));
        card.add(Theme.boldLabel("Registered Patients"), BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);
        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private void registerPatient() {
        String name    = tfName.getText().trim();
        String ageStr  = tfAge.getText().trim();
        String gender  = (String) cbGender.getSelectedItem();
        String phone   = tfPhone.getText().trim();
        String email   = tfEmail.getText().trim();
        String address = tfAddress.getText().trim();

        int age;
        try { age = Integer.parseInt(ageStr); }
        catch (NumberFormatException e) { Theme.showError(this, "Age must be a number."); return; }

        String err = service.registerPatient(name, age, gender, phone, email, address);
        if (err != null) { Theme.showError(this, err); return; }

        Theme.showSuccess(this, "Patient registered successfully!");
        tfName.setText(""); tfAge.setText(""); tfPhone.setText(""); tfEmail.setText(""); tfAddress.setText("");
        refresh();
    }

    private void deletePatient() {
        int row = table.getSelectedRow();
        if (row < 0) { Theme.showError(this, "Select a patient first."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this patient?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            service.deletePatient(id);
            refresh();
        }
    }

    public void refresh() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        for (Patient p : service.getAllPatients()) {
            tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getAge(), p.getGender(),
                    p.getPhone(), p.getEmail(), p.getAddress()
            });
        }
    }
}
