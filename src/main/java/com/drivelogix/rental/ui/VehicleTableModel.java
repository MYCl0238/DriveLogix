package com.drivelogix.rental.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class VehicleTableModel extends AbstractTableModel {

    private final String[] columns = {"Plate", "Brand", "Model", "Class", "Daily Price", "Status"};
    private List<VehicleDto> vehicles = new ArrayList<>();

    public void setVehicles(List<VehicleDto> vehicles) {
        this.vehicles = new ArrayList<>(vehicles);
        fireTableDataChanged();
    }

    public VehicleDto getVehicleAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= vehicles.size()) {
            return null;
        }
        return vehicles.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return vehicles.size();
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
        VehicleDto vehicle = vehicles.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> vehicle.numberPlate();
            case 1 -> vehicle.brand();
            case 2 -> vehicle.model();
            case 3 -> vehicle.vehicleClass();
            case 4 -> vehicle.price();
            case 5 -> vehicle.status();
            default -> "";
        };
    }
}
