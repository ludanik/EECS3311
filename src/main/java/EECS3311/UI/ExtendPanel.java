package EECS3311.UI;

import javax.swing.*;
import java.awt.*;

public class ExtendPanel extends JPanel {
    public JTextField hoursField;

    public ExtendPanel() {
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Extend Booking", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Number of Hours to Extend By:"), gbc);
        gbc.gridx = 1;
        hoursField = new JTextField(20);
        hoursField.setText("1");
        formPanel.add(hoursField, gbc);
        add(formPanel, BorderLayout.CENTER);
    }
}
