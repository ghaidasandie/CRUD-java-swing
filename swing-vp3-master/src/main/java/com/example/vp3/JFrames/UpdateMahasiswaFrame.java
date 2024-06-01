package com.example.vp3.JFrames;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class UpdateMahasiswaFrame {
    private MahasiswaFrame parentFrame;
    private int mahasiswaId;
    private String mahasiswaNama;
    private String mahasiswaNim;

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost/mahasiswaku";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public UpdateMahasiswaFrame(MahasiswaFrame parentFrame, int id, String nama, String nim) {
        this.parentFrame = parentFrame;
        this.mahasiswaId = id;
        this.mahasiswaNama = nama;
        this.mahasiswaNim = nim;
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    private void createAndShowGUI() {
        JFrame updateFrame = new JFrame("Update Mahasiswa");
        updateFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        JLabel nameLabel = new JLabel("Nama:");
        JTextField nameField = new JTextField(mahasiswaNama);
        JLabel nimLabel = new JLabel("NIM:");
        JTextField nimField = new JTextField(mahasiswaNim);
        JButton submitButton = new JButton("Update");

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nama = nameField.getText();
                String nim = nimField.getText();
                if (nama.isEmpty() || nim.isEmpty()) {
                    JOptionPane.showMessageDialog(updateFrame, "Nama and NIM cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    updateMahasiswaInDatabase(mahasiswaId, nama, nim);
                    parentFrame.refreshTableData();
                    updateFrame.dispose();
                }
            }
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(nimLabel);
        panel.add(nimField);
        panel.add(submitButton);

        updateFrame.getContentPane().add(panel, BorderLayout.CENTER);
        updateFrame.pack();
        updateFrame.setVisible(true);
        updateFrame.setBounds(100, 100, 300, 200);
    }

    private void updateMahasiswaInDatabase(int id, String nama, String nim) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("UPDATE mahasiswa SET nama = ?, nim = ? WHERE id = ?")) {
            stmt.setString(1, nama);
            stmt.setString(2, nim);
            stmt.setInt(3, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Mahasiswa updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error updating mahasiswa: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("SQLException: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
