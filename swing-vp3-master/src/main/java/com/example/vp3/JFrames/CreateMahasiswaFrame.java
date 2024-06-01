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
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class CreateMahasiswaFrame {
    private MahasiswaFrame parentFrame;

    public CreateMahasiswaFrame(MahasiswaFrame parentFrame) {
        this.parentFrame = parentFrame;
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private void createAndShowGUI() {
        JFrame newFrame = new JFrame("Create Mahasiswa");
        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2));
        JLabel nameLabel = new JLabel("Nama:");
        JTextField nameField = new JTextField();
        JLabel nimLabel = new JLabel("NIM:");
        JTextField nimField = new JTextField();
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nama = nameField.getText();
                String nim = nimField.getText();
                addMahasiswaToDatabase(nama, nim);
                parentFrame.refreshTableData();
                newFrame.dispose();
            }
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(nimLabel);
        panel.add(nimField);
        panel.add(submitButton);

        newFrame.getContentPane().add(panel, BorderLayout.CENTER);
        newFrame.pack();
        newFrame.setVisible(true);
        newFrame.setBounds(100, 100, 300, 200);
    }

    private void addMahasiswaToDatabase(String nama, String nim) {
        String url = "jdbc:mysql://localhost/mahasiswaku?user=root&password="; 
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO mahasiswa (nama, nim) VALUES (?, ?)")) {
            stmt.setString(1, nama);
            stmt.setString(2, nim);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }
}
