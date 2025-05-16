import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PasswordGenerator {
    private static final int PASSWORD_LENGTH = 12;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_";
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final JFrame frame;
    private final JTextArea passwordArea;
    private final JLabel statusLabel;
    private File selectedFolder;
    private String currentPassword;


    public PasswordGenerator() {
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

        frame.setLocationRelativeTo(null); // Центрируем окно
        frame.setVisible(true);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return area;
    }

    private JLabel createStatusLabel() {
        JLabel label = new JLabel("Выберите папку для сохранения паролей", SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return label;
    }

    private void selectFolder(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Выберите папку для сохранения");

        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFolder = fileChooser.getSelectedFile();
            statusLabel.setText("Выбрана папка: " + selectedFolder.getAbsolutePath());
        }
    }

    private void generatePassword(ActionEvent e) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        currentPassword = password.toString();
        passwordArea.setText("Сгенерированный пароль:\n" + currentPassword);
        statusLabel.setText("Пароль сгенерирован. Нажмите 'Сохранить' для сохранения в файл.");
    }

    private void savePassword(ActionEvent e) {
        if (selectedFolder == null) {
            showError("Сначала выберите папку для сохранения!");
            return;
        }

        if (currentPassword == null || currentPassword.isEmpty()) {
            showError("Сначала сгенерируйте пароль!");
            return;
        }

        try {
            String timestamp = LocalDateTime.now().format(FILE_DATE_FORMAT);
            Path filePath = Path.of(selectedFolder.getAbsolutePath(), "password_" + timestamp + ".txt");

            Files.writeString(filePath, "Ваш пароль: " + currentPassword);

            statusLabel.setText("Пароль сохранен в файл: " + filePath.getFileName());
            currentPassword = null;
            passwordArea.setText("");
        } catch (IOException ex) {
            showError("Ошибка при сохранении файла: " + ex.getMessage());
        }
    }

    private void showError(String message) {
        statusLabel.setText("Ошибка: " + message);
        JOptionPane.showMessageDialog(frame, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Не удалось установить системный вид и ощущение: " + e.getMessage());
            }
            new PasswordGenerator();
        });
    }
}