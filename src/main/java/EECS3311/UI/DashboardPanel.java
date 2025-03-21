package EECS3311.UI;

import EECS3311.DAO.*;
import EECS3311.Models.*;
import javax.swing.*;
import java.awt.*;

// Dashboard Panel
class DashboardPanel extends JPanel {
    private MainFrame mainFrame;
    private JPanel contentPanel;
    private CardLayout contentCardLayout;
    private JPanel homePanel;
    private BookingPanel bookingPanel;
    private ManagementPanel managementPanel;

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

        // Create header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("YorkU Parking Booking System - Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            mainFrame.showPanel("LOGIN");
        });
        headerPanel.add(logoutButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Create navigation panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));

        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(e -> contentCardLayout.show(contentPanel, "HOME"));
        navPanel.add(homeButton);

        JButton bookingButton = new JButton("Book Parking");
        bookingButton.addActionListener(e -> contentCardLayout.show(contentPanel, "BOOKING"));
        navPanel.add(bookingButton);

        JButton myBookingsButton = new JButton("My Bookings");
        myBookingsButton.addActionListener(e -> {
            // TODO: Implement my bookings panel
            JOptionPane.showMessageDialog(this, "My Bookings feature will be implemented in the next phase",
                    "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
        });
        navPanel.add(myBookingsButton);

        // Management button (only visible for managers)
        JButton managementButton = new JButton("Management");
        managementButton.addActionListener(e -> contentCardLayout.show(contentPanel, "MANAGEMENT"));
        navPanel.add(managementButton);

        add(navPanel, BorderLayout.WEST);

        // Create content panel with card layout
        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);

        // Create content panels
        homePanel = createHomePanel();
        bookingPanel = new BookingPanel(mainFrame);
        managementPanel = new ManagementPanel();

        contentPanel.add(homePanel, "HOME");
        contentPanel.add(bookingPanel, "BOOKING");
        contentPanel.add(managementPanel, "MANAGEMENT");

        add(contentPanel, BorderLayout.CENTER);

        // Show home by default
        contentCardLayout.show(contentPanel, "HOME");
    }

    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome to YorkU Parking Booking System", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(welcomeLabel, BorderLayout.NORTH);

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setText("This system allows you to book parking spaces at York University.\n\n" +
                "Use the navigation panel on the left to:\n" +
                "- Book a new parking space\n" +
                "- View and manage your bookings\n" +
                "- Make payments");

        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        return panel;
    }

    public void updateForUser(User user) {
        // Update UI based on user type
        Component[] navButtons = ((JPanel)getComponent(1)).getComponents();

        // Management button is the last button
        navButtons[navButtons.length - 1].setVisible(user.getUserType() == UserType.MANAGER);
    }
}