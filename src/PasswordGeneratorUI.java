import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;

public class PasswordGeneratorUI {
    private final JFrame frame;
    private final JTextArea passwordArea;
    private final JLabel statusLabel;
    private File selectedFolder;
    private String currentPassword;
    private final PasswordService service;

    public PasswordGeneratorUI() {
        service = new PasswordService();

        frame = new JFrame("Password Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel buttonPanel = createButtonPanel();
        passwordArea = createPasswordArea();
        statusLabel = createStatusLabel();

        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(passwordArea), BorderLayout.CENTER);
        frame.add(statusLabel, BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton selectFolderButton = new JButton("1. Select Folder");
        selectFolderButton.addActionListener(this::selectFolder);

        JButton generateButton = new JButton("2. Generate Password");
        generateButton.addActionListener(this::generatePassword);

        JButton saveButton = new JButton("3. Save Password");
        saveButton.addActionListener(this::savePassword);

        panel.add(selectFolderButton);
        panel.add(generateButton);
        panel.add(saveButton);

        return panel;
    }

    private JTextArea createPasswordArea() {
        JTextArea area = new JTextArea(10, 30);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private JLabel createStatusLabel() {
        JLabel label = new JLabel("Выберите папку для сохранения паролей", SwingConstants.CENTER);
        return label;
    }

    private void selectFolder(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Выберите папку");

        int result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFolder = chooser.getSelectedFile();
            statusLabel.setText("Выбрана папка: " + selectedFolder.getAbsolutePath());
        }
    }

    private void generatePassword(ActionEvent e) {
        currentPassword = service.generatePassword();
        passwordArea.setText("Сгенерированный пароль:\n" + currentPassword);
        statusLabel.setText("Пароль сгенерирован. Нажмите 'Сохранить'.");
    }

    private void savePassword(ActionEvent e) {
        if (selectedFolder == null) {
            showError("Сначала выберите папку!");
            return;
        }
        if (currentPassword == null || currentPassword.isEmpty()) {
            showError("Сначала сгенерируйте пароль!");
            return;
        }
        try {
            Path savedPath = service.savePassword(selectedFolder, currentPassword);
            statusLabel.setText("Пароль сохранён: " + savedPath);
            passwordArea.setText("");
            currentPassword = null;
        } catch (Exception ex) {
            showError("Ошибка при сохранении: " + ex.getMessage());
        }
    }

    private void showError(String msg) {
        statusLabel.setText("Ошибка: " + msg);
        JOptionPane.showMessageDialog(frame, msg, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new PasswordGeneratorUI();
        });
    }
}
