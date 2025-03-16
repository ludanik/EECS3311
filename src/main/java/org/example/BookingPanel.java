package org.example;

import javax.swing.*;
import java.awt.*;

// Booking Panel
class BookingPanel extends JPanel {
    private JComboBox<String> parkingLotComboBox;
    private JTextField licensePlateField;
    private JSpinner hoursSpinner;
    private JLabel costLabel;
    private JButton bookButton;

    public BookingPanel() {
        setLayout(new BorderLayout());

        // Create header
        JLabel titleLabel = new JLabel("Book a Parking Space", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Parking Lot
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Parking Lot:"), gbc);

        gbc.gridx = 1;
        parkingLotComboBox = new JComboBox<>(new String[] {"Lot A", "Lot B", "Lot C"});
        formPanel.add(parkingLotComboBox, gbc);

        // License Plate
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("License Plate:"), gbc);

        gbc.gridx = 1;
        licensePlateField = new JTextField(15);
        formPanel.add(licensePlateField, gbc);

        // Hours
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Duration (hours):"), gbc);

        gbc.gridx = 1;
        hoursSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));
        formPanel.add(hoursSpinner, gbc);

        // Cost display
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Estimated Cost:"), gbc);

        gbc.gridx = 1;
        costLabel = new JLabel("$5.00 (Deposit: $5.00)");
        formPanel.add(costLabel, gbc);

        // Book button
        gbc.gridx = 1;
        gbc.gridy = 4;
        bookButton = new JButton("Book Space");
        bookButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Booking confirmed for " + licensePlateField.getText() +
                            " at " + parkingLotComboBox.getSelectedItem() +
                            " for " + hoursSpinner.getValue() + " hours.",
                    "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
        });
        formPanel.add(bookButton, gbc);

        // Add form to panel
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.add(formPanel);
        add(wrapperPanel, BorderLayout.CENTER);
    }
}