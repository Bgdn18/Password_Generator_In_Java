import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PasswordService {
    private static final int PASSWORD_LENGTH = 12;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_";
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public String generatePassword() {
        SecureRandom passwordrandom = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password.append(CHARACTERS.charAt(passwordrandom.nextInt(CHARACTERS.length())));
        }
        return password.toString();
    }

    public Path savePassword(File folder, String password) throws IOException {
        String timestamp = LocalDateTime.now().format(FILE_DATE_FORMAT);
        Path filePath = Path.of(folder.getAbsolutePath(), "password_" + timestamp + ".txt");
        Files.writeString(filePath, "Ваш пароль: " + password);
        return filePath;
    }
}
