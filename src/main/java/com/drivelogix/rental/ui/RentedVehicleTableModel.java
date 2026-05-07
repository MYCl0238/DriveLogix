package com.drivelogix.rental.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class RentedVehicleTableModel extends AbstractTableModel {

    private final String[] columns = {"Plate", "Brand", "Model", "Class", "Rented By", "Start", "End", "Return From", "Total"};
    private List<RentedVehicleDto> rentals = new ArrayList<>();

    public void setRentals(List<RentedVehicleDto> rentals) {
        this.rentals = new ArrayList<>(rentals);
        fireTableDataChanged();
    }

    public RentedVehicleDto getRentalAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= rentals.size()) {
            return null;
        }
        return rentals.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return rentals.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        RentedVehicleDto rental = rentals.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> rental.numberPlate();
            case 1 -> rental.brand();
            case 2 -> rental.model();
            case 3 -> rental.vehicleClass();
            case 4 -> rental.customerName();
            case 5 -> rental.startDate();
            case 6 -> rental.endDate();
            case 7 -> rental.eligibleReturnDate();
            case 8 -> rental.totalPrice();
            default -> "";
        };
    }
}
