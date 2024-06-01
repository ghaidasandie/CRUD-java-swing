package com.example.vp3.JFrames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class MahasiswaFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Integer> databaseIds; 
    private static final String DB_URL = "jdbc:mysql://localhost/mahasiswaku";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public MahasiswaFrame() {
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    private void createAndShowGUI() {
        JFrame jFrame = new JFrame("Aplikasi Mahasiswa");
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("No");
        tableModel.addColumn("Nama");
        tableModel.addColumn("NIM");
        tableModel.addColumn("Update");
        tableModel.addColumn("Delete");

        databaseIds = new ArrayList<>(); 
        refreshTableData();

        table = new JTable(tableModel);
        table.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer("Update"));
        table.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JButton("Update")));
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer("Delete"));
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JButton("Delete")));

        JScrollPane pane = new JScrollPane(table);

       
        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("Create Mahasiswa");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateMahasiswaFrame(MahasiswaFrame.this);
            }
        });
        buttonPanel.add(createButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buttonPanel, BorderLayout.NORTH); 
        panel.add(pane, BorderLayout.CENTER);

        jFrame.getContentPane().add(panel, BorderLayout.CENTER);
        jFrame.pack();
        jFrame.setVisible(true);
        jFrame.setBounds(100, 100, 600, 400);
    }

    public void refreshTableData() {
        tableModel.setRowCount(0); 
        databaseIds.clear(); 

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM mahasiswa");
             ResultSet rs = preparedStatement.executeQuery()) {

            int counter = 1; 
            while (rs.next()) {
                int id = rs.getInt("id");
                String nama = rs.getString("nama");
                String nim = rs.getString("nim");
                tableModel.addRow(new Object[] { counter, nama, nim, "Update", "Delete" });
                databaseIds.add(id); 
                counter++; 
            }

        } catch (SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text) {
            setText(text);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    private class ButtonEditor extends javax.swing.DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;

        public ButtonEditor(JButton button) {
            super(new javax.swing.JTextField());
            this.button = button;
            this.button.setOpaque(true);

           
            this.button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
               
                int selectedRow = table.getSelectedRow();
                int id = databaseIds.get(selectedRow); // Get the actual database ID
                String nama = (String) tableModel.getValueAt(selectedRow, 1);
                String nim = (String) tableModel.getValueAt(selectedRow, 2);

                if ("Update".equals(label)) {
                    new UpdateMahasiswaFrame(MahasiswaFrame.this, id, nama, nim);
                } else if ("Delete".equals(label)) {
                    deleteMahasiswaFromDatabase(id);
                    refreshTableData();
                }
            }
            clicked = false;
            return label;
        }
    }

    private void deleteMahasiswaFromDatabase(int id) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM mahasiswa WHERE id = ?")) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
