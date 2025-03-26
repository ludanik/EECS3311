package EECS3311.UI;

import EECS3311.DAO.*;
import EECS3311.Models.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

class BookingPanel extends JPanel {
    private JComboBox<String> parkingLotComboBox;
    private JTextField licensePlateField;
    private JComboBox<String> parkingSpaceComboBox;
    private JSpinner hoursSpinner;
    private JComboBox<String> startHourComboBox;
    private JComboBox<String> startMinuteComboBox;
    private JLabel costLabel;
    private JButton bookButton;
    private MainFrame mainFrame;

    private AtomicReference<HashMap<String, Integer>> lotNameToIdMap = new AtomicReference<>(new HashMap<>());

    public BookingPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Book a Parking Space", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Parking Lot:"), gbc);

        gbc.gridx = 1;
        parkingLotComboBox = new JComboBox<>();
        formPanel.add(parkingLotComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Parking Space:"), gbc);

        gbc.gridx = 1;
        parkingSpaceComboBox = new JComboBox<>();
        formPanel.add(parkingSpaceComboBox, gbc);

        parkingLotComboBox.addActionListener(e -> {
            String selectedLot = (String) parkingLotComboBox.getSelectedItem();
            if (selectedLot != null) {
                int lotId = lotNameToIdMap.get().get(selectedLot);
                ArrayList<ParkingSpace> spaces = ParkingSpaceDAO.getAvailableSpaces(lotId);
                parkingSpaceComboBox.removeAllItems();
                for (ParkingSpace p : spaces) {
                    parkingSpaceComboBox.addItem(Integer.toString(p.getSpaceNumber()));
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("License Plate:"), gbc);

        gbc.gridx = 1;
        licensePlateField = new JTextField(15);
        formPanel.add(licensePlateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Starting Time:"), gbc);

        gbc.gridx = 1;
        startHourComboBox = new JComboBox<>();
        for (int i = 0; i < 24; i++) {
            startHourComboBox.addItem(String.format("%02d", i));
        }
        formPanel.add(startHourComboBox, gbc);

        gbc.gridx = 2;
        startMinuteComboBox = new JComboBox<>();
        for (int i = 0; i < 60; i += 5) {
            startMinuteComboBox.addItem(String.format("%02d", i));
        }
        formPanel.add(startMinuteComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Duration (hours) (max 24):"), gbc);

        gbc.gridx = 1;
        hoursSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));
        ((JSpinner.DefaultEditor) hoursSpinner.getEditor()).getTextField().setEditable(false);
        formPanel.add(hoursSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Estimated Cost:"), gbc);

        gbc.gridx = 1;
        costLabel = new JLabel();
        formPanel.add(costLabel, gbc);

        final double[] hourlyRate = {0};
        final double[] deposit = {0};
        updateCostLabel(hourlyRate[0], deposit[0], (Integer) hoursSpinner.getValue(), costLabel);

        hoursSpinner.addChangeListener(e -> {
            User currentUser = mainFrame.getCurrentUser();
            if (currentUser != null) {
                hourlyRate[0] = currentUser.getUserType().getHourlyRate();
                deposit[0] = currentUser.getUserType().getHourlyRate();
            } else {
                hourlyRate[0] = 0;
                deposit[0] = 0;
            }
            int hours = (Integer) hoursSpinner.getValue();
            updateCostLabel(hourlyRate[0], deposit[0], hours, costLabel);
        });

        // Book button
        gbc.gridx = 1;
        gbc.gridy = 6;
        bookButton = new JButton("Book Space");
        bookButton.addActionListener(e -> createBooking());
        formPanel.add(bookButton, gbc);

        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.add(formPanel);
        add(wrapperPanel, BorderLayout.CENTER);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshParkingLotComboBox();
            }
        });
    }

    private void createBooking() {
        if (validateInputs()) {
            PaymentPanel paymentPanel = new PaymentPanel();
            int option = JOptionPane.showConfirmDialog(this, paymentPanel,
                    "Enter Payment Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION || !paymentPanel.isPaymentValid()) {
                // Payment validation failed or user cancelled
                return;
            }

            User currentUser = mainFrame.getCurrentUser();
            String selectedLot = (String) parkingLotComboBox.getSelectedItem();
            String selectedSpace = (String) parkingSpaceComboBox.getSelectedItem();
            int startHour = Integer.parseInt((String) startHourComboBox.getSelectedItem());
            int startMinute = Integer.parseInt((String) startMinuteComboBox.getSelectedItem());
            int duration = (Integer) hoursSpinner.getValue();

            LocalDateTime startTime = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), startHour, startMinute);
            LocalDateTime endTime = startTime.plusHours(duration);

            int lotId = lotNameToIdMap.get().get(selectedLot);
            int spaceNumber = Integer.parseInt(selectedSpace);

            ParkingSpace parkingSpace = new ParkingSpace(
                    lotId,
                    spaceNumber,
                    ParkingStatus.AVAILABLE,
                    ParkingSpaceDAO.getParkingSpaceId(lotId, spaceNumber)
            );

            int hourlyRate = (int) currentUser.getUserType().getHourlyRate();
            int totalCost = hourlyRate * duration;
            int deposit = (int) currentUser.getUserType().getHourlyRate();

            Booking booking = new Booking(
                    0,  // Temporary ID, will be set by database
                    currentUser,
                    parkingSpace,
                    startTime,
                    endTime,
                    deposit,
                    totalCost,
                    "credit",  // Payment method (can be set later)
                    licensePlateField.getText(),
                    BookingStatus.BOOKED  // Initial status
            );

            try {
                BookingDAO.addBooking(booking);

                JOptionPane.showMessageDialog(this,
                        "Booking confirmed for " + currentUser.getEmail() +
                                " at Lot " + selectedLot +
                                " Space " + selectedSpace +
                                " starting at " + String.format("%02d:%02d", startHour, startMinute) +
                                " for " + duration + " hours.",
                        "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Booking failed: " + ex.getMessage(),
                        "Booking Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private boolean validateInputs() {
        if (mainFrame.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please log in first.",
                    "Login Required",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (parkingLotComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a parking lot.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (parkingSpaceComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a parking space.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (licensePlateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid license plate.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        int duration = (Integer) hoursSpinner.getValue();
        if (duration <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Duration must be greater than zero.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void updateCostLabel(double rate, double deposit, int hours, JLabel label) {
        double cost = rate * hours;
        label.setText(String.format("$%.2f (Deposit: $%.2f)", cost, deposit));
    }

    private void refreshParkingLotComboBox() {
        ArrayList<ParkingLot> lots = ParkingLotDAO.getEnabledLots();
        HashMap<String, Integer> map = new HashMap<>();
        for (ParkingLot lot : lots) {
            map.put(lot.getName(), lot.getId());
        }
        lotNameToIdMap.set(map);

        parkingLotComboBox.removeAllItems();
        for (ParkingLot lot : lots) {
            parkingLotComboBox.addItem(lot.getName());
        }

        if (lots.size() > 0) {
            parkingLotComboBox.setSelectedIndex(0);
            int lotId = lots.get(0).getId();
            ArrayList<ParkingSpace> spaces = ParkingSpaceDAO.getAvailableSpaces(lotId);
            parkingSpaceComboBox.removeAllItems();
            for (ParkingSpace p : spaces) {
                parkingSpaceComboBox.addItem(Integer.toString(p.getSpaceNumber()));
            }
        } else {
            parkingSpaceComboBox.removeAllItems();
        }
    }
}