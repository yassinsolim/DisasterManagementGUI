package edu.ucalgary.oop;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

public class DisasterManagementGUI extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(51, 153, 255);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 16);
    private DatabaseAdapter dbAdapter;
    private JTabbedPane tabbedPane;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Arial", Font.PLAIN, 16);

    public DisasterManagementGUI() {
        // Show language selection dialog
        String selectedLanguage = showLanguageSelectionDialog();
        if (selectedLanguage == null) {
            System.exit(0); // Exit if user cancels language selection
        }

        // Initialize language loader with selected language
        if (!LanguageLoader.initialize(selectedLanguage)) {
            System.err.println("Error: Could not load language file");
            System.exit(1);
        }

        setupFrame();
        initializeDatabase();
        createGUI();
    }

    private String showLanguageSelectionDialog() {
        JDialog dialog = new JDialog(this, "Select Language / Sélectionner la langue", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Please select your preferred language / Veuillez sélectionner votre langue préférée");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(titleLabel, gbc);

        // Store the selected language
        final String[] selectedLanguage = new String[1];

        // Create a panel for buttons with FlowLayout for equal sizing
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // English Button
        JButton englishButton = new JButton("English");
        englishButton.setPreferredSize(new Dimension(150, 40));
        englishButton.addActionListener(e -> {
            selectedLanguage[0] = "en-CA";
            dialog.setVisible(false);
            dialog.dispose();
        });

        // French Button
        JButton frenchButton = new JButton("Français");
        frenchButton.setPreferredSize(new Dimension(150, 40));
        frenchButton.addActionListener(e -> {
            selectedLanguage[0] = "fr-CA";
            dialog.setVisible(false);
            dialog.dispose();
        });

        // Add buttons to panel
        buttonPanel.add(englishButton);
        buttonPanel.add(frenchButton);

        // Add button panel to dialog
        gbc.gridy = 1;
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);

        return selectedLanguage[0];
    }

    private void setupFrame() {
        setTitle(LanguageLoader.getTranslation("menu.title"));
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (dbAdapter != null) {
                    dbAdapter.closeConnection();
                }
            }
        });
    }

    private void initializeDatabase() {
        try {
            dbAdapter = DatabaseAdapter.createInstance();
        } catch (Exception e) {
            showError("Database Error", "Failed to connect to database: " + e.getMessage());
            System.exit(1);
        }
    }

    private void createGUI() {
        // Create welcome panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(LanguageLoader.getTranslation("menu.title"));
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(LanguageLoader.getTranslation("menu.subtitle"));
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        welcomePanel.add(titleLabel);
        welcomePanel.add(Box.createVerticalStrut(10));
        welcomePanel.add(subtitleLabel);

        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab(LanguageLoader.getTranslation("menu.victim"), createVictimPanel());
        tabbedPane.addTab(LanguageLoader.getTranslation("menu.supply"), createSupplyPanel());
        tabbedPane.addTab(LanguageLoader.getTranslation("menu.inquiry"), createInquiryPanel());
        tabbedPane.addTab(LanguageLoader.getTranslation("menu.location"), createLocationPanel());

        // Add components to frame
        setLayout(new BorderLayout());
        add(welcomePanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createVictimPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(createStyledButton(LanguageLoader.getTranslation("menu.add_victim"), e -> showAddVictimDialog()));
        panel.add(createStyledButton(LanguageLoader.getTranslation("menu.update_victim"), e -> showUpdateVictimDialog()));
        panel.add(createStyledButton(LanguageLoader.getTranslation("menu.view_victims"), e -> showViewVictimsDialog()));
        panel.add(createStyledButton(LanguageLoader.getTranslation("menu.delete_victim"), e -> showDeleteVictimDialog()));

        return panel;
    }

    private JPanel createSupplyPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(createStyledButton(LanguageLoader.getTranslation("menu.add_supply"), e -> showAddSupplyDialog()));
        panel.add(createStyledButton(LanguageLoader.getTranslation("menu.update_supply"), e -> showViewSuppliesDialog()));
        panel.add(createStyledButton(LanguageLoader.getTranslation("menu.view_supplies"), e -> showViewSuppliesDialog()));
        panel.add(createStyledButton(LanguageLoader.getTranslation("menu.delete_supply"), e -> showDeleteSupplyDialog()));

        return panel;
    }

    private JPanel createInquiryPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(createStyledButton(LanguageLoader.getTranslation("menu.add_inquiry"), e -> showAddInquiryDialog()));
        panel.add(createStyledButton(LanguageLoader.getTranslation("menu.update_inquiry"), e -> showUpdateInquiryDialog()));
        panel.add(createStyledButton(LanguageLoader.getTranslation("menu.view_inquiries"), e -> showViewInquiriesDialog()));
        panel.add(createStyledButton(LanguageLoader.getTranslation("menu.delete_inquiry"), e -> showDeleteInquiryDialog()));

        return panel;
    }

    private JPanel createLocationPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(createStyledButton(LanguageLoader.getTranslation("menu.add_location"), e -> showAddLocationDialog()));
        panel.add(createStyledButton(LanguageLoader.getTranslation("menu.update_location"), e -> showUpdateLocationDialog()));
        panel.add(createStyledButton(LanguageLoader.getTranslation("menu.view_locations"), e -> showViewLocationsDialog()));
        panel.add(createStyledButton(LanguageLoader.getTranslation("menu.delete_location"), e -> showDeleteLocationDialog()));

        return panel;
    }

    private JButton createStyledButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.addActionListener(listener);
        return button;
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this, message, LanguageLoader.getTranslation("gui.error.title"), JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAddVictimDialog() {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("gui.dialog.add_victim"), true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel(LanguageLoader.getTranslation("gui.victim.firstname")), gbc);
        gbc.gridx = 1;
        JTextField firstNameField = new JTextField(20);
        dialog.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel(LanguageLoader.getTranslation("gui.victim.lastname")), gbc);
        gbc.gridx = 1;
        JTextField lastNameField = new JTextField(20);
        dialog.add(lastNameField, gbc);

        // Date of Birth
        gbc.gridx = 0;
        gbc.gridy = 2;
        JPanel dobPanel = new JPanel(new BorderLayout());
        dobPanel.add(new JLabel(LanguageLoader.getTranslation("gui.victim.dob")), BorderLayout.WEST);
        JLabel formatLabel = new JLabel("(YYYY-MM-DD)");
        formatLabel.setForeground(Color.GRAY);
        dobPanel.add(formatLabel, BorderLayout.EAST);
        dialog.add(dobPanel, gbc);
        
        gbc.gridx = 1;
        JTextField dobField = new JTextField(20);
        dobField.setToolTipText("Format: YYYY-MM-DD (e.g., 1990-01-31)");
        dialog.add(dobField, gbc);

        // Gender
        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel(LanguageLoader.getTranslation("gui.victim.gender")), gbc);
        gbc.gridx = 1;
        String[] genders = {
            LanguageLoader.getTranslation("gui.gender.man"),
            LanguageLoader.getTranslation("gui.gender.woman"),
            LanguageLoader.getTranslation("gui.gender.nonbinary")
        };
        JComboBox<String> genderCombo = new JComboBox<>(genders);
        dialog.add(genderCombo, gbc);

        // Location
        gbc.gridx = 0;
        gbc.gridy = 4;
        dialog.add(new JLabel(LanguageLoader.getTranslation("gui.victim.location")), gbc);
        gbc.gridx = 1;
        JComboBox<Location> locationCombo = new JComboBox<>();
        try {
            List<Location> locations = dbAdapter.getAllLocations();
            for (Location location : locations) {
                locationCombo.addItem(location);
            }
        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("gui.error.title"), 
                     LanguageLoader.getTranslation("gui.error.locations") + " " + e.getMessage());
        }
        dialog.add(locationCombo, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton(LanguageLoader.getTranslation("gui.button.save"));
        JButton cancelButton = new JButton(LanguageLoader.getTranslation("gui.button.cancel"));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        // Save button action
        saveButton.addActionListener(e -> {
            try {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String dob = dobField.getText().trim();
                String selectedGender = (String) genderCombo.getSelectedItem();
                Location location = (Location) locationCombo.getSelectedItem();

                if (firstName.isEmpty() || lastName.isEmpty() || dob.isEmpty()) {
                    showError(LanguageLoader.getTranslation("gui.error.title"), 
                            LanguageLoader.getTranslation("gui.validation.required_fields"));
                    return;
                }

                LocalDate dateOfBirth = LocalDate.parse(dob);
                DisasterVictim victim = new DisasterVictim(firstName, lastName, dateOfBirth);
                
                // Convert the gender display text back to the single letter format
                if (selectedGender.equals(LanguageLoader.getTranslation("gui.gender.man"))) {
                    victim.setGender("M");
                } else if (selectedGender.equals(LanguageLoader.getTranslation("gui.gender.woman"))) {
                    victim.setGender("F");
                } else if (selectedGender.equals(LanguageLoader.getTranslation("gui.gender.nonbinary"))) {
                    victim.setGender("X");
                }

                if (location != null) {
                    victim.setLocation(location);
                }

                dbAdapter.addVictim(victim);
                dialog.dispose();
                showSuccess(LanguageLoader.getTranslation("gui.success.title"), 
                          LanguageLoader.getTranslation("gui.success.victim_added"));
            } catch (SQLException ex) {
                showError(LanguageLoader.getTranslation("gui.error.title"), 
                         LanguageLoader.getTranslation("gui.error.add_victim") + " " + ex.getMessage());
            } catch (DateTimeParseException ex) {
                showError(LanguageLoader.getTranslation("gui.error.title"), 
                         LanguageLoader.getTranslation("gui.error.invalid_date"));
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showUpdateVictimDialog() {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("gui.dialog.update_victim"), true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Victim Selection
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel(LanguageLoader.getTranslation("gui.victim.select")), gbc);
        gbc.gridx = 1;
        JComboBox<DisasterVictim> victimCombo = new JComboBox<>();
        try {
            List<DisasterVictim> victims = dbAdapter.getAllVictims();
            victims.forEach(victimCombo::addItem);
        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("gui.error.title"), 
                     LanguageLoader.getTranslation("gui.error.victims") + " " + e.getMessage());
        }
        dialog.add(victimCombo, gbc);

        // First Name
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel(LanguageLoader.getTranslation("gui.victim.firstname")), gbc);
        gbc.gridx = 1;
        JTextField firstNameField = new JTextField(20);
        dialog.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel(LanguageLoader.getTranslation("gui.victim.lastname")), gbc);
        gbc.gridx = 1;
        JTextField lastNameField = new JTextField(20);
        dialog.add(lastNameField, gbc);

        // Date of Birth
        gbc.gridx = 0;
        gbc.gridy = 3;
        JPanel updateDobPanel = new JPanel(new BorderLayout());
        updateDobPanel.add(new JLabel(LanguageLoader.getTranslation("gui.victim.dob")), BorderLayout.WEST);
        JLabel updateFormatLabel = new JLabel("(YYYY-MM-DD)");
        updateFormatLabel.setForeground(Color.GRAY);
        updateDobPanel.add(updateFormatLabel, BorderLayout.EAST);
        dialog.add(updateDobPanel, gbc);
        
        gbc.gridx = 1;
        JTextField dobField = new JTextField(20);
        dobField.setToolTipText("Format: YYYY-MM-DD (e.g., 1990-01-31)");
        dialog.add(dobField, gbc);

        // Gender
        gbc.gridx = 0; gbc.gridy = 4;
        dialog.add(new JLabel(LanguageLoader.getTranslation("gui.victim.gender")), gbc);
        gbc.gridx = 1;
        String[] genders = {
            LanguageLoader.getTranslation("gui.gender.man"),
            LanguageLoader.getTranslation("gui.gender.woman"),
            LanguageLoader.getTranslation("gui.gender.nonbinary")
        };
        JComboBox<String> genderCombo = new JComboBox<>(genders);
        dialog.add(genderCombo, gbc);

        // Location
        gbc.gridx = 0; gbc.gridy = 5;
        dialog.add(new JLabel(LanguageLoader.getTranslation("gui.victim.location")), gbc);
        gbc.gridx = 1;
        JComboBox<Location> locationCombo = new JComboBox<>();
        try {
            List<Location> locations = dbAdapter.getAllLocations();
            locations.forEach(locationCombo::addItem);
        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("gui.error.title"), 
                     LanguageLoader.getTranslation("gui.error.locations") + " " + e.getMessage());
        }
        dialog.add(locationCombo, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton(LanguageLoader.getTranslation("gui.button.save"));
        JButton cancelButton = new JButton(LanguageLoader.getTranslation("gui.button.cancel"));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        // Load selected victim's data
        victimCombo.addActionListener(e -> {
            DisasterVictim selected = (DisasterVictim) victimCombo.getSelectedItem();
            if (selected != null) {
                firstNameField.setText(selected.getFirstName());
                lastNameField.setText(selected.getLastName());
                dobField.setText(selected.getDateOfBirth() != null ? selected.getDateOfBirth().toString() : "");
                String selectedGender = selected.getGender();
                if ("M".equals(selectedGender)) {
                    genderCombo.setSelectedItem("Homme");
                } else if ("F".equals(selectedGender)) {
                    genderCombo.setSelectedItem("Femme");
                } else if ("X".equals(selectedGender)) {
                    genderCombo.setSelectedItem("Personne non-binaire");
                }
                locationCombo.setSelectedItem(selected.getLocation());
            }
        });

        saveButton.addActionListener(e -> {
            try {
                DisasterVictim selected = (DisasterVictim) victimCombo.getSelectedItem();
                if (selected != null) {
                    selected.setFirstName(firstNameField.getText());
                    selected.setLastName(lastNameField.getText());
                    selected.setDateOfBirth(LocalDate.parse(dobField.getText()));
                    String selectedGender = (String) genderCombo.getSelectedItem();
                    if ("Homme".equals(selectedGender)) {
                        selected.setGender("M");
                    } else if ("Femme".equals(selectedGender)) {
                        selected.setGender("F");
                    } else if ("Personne non-binaire".equals(selectedGender)) {
                        selected.setGender("X");
                    }
                    selected.setLocation((Location) locationCombo.getSelectedItem());

                    dbAdapter.updateVictim(selected.getId(), selected);
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, "Victime mise à jour avec succès!");
                }
            } catch (Exception ex) {
                showError("Erreur", "Échec de la mise à jour de la victime: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showViewVictimsDialog() {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("gui.dialog.view_victims"), true);
        dialog.setLayout(new BorderLayout());

        // Create table model
        String[] columnNames = {
            LanguageLoader.getTranslation("gui.table.id"),
            LanguageLoader.getTranslation("gui.table.name"),
            LanguageLoader.getTranslation("gui.table.dob"),
            LanguageLoader.getTranslation("gui.table.gender"),
            LanguageLoader.getTranslation("gui.table.location")
        };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Load data
        try {
            List<DisasterVictim> victims = dbAdapter.getAllVictims();
            for (DisasterVictim victim : victims) {
                String gender = victim.getGender();
                if ("M".equals(gender)) {
                    gender = LanguageLoader.getTranslation("gui.gender.man");
                } else if ("F".equals(gender)) {
                    gender = LanguageLoader.getTranslation("gui.gender.woman");
                } else if ("X".equals(gender)) {
                    gender = LanguageLoader.getTranslation("gui.gender.nonbinary");
                }
                
                model.addRow(new Object[]{
                    victim.getId(),
                    victim.getFirstName() + " " + victim.getLastName(),
                    victim.getDateOfBirth(),
                    gender,
                    victim.getLocation() != null ? victim.getLocation().getName() : LanguageLoader.getTranslation("gui.location.unassigned")
                });
            }
        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("gui.error.title"), 
                     LanguageLoader.getTranslation("gui.error.victims") + " " + e.getMessage());
        }

        // Add buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton viewDetailsButton = new JButton(LanguageLoader.getTranslation("gui.button.view_details"));
        JButton closeButton = new JButton(LanguageLoader.getTranslation("gui.button.close"));
        
        viewDetailsButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    int victimId = (int) model.getValueAt(selectedRow, 0);
                    List<DisasterVictim> victims = dbAdapter.getAllVictims();
                    for (DisasterVictim victim : victims) {
                        if (victim.getId() == victimId) {
                            dialog.dispose();
                            showManageVictimDialog(victim);
                            break;
                        }
                    }
                } catch (SQLException ex) {
                    showError(LanguageLoader.getTranslation("gui.error.title"),
                            LanguageLoader.getTranslation("gui.error.load_victim") + " " + ex.getMessage());
                }
            } else {
                showError(LanguageLoader.getTranslation("gui.error.title"),
                         LanguageLoader.getTranslation("gui.error.select_victim"));
            }
        });
        
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showDeleteVictimDialog() {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("gui.dialog.delete_victim"), true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Victim Selection
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel(LanguageLoader.getTranslation("gui.victim.select")), gbc);
        gbc.gridx = 1;
        JComboBox<DisasterVictim> victimCombo = new JComboBox<>();
        try {
            List<DisasterVictim> victims = dbAdapter.getAllVictims();
            victims.forEach(victimCombo::addItem);
        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("gui.error.title"), 
                     LanguageLoader.getTranslation("gui.error.victims") + " " + e.getMessage());
        }
        dialog.add(victimCombo, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton deleteButton = new JButton(LanguageLoader.getTranslation("gui.button.delete"));
        JButton cancelButton = new JButton(LanguageLoader.getTranslation("gui.button.cancel"));
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        deleteButton.addActionListener(e -> {
            try {
                DisasterVictim selected = (DisasterVictim) victimCombo.getSelectedItem();
                if (selected != null) {
                    String confirmMessage = LanguageLoader.getTranslation("confirmations.delete_message");
                    
                    int confirm = JOptionPane.showConfirmDialog(dialog,
                        confirmMessage,
                        LanguageLoader.getTranslation("gui.dialog.confirm"),
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (dbAdapter.deleteVictim(selected.getId())) {
                            dialog.dispose();
                            showSuccess(LanguageLoader.getTranslation("messages.success"),
                                      LanguageLoader.getTranslation("gui.success.victim_deleted"));
                        } else {
                            showError(LanguageLoader.getTranslation("messages.e"),
                                    LanguageLoader.getTranslation("gui.e.delete_victim"));
                        }
                    }
                }
            } catch (Exception ex) {
                showError(LanguageLoader.getTranslation("messages.e"),
                         LanguageLoader.getTranslation("gui.e.delete_victim") + " " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showAddSupplyDialog() {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("gui.dialog.add_supply"), true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Type
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel typeLabel = new JLabel(LanguageLoader.getTranslation("gui.supply.type"));
        typeLabel.setPreferredSize(new Dimension(200, 25));
        dialog.add(typeLabel, gbc);

        gbc.gridx = 1;
        JTextField typeField = new JTextField();
        typeField.setPreferredSize(new Dimension(300, 25));
        dialog.add(typeField, gbc);

        // Comments
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel commentsLabel = new JLabel(LanguageLoader.getTranslation("gui.supply.comments"));
        commentsLabel.setPreferredSize(new Dimension(200, 25));
        dialog.add(commentsLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JTextArea commentsArea = new JTextArea();
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(commentsArea);
        scrollPane.setPreferredSize(new Dimension(300, 100));
        dialog.add(scrollPane, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = new JButton(LanguageLoader.getTranslation("gui.button.save"));
        JButton cancelButton = new JButton(LanguageLoader.getTranslation("gui.button.cancel"));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
            try {
                String type = typeField.getText().trim();
                String comments = commentsArea.getText().trim();

                if (type.isEmpty()) {
                    showError(LanguageLoader.getTranslation("gui.error.title"),
                            LanguageLoader.getTranslation("gui.validation.type_required"));
                    return;
                }

                Supply supply = new Supply(type, comments);
                dbAdapter.addSupply(supply);
                dialog.dispose();
                showSuccess(LanguageLoader.getTranslation("gui.success.title"),
                          LanguageLoader.getTranslation("gui.success.supply_added"));
            } catch (SQLException ex) {
                showError(LanguageLoader.getTranslation("gui.error.title"),
                         LanguageLoader.getTranslation("gui.error.add_supply") + " " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showViewSuppliesDialog() {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("gui.dialog.view_supplies"), true);
        dialog.setLayout(new BorderLayout());

        // Create table model with column names
        String[] columnNames = {
            LanguageLoader.getTranslation("gui.table.id"),
            LanguageLoader.getTranslation("gui.supply.type"),
            LanguageLoader.getTranslation("gui.supply.comments")
        };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            List<Supply> supplies = dbAdapter.getAllSupplies();
            for (Supply supply : supplies) {
                model.addRow(new Object[]{
                    supply.getId(),
                    supply.getType(),
                    supply.getComments()
                });
            }
        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("gui.error.title"),
                     LanguageLoader.getTranslation("gui.error.load_supplies") + " " + e.getMessage());
            return;
        }

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton updateButton = new JButton(LanguageLoader.getTranslation("actions.update"));
        JButton closeButton = new JButton(LanguageLoader.getTranslation("actions.close"));

        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                showError(LanguageLoader.getTranslation("gui.error.title"),
                         LanguageLoader.getTranslation("gui.error.no_selection"));
                return;
            }

            int id = (int) table.getValueAt(selectedRow, 0);
            String type = (String) table.getValueAt(selectedRow, 1);
            String comments = (String) table.getValueAt(selectedRow, 2);

            showUpdateSupplyDialog(id, type, comments);
            dialog.dispose();
        });

        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showUpdateSupplyDialog(int id, String currentType, String currentComments) {
        String translationKey = "gui.dialog.update_supply";
        String translatedTitle = LanguageLoader.getTranslation(translationKey);
        JDialog dialog = new JDialog(this, translatedTitle, true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Type
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel typeLabel = new JLabel(LanguageLoader.getTranslation("gui.supply.type"));
        dialog.add(typeLabel, gbc);

        gbc.gridx = 1;
        JTextField typeField = new JTextField(currentType);
        dialog.add(typeField, gbc);

        // Comments
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel commentsLabel = new JLabel(LanguageLoader.getTranslation("gui.supply.comments"));
        dialog.add(commentsLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JTextArea commentsArea = new JTextArea(currentComments);
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(commentsArea);
        scrollPane.setPreferredSize(new Dimension(300, 100));
        dialog.add(scrollPane, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton(LanguageLoader.getTranslation("gui.button.save"));
        JButton cancelButton = new JButton(LanguageLoader.getTranslation("gui.button.cancel"));

        saveButton.addActionListener(e -> {
            try {
                String type = typeField.getText().trim();
                String comments = commentsArea.getText().trim();

                if (type.isEmpty()) {
                    showError(LanguageLoader.getTranslation("gui.error.title"),
                            LanguageLoader.getTranslation("gui.validation.type_required"));
                    return;
                }

                Supply supply = new Supply(id, type, comments);
                dbAdapter.updateSupply(supply);
                dialog.dispose();
                showSuccess(LanguageLoader.getTranslation("gui.success.title"),
                          LanguageLoader.getTranslation("gui.success.supply_updated"));
            } catch (SQLException ex) {
                showError(LanguageLoader.getTranslation("gui.error.title"),
                         LanguageLoader.getTranslation("gui.error.update_supply") + " " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showDeleteSupplyDialog() {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("gui.dialog.delete_supply"), true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Supply Selection
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel selectLabel = new JLabel(LanguageLoader.getTranslation("gui.supply.select"));
        selectLabel.setPreferredSize(new Dimension(200, 25));
        dialog.add(selectLabel, gbc);

        gbc.gridx = 1;
        DefaultComboBoxModel<SupplyItem> supplyModel = new DefaultComboBoxModel<>();
        JComboBox<SupplyItem> supplyCombo = new JComboBox<>(supplyModel);
        supplyCombo.setPreferredSize(new Dimension(300, 25));
        try {
            List<Supply> supplies = dbAdapter.getAllSupplies();
            for (Supply supply : supplies) {
                supplyModel.addElement(new SupplyItem(
                    supply.getId(),
                    supply.getType(),
                    supply.getComments()
                ));
            }
        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("messages.e"),
                     LanguageLoader.getTranslation("messages.load_error").replace("{0}", e.getMessage()));
        }
        dialog.add(supplyCombo, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton deleteButton = new JButton(LanguageLoader.getTranslation("actions.delete"));
        JButton cancelButton = new JButton(LanguageLoader.getTranslation("actions.cancel"));
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        deleteButton.addActionListener(e -> {
            try {
                SupplyItem selected = (SupplyItem) supplyCombo.getSelectedItem();
                if (selected != null) {
                    int confirm = JOptionPane.showConfirmDialog(dialog,
                        LanguageLoader.getTranslation("confirmations.delete_message"),
                        LanguageLoader.getTranslation("confirmations.delete_supply"),
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        dbAdapter.deleteSupply(selected.getId());
                        dialog.dispose();
                        showSuccess(LanguageLoader.getTranslation("messages.success"),
                                  LanguageLoader.getTranslation("messages.deleted"));
                    }
                }
            } catch (SQLException ex) {
                showError(LanguageLoader.getTranslation("messages.e"),
                         LanguageLoader.getTranslation("messages.delete_error").replace("{0}", ex.getMessage()));
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private static class SupplyItem {
        private final int id;
        private final String type;
        private final String comments;

        public SupplyItem(int id, String type, String comments) {
            this.id = id;
            this.type = type;
            this.comments = comments;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return String.format("%s (ID: %d)", type, id);
        }
    }

    private void showAddInquiryDialog() {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("menu.add_inquiry"), true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Inquirer Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel(LanguageLoader.getTranslation("labels.inquirer"));
        nameLabel.setPreferredSize(new Dimension(200, 25));
        dialog.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        JTextField nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(300, 25));
        dialog.add(nameField, gbc);

        // Inquirer Contact
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel contactLabel = new JLabel(LanguageLoader.getTranslation("labels.contact"));
        contactLabel.setPreferredSize(new Dimension(200, 25));
        dialog.add(contactLabel, gbc);
        
        gbc.gridx = 1;
        JTextField contactField = new JTextField();
        contactField.setPreferredSize(new Dimension(300, 25));
        dialog.add(contactField, gbc);

        // Inquiry Details
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel detailsLabel = new JLabel(LanguageLoader.getTranslation("labels.details"));
        detailsLabel.setPreferredSize(new Dimension(200, 25));
        dialog.add(detailsLabel, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JTextArea detailsArea = new JTextArea();
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(detailsArea);
        scrollPane.setPreferredSize(new Dimension(300, 150));
        dialog.add(scrollPane, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = new JButton(LanguageLoader.getTranslation("actions.save"));
        JButton cancelButton = new JButton(LanguageLoader.getTranslation("actions.cancel"));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String contact = contactField.getText().trim();
                String details = detailsArea.getText().trim();

                if (name.isEmpty() || contact.isEmpty() || details.isEmpty()) {
                    showError(LanguageLoader.getTranslation("messages.error"), 
                            LanguageLoader.getTranslation("messages.required_fields"));
                    return;
                }

                String fullDetails = String.format("%s: %s\n%s: %s\n\n%s",
                    LanguageLoader.getTranslation("labels.inquirer"), name,
                    LanguageLoader.getTranslation("labels.contact"), contact,
                    details);
                Inquiry inquiry = new Inquiry(fullDetails, LocalDate.now());
                dbAdapter.addInquiry(inquiry);
                dialog.dispose();
                showSuccess(LanguageLoader.getTranslation("messages.success"), 
                          LanguageLoader.getTranslation("messages.added"));
                showViewInquiriesDialog();
            } catch (Exception ex) {
                showError(LanguageLoader.getTranslation("messages.error"), 
                         LanguageLoader.getTranslation("messages.save_error").replace("{0}", ex.getMessage()));
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showUpdateInquiryDialog() {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("menu.update_inquiry"), true);
        dialog.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn(LanguageLoader.getTranslation("table.id"));
        model.addColumn(LanguageLoader.getTranslation("labels.inquirer"));
        model.addColumn(LanguageLoader.getTranslation("labels.seeking"));
        model.addColumn(LanguageLoader.getTranslation("labels.location"));
        model.addColumn(LanguageLoader.getTranslation("table.date"));
        model.addColumn(LanguageLoader.getTranslation("table.comments"));

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        try {
            List<Inquiry> inquiries = dbAdapter.getAllInquiries();
            List<DisasterVictim> allVictims = dbAdapter.getAllVictims();
            List<Location> allLocations = dbAdapter.getAllLocations();

            for (Inquiry inquiry : inquiries) {
                String inquirerName = LanguageLoader.getTranslation("labels.not_assigned");
                String seekingName = LanguageLoader.getTranslation("labels.not_assigned");
                String locationName = LanguageLoader.getTranslation("labels.not_assigned");
                
                if (inquiry.getInquirerId() != null) {
                    for (DisasterVictim victim : allVictims) {
                        if (victim.getId() == inquiry.getInquirerId()) {
                            inquirerName = victim.getFirstName() + " " + victim.getLastName();
                            break;
                        }
                    }
                }
                
                if (inquiry.getSeekingId() != null) {
                    for (DisasterVictim victim : allVictims) {
                        if (victim.getId() == inquiry.getSeekingId()) {
                            seekingName = victim.getFirstName() + " " + victim.getLastName();
                            break;
                        }
                    }
                }
                
                if (inquiry.getLocationId() != null) {
                    for (Location location : allLocations) {
                        if (location.getLocationId() == inquiry.getLocationId()) {
                            locationName = location.getName();
                            break;
                        }
                    }
                }

                model.addRow(new Object[]{
                    inquiry.getId(),
                    inquirerName,
                    seekingName,
                    locationName,
                    inquiry.getDateOfInquiry(),
                    inquiry.getComments()
                });
            }
        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("messages.error"),
                     LanguageLoader.getTranslation("messages.load_error").replace("{0}", e.getMessage()));
            return;
        }

        JPanel buttonPanel = new JPanel();
        JButton updateButton = new JButton(LanguageLoader.getTranslation("actions.update"));
        JButton cancelButton = new JButton(LanguageLoader.getTranslation("actions.cancel"));
        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                showError(LanguageLoader.getTranslation("messages.error"),
                         LanguageLoader.getTranslation("messages.select_item"));
                return;
            }

            int inquiryId = (int) table.getValueAt(selectedRow, 0);
            showEditInquiryDialog(inquiryId);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditInquiryDialog(int inquiryId) {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("menu.update_inquiry"), true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        try {
            List<Inquiry> inquiries = dbAdapter.getAllInquiries();
            Inquiry inquiry = inquiries.stream()
                .filter(i -> i.getId() == inquiryId)
                .findFirst()
                .orElseThrow(() -> new SQLException(LanguageLoader.getTranslation("messages.inquiry_not_found")));

            List<DisasterVictim> allVictims = dbAdapter.getAllVictims();
            List<Location> allLocations = dbAdapter.getAllLocations();

            // Inquirer ComboBox
            gbc.gridx = 0; gbc.gridy = 0;
            dialog.add(new JLabel(LanguageLoader.getTranslation("labels.inquirer") + ":"), gbc);
            gbc.gridx = 1;
            JComboBox<DisasterVictim> inquirerCombo = new JComboBox<>(allVictims.toArray(new DisasterVictim[0]));
            if (inquiry.getInquirerId() != null) {
                for (DisasterVictim victim : allVictims) {
                    if (victim.getId() == inquiry.getInquirerId()) {
                        inquirerCombo.setSelectedItem(victim);
                        break;
                    }
                }
            }
            dialog.add(inquirerCombo, gbc);

            // Seeking ComboBox
            gbc.gridx = 0; gbc.gridy = 1;
            dialog.add(new JLabel(LanguageLoader.getTranslation("labels.seeking") + ":"), gbc);
            gbc.gridx = 1;
            JComboBox<DisasterVictim> seekingCombo = new JComboBox<>(allVictims.toArray(new DisasterVictim[0]));
            if (inquiry.getSeekingId() != null) {
                for (DisasterVictim victim : allVictims) {
                    if (victim.getId() == inquiry.getSeekingId()) {
                        seekingCombo.setSelectedItem(victim);
                        break;
                    }
                }
            }
            dialog.add(seekingCombo, gbc);

            // Location ComboBox
            gbc.gridx = 0; gbc.gridy = 2;
            dialog.add(new JLabel(LanguageLoader.getTranslation("labels.location") + ":"), gbc);
            gbc.gridx = 1;
            JComboBox<Location> locationCombo = new JComboBox<>(allLocations.toArray(new Location[0]));
            if (inquiry.getLocationId() != null) {
                for (Location location : allLocations) {
                    if (location.getLocationId() == inquiry.getLocationId()) {
                        locationCombo.setSelectedItem(location);
                        break;
                    }
                }
            }
            dialog.add(locationCombo, gbc);

            // Comments
            gbc.gridx = 0; gbc.gridy = 3;
            dialog.add(new JLabel(LanguageLoader.getTranslation("labels.comments") + ":"), gbc);
            gbc.gridx = 1;
            JTextArea commentsArea = new JTextArea(inquiry.getComments(), 5, 30);
            commentsArea.setLineWrap(true);
            commentsArea.setWrapStyleWord(true);
            JScrollPane commentsScroll = new JScrollPane(commentsArea);
            dialog.add(commentsScroll, gbc);

            // Buttons
            gbc.gridx = 0; gbc.gridy = 4;
            gbc.gridwidth = 2;
            JPanel buttonPanel = new JPanel();
            JButton saveButton = new JButton(LanguageLoader.getTranslation("actions.save"));
            JButton cancelButton = new JButton(LanguageLoader.getTranslation("actions.cancel"));

            saveButton.addActionListener(e -> {
                try {
                    DisasterVictim selectedInquirer = (DisasterVictim) inquirerCombo.getSelectedItem();
                    DisasterVictim selectedSeeking = (DisasterVictim) seekingCombo.getSelectedItem();
                    Location selectedLocation = (Location) locationCombo.getSelectedItem();
                    
                    inquiry.setInquirerId(selectedInquirer != null ? selectedInquirer.getId() : null);
                    inquiry.setSeekingId(selectedSeeking != null ? selectedSeeking.getId() : null);
                    inquiry.setLocationId(selectedLocation != null ? selectedLocation.getLocationId() : null);
                    inquiry.setComments(commentsArea.getText().trim());

                    dbAdapter.updateInquiry(inquiry);
                    dialog.dispose();
                    showSuccess(LanguageLoader.getTranslation("messages.success"),
                              LanguageLoader.getTranslation("messages.updated"));
                } catch (SQLException ex) {
                    showError(LanguageLoader.getTranslation("messages.e"),
                            LanguageLoader.getTranslation("messages.update_error").replace("{0}", ex.getMessage()));
                }
            });

            cancelButton.addActionListener(e -> dialog.dispose());

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            dialog.add(buttonPanel, gbc);

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("messages.e"),
                     LanguageLoader.getTranslation("messages.load_error").replace("{0}", e.getMessage()));
        }
    }

    private void showViewInquiriesDialog() {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("menu.view_inquiries"), true);
        dialog.setLayout(new BorderLayout(10, 10));

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn(LanguageLoader.getTranslation("table.id"));
        model.addColumn(LanguageLoader.getTranslation("labels.inquirer"));
        model.addColumn(LanguageLoader.getTranslation("labels.seeking"));
        model.addColumn(LanguageLoader.getTranslation("labels.location"));
        model.addColumn(LanguageLoader.getTranslation("labels.date"));
        model.addColumn(LanguageLoader.getTranslation("labels.comments"));

        JTable table = new JTable(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(300);
        
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Set up cell renderer for the comments column to wrap text
        table.getColumnModel().getColumn(5).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JTextArea area = new JTextArea(value != null ? value.toString() : "");
                area.setWrapStyleWord(true);
                area.setLineWrap(true);
                area.setMargin(new Insets(5, 5, 5, 5));
                return area;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(scrollPane, BorderLayout.CENTER);

        try {
            List<Inquiry> inquiries = dbAdapter.getAllInquiries();
            List<DisasterVictim> allVictims = dbAdapter.getAllVictims();
            List<Location> allLocations = dbAdapter.getAllLocations();

            for (Inquiry inquiry : inquiries) {
                String inquirerName = "N/A";
                String seekingName = "N/A";
                String locationName = "N/A";
                
                if (inquiry.getInquirerId() != null) {
                    for (DisasterVictim victim : allVictims) {
                        if (victim.getId() == inquiry.getInquirerId()) {
                            inquirerName = victim.getFirstName() + " " + victim.getLastName();
                            break;
                        }
                    }
                }
                
                if (inquiry.getSeekingId() != null) {
                    for (DisasterVictim victim : allVictims) {
                        if (victim.getId() == inquiry.getSeekingId()) {
                            seekingName = victim.getFirstName() + " " + victim.getLastName();
                            break;
                        }
                    }
                }
                
                if (inquiry.getLocationId() != null) {
                    for (Location location : allLocations) {
                        if (location.getLocationId() == inquiry.getLocationId()) {
                            locationName = location.getName();
                            break;
                        }
                    }
                }

                model.addRow(new Object[]{
                    inquiry.getId(),
                    inquirerName,
                    seekingName,
                    locationName,
                    inquiry.getDateOfInquiry(),
                    inquiry.getComments()
                });
            }
        } catch (SQLException e) {
            showError("Erreur", "Échec du chargement des demandes: " + e.getMessage());
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton editButton = new JButton(LanguageLoader.getTranslation("gui.button.edit"));
        JButton closeButton = new JButton(LanguageLoader.getTranslation("gui.button.close"));

        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int inquiryId = (int) table.getValueAt(selectedRow, 0);
                showEditInquiryDialog(inquiryId);
                refreshInquiryTable(model);
            } else {
                showError(LanguageLoader.getTranslation("gui.e.title"), 
                         LanguageLoader.getTranslation("messages.select_item"));
            }
        });

        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(editButton);
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(900, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void refreshInquiryTable(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<Inquiry> inquiries = dbAdapter.getAllInquiries();
            List<DisasterVictim> allVictims = dbAdapter.getAllVictims();
            List<Location> allLocations = dbAdapter.getAllLocations();

            for (Inquiry inquiry : inquiries) {
                String inquirerName = LanguageLoader.getTranslation("gui.location.unassigned");
                String seekingName = LanguageLoader.getTranslation("gui.location.unassigned");
                String locationName = LanguageLoader.getTranslation("gui.location.unassigned");
                
                if (inquiry.getInquirerId() != null) {
                    for (DisasterVictim victim : allVictims) {
                        if (victim.getId() == inquiry.getInquirerId()) {
                            inquirerName = victim.getFirstName() + " " + victim.getLastName();
                            break;
                        }
                    }
                }
                
                if (inquiry.getSeekingId() != null) {
                    for (DisasterVictim victim : allVictims) {
                        if (victim.getId() == inquiry.getSeekingId()) {
                            seekingName = victim.getFirstName() + " " + victim.getLastName();
                            break;
                        }
                    }
                }
                
                if (inquiry.getLocationId() != null) {
                    for (Location location : allLocations) {
                        if (location.getLocationId() == inquiry.getLocationId()) {
                            locationName = location.getName();
                            break;
                        }
                    }
                }

                model.addRow(new Object[]{
                    inquiry.getId(),
                    inquirerName,
                    seekingName,
                    locationName,
                    inquiry.getDateOfInquiry(),
                    inquiry.getComments()
                });
            }
        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("gui.e.title"), 
                     LanguageLoader.getTranslation("messages.load_error").replace("{0}", e.getMessage()));
        }
    }

    private void showDeleteInquiryDialog() {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("menu.delete_inquiry"), true);
        dialog.setLayout(new BorderLayout());

        // Create table model with translated column headers
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn(LanguageLoader.getTranslation("table.id"));
        model.addColumn(LanguageLoader.getTranslation("table.details"));

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Create button panel with translated buttons
        JPanel buttonPanel = new JPanel();
        JButton deleteButton = new JButton(LanguageLoader.getTranslation("actions.delete"));
        JButton cancelButton = new JButton(LanguageLoader.getTranslation("actions.cancel"));
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Load inquiries
        try {
            List<Inquiry> inquiries = dbAdapter.getAllInquiries();
            for (Inquiry inquiry : inquiries) {
                model.addRow(new Object[]{inquiry.getId(), inquiry.getComments()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                LanguageLoader.getTranslation("messages.load_error").replace("{0}", e.getMessage()),
                LanguageLoader.getTranslation("messages.e"),
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Add action listeners
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog,
                    LanguageLoader.getTranslation("messages.select_item"),
                    LanguageLoader.getTranslation("messages.e"),
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            int inquiryId = (int) table.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(dialog,
                LanguageLoader.getTranslation("confirmations.delete_message"),
                LanguageLoader.getTranslation("confirmations.delete_inquiry"),
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = dbAdapter.deleteInquiry(inquiryId);
                if (success) {
                    model.removeRow(selectedRow);
                    JOptionPane.showMessageDialog(dialog,
                        LanguageLoader.getTranslation("messages.deleted"),
                        LanguageLoader.getTranslation("messages.success"),
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        LanguageLoader.getTranslation("messages.delete_error").replace("{0}", "Failed to delete inquiry"),
                        LanguageLoader.getTranslation("messages.e"),
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showAddLocationDialog() {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("menu.add_location"), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel(LanguageLoader.getTranslation("labels.location_name")), gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Address
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel(LanguageLoader.getTranslation("labels.address")), gbc);
        gbc.gridx = 1;
        JTextField addressField = new JTextField(20);
        formPanel.add(addressField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton(LanguageLoader.getTranslation("actions.save"));
        JButton cancelButton = new JButton(LanguageLoader.getTranslation("actions.cancel"));

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String address = addressField.getText().trim();

                if (name.isEmpty() || address.isEmpty()) {
                    showError(LanguageLoader.getTranslation("messages.error"), 
                            LanguageLoader.getTranslation("messages.required_fields"));
                    return;
                }

                Location location = new Location(name, address);
                dbAdapter.addLocation(location);
                dialog.dispose();
                showViewLocationsDialog();
            } catch (Exception ex) {
                showError(LanguageLoader.getTranslation("messages.error"), 
                         LanguageLoader.getTranslation("messages.save_error").replace("{0}", ex.getMessage()));
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showUpdateLocationDialog() {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("menu.update_location"), true);
        dialog.setLayout(new BorderLayout());

        // Create table model with translated column headers
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn(LanguageLoader.getTranslation("table.id"));
        model.addColumn(LanguageLoader.getTranslation("labels.location_name"));
        model.addColumn(LanguageLoader.getTranslation("labels.address"));

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        dialog.add(scrollPane, BorderLayout.CENTER);

        try {
            List<Location> locations = dbAdapter.getAllLocations();
            for (Location location : locations) {
                model.addRow(new Object[]{
                    location.getLocationId(),
                    location.getName(),
                    location.getAddress()
                });
            }
        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("messages.e"),
                     LanguageLoader.getTranslation("messages.load_error").replace("{0}", e.getMessage()));
            return;
        }

        JPanel buttonPanel = new JPanel();
        JButton updateButton = new JButton(LanguageLoader.getTranslation("actions.update"));
        JButton cancelButton = new JButton(LanguageLoader.getTranslation("actions.cancel"));

        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int locationId = (int) table.getValueAt(selectedRow, 0);
                showUpdateLocationDialog(locationId);
                dialog.dispose();
            } else {
                showError(LanguageLoader.getTranslation("messages.e"),
                         LanguageLoader.getTranslation("messages.select_item"));
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(updateButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showViewLocationsDialog() {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("menu.view_locations"), true);
        dialog.setLayout(new BorderLayout(10, 10));

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn(LanguageLoader.getTranslation("table.id"));
        model.addColumn(LanguageLoader.getTranslation("labels.location_name"));
        model.addColumn(LanguageLoader.getTranslation("labels.address"));

        JTable table = new JTable(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(400);
        
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(scrollPane, BorderLayout.CENTER);

        try {
            List<Location> locations = dbAdapter.getAllLocations();
            for (Location location : locations) {
                model.addRow(new Object[]{
                    location.getLocationId(),
                    location.getName(),
                    location.getAddress()
                });
            }
        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("messages.e"),
                     LanguageLoader.getTranslation("messages.load_error").replace("{0}", e.getMessage()));
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton editButton = new JButton(LanguageLoader.getTranslation("actions.edit"));
        JButton closeButton = new JButton(LanguageLoader.getTranslation("actions.close"));

        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int locationId = (int) table.getValueAt(selectedRow, 0);
                showUpdateLocationDialog(locationId);
                refreshLocationTable(model);
            } else {
                showError(LanguageLoader.getTranslation("messages.e"),
                         LanguageLoader.getTranslation("messages.select_item"));
            }
        });

        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(editButton);
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showDeleteLocationDialog() {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("menu.delete_location"), true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Location Selection
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel selectLabel = new JLabel(LanguageLoader.getTranslation("labels.select_location"));
        selectLabel.setPreferredSize(new Dimension(200, 25));
        dialog.add(selectLabel, gbc);

        gbc.gridx = 1;
        JComboBox<Location> locationCombo = new JComboBox<>();
        locationCombo.setPreferredSize(new Dimension(300, 25));
        try {
            List<Location> locations = dbAdapter.getAllLocations();
            locations.forEach(locationCombo::addItem);
        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("messages.e"),
                     LanguageLoader.getTranslation("messages.load_error").replace("{0}", e.getMessage()));
        }
        dialog.add(locationCombo, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton deleteButton = new JButton(LanguageLoader.getTranslation("actions.delete"));
        JButton cancelButton = new JButton(LanguageLoader.getTranslation("actions.cancel"));
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        deleteButton.addActionListener(e -> {
            try {
                Location selected = (Location) locationCombo.getSelectedItem();
                if (selected != null) {
                    int confirm = JOptionPane.showConfirmDialog(dialog,
                        LanguageLoader.getTranslation("confirmations.delete_message"),
                        LanguageLoader.getTranslation("confirmations.delete_location"),
                        JOptionPane.YES_NO_OPTION);
                    
                    if (confirm == JOptionPane.YES_OPTION) {
                        dbAdapter.deleteLocation(selected.getLocationId());
                        dialog.dispose();
                        showSuccess(LanguageLoader.getTranslation("messages.success"),
                                  LanguageLoader.getTranslation("messages.deleted"));
                    }
                }
            } catch (SQLException ex) {
                showError(LanguageLoader.getTranslation("messages.e"),
                         LanguageLoader.getTranslation("messages.delete_error").replace("{0}", ex.getMessage()));
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showUpdateLocationDialog(int locationId) {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("menu.update_location"), true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        try {
            Location location = null;
            List<Location> locations = dbAdapter.getAllLocations();
            for (Location l : locations) {
                if (l.getLocationId() == locationId) {
                    location = l;
                    break;
                }
            }

            if (location == null) {
                showError(LanguageLoader.getTranslation("messages.e"),
                         LanguageLoader.getTranslation("messages.location_not_found"));
                return;
            }

            // Name
            gbc.gridx = 0; gbc.gridy = 0;
            JLabel nameLabel = new JLabel(LanguageLoader.getTranslation("labels.location_name") + ":");
            nameLabel.setPreferredSize(new Dimension(200, 25));
            dialog.add(nameLabel, gbc);

            gbc.gridx = 1;
            JTextField nameField = new JTextField(location.getName());
            nameField.setPreferredSize(new Dimension(300, 25));
            dialog.add(nameField, gbc);

            // Address
            gbc.gridx = 0; gbc.gridy = 1;
            JLabel addressLabel = new JLabel(LanguageLoader.getTranslation("labels.address") + ":");
            addressLabel.setPreferredSize(new Dimension(200, 25));
            dialog.add(addressLabel, gbc);

            gbc.gridx = 1;
            JTextField addressField = new JTextField(location.getAddress());
            addressField.setPreferredSize(new Dimension(300, 25));
            dialog.add(addressField, gbc);

            // Buttons
            gbc.gridx = 0; gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            JButton saveButton = new JButton(LanguageLoader.getTranslation("actions.save"));
            JButton cancelButton = new JButton(LanguageLoader.getTranslation("actions.cancel"));
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            dialog.add(buttonPanel, gbc);

            Location finalLocation = location;
            saveButton.addActionListener(e -> {
                try {
                    String name = nameField.getText().trim();
                    String address = addressField.getText().trim();

                    if (name.isEmpty() || address.isEmpty()) {
                        showError(LanguageLoader.getTranslation("messages.e"),
                                LanguageLoader.getTranslation("messages.required_fields"));
                        return;
                    }

                    finalLocation.setName(name);
                    finalLocation.setAddress(address);
                    dbAdapter.updateLocation(finalLocation);
                    dialog.dispose();
                    showSuccess(LanguageLoader.getTranslation("messages.success"),
                              LanguageLoader.getTranslation("messages.updated"));
                } catch (SQLException ex) {
                    showError(LanguageLoader.getTranslation("messages.e"),
                            LanguageLoader.getTranslation("messages.update_error").replace("{0}", ex.getMessage()));
                }
            });

            cancelButton.addActionListener(e -> dialog.dispose());

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("messages.e"),
                     LanguageLoader.getTranslation("messages.load_error").replace("{0}", e.getMessage()));
        }
    }

    private void refreshLocationTable(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<Location> locations = dbAdapter.getAllLocations();
            for (Location location : locations) {
                model.addRow(new Object[]{
                    location.getLocationId(),
                    location.getName(),
                    location.getAddress()
                });
            }
        } catch (SQLException e) {
            showError("Erreur", "Échec du rafraîchissement des emplacements: " + e.getMessage());
        }
    }

    private void showUpdateSupplyDialog(int supplyId) {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("gui.dialog.update_supply"), true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        try {
            Supply supply = null;
            List<Supply> supplies = dbAdapter.getAllSupplies();
            for (Supply s : supplies) {
                if (s.getId() == supplyId) {
                    supply = s;
                    break;
                }
            }

            if (supply == null) {
                showError(LanguageLoader.getTranslation("gui.error.title"), 
                         LanguageLoader.getTranslation("gui.error.supply_not_found"));
                return;
            }

            // Type
            gbc.gridx = 0; gbc.gridy = 0;
            JLabel typeLabel = new JLabel(LanguageLoader.getTranslation("gui.supply.type"));
            typeLabel.setPreferredSize(new Dimension(200, 25));
            dialog.add(typeLabel, gbc);

            gbc.gridx = 1;
            JTextField typeField = new JTextField(supply.getType());
            typeField.setPreferredSize(new Dimension(300, 25));
            dialog.add(typeField, gbc);

            // Comments
            gbc.gridx = 0; gbc.gridy = 1;
            JLabel commentsLabel = new JLabel(LanguageLoader.getTranslation("gui.supply.comments"));
            commentsLabel.setPreferredSize(new Dimension(200, 25));
            dialog.add(commentsLabel, gbc);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1.0;
            JTextArea commentsArea = new JTextArea(supply.getComments());
            commentsArea.setLineWrap(true);
            commentsArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(commentsArea);
            scrollPane.setPreferredSize(new Dimension(300, 100));
            dialog.add(scrollPane, gbc);

            // Buttons
            gbc.gridx = 0; gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.weighty = 0.0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.CENTER;
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            JButton saveButton = new JButton(LanguageLoader.getTranslation("gui.button.save"));
            JButton cancelButton = new JButton(LanguageLoader.getTranslation("gui.button.cancel"));
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            dialog.add(buttonPanel, gbc);

            Supply finalSupply = supply;
            saveButton.addActionListener(e -> {
                try {
                    String type = typeField.getText().trim();
                    String comments = commentsArea.getText().trim();

                    if (type.isEmpty()) {
                        showError(LanguageLoader.getTranslation("gui.error.title"),
                                LanguageLoader.getTranslation("gui.validation.type_required"));
                        return;
                    }

                    finalSupply.setType(type);
                    finalSupply.setComments(comments);
                    dbAdapter.updateSupply(finalSupply);
                    dialog.dispose();
                    showSuccess(LanguageLoader.getTranslation("gui.success.title"),
                              LanguageLoader.getTranslation("gui.success.supply_updated"));
                } catch (SQLException ex) {
                    showError(LanguageLoader.getTranslation("gui.error.title"),
                            LanguageLoader.getTranslation("gui.error.update_supply") + " " + ex.getMessage());
                }
            });

            cancelButton.addActionListener(e -> dialog.dispose());

            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("gui.error.title"),
                     LanguageLoader.getTranslation("gui.error.load_supply") + " " + e.getMessage());
        }
    }

    private void refreshSupplyTable(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<Supply> supplies = dbAdapter.getAllSupplies();
            for (Supply supply : supplies) {
                model.addRow(new Object[]{
                    supply.getId(),
                    supply.getType(),
                    supply.getComments()
                });
            }
        } catch (SQLException e) {
            showError("Erreur", "Échec du rafraîchissement des articles: " + e.getMessage());
        }
    }

    private void showManageVictimDialog(DisasterVictim victim) {
        JDialog dialog = new JDialog(this, 
            LanguageLoader.getTranslation("gui.victim.manage")
                .replace("{0}", victim.getFirstName())
                .replace("{1}", victim.getLastName()), 
            true);
        dialog.setLayout(new BorderLayout());

        // Create tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(LanguageLoader.getTranslation("gui.victim.details_tab"), createVictimDetailsPanel(victim));
        tabbedPane.addTab(LanguageLoader.getTranslation("gui.victim.medical_tab"), createMedicalRecordsPanel(victim));

        dialog.add(tabbedPane, BorderLayout.CENTER);

        // Add close button
        JButton closeButton = new JButton(LanguageLoader.getTranslation("gui.button.close"));
        closeButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private JPanel createVictimDetailsPanel(DisasterVictim victim) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // First Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel firstNameLabel = new JLabel(LanguageLoader.getTranslation("labels.first_name"));
        firstNameLabel.setPreferredSize(new Dimension(200, 25));
        panel.add(firstNameLabel, gbc);

        gbc.gridx = 1;
        JTextField firstNameField = new JTextField(victim.getFirstName() != null ? victim.getFirstName() : "N/A");
        firstNameField.setPreferredSize(new Dimension(300, 25));
        firstNameField.setEditable(false);
        panel.add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lastNameLabel = new JLabel(LanguageLoader.getTranslation("labels.last_name"));
        lastNameLabel.setPreferredSize(new Dimension(200, 25));
        panel.add(lastNameLabel, gbc);

        gbc.gridx = 1;
        JTextField lastNameField = new JTextField(victim.getLastName() != null ? victim.getLastName() : "N/A");
        lastNameField.setPreferredSize(new Dimension(300, 25));
        lastNameField.setEditable(false);
        panel.add(lastNameField, gbc);

        // Date of Birth
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel dobLabel = new JLabel(LanguageLoader.getTranslation("labels.date_of_birth"));
        dobLabel.setPreferredSize(new Dimension(200, 25));
        panel.add(dobLabel, gbc);

        gbc.gridx = 1;
        String dobText = victim.getDateOfBirth() != null ? victim.getDateOfBirth().toString() : "N/A";
        JTextField dobField = new JTextField(dobText);
        dobField.setPreferredSize(new Dimension(300, 25));
        dobField.setEditable(false);
        panel.add(dobField, gbc);

        // Gender
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel genderLabel = new JLabel(LanguageLoader.getTranslation("labels.gender"));
        genderLabel.setPreferredSize(new Dimension(200, 25));
        panel.add(genderLabel, gbc);

        gbc.gridx = 1;
        String genderText = victim.getGender() != null ? victim.getGender() : "N/A";
        JTextField genderField = new JTextField(genderText);
        genderField.setPreferredSize(new Dimension(300, 25));
        genderField.setEditable(false);
        panel.add(genderField, gbc);

        // Location
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel locationLabel = new JLabel(LanguageLoader.getTranslation("labels.location"));
        locationLabel.setPreferredSize(new Dimension(200, 25));
        panel.add(locationLabel, gbc);

        gbc.gridx = 1;
        String locationText = victim.getLocation() != null ? 
            victim.getLocation().getName() + " (" + victim.getLocation().getAddress() + ")" : 
            "N/A";
        JTextField locationField = new JTextField(locationText);
        locationField.setPreferredSize(new Dimension(300, 25));
        locationField.setEditable(false);
        panel.add(locationField, gbc);

        return panel;
    }

    private JPanel createMedicalRecordsPanel(DisasterVictim victim) {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create table model for medical records
        String[] columnNames = {
            LanguageLoader.getTranslation("table.id"),
            LanguageLoader.getTranslation("table.date"),
            LanguageLoader.getTranslation("table.location"),
            LanguageLoader.getTranslation("table.treatment")
        };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create table and scroll pane
        JTable table = new JTable(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(300);
        
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        // Set up cell renderer for the treatment details column to wrap text
        table.getColumnModel().getColumn(3).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JTextArea area = new JTextArea(value != null ? value.toString() : "");
                area.setWrapStyleWord(true);
                area.setLineWrap(true);
                area.setMargin(new Insets(5, 5, 5, 5));
                return area;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Load medical records
        try {
            List<MedicalRecord> records = dbAdapter.getMedicalRecords(victim.getId());
            for (MedicalRecord record : records) {
                model.addRow(new Object[]{
                    record.getId(),
                    record.getDateOfTreatment().toLocalDate(),
                    record.getLocation(),
                    record.getTreatmentDetails()
                });
            }
        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("gui.error.title"),
                     LanguageLoader.getTranslation("gui.error.load_medical_records") + " " + e.getMessage());
        }

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton addButton = new JButton(LanguageLoader.getTranslation("gui.button.add_medical"));
        JButton editButton = new JButton(LanguageLoader.getTranslation("gui.button.edit_medical"));
        JButton deleteButton = new JButton(LanguageLoader.getTranslation("gui.button.delete"));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Add record button action
        addButton.addActionListener(e -> showAddMedicalRecordDialog(victim));

        // Edit record button action
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int recordId = (int) model.getValueAt(selectedRow, 0);
                try {
                    List<MedicalRecord> records = dbAdapter.getMedicalRecords(victim.getId());
                    for (MedicalRecord record : records) {
                        if (record.getId() == recordId) {
                            showEditMedicalRecordDialog(record, victim);
                            break;
                        }
                    }
                } catch (SQLException ex) {
                    showError(LanguageLoader.getTranslation("gui.error.title"),
                            LanguageLoader.getTranslation("gui.error.load_medical_record") + " " + ex.getMessage());
                }
            } else {
                showError(LanguageLoader.getTranslation("gui.error.title"),
                         LanguageLoader.getTranslation("gui.error.select_medical_record"));
            }
        });

        // Delete record button action
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int recordId = (int) model.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(panel,
                    LanguageLoader.getTranslation("gui.medical.delete_message"),
                    LanguageLoader.getTranslation("gui.medical.confirm_delete"),
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        if (dbAdapter.deleteMedicalRecord(recordId)) {
                            model.removeRow(selectedRow);
                            showSuccess(LanguageLoader.getTranslation("gui.success.title"),
                                      LanguageLoader.getTranslation("gui.success.deleted"));
                        }
                    } catch (SQLException ex) {
                        showError(LanguageLoader.getTranslation("gui.error.title"),
                                LanguageLoader.getTranslation("gui.error.delete_medical_record") + " " + ex.getMessage());
                    }
                }
            } else {
                showError(LanguageLoader.getTranslation("gui.error.title"),
                         LanguageLoader.getTranslation("gui.error.select_medical_record"));
            }
        });

        return panel;
    }

    private void showAddMedicalRecordDialog(DisasterVictim victim) {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("gui.dialog.add_medical_record"), true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Treatment details
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel treatmentLabel = new JLabel(LanguageLoader.getTranslation("gui.medical.treatment"));
        treatmentLabel.setPreferredSize(new Dimension(200, 25));
        dialog.add(treatmentLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JTextArea treatmentArea = new JTextArea();
        treatmentArea.setLineWrap(true);
        treatmentArea.setWrapStyleWord(true);
        JScrollPane treatmentScroll = new JScrollPane(treatmentArea);
        treatmentScroll.setPreferredSize(new Dimension(300, 100));
        dialog.add(treatmentScroll, gbc);

        // Date
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel dateLabel = new JLabel(LanguageLoader.getTranslation("gui.medical.date"));
        dateLabel.setPreferredSize(new Dimension(200, 25));
        dialog.add(dateLabel, gbc);

        gbc.gridx = 1;
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setPreferredSize(new Dimension(300, 25));
        dialog.add(dateSpinner, gbc);

        // Location
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel locationLabel = new JLabel(LanguageLoader.getTranslation("gui.medical.location"));
        locationLabel.setPreferredSize(new Dimension(200, 25));
        dialog.add(locationLabel, gbc);

        gbc.gridx = 1;
        JComboBox<Location> locationCombo = new JComboBox<>();
        locationCombo.setPreferredSize(new Dimension(300, 25));
        try {
            List<Location> locations = dbAdapter.getAllLocations();
            locations.forEach(locationCombo::addItem);
        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("gui.error.title"),
                     LanguageLoader.getTranslation("gui.error.locations") + " " + e.getMessage());
        }
        dialog.add(locationCombo, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = new JButton(LanguageLoader.getTranslation("gui.button.save"));
        JButton cancelButton = new JButton(LanguageLoader.getTranslation("gui.button.cancel"));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
            try {
                String treatment = treatmentArea.getText().trim();
                if (treatment.isEmpty()) {
                    showError(LanguageLoader.getTranslation("gui.error.title"),
                            LanguageLoader.getTranslation("gui.validation.treatment_required"));
                    return;
                }

                java.util.Date date = (java.util.Date) dateSpinner.getValue();
                LocalDate localDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                Location selectedLocation = (Location) locationCombo.getSelectedItem();

                if (selectedLocation == null) {
                    showError(LanguageLoader.getTranslation("gui.error.title"),
                            LanguageLoader.getTranslation("gui.validation.location_required"));
                    return;
                }

                if (dbAdapter.addMedicalRecord(victim.getId(), treatment, localDate, selectedLocation.getLocationId())) {
                    dialog.dispose();
                    showSuccess(LanguageLoader.getTranslation("gui.success.title"),
                              LanguageLoader.getTranslation("gui.success.medical_record_added"));
                    showManageVictimDialog(victim); // Refresh the dialog
                }
            } catch (SQLException ex) {
                showError(LanguageLoader.getTranslation("gui.error.title"),
                         LanguageLoader.getTranslation("gui.error.add_medical_record") + " " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditMedicalRecordDialog(MedicalRecord record, DisasterVictim victim) {
        JDialog dialog = new JDialog(this, LanguageLoader.getTranslation("actions.edit_medical"), true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Treatment details
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel treatmentLabel = new JLabel(LanguageLoader.getTranslation("gui.medical.treatment"));
        treatmentLabel.setPreferredSize(new Dimension(200, 25));
        dialog.add(treatmentLabel, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        JTextArea treatmentArea = new JTextArea();
        treatmentArea.setText(record.getTreatmentDetails());
        treatmentArea.setLineWrap(true);
        treatmentArea.setWrapStyleWord(true);
        JScrollPane treatmentScroll = new JScrollPane(treatmentArea);
        treatmentScroll.setPreferredSize(new Dimension(300, 100));
        dialog.add(treatmentScroll, gbc);

        // Date
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel dateLabel = new JLabel(LanguageLoader.getTranslation("gui.medical.date"));
        dateLabel.setPreferredSize(new Dimension(200, 25));
        dialog.add(dateLabel, gbc);

        gbc.gridx = 1;
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(java.sql.Timestamp.valueOf(record.getDateOfTreatment()));
        dateSpinner.setPreferredSize(new Dimension(300, 25));
        dialog.add(dateSpinner, gbc);

        // Location
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel locationLabel = new JLabel(LanguageLoader.getTranslation("gui.medical.location"));
        locationLabel.setPreferredSize(new Dimension(200, 25));
        dialog.add(locationLabel, gbc);

        gbc.gridx = 1;
        JComboBox<Location> locationCombo = new JComboBox<>();
        locationCombo.setPreferredSize(new Dimension(300, 25));
        try {
            List<Location> locations = dbAdapter.getAllLocations();
            Location currentLocation = null;
            for (Location loc : locations) {
                locationCombo.addItem(loc);
                if (loc.getName().equals(record.getLocation())) {
                    currentLocation = loc;
                }
            }
            if (currentLocation != null) {
                locationCombo.setSelectedItem(currentLocation);
            }
        } catch (SQLException e) {
            showError(LanguageLoader.getTranslation("messages.e"), 
                     LanguageLoader.getTranslation("gui.e.locations") + " " + e.getMessage());
        }
        dialog.add(locationCombo, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton saveButton = new JButton(LanguageLoader.getTranslation("actions.save"));
        JButton cancelButton = new JButton(LanguageLoader.getTranslation("actions.cancel"));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        saveButton.addActionListener(e -> {
            try {
                String treatment = treatmentArea.getText().trim();
                if (treatment.isEmpty()) {
                    showError(LanguageLoader.getTranslation("messages.e"), 
                            LanguageLoader.getTranslation("gui.validation.treatment_required"));
                    return;
                }

                java.util.Date date = (java.util.Date) dateSpinner.getValue();
                LocalDate localDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                Location selectedLocation = (Location) locationCombo.getSelectedItem();

                if (selectedLocation == null) {
                    showError(LanguageLoader.getTranslation("messages.e"), 
                            LanguageLoader.getTranslation("gui.validation.location_required"));
                    return;
                }

                record.setTreatmentDetails(treatment);
                record.setDateOfTreatment(localDate.atStartOfDay());
                record.setLocation(selectedLocation.getName());

                if (dbAdapter.updateMedicalRecord(record)) {
                    dialog.dispose();
                    showSuccess(LanguageLoader.getTranslation("messages.success"), 
                              LanguageLoader.getTranslation("messages.updated"));
                    showManageVictimDialog(victim); // Refresh the dialog
                }
            } catch (SQLException ex) {
                showError(LanguageLoader.getTranslation("messages.e"), 
                         LanguageLoader.getTranslation("gui.e.add_medical_record") + " " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new DisasterManagementGUI().setVisible(true);
        });
    }
} 