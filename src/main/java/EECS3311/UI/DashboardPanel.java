package EECS3311.UI;

import EECS3311.DAO.*;
import EECS3311.Models.*;
import javax.swing.*;
import java.awt.*;
import java.awt.print.Book;

class DashboardPanel extends JPanel {
    private MainFrame mainFrame;
    private JPanel contentPanel;
    private CardLayout contentCardLayout;
    private JPanel homePanel;
    private BookingPanel bookingPanel;
    private ManagementPanel managementPanel;
    private MyBookingsPanel myBookingsPanel;
    private SuperManagerAccountGenerationPanel superManagerPanel;

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());

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

        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));

        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(e -> contentCardLayout.show(contentPanel, "HOME"));
        navPanel.add(homeButton);

        JButton bookingButton = new JButton("Book Parking");

        bookingButton.addActionListener(e -> {
            contentCardLayout.show(contentPanel, "BOOKING");
        });

        navPanel.add(bookingButton);

        JButton myBookingsButton = new JButton("My Bookings");
        myBookingsButton.addActionListener(e -> contentCardLayout.show(contentPanel, "MYBOOKINGS"));
        navPanel.add(myBookingsButton);

        JButton managementButton = new JButton("Management");
        managementButton.addActionListener(e -> contentCardLayout.show(contentPanel, "MANAGEMENT"));
        navPanel.add(managementButton);

        JButton superManagerButton = new JButton("Super Manager Tools");
        superManagerButton.addActionListener(e -> contentCardLayout.show(contentPanel, "SUPER_MANAGER"));
        navPanel.add(superManagerButton);

        add(navPanel, BorderLayout.WEST);

        contentCardLayout = new CardLayout();
        contentPanel = new JPanel(contentCardLayout);

        homePanel = createHomePanel();
        bookingPanel = new BookingPanel(mainFrame);
        managementPanel = new ManagementPanel(mainFrame);
        myBookingsPanel = new MyBookingsPanel(mainFrame);
        superManagerPanel = new SuperManagerAccountGenerationPanel();

        contentPanel.add(homePanel, "HOME");
        contentPanel.add(bookingPanel, "BOOKING");
        contentPanel.add(managementPanel, "MANAGEMENT");
        contentPanel.add(myBookingsPanel, "MYBOOKINGS");
        contentPanel.add(superManagerPanel, "SUPER_MANAGER");

        add(contentPanel, BorderLayout.CENTER);

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
        Component[] navButtons = ((JPanel)getComponent(1)).getComponents();

        navButtons[navButtons.length - 4].setVisible(user.getUserType() != UserType.MANAGER && user.getUserType() != UserType.SUPERMANAGER );
        navButtons[navButtons.length - 3].setVisible(user.getUserType() != UserType.MANAGER && user.getUserType() != UserType.SUPERMANAGER);
        navButtons[navButtons.length - 2].setVisible(user.getUserType() == UserType.MANAGER || user.getUserType() == UserType.SUPERMANAGER);
        navButtons[navButtons.length - 1].setVisible(user.getUserType() == UserType.SUPERMANAGER);
    }
}