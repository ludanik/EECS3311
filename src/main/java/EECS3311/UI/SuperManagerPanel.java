package EECS3311.UI;

import EECS3311.DAO.UserDAO;
import EECS3311.Models.User;
import EECS3311.Models.UserType;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class SuperManagerAccountGenerationPanel extends JPanel {
    private JButton generateManagerButton;
    private JTextArea infoArea;

    public SuperManagerAccountGenerationPanel() {
        // Use BorderLayout for the outer panel
        setLayout(new BorderLayout());

        // Header label
        JLabel headerLabel = new JLabel("SuperManager - Generate Manager Accounts", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(headerLabel, BorderLayout.NORTH);

        // Create a center panel using GridBagLayout
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Text area to show info about generated accounts
        infoArea = new JTextArea(8, 30);
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        // Button to generate manager accounts
        generateManagerButton = new JButton("Generate Manager Account");
        generateManagerButton.addActionListener(e -> generateManagerAccount());

        // Layout configuration
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(generateManagerButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(new JScrollPane(infoArea), gbc);

        add(centerPanel, BorderLayout.CENTER);
    }

    // Generate a new Manager account with a random email and password
    private void generateManagerAccount() {
        // Generate random credentials
        String randomEmail = generateRandomEmail();
        String randomPassword = generateRandomPassword();

        // Create a new user object of type MANAGER
        User manager = new User(randomEmail, randomPassword, UserType.MANAGER, false);

        // Store the new user in the system
        UserDAO.addUser(manager);

        // Display account information
        String message = String.format(
            "A new Manager account has been created:\n\n" +
            "Email:    %s\n" +
            "Password: %s\n\n" +
            "Please record these credentials securely!",
            randomEmail, randomPassword
        );
        infoArea.setText(message);
    }

    // Generate a random email for demonstration purposes
    private String generateRandomEmail() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();

        // Append 8 random letters for the local part of the email
        for (int i = 0; i < 8; i++) {
            sb.append(alphabet.charAt(rand.nextInt(alphabet.length())));
        }
        sb.append("@yorku.ca");
        return sb.toString();
    }

    // Generate a simple random password containing uppercase, lowercase, digits, and symbols
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        Random rand = new Random();
        int length = 10;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
