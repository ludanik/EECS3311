package EECS3311.UI;

import javax.swing.*;
import java.awt.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class PaymentPanel extends JPanel {
    private JTextField cardNumberField;
    private JTextField securityCodeField;
    private JTextField expirationField;

    public PaymentPanel() {
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Payment Information", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 18));
        add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Credit Card Number:"), gbc);
        gbc.gridx = 1;
        cardNumberField = new JTextField(20);
        cardNumberField.setText("123456789015");
        formPanel.add(cardNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Security Code:"), gbc);
        gbc.gridx = 1;
        securityCodeField = new JTextField(4);
        securityCodeField.setText("123");
        formPanel.add(securityCodeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Expiration Date (MM/yy):"), gbc);
        gbc.gridx = 1;
        expirationField = new JTextField(5);
        expirationField.setText("01/29");
        formPanel.add(expirationField, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    public boolean isPaymentValid() {
        String cardNumber = cardNumberField.getText().replaceAll("\\s+", "");
        String securityCode = securityCodeField.getText().trim();
        String expiration = expirationField.getText().trim();

        if (!isValidCreditCard(cardNumber)) {
            JOptionPane.showMessageDialog(this,
                    "Invalid credit card number.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!securityCode.matches("\\d{3,4}")) {
            JOptionPane.showMessageDialog(this,
                    "Invalid security code. It should be 3 or 4 digits.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!isValidExpiration(expiration)) {
            JOptionPane.showMessageDialog(this,
                    "Invalid or expired expiration date.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean isValidCreditCard(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            char c = cardNumber.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
            int n = c - '0';
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }

    private boolean isValidExpiration(String expiration) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth expDate = YearMonth.parse(expiration, formatter);
            YearMonth currentMonth = YearMonth.now();
            return expDate.isAfter(currentMonth) || expDate.equals(currentMonth);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
