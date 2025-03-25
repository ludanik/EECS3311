package EECS3311.UI;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import EECS3311.DAO.*;
import EECS3311.Models.*;

// Management Panel
class ManagementPanel extends JPanel {
    private JComboBox<String> parkingLotComboBox;
    private JComboBox<String> parkingLotComboBox2;
    private JButton addLotButton;
    private JButton enableLotButton;
    private JButton disableLotButton;
    private JTextField spaceIdField;
    private JButton enableSpaceButton;
    private JButton disableSpaceButton;

    public ManagementPanel() {
        // Use BorderLayout as the main layout
        setLayout(new BorderLayout());

        // Header label
        JLabel titleLabel = new JLabel("Parking Management", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Create a tabbed pane for different management functions
        JTabbedPane tabbedPane = new JTabbedPane();

        // 1. Lot management panel
        JPanel lotPanel = buildLotManagementPanel();

        // 2. Space management panel
        JPanel spacePanel = buildSpaceManagementPanel();

        // 3. User management panel (existing user validation)
        JPanel userPanel = new UserValidationPanel();

        // 4. (NEW) SuperManager: Generate Manager Accounts
        SuperManagerAccountGenerationPanel superManagerPanel = new SuperManagerAccountGenerationPanel();

        // Add each panel to the tabbed pane
        tabbedPane.addTab("Manage Lots", lotPanel);
        tabbedPane.addTab("Manage Spaces", spacePanel);
        tabbedPane.addTab("Manage Users", userPanel);
        tabbedPane.addTab("Generate Manager Accounts", superManagerPanel);

        // Add the tabbed pane to the ManagementPanel
        add(tabbedPane, BorderLayout.CENTER);
    }

    // ----------------------------------------------------------
    //  BUILD LOT MANAGEMENT PANEL
    // ----------------------------------------------------------
    private JPanel buildLotManagementPanel() {
        JPanel lotPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Retrieve all parking lots
        ArrayList<ParkingLot> lots = ParkingLotDAO.getAllLots();
        AtomicReference<HashMap<String, Integer>> lotNameToIdMap = new AtomicReference<>(new HashMap<>());

        // Extract lot names for the combo box
        String[] lotNames = lots.stream()
                .map(ParkingLot::getName)
                .toArray(String[]::new);

        for (ParkingLot lot : lots) {
            lotNameToIdMap.get().put(lot.getName(), lot.getId());
        }

        // Build UI for the lot panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        lotPanel.add(new JLabel("Parking Lot:"), gbc);

        gbc.gridx = 1;
        parkingLotComboBox = new JComboBox<>(lotNames);
        lotPanel.add(parkingLotComboBox, gbc);

        // "Add New Lot" button
        gbc.gridx = 0;
        gbc.gridy = 1;
        addLotButton = new JButton("Add New Lot");
        addLotButton.addActionListener(e -> {
            String lotName = JOptionPane.showInputDialog(this, "Enter new lot name:");
            if (lotName != null && !lotName.isEmpty()) {
                // Create and add new lot
                ParkingLot p = new ParkingLot(ParkingLotDAO.getNextId(), lotName, true);
                ParkingLotDAO.addLot(p);

                // Refresh lots
                ArrayList<ParkingLot> updatedLots = ParkingLotDAO.getAllLots();
                String[] updatedLotNames = updatedLots.stream()
                        .map(ParkingLot::getName)
                        .toArray(String[]::new);

                parkingLotComboBox.setModel(new DefaultComboBoxModel<>(updatedLotNames));
                parkingLotComboBox2.setModel(new DefaultComboBoxModel<>(updatedLotNames));

                lotNameToIdMap.set(new HashMap<>());
                for (ParkingLot updatedLot : updatedLots) {
                    lotNameToIdMap.get().put(updatedLot.getName(), updatedLot.getId());
                }

                JOptionPane.showMessageDialog(this,
                        "Lot " + lotName + " added successfully");
            }
        });
        lotPanel.add(addLotButton, gbc);

        // "Enable Lot" button
        gbc.gridx = 1;
        enableLotButton = new JButton("Enable Lot");
        enableLotButton.addActionListener(e -> {
            if (parkingLotComboBox.getSelectedItem() != null) {
                int lotId = lotNameToIdMap.get().get(parkingLotComboBox.getSelectedItem().toString());
                ParkingLotDAO.enableLot(lotId);
                JOptionPane.showMessageDialog(this,
                        parkingLotComboBox.getSelectedItem() + " enabled successfully");
            }
        });
        lotPanel.add(enableLotButton, gbc);

        // "Disable Lot" button
        gbc.gridx = 2;
        disableLotButton = new JButton("Disable Lot");
        disableLotButton.addActionListener(e -> {
            if (parkingLotComboBox.getSelectedItem() != null) {
                int lotId = lotNameToIdMap.get().get(parkingLotComboBox.getSelectedItem().toString());
                ParkingLotDAO.disableLot(lotId);
                JOptionPane.showMessageDialog(this,
                        parkingLotComboBox.getSelectedItem() + " disabled successfully");
            }
        });
        lotPanel.add(disableLotButton, gbc);

        return lotPanel;
    }

    // ----------------------------------------------------------
    //  BUILD SPACE MANAGEMENT PANEL
    // ----------------------------------------------------------
    private JPanel buildSpaceManagementPanel() {
        JPanel spacePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Retrieve all lots
        ArrayList<ParkingLot> lots = ParkingLotDAO.getAllLots();
        AtomicReference<HashMap<String, Integer>> lotNameToIdMap = new AtomicReference<>(new HashMap<>());

        String[] lotNames = lots.stream()
                .map(ParkingLot::getName)
                .toArray(String[]::new);

        for (ParkingLot lot : lots) {
            lotNameToIdMap.get().put(lot.getName(), lot.getId());
        }

        // Build UI for space panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        spacePanel.add(new JLabel("Parking Lot:"), gbc);

        gbc.gridx = 1;
        parkingLotComboBox2 = new JComboBox<>(lotNames);
        spacePanel.add(parkingLotComboBox2, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        spacePanel.add(new JLabel("Space ID (1-100):"), gbc);

        gbc.gridx = 1;
        spaceIdField = new JTextField(10);
        spacePanel.add(spaceIdField, gbc);

        // "Enable Space" button
        gbc.gridx = 0;
        gbc.gridy = 2;
        enableSpaceButton = new JButton("Enable Space");
        enableSpaceButton.addActionListener(e -> {
            if (parkingLotComboBox2.getSelectedItem() != null && !spaceIdField.getText().trim().isEmpty()) {
                int lotId = lotNameToIdMap.get().get(parkingLotComboBox2.getSelectedItem().toString());
                int spaceId = Integer.parseInt(spaceIdField.getText());
                ParkingSpaceDAO.enableSpace(lotId, spaceId);

                JOptionPane.showMessageDialog(this,
                        "Space " + spaceId + " enabled successfully");
            }
        });
        spacePanel.add(enableSpaceButton, gbc);

        // "Disable Space" button
        gbc.gridx = 1;
        disableSpaceButton = new JButton("Disable Space");
        disableSpaceButton.addActionListener(e -> {
            if (parkingLotComboBox2.getSelectedItem() != null && !spaceIdField.getText().trim().isEmpty()) {
                int lotId = lotNameToIdMap.get().get(parkingLotComboBox2.getSelectedItem().toString());
                int spaceId = Integer.parseInt(spaceIdField.getText());
                ParkingSpaceDAO.disableSpace(lotId, spaceId);

                JOptionPane.showMessageDialog(this,
                        "Space " + spaceId + " disabled successfully");
            }
        });
        spacePanel.add(disableSpaceButton, gbc);

        return spacePanel;
    }
}
