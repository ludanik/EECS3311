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
import java.sql.Timestamp;

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

    // Use AtomicReference to hold the mapping from lot name to ID.
    private AtomicReference<HashMap<String, Integer>> lotNameToIdMap = new AtomicReference<>(new HashMap<>());

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

        // Parking Lot label
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Parking Lot:"), gbc);

        // Initialize parkingLotComboBox (will be refreshed when shown)
        gbc.gridx = 1;
        parkingLotComboBox = new JComboBox<>();
        formPanel.add(parkingLotComboBox, gbc);

        // Parking Space label
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Parking Space:"), gbc);

        // Initialize parkingSpaceComboBox
        gbc.gridx = 1;
        parkingSpaceComboBox = new JComboBox<>();
        formPanel.add(parkingSpaceComboBox, gbc);

        // Listener to update parkingSpaceComboBox when a parking lot is selected
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

        // License Plate
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("License Plate:"), gbc);

        gbc.gridx = 1;
        licensePlateField = new JTextField(15);
        formPanel.add(licensePlateField, gbc);

        // Start Time Selection
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Starting Time:"), gbc);

        // Hour Combo Box
        gbc.gridx = 1;
        startHourComboBox = new JComboBox<>();
        for (int i = 0; i < 24; i++) {
            startHourComboBox.addItem(String.format("%02d", i));
        }
        formPanel.add(startHourComboBox, gbc);

        // Minute Combo Box
        gbc.gridx = 2;
        startMinuteComboBox = new JComboBox<>();
        for (int i = 0; i < 60; i += 5) {
            startMinuteComboBox.addItem(String.format("%02d", i));
        }
        formPanel.add(startMinuteComboBox, gbc);

        // Hours
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Duration (hours) (max 24):"), gbc);

        gbc.gridx = 1;
        hoursSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 24, 1));
        ((JSpinner.DefaultEditor) hoursSpinner.getEditor()).getTextField().setEditable(false);
        formPanel.add(hoursSpinner, gbc);

        // Cost display
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

        // Add form panel to a wrapper and then to the BookingPanel
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.add(formPanel);
        add(wrapperPanel, BorderLayout.CENTER);

        // Add a component listener to refresh the parking lots each time the panel is shown
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshParkingLotComboBox();
            }
        });
    }

    // Create Booking object and process the booking
    private void createBooking() {
        // Validate booking inputs first
        if (validateInputs()) {
            // Before booking, display payment details dialog
            PaymentPanel paymentPanel = new PaymentPanel();
            int option = JOptionPane.showConfirmDialog(this, paymentPanel,
                    "Enter Payment Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option != JOptionPane.OK_OPTION || !paymentPanel.isPaymentValid()) {
                // Payment validation failed or user cancelled
                return;
            }

            // Get selected values for booking
            User currentUser = mainFrame.getCurrentUser();
            String selectedLot = (String) parkingLotComboBox.getSelectedItem();
            String selectedSpace = (String) parkingSpaceComboBox.getSelectedItem();
            int startHour = Integer.parseInt((String) startHourComboBox.getSelectedItem());
            int startMinute = Integer.parseInt((String) startMinuteComboBox.getSelectedItem());
            int duration = (Integer) hoursSpinner.getValue();

            LocalDateTime startTime = LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), startHour, startMinute);
            LocalDateTime endTime = startTime.plusHours(duration);

            // Get lot ID and space number
            int lotId = lotNameToIdMap.get().get(selectedLot);
            int spaceNumber = Integer.parseInt(selectedSpace);

            // Find the specific ParkingSpace object
            ParkingSpace parkingSpace = new ParkingSpace(
                    lotId,
                    spaceNumber,
                    ParkingStatus.AVAILABLE,
                    ParkingSpaceDAO.getParkingSpaceId(lotId, spaceNumber)
            );

            // Calculate costs
            int hourlyRate = (int) currentUser.getUserType().getHourlyRate();
            int totalCost = hourlyRate * duration;
            int deposit = (int) currentUser.getUserType().getHourlyRate();

            // Create Booking object
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

            // Attempt to save booking
            try {
                // Typically, call a BookingDAO method here to save the booking
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


    // Validate user inputs before creating a booking
    private boolean validateInputs() {
        // Check current user
        if (mainFrame.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please log in first.",
                    "Login Required",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check parking lot selection
        if (parkingLotComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a parking lot.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check parking space selection
        if (parkingSpaceComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a parking space.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check license plate
        if (licensePlateField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid license plate.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Check duration is not zero (extra safety check)
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


    // Helper method to update cost label based on rate, deposit, and duration
    private void updateCostLabel(double rate, double deposit, int hours, JLabel label) {
        double cost = rate * hours;
        label.setText(String.format("$%.2f (Deposit: $%.2f)", cost, deposit));
    }

    // Refresh the parking lot combo box with current enabled lots and update the parking space combo box accordingly.
    private void refreshParkingLotComboBox() {
        ArrayList<ParkingLot> lots = ParkingLotDAO.getEnabledLots();
        HashMap<String, Integer> map = new HashMap<>();
        for (ParkingLot lot : lots) {
            map.put(lot.getName(), lot.getId());
        }
        lotNameToIdMap.set(map);

        // Remove existing items and add the new ones
        parkingLotComboBox.removeAllItems();
        for (ParkingLot lot : lots) {
            parkingLotComboBox.addItem(lot.getName());
        }

        // If there is at least one lot, select it and update parking spaces
        if (lots.size() > 0) {
            parkingLotComboBox.setSelectedIndex(0);
            int lotId = lots.get(0).getId();
            ArrayList<ParkingSpace> spaces = ParkingSpaceDAO.getAvailableSpaces(lotId);
            parkingSpaceComboBox.removeAllItems();
            for (ParkingSpace p : spaces) {
                parkingSpaceComboBox.addItem(Integer.toString(p.getSpaceNumber()));
            }
        } else {
            // No enabled lots: clear parking spaces
            parkingSpaceComboBox.removeAllItems();
        }
    }
}