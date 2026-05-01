import clinic.ui.MainFrame;
import javax.swing.*;

/**
 * Clinic Appointment System — Entry Point
 * Run: java -cp out Main
 */
public class Main {
    public static void main(String[] args) {
        // Set system look and feel for clean native rendering
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // fallback to default
        }

        // Override some UI defaults for a cleaner look
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 6);
        UIManager.put("ProgressBar.arc", 6);
        UIManager.put("TextComponent.arc", 6);

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
