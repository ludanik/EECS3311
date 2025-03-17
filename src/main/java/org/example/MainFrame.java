package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

// Main frame class
class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private DashboardPanel dashboardPanel;
    private User currentUser;

    // Simulated user database
    private Map<String, User> userDatabase;

    public MainFrame() {
        setTitle("YorkU Parking Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Initialize simulated database
        userDatabase = new HashMap<>();
        // Add a default manager account
        userDatabase.put("admin@yorku.ca", new User("admin@yorku.ca", "meow", "x", UserType.MANAGER, false));
        //    public User(String email, String username, String password, UserType userType, boolean validation) {
        // Initialize UI components
        initComponents();
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create panels
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        dashboardPanel = new DashboardPanel(this);

        // Add panels to card layout
        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(registerPanel, "REGISTER");
        mainPanel.add(dashboardPanel, "DASHBOARD");

        // Show login panel by default
        cardLayout.show(mainPanel, "LOGIN");

        // Add main panel to frame
        add(mainPanel);
    }

    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    public boolean authenticateUser(String email, String password) {
        User user = userDatabase.get(email);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            dashboardPanel.updateForUser(currentUser);
            return true;
        }
        return false;
    }

    public boolean registerUser(String email, String password, UserType userType) {
        // Check if user already exists
        if (userDatabase.containsKey(email)) {
            return false;
        }

        // Validate password strength
        if (!isPasswordStrong(password)) {
            return false;
        }

        // Create and store new user
        User newUser = new User(email, "", password, userType, false);
        userDatabase.put(email, newUser);

        // For non-visitor accounts, set pending validation status
        if (userType != UserType.VISITOR) {
            newUser.setPendingValidation(true);
        }

        return true;
    }

    private boolean isPasswordStrong(String password) {
        // Password must contain uppercase, lowercase, number, and symbol
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
        boolean isLongEnough = password.length() >= 8;

        return hasUppercase && hasLowercase && hasDigit && hasSpecial && isLongEnough;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
