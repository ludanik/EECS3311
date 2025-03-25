package EECS3311.UI;

import EECS3311.DAO.UserDAO;
import EECS3311.Models.User;
import EECS3311.Models.UserType;

import javax.swing.*;
import java.awt.*;

public class SuperManagerPanel extends JPanel {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton createManagerButton;

    public SuperManagerPanel() {
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel("Super Manager - Create Manager Account", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Manager Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Confirm Password:"), gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);

        // Create Manager button
        gbc.gridx = 1;
        gbc.gridy = 3;
        createManagerButton = new JButton("Create Manager Account");
        createManagerButton.addActionListener(e -> createManager());
        formPanel.add(createManagerButton, gbc);

        // Place form in center
        add(formPanel, BorderLayout.CENTER);
    }

    private void createManager() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "All fields must be filled.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Optionally do password strength checks here (like in MainFrame.isPasswordStrong).
        // If you'd like to replicate that check, you can call that method if it’s static
        // or just copy/paste the logic.

        // Create the new Manager user
        // By default, a Manager user has status=APPROVED (based on your enum).
        // Also ensure we set pendingValidation = false so they can log in immediately.
        User managerUser = new User(email, password, UserType.MANAGER, false);

        // Check if user already exists
        if (UserDAO.getUser(email) != null) {
            JOptionPane.showMessageDialog(
                    this,
                    "A user with this email already exists.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Add user
        UserDAO.addUser(managerUser);

        JOptionPane.showMessageDialog(
                this,
                "Manager account created successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Clear fields
        emailField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }
}