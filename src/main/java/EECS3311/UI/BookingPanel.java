package EECS3311.UI;


import com.sun.tools.javac.Main;

import EECS3311.DAO.*;
import EECS3311.Models.*;
import javax.swing.*;
import java.awt.*;

// Booking Panel
class BookingPanel extends JPanel {
    private JComboBox<String> parkingLotComboBox;
    private JTextField licensePlateField;
    private JTextField parkingSpotField;
    private JSpinner hoursSpinner;
    private JLabel costLabel;
    private JButton bookButton;
    private MainFrame mainFrame;

    public BookingPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
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
        // Need to populate this list from DB
        parkingLotComboBox = new JComboBox<>(new String[] {"Lot A", "Lot B", "Lot C"});
        formPanel.add(parkingLotComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Parking Space (0-100):"), gbc);

        gbc.gridx = 1;
        parkingSpotField = new JTextField(15);
        formPanel.add(parkingSpotField, gbc);

        // License Plate
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("License Plate:"), gbc);

        gbc.gridx = 1;
        licensePlateField = new JTextField(15);
        formPanel.add(licensePlateField, gbc);

        // Hours
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Duration (hours) (max 24):"), gbc);

        gbc.gridx = 1;
        hoursSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));
        ((JSpinner.DefaultEditor) hoursSpinner.getEditor()).getTextField().setEditable(false);
        formPanel.add(hoursSpinner, gbc);

        // Cost display
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Estimated Cost:"), gbc);

        gbc.gridx = 1;
        costLabel = new JLabel();
        formPanel.add(costLabel, gbc);

        final double[] hourlyRate = {0};

        updateCostLabel(hourlyRate[0], hourlyRate[0], (Integer) hoursSpinner.getValue(), costLabel);

        hoursSpinner.addChangeListener(e -> {
            if (mainFrame.getCurrentUser() != null) {
                hourlyRate[0] = mainFrame.getCurrentUser().getUserType().getHourlyRate();
            } else {
                hourlyRate[0] = 0;
            }
            int hours = (Integer) hoursSpinner.getValue();
            updateCostLabel(hourlyRate[0], hourlyRate[0], hours, costLabel);
        });

        // Book button
        gbc.gridx = 1;
        gbc.gridy = 5;
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

    private void updateCostLabel(double rate, double deposit, int hours, JLabel label) {
        double cost = rate * hours;
        label.setText(String.format("$%.2f (Deposit: $%.2f)", cost, deposit));
    }
}