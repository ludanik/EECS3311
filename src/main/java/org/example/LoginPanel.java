package org.example;

import javax.swing.*;
import java.awt.*;

// Login Panel
class LoginPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 1;
        gbc.gridy = 2;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> onLogin());
        formPanel.add(loginButton, gbc);

        // Register link
        gbc.gridx = 1;
        gbc.gridy = 3;
        JButton registerLink = new JButton("Register new account");
        registerLink.addActionListener(e -> mainFrame.showPanel("REGISTER"));
        formPanel.add(registerLink, gbc);

        // Add form to panel
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.add(formPanel);
        add(wrapperPanel, BorderLayout.CENTER);

        // Add header
        JLabel titleLabel = new JLabel("YorkU Parking Booking System", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
    }

    private void onLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both email and password", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (mainFrame.authenticateUser(email, password)) {
            // Clear fields
            emailField.setText("");
            passwordField.setText("");

            // Show dashboard
            mainFrame.showPanel("DASHBOARD");
        } else {
            JOptionPane.showMessageDialog(this, "Invalid email or password", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}