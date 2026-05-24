/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mo.it103.ioshua.surigao;

// import for GUI
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Ioshua Jericho Surigao
 * 
 */
public class MOIT103IoshuaSurigao {
    // initialize the CSV files needed
    static String employeeCSV = "resources/MotorPH_Employee Data - Employee Details.csv";
    static String attendanceCSV = "resources/MotorPH_Employee Data - Attendance Record.csv";

    static JLabel empNumLabel = new JLabel("Employee #: ");
    static JLabel empNameLabel = new JLabel("Employee Name: ");
    static JLabel empBdayLabel = new JLabel("Birthday: ");

    static void loginWindow() {

        JFrame frame = new JFrame("Login - MotorPH Payroll System");
        frame.setSize(350, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(30, 30, 100, 25);

        JTextField userField = new JTextField();
        userField.setBounds(130, 30, 150, 25);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(30, 70, 100, 25);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(130, 70, 150, 25);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(130, 110, 100, 30);

        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(loginBtn);

        loginBtn.addActionListener(e -> {

            String username = userField.getText();
            String password = new String(passField.getPassword());

            if (username.equals("admin") && password.equals("admin")) {
                frame.dispose();
                mainGUI();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password.");
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    static void mainGUI() {

        JFrame frame = new JFrame("MotorPH Payroll System");
        frame.setSize(900, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(0xA5A5A4));
        leftPanel.setPreferredSize(new Dimension(200, 500));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        JLabel greet = new JLabel("Welcome, Admin");
        greet.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnPayroll = new JButton("Calculate Payroll");
        btnPayroll.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(greet);
        leftPanel.add(Box.createVerticalStrut(40));
        leftPanel.add(btnPayroll);

        CardLayout cardLayout = new CardLayout();
        JPanel rightPanel = new JPanel(cardLayout);

        JPanel payrollPanel = new JPanel(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        headerPanel.add(empNumLabel);
        headerPanel.add(empNameLabel);
        headerPanel.add(empBdayLabel);
        headerPanel.add(Box.createVerticalStrut(10));

        JPanel inputPanel = new JPanel();

        JTextField empField = new JTextField(10);
        JButton submitBtn = new JButton("Submit");
        JButton clearBtn = new JButton("Clear");

        inputPanel.add(new JLabel("Enter Employee #: "));
        inputPanel.add(empField);
        inputPanel.add(submitBtn);
        inputPanel.add(clearBtn);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(inputPanel, BorderLayout.SOUTH);

        String[] columns = {"Month","Cutoff","Hours","Gross","SSS","PhilHealth","PagIBIG","Tax","Net"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);

        payrollPanel.add(topContainer, BorderLayout.NORTH);
        payrollPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        rightPanel.add(new JPanel(), "EMPTY");
        rightPanel.add(payrollPanel, "PAYROLL");

        btnPayroll.addActionListener(e -> cardLayout.show(rightPanel, "PAYROLL"));

        submitBtn.addActionListener(e -> populateTable(empField.getText(), model));

        clearBtn.addActionListener(e -> {
            model.setRowCount(0);
            empNumLabel.setText("Employee #: ");
            empNameLabel.setText("Employee Name: ");
            empBdayLabel.setText("Birthday: ");
            empField.setText("");
        });

        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(rightPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    static void populateTable(String inputEmpNum, DefaultTableModel model) {

        model.setRowCount(0);

        String employeeNum = "";
        String f_name = "";
        String l_name = "";
        String birthday = "";
        double hourlyRate = 0;
        boolean match = false;

        try (CSVReader reader = new CSVReader(new FileReader(employeeCSV))) {
            reader.readNext();
            String[] data;

            while ((data = reader.readNext()) != null) {
                if (data[0].equals(inputEmpNum)) {
                    employeeNum = data[0];
                    l_name = data[1];
                    f_name = data[2];
                    birthday = data[3];
                    hourlyRate = Double.parseDouble(data[data.length - 1].replace("\"", "").trim());
                    match = true;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!match) {
            JOptionPane.showMessageDialog(null, "Employee not found.");
            return;
        }

        empNumLabel.setText("Employee #: " + employeeNum);
        empNameLabel.setText("Employee Name: " + f_name + " " + l_name);
        empBdayLabel.setText("Birthday: " + birthday);

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm");

        for (int month_num = 6; month_num <= 12; month_num++) {

            double first = 0;
            double second = 0;
            int days = YearMonth.of(2024, month_num).lengthOfMonth();

            try (BufferedReader br = new BufferedReader(new FileReader(attendanceCSV))) {

                br.readLine();
                String line;

                while ((line = br.readLine()) != null) {

                    String[] data = line.split(",");

                    if (data[0].equals(inputEmpNum)) {

                        String[] dateParts = data[3].split("/");
                        int month = Integer.parseInt(dateParts[0]);
                        int day = Integer.parseInt(dateParts[1]);
                        int year = Integer.parseInt(dateParts[2]);

                        if (year != 2024 || month != month_num) continue;

                        LocalTime in = LocalTime.parse(data[4].trim(), timeFormat);
                        LocalTime out = LocalTime.parse(data[5].trim(), timeFormat);

                        double hours = computeHours(in, out);

                        if (day <= 15) first += hours;
                        else second += hours;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            String monthName = Month.of(month_num).name();

            double grossFirst = first * hourlyRate;
            double grossSecond = second * hourlyRate;
            double monthlyGross = grossFirst + grossSecond;

            double sss = SSS(monthlyGross);
            double ph = PhilHealth(monthlyGross);
            double pi = PagIBIG(monthlyGross);
            double tax = WithholdingTax(monthlyGross);

            double netFirst = grossFirst;
            double netSecond = grossSecond - (sss + ph + pi + tax);

            model.addRow(new Object[]{
                monthName, "1-15",
                String.format("%.2f", first),
                String.format("%.2f", grossFirst),
                "0.00","0.00","0.00","0.00",
                String.format("%.2f", netFirst)
            });

            model.addRow(new Object[]{
                monthName, "16-" + days,
                String.format("%.2f", second),
                String.format("%.2f", grossSecond),
                String.format("%.2f", sss),
                String.format("%.2f", ph),
                String.format("%.2f", pi),
                String.format("%.2f", tax),
                String.format("%.2f", netSecond)
            });
        }
    }

    static double computeHours(LocalTime in, LocalTime out) {

        LocalTime grace = LocalTime.of(8, 10);
        LocalTime cutoff = LocalTime.of(17, 0);

        if (out.isAfter(cutoff)) out = cutoff;

        long mins = Duration.between(in, out).toMinutes();

        if (mins > 60) mins -= 60;
        else mins = 0;

        double hrs = mins / 60.0;

        if (!in.isAfter(grace)) return 8.0;

        return Math.min(hrs, 8.0);
    }

    static double SSS(double gross) {
        return gross >= 24750 ? 1125 : 900;
    }

    static double PhilHealth(double gross) {
        double p = gross * 0.03;
        if (p < 300) p = 300;
        if (p > 1800) p = 1800;
        return p / 2;
    }

    static double PagIBIG(double gross) {
        return Math.min(gross * 0.02, 100);
    }

    static double WithholdingTax(double gross) {
        double taxable = gross - (SSS(gross) + PhilHealth(gross) + PagIBIG(gross));

        if (taxable <= 20832) return 0;
        else if (taxable <= 33332) return (taxable - 20833) * 0.20;
        else if (taxable <= 66666) return 2500 + (taxable - 33332) * 0.25;
        else if (taxable <= 166666) return 10833 + (taxable - 66666) * 0.30;
        else if (taxable <= 666666) return 40833.33 + (taxable - 166667) * 0.32;
        else return 200833.33 + (taxable - 666667) * 0.35;
    }
    
    public static void main(String[] args) { 
        loginWindow();
    }
}
