package EECS3311.UI;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import EECS3311.DAO.*;
import EECS3311.Models.*;

public class UserValidationPanel extends JPanel {
    private JTable userTable;
    private UserTableModel tableModel;

    public UserValidationPanel() {
        setLayout(new BorderLayout());

        // Create header
        JLabel titleLabel = new JLabel("Pending User Validation", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // Create table model with the dummy data
        tableModel = new UserTableModel(UserDAO.getPendingUsers());

        // Create user table with the table model
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setFillsViewportHeight(true);

        // Set custom renderer and editor for the Approve and Deny button columns.
        userTable.getColumn("Approve").setCellRenderer(new ButtonRenderer());
        userTable.getColumn("Approve").setCellEditor(new ButtonEditor(new JCheckBox(), true));
        userTable.getColumn("Deny").setCellRenderer(new ButtonRenderer());
        userTable.getColumn("Deny").setCellEditor(new ButtonEditor(new JCheckBox(), false));

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);

        // Add a component listener to refresh the table model each time the panel is shown
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // Update the table model with the latest pending users
                tableModel.updateData(UserDAO.getPendingUsers());
            }
        });
    }

    // --- Button Renderer for table cells ---
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText(value == null ? "" : value.toString());
            return this;
        }
    }

    // --- Button Editor for table cells ---
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isApprove; // true = Approve button, false = Deny button
        private boolean clicked;
        private int row;

        public ButtonEditor(JCheckBox checkBox, boolean isApprove) {
            super(checkBox);
            this.isApprove = isApprove;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            clicked = true;
            return button;
        }
        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                // Retrieve the user for the clicked row
                User user = tableModel.getUserAt(row);
                if (user != null) {
                    if (isApprove) {
                        int confirm = JOptionPane.showConfirmDialog(button,
                                "Approve user: " + user.getEmail() + " (" + user.getUserType() + ")?",
                                "Confirm Approval", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            UserDAO.approveUser(user);
                            JOptionPane.showMessageDialog(button, "User approved successfully",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        int confirm = JOptionPane.showConfirmDialog(button,
                                "Deny user: " + user.getEmail() + " (" + user.getUserType() + ")?\nThis will delete the account.",
                                "Confirm Denial", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            UserDAO.deleteUser(user);
                            JOptionPane.showMessageDialog(button, "User denied and removed from system",
                                    "Success", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    // Refresh the table model after the action
                    tableModel.updateData(UserDAO.getPendingUsers());
                }
            }
            clicked = false;
            return label;
        }
        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
