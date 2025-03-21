package EECS3311.UI;


import javax.swing.*;
import java.awt.*;
import EECS3311.DAO.*;
import EECS3311.Models.*;

// Management Panel
class ManagementPanel extends JPanel {
    private JComboBox<String> parkingLotComboBox;
    private JButton addLotButton;
    private JButton enableLotButton;
    private JButton disableLotButton;
    private JTextField spaceIdField;
    private JButton enableSpaceButton;
    private JButton disableSpaceButton;

    public ManagementPanel() {
        setLayout(new BorderLayout());

        // Create header
        JLabel titleLabel = new JLabel("Parking Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Create tabbed pane for different management functions
        JTabbedPane tabbedPane = new JTabbedPane();

        // Lot management panel
        JPanel lotPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        lotPanel.add(new JLabel("Parking Lot:"), gbc);

        gbc.gridx = 1;
        parkingLotComboBox = new JComboBox<>(new String[] {"Lot A", "Lot B", "Lot C"});
        lotPanel.add(parkingLotComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        addLotButton = new JButton("Add New Lot");
        addLotButton.addActionListener(e -> {
            String lotName = JOptionPane.showInputDialog(this, "Enter new lot name:");
            if (lotName != null && !lotName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lot " + lotName + " added successfully");
            }
        });
        lotPanel.add(addLotButton, gbc);

        gbc.gridx = 1;
        enableLotButton = new JButton("Enable Lot");
        enableLotButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    parkingLotComboBox.getSelectedItem() + " enabled successfully");
        });
        lotPanel.add(enableLotButton, gbc);

        gbc.gridx = 2;
        disableLotButton = new JButton("Disable Lot");
        disableLotButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    parkingLotComboBox.getSelectedItem() + " disabled successfully");
        });
        lotPanel.add(disableLotButton, gbc);

        // Space management panel
        JPanel spacePanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        spacePanel.add(new JLabel("Space ID (1-100):"), gbc);

        gbc.gridx = 1;
        spaceIdField = new JTextField(10);
        spacePanel.add(spaceIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        enableSpaceButton = new JButton("Enable Space");
        enableSpaceButton.addActionListener(e -> {
            String spaceId = spaceIdField.getText();
            if (spaceId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a space ID",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Space " + spaceId + " enabled successfully");
        });
        spacePanel.add(enableSpaceButton, gbc);

        gbc.gridx = 1;
        disableSpaceButton = new JButton("Disable Space");
        disableSpaceButton.addActionListener(e -> {
            String spaceId = spaceIdField.getText();
            if (spaceId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a space ID",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Space " + spaceId + " disabled successfully");
        });
        spacePanel.add(disableSpaceButton, gbc);

        // User management panel (placeholder)
        JPanel userPanel = new UserValidationPanel();

        // Add panels to tabbed pane
        tabbedPane.addTab("Manage Lots", lotPanel);
        tabbedPane.addTab("Manage Spaces", spacePanel);
        tabbedPane.addTab("Manage Users", userPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }
}