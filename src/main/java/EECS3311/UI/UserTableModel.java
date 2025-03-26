package EECS3311.UI;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import EECS3311.DAO.*;
import EECS3311.Models.*;

public class UserTableModel extends AbstractTableModel {
    private List<User> users;
    private final String[] columnNames = {"Email", "User Type", "Approve", "Deny"};

    public UserTableModel(List<User> users) {
        this.users = users;
    }

    @Override
    public int getRowCount() {
        return users == null ? 0 : users.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        User user = users.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return user.getEmail();
            case 1:
                return user.getUserType();
            case 2:
                return "Approve";
            case 3:
                return "Deny";
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2 || columnIndex == 3;
    }

    public User getUserAt(int rowIndex) {
        return users.get(rowIndex);
    }

    public void updateData(List<User> newUsers) {
        this.users = newUsers;
        fireTableDataChanged();
    }
}
