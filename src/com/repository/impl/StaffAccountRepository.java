package com.repository.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import com.model.User;
import com.model.enums.UserRole;
import com.util.CsvFileLock;

public final class StaffAccountRepository {
    private final Path dataDirectory;
    private final UserRepository userRepository;

    public StaffAccountRepository() {
        this(Paths.get(System.getProperty("stadium.data.dir", "data")));
    }

    public StaffAccountRepository(Path dataDirectory) {
        this.dataDirectory = dataDirectory.toAbsolutePath().normalize();
        this.userRepository = new UserRepository(this.dataDirectory);
        migrateLegacyAccounts();
    }

    public Path getDataDirectory() {
        return dataDirectory;
    }

    public User findByUsername(String username) {
        if (username == null) {
            return null;
        }
        String normalized = username.trim().toLowerCase(Locale.ROOT);
        for (User user : userRepository.findAll()) {
            if (normalized.equals(user.getUsername().trim().toLowerCase(Locale.ROOT))) {
                return user;
            }
        }
        return null;
    }

    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        for (User user : userRepository.findAll()) {
            if (user.getEmail() != null && normalized.equals(user.getEmail().trim().toLowerCase(Locale.ROOT))) {
                return user;
            }
        }
        return null;
    }

    public User findByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        String cleanPhone = phone.trim().replaceAll("[^0-9]", "");
        if (cleanPhone.isEmpty()) {
            return null;
        }
        for (User user : userRepository.findAll()) {
            String uPhone = user.getPhoneNumber() != null ? user.getPhoneNumber().trim().replaceAll("[^0-9]", "") : "";
            if (cleanPhone.equals(uPhone)) {
                return user;
            }
        }
        return null;
    }

    public User findById(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return null;
        }
        for (User user : userRepository.findAll()) {
            if (userId.trim().equalsIgnoreCase(user.getUserId())) {
                return user;
            }
        }
        return null;
    }

    public boolean hasRole(UserRole role) {
        return !findByRole(role).isEmpty();
    }

    public List<User> findByRole(UserRole role) {
        List<User> result = new ArrayList<User>();
        if (role == null) {
            return result;
        }
        for (User user : userRepository.findAll()) {
            if (role.name().equalsIgnoreCase(user.getRole())) {
                result.add(user);
            }
        }
        return result;
    }

    public boolean save(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }
        return CsvFileLock.withLock(dataDirectory.resolve(".users-write.lock"), () -> {
            if (findByUsername(user.getUsername()) != null) {
                return false;
            }
            return userRepository.save(user);
        });
    }

    public boolean saveInitialAdmin(User admin) {
        if (admin == null || !UserRole.ADMIN.name().equalsIgnoreCase(admin.getRole())) {
            return false;
        }
        return CsvFileLock.withLock(dataDirectory.resolve(".users-write.lock"), () -> {
            if (hasRole(UserRole.ADMIN) || findByUsername(admin.getUsername()) != null) {
                return false;
            }
            return userRepository.save(admin);
        });
    }

    private void migrateLegacyAccounts() {
        Path legacyPath = dataDirectory.resolve("accounts.properties");
        if (!Files.exists(legacyPath)) {
            return;
        }
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(legacyPath)) {
            properties.load(input);
            int count = Integer.parseInt(properties.getProperty("account.count", "0"));
            for (int i = 0; i < count; i++) {
                String prefix = "account." + i + ".";
                User user = new User(properties.getProperty(prefix + "id", ""),
                        properties.getProperty(prefix + "username", ""),
                        properties.getProperty(prefix + "passwordHash", ""),
                        properties.getProperty(prefix + "email", ""),
                        properties.getProperty(prefix + "role", ""));
                if (!user.getUsername().trim().isEmpty()) {
                    save(user);
                }
            }
        } catch (IOException | NumberFormatException exception) {
            throw new IllegalStateException("Unable to migrate legacy account data.", exception);
        }
    }
}