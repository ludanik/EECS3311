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
        setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("SuperManager - Generate Manager Accounts", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(headerLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        infoArea = new JTextArea(8, 30);
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        generateManagerButton = new JButton("Generate Manager Account");
        generateManagerButton.addActionListener(e -> generateManagerAccount());

        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(generateManagerButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(new JScrollPane(infoArea), gbc);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void generateManagerAccount() {
        String randomEmail = generateRandomEmail();
        String randomPassword = generateRandomPassword();

        User manager = new User(randomEmail, randomPassword, UserType.MANAGER, false);

        UserDAO.addUser(manager);

        String message = String.format(
            "A new Manager account has been created:\n\n" +
            "Email:    %s\n" +
            "Password: %s\n\n" +
            "Please record these credentials securely!",
            randomEmail, randomPassword
        );
        infoArea.setText(message);
    }

    private String generateRandomEmail() {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            sb.append(alphabet.charAt(rand.nextInt(alphabet.length())));
        }
        sb.append("@yorku.ca");
        return sb.toString();
    }

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
