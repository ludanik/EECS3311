package EECS3311.UI;

import javax.swing.*;
import java.awt.*;
import EECS3311.Models.*;

class RegisterPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JComboBox<UserType> userTypeComboBox;

    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("User Type:"), gbc);

        gbc.gridx = 1;
        userTypeComboBox = new JComboBox<>(new UserType[] {
                UserType.STUDENT, UserType.FACULTY, UserType.STAFF, UserType.VISITOR
        });
        formPanel.add(userTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JLabel hintLabel = new JLabel("Password must include uppercase, lowercase, numbers, and symbols");
        formPanel.add(hintLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> onRegister());
        formPanel.add(registerButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        JButton loginLink = new JButton("Back to Login");
        loginLink.addActionListener(e -> mainFrame.showPanel("LOGIN"));
        formPanel.add(loginLink, gbc);

        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.add(formPanel);
        add(wrapperPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("Register New Account", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
    }

    private void onRegister() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        UserType userType = (UserType) userTypeComboBox.getSelectedItem();

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (mainFrame.registerUser(email, password, userType)) {
            emailField.setText("");
            passwordField.setText("");
            confirmPasswordField.setText("");

            String message = "Registration successful!";
            if (userType != UserType.VISITOR) {
                message += " Your account requires validation by management.";
            }
            JOptionPane.showMessageDialog(this, message, "Registration Success", JOptionPane.INFORMATION_MESSAGE);

            mainFrame.showPanel("LOGIN");
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Email may already be in use or password is not strong enough.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
