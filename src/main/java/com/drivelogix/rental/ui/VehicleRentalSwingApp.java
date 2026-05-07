package com.drivelogix.rental.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JSpinner;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class VehicleRentalSwingApp {

    private static final Color PAGE_BACKGROUND = new Color(245, 247, 250);
    private static final Color PANEL_BACKGROUND = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(35, 98, 181);
    private static final Color PRIMARY_TEXT = new Color(32, 37, 43);
    private static final Color MUTED_TEXT = new Color(110, 117, 125);
    private static final Color ACCENT_COLOR = new Color(0, 153, 122);

    private final ApiClient apiClient;
    private final JFrame frame;
    private final VehicleTableModel tableModel;
    private final JTable vehicleTable;
    private final RentedVehicleTableModel rentedVehicleTableModel;
    private final JTable rentedVehicleTable;
    private final JTextField brandField;
    private final JTextField modelField;
    private final JComboBox<String> classBox;
    private final JTextField maxPriceField;
    private final JTextField customerField;
    private final JSpinner startDateSpinner;
    private final JSpinner endDateSpinner;
    private final JLabel pricingLabel;
    private final JLabel rentedSummaryLabel;
    private final JButton returnVehicleButton;
    private final JTabbedPane tabbedPane;

    public VehicleRentalSwingApp(String baseUrl) {
        this.apiClient = new ApiClient(baseUrl);
        configureLookAndFeel();
        this.frame = new JFrame("DriveLogix Vehicle Rental");
        this.tableModel = new VehicleTableModel();
        this.vehicleTable = new JTable(tableModel);
        this.rentedVehicleTableModel = new RentedVehicleTableModel();
        this.rentedVehicleTable = new JTable(rentedVehicleTableModel);
        this.brandField = new JTextField();
        this.modelField = new JTextField();
        this.classBox = new JComboBox<>(new String[]{"", "Sedan", "SUV", "Van", "Hatchback"});
        this.maxPriceField = new JTextField();
        this.customerField = new JTextField();
        this.startDateSpinner = createDateSpinner(LocalDate.now());
        this.endDateSpinner = createDateSpinner(LocalDate.now().plusDays(3));
        this.pricingLabel = new JLabel("Select a vehicle and click Preview Pricing.");
        this.rentedSummaryLabel = new JLabel("No rented vehicles loaded yet.");
        this.returnVehicleButton = new JButton("Return Selected Vehicle");
        this.tabbedPane = new JTabbedPane();
        buildUi();
    }

    public void show() {
        frame.setVisible(true);
        loadVehicles();
    }

    private void buildUi() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(820, 600));
        frame.getContentPane().setBackground(PAGE_BACKGROUND);
        frame.setLayout(new BorderLayout(0, 0));

        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(PAGE_BACKGROUND);
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(PAGE_BACKGROUND);
        topPanel.add(buildHeaderPanel());

        buildTabbedPane();

        root.add(topPanel, BorderLayout.NORTH);
        root.add(tabbedPane, BorderLayout.CENTER);
        frame.setContentPane(root);
        frame.setLocationRelativeTo(null);
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                frame.revalidate();
            }
        });
    }

    private JPanel buildHeaderPanel() {
        JPanel panel = createCardPanel(new BorderLayout(12, 8));

        JLabel titleLabel = new JLabel("DriveLogix Vehicle Rental");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_TEXT);

        JLabel subtitleLabel = new JLabel("Browse available vehicles, filter inventory, preview rental pricing, and complete bookings.");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(MUTED_TEXT);

        JPanel textPanel = new JPanel();
        textPanel.setBackground(PANEL_BACKGROUND);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(6));
        textPanel.add(subtitleLabel);

        JLabel badgeLabel = new JLabel("Live availability");
        badgeLabel.setOpaque(true);
        badgeLabel.setBackground(new Color(228, 244, 240));
        badgeLabel.setForeground(ACCENT_COLOR.darker());
        badgeLabel.setBorder(new EmptyBorder(8, 12, 8, 12));
        badgeLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(badgeLabel, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildFiltersPanel() {
        JPanel filtersPanel = createCardPanel(new BorderLayout(0, 12));

        JLabel sectionTitle = new JLabel("Search Available Vehicles");
        sectionTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        sectionTitle.setForeground(PRIMARY_TEXT);
        filtersPanel.add(sectionTitle, BorderLayout.NORTH);

        JPanel grid = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));
        grid.setBackground(PANEL_BACKGROUND);
        grid.add(createFieldPanel("Brand", brandField));
        grid.add(createFieldPanel("Model", modelField));
        grid.add(createFieldPanel("Class", classBox));
        grid.add(createFieldPanel("Max Daily Price", maxPriceField));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setBackground(PANEL_BACKGROUND);

        JButton clearButton = new JButton("Clear");
        styleSecondaryButton(clearButton);
        clearButton.addActionListener(event -> {
            brandField.setText("");
            modelField.setText("");
            classBox.setSelectedIndex(0);
            maxPriceField.setText("");
            loadVehicles();
        });

        JButton filterButton = new JButton("Search");
        stylePrimaryButton(filterButton);
        filterButton.addActionListener(event -> loadVehicles());

        actions.add(clearButton);
        actions.add(filterButton);

        filtersPanel.add(grid, BorderLayout.CENTER);
        filtersPanel.add(actions, BorderLayout.SOUTH);
        return filtersPanel;
    }

    private void buildTabbedPane() {
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabbedPane.addTab("Available Vehicles", buildAvailableTab());
        tabbedPane.addTab("Rented Vehicles", buildRentedTab());
        tabbedPane.addChangeListener(event -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                loadVehicles();
            } else {
                loadRentedVehicles();
            }
        });
    }

    private JPanel buildAvailableTab() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(PAGE_BACKGROUND);
        content.setBorder(new EmptyBorder(16, 0, 0, 0));
        content.add(buildFiltersPanel());
        content.add(Box.createVerticalStrut(16));
        content.add(buildVehicleTableCard());
        content.add(Box.createVerticalStrut(16));
        content.add(buildRentalPanel());

        JScrollPane scrollPane = createPageScrollPane(content);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PAGE_BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRentedTab() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(PAGE_BACKGROUND);
        content.setBorder(new EmptyBorder(16, 0, 0, 0));
        content.add(buildRentedTableCard());
        content.add(Box.createVerticalStrut(16));
        content.add(buildReturnPanel());

        JScrollPane scrollPane = createPageScrollPane(content);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PAGE_BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildVehicleTableCard() {
        JPanel panel = createCardPanel(new BorderLayout(0, 12));
        JLabel title = new JLabel("Available Vehicles");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(PRIMARY_TEXT);
        panel.add(title, BorderLayout.NORTH);
        panel.add(buildVehicleTablePane(), BorderLayout.CENTER);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private JPanel buildRentedTableCard() {
        JPanel panel = createCardPanel(new BorderLayout(0, 12));

        JPanel heading = new JPanel(new BorderLayout());
        heading.setBackground(PANEL_BACKGROUND);
        JLabel title = new JLabel("Vehicles Currently Rented By Other Customers");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(PRIMARY_TEXT);
        rentedSummaryLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rentedSummaryLabel.setForeground(MUTED_TEXT);
        heading.add(title, BorderLayout.WEST);
        heading.add(rentedSummaryLabel, BorderLayout.EAST);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(buildRentedTablePane(), BorderLayout.CENTER);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private JPanel buildReturnPanel() {
        JPanel panel = createCardPanel(new BorderLayout(12, 12));

        JLabel infoLabel = new JLabel("Returned vehicles become available again on or after one day after the rental end date.");
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        infoLabel.setForeground(MUTED_TEXT);

        stylePrimaryButton(returnVehicleButton);
        returnVehicleButton.addActionListener(event -> returnSelectedVehicle());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setBackground(PANEL_BACKGROUND);
        actions.add(returnVehicleButton);

        panel.add(infoLabel, BorderLayout.CENTER);
        panel.add(actions, BorderLayout.EAST);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private JScrollPane buildVehicleTablePane() {
        vehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vehicleTable.setRowHeight(34);
        vehicleTable.setShowHorizontalLines(true);
        vehicleTable.setGridColor(new Color(232, 236, 240));
        vehicleTable.setSelectionBackground(new Color(223, 235, 251));
        vehicleTable.setSelectionForeground(PRIMARY_TEXT);
        vehicleTable.setIntercellSpacing(new Dimension(0, 1));
        vehicleTable.setFillsViewportHeight(true);
        vehicleTable.setDefaultRenderer(Object.class, new VehicleTableCellRenderer());

        JTableHeader header = vehicleTable.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(new Color(238, 242, 247));
        header.setForeground(PRIMARY_TEXT);

        JScrollPane tablePane = new JScrollPane(vehicleTable);
        tablePane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 232)),
                new EmptyBorder(8, 8, 8, 8)
        ));
        tablePane.getViewport().setBackground(PANEL_BACKGROUND);
        tablePane.setPreferredSize(new Dimension(760, 280));
        return tablePane;
    }

    private JScrollPane buildRentedTablePane() {
        rentedVehicleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rentedVehicleTable.setRowHeight(32);
        rentedVehicleTable.setShowHorizontalLines(true);
        rentedVehicleTable.setGridColor(new Color(232, 236, 240));
        rentedVehicleTable.setSelectionBackground(new Color(235, 240, 245));
        rentedVehicleTable.setSelectionForeground(PRIMARY_TEXT);
        rentedVehicleTable.setIntercellSpacing(new Dimension(0, 1));
        rentedVehicleTable.setFillsViewportHeight(true);
        rentedVehicleTable.setDefaultRenderer(Object.class, new VehicleTableCellRenderer());

        JTableHeader header = rentedVehicleTable.getTableHeader();
        header.setReorderingAllowed(false);
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(new Color(238, 242, 247));
        header.setForeground(PRIMARY_TEXT);

        JScrollPane tablePane = new JScrollPane(rentedVehicleTable);
        tablePane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 232)),
                new EmptyBorder(8, 8, 8, 8)
        ));
        tablePane.getViewport().setBackground(PANEL_BACKGROUND);
        tablePane.setPreferredSize(new Dimension(760, 300));
        return tablePane;
    }

    private JPanel buildRentalPanel() {
        JPanel rentalPanel = createCardPanel(new BorderLayout(16, 16));

        JPanel headingPanel = new JPanel(new BorderLayout());
        headingPanel.setBackground(PANEL_BACKGROUND);
        JLabel sectionTitle = new JLabel("Rental Details");
        sectionTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        sectionTitle.setForeground(PRIMARY_TEXT);
        JLabel helperText = new JLabel("Choose a vehicle, enter rental dates, preview pricing, then confirm the booking.");
        helperText.setForeground(MUTED_TEXT);
        helperText.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel titleStack = new JPanel();
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setBackground(PANEL_BACKGROUND);
        titleStack.add(sectionTitle);
        titleStack.add(Box.createVerticalStrut(4));
        titleStack.add(helperText);
        headingPanel.add(titleStack, BorderLayout.CENTER);

        JPanel formGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));
        formGrid.setBackground(PANEL_BACKGROUND);
        formGrid.add(createFieldPanel("Customer Name", customerField));
        formGrid.add(createFieldPanel("Start Date", startDateSpinner));
        formGrid.add(createFieldPanel("End Date", endDateSpinner));

        JPanel footer = new JPanel(new BorderLayout(12, 12));
        footer.setBackground(PANEL_BACKGROUND);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonRow.setBackground(PANEL_BACKGROUND);

        JButton previewButton = new JButton("Preview Pricing");
        styleSecondaryButton(previewButton);
        previewButton.addActionListener(event -> previewPricing());

        JButton rentButton = new JButton("Rent Selected Vehicle");
        stylePrimaryButton(rentButton);
        rentButton.addActionListener(event -> rentSelectedVehicle());
        buttonRow.add(previewButton);
        buttonRow.add(rentButton);

        JPanel pricingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pricingPanel.setBackground(new Color(244, 249, 255));
        pricingPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(212, 223, 235)),
                new EmptyBorder(12, 14, 12, 14)
        ));

        JLabel pricingTitle = new JLabel("Pricing Summary: ");
        pricingTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        pricingTitle.setForeground(PRIMARY_TEXT);
        pricingLabel.setForeground(PRIMARY_TEXT);
        pricingLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        pricingPanel.add(pricingTitle);
        pricingPanel.add(pricingLabel);

        footer.add(pricingPanel, BorderLayout.CENTER);
        footer.add(buttonRow, BorderLayout.EAST);

        rentalPanel.add(headingPanel, BorderLayout.NORTH);
        rentalPanel.add(formGrid, BorderLayout.CENTER);
        rentalPanel.add(footer, BorderLayout.SOUTH);
        return rentalPanel;
    }

    private void loadVehicles() {
        runAsync(() -> apiClient.fetchVehicles(
                brandField.getText(),
                modelField.getText(),
                String.valueOf(classBox.getSelectedItem()),
                maxPriceField.getText()
        ), (java.util.List<VehicleDto> vehicles) -> {
            tableModel.setVehicles(vehicles);
            pricingLabel.setText("Loaded " + vehicles.size() + " available vehicles.");
        });
    }

    private void loadRentedVehicles() {
        runAsync(apiClient::fetchRentedVehicles, (java.util.List<RentedVehicleDto> rentals) -> {
            rentedVehicleTableModel.setRentals(rentals);
            rentedSummaryLabel.setText(rentals.size() + " active rentals");
        });
    }

    private void previewPricing() {
        VehicleDto selectedVehicle = getSelectedVehicle();
        if (selectedVehicle == null) {
            showError("Please select a vehicle first.");
            return;
        }

        RentalPayload payload = buildPayload();
        if (payload == null) {
            return;
        }

        runAsync(() -> apiClient.previewPricing(selectedVehicle.id(), payload), pricing -> pricingLabel.setText(
                pricing.rentalDays() + " days, tier " + pricing.pricingTier() + ", total " + pricing.totalPrice()
        ));
    }

    private void rentSelectedVehicle() {
        VehicleDto selectedVehicle = getSelectedVehicle();
        if (selectedVehicle == null) {
            showError("Please select a vehicle first.");
            return;
        }

        RentalPayload payload = buildPayload();
        if (payload == null) {
            return;
        }

        runAsync(() -> apiClient.rentVehicle(selectedVehicle.id(), payload), rental -> {
            pricingLabel.setText("Rental created: " + rental.totalPrice());
            JOptionPane.showMessageDialog(frame,
                    "Rental #" + rental.rentalId() + " created for " + rental.customerName() + ".",
                    "Rental Created",
                    JOptionPane.INFORMATION_MESSAGE);
            loadVehicles();
        });
    }

    private void returnSelectedVehicle() {
        int selectedRow = rentedVehicleTable.getSelectedRow();
        if (selectedRow < 0) {
            showError("Please select a rented vehicle first.");
            return;
        }

        RentedVehicleDto rental = rentedVehicleTableModel.getRentalAt(selectedRow);
        if (rental == null) {
            showError("Please select a rented vehicle first.");
            return;
        }

        runAsync(() -> apiClient.returnVehicle(rental.rentalId()), response -> {
            JOptionPane.showMessageDialog(frame,
                    response.message(),
                    "Vehicle Returned",
                    JOptionPane.INFORMATION_MESSAGE);
            loadRentedVehicles();
        });
    }

    private RentalPayload buildPayload() {
        LocalDate startDate = toLocalDate((Date) startDateSpinner.getValue());
        LocalDate endDate = toLocalDate((Date) endDateSpinner.getValue());
        if (startDate == null || endDate == null) {
            showError("Please choose both start and end dates.");
            return null;
        }

        return new RentalPayload(
                customerField.getText().trim(),
                startDate.toString(),
                endDate.toString()
        );
    }

    private VehicleDto getSelectedVehicle() {
        int selectedRow = vehicleTable.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        return tableModel.getVehicleAt(selectedRow);
    }

    private <T> void runAsync(ApiSupplier<T> supplier, ApiConsumer<T> consumer) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return supplier.get();
            }

            @Override
            protected void done() {
                try {
                    consumer.accept(get());
                } catch (Exception exception) {
                    Throwable cause = exception.getCause() == null ? exception : exception.getCause();
                    showError(cause.getMessage());
                }
            }
        }.execute();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void configureLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }

    private JPanel createCardPanel(BorderLayout layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 232)),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    private JScrollPane createPageScrollPane(JPanel content) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PAGE_BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(18);
        return scrollPane;
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setBackground(PANEL_BACKGROUND);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(PRIMARY_TEXT);

        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(220, 34));
        field.setMaximumSize(new Dimension(320, 34));

        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(field);
        return panel;
    }

    private JSpinner createDateSpinner(LocalDate initialDate) {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        spinner.setValue(Date.from(initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
        return spinner;
    }

    private LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    private void stylePrimaryButton(JButton button) {
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(new Color(239, 243, 248));
        button.setForeground(PRIMARY_TEXT);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
    }

    @FunctionalInterface
    private interface ApiSupplier<T> {
        T get() throws IOException, InterruptedException;
    }

    @FunctionalInterface
    private interface ApiConsumer<T> {
        void accept(T value);
    }

    private static class VehicleTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                component.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 251, 253));
                component.setForeground(PRIMARY_TEXT);
            }
            if (component instanceof JLabel label) {
                label.setBorder(new EmptyBorder(0, 8, 0, 8));
            }
            return component;
        }
    }
}
