/*
* Display a window with text fields (JTextField) and date input.
* Provide Save button → calls FileStorage.save().
* Show list of existing assignments.
* Notify user if input is invalid. 
*/

package ui;

//Swing Impoerts
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

import backend.AssignmentComponent;
import backend.FileStorage;

//Misc Imports 
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Time;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;


//Button Class
class Button extends JButton {
    public Button(String text) {
        super(text);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setBackground(Color.WHITE); 
        setForeground(new Color(41, 68, 90));
        setFont(new Font("Arial",Font.PLAIN,25));
    }
    

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        super.paintComponent(g2);
        g2.dispose();
    }
}
public class Form extends JFrame {

    private FileStorage fileStorage = new FileStorage();
    private DefaultTableModel tableModel;

    public Form() {
        initUI();
    }

    public void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(725, 700);
        setResizable(false);
        setTitle("NoAmnesia");
        setLayout(null);

        // setContentPane(rootPane);
        // Background color and icon
        getContentPane().setBackground(new Color(41, 68, 90));
        ImageIcon icon = new ImageIcon("resources/icon.png"); 
        setIconImage(icon.getImage());

        // Labels
        JLabel expLabel = new JLabel("Experiment Name:");
        expLabel.setBounds(50, 250, 120, 25);
        expLabel.setForeground(Color.WHITE);
        add(expLabel);

        JLabel subjLabel = new JLabel("Subject Name:");
        subjLabel.setBounds(50, 280, 120, 25);
        subjLabel.setForeground(Color.WHITE);
        add(subjLabel);

        JLabel dateLabel = new JLabel("Submission Date:");
        dateLabel.setBounds(50, 310, 120, 25);
        dateLabel.setForeground(Color.WHITE);
        add(dateLabel);

        JLabel timeLabel = new JLabel("Submission Time:");
        timeLabel.setBounds(50, 340, 120, 25);
        timeLabel.setForeground(Color.WHITE);
        add(timeLabel);
        
        // Input fields
        JTextField experimentField = new JTextField();
        experimentField.setBounds(180, 250, 200, 25);
        add(experimentField);
        
        JTextField subjectField = new JTextField();
        subjectField.setBounds(180, 280, 200, 25);
        add(subjectField);

        // Date spinner
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        dateSpinner.setBounds(180, 310, 200, 25);
        add(dateSpinner);

        // Time spinner
        SpinnerDateModel timeModel = new SpinnerDateModel();
        JSpinner timeSpinner = new JSpinner(timeModel);
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm:ss"));
        timeSpinner.setBounds(180, 340, 200, 25);
        add(timeSpinner);

        // JTable with DefaultTableModel
        String[] columns = {"Experiment", "Subject", "Date", "Time"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 70, 600, 150);
        add(scrollPane);

        // Load existing pending assignments
        List<AssignmentComponent> pendingList = fileStorage.retrievePendingAssignments();
        for (AssignmentComponent a : pendingList) {
            tableModel.addRow(new Object[]{
                a.getExperimentName(),
                a.getSubjectName(),
                a.getSubmissionDate().toString(),
                a.getSubmissionTime().toString()
            });
        }


        // Add button
        Button addButton = new Button("+ Add Deadline");
        addButton.setFont(new Font("Arial",Font.PLAIN,15));
        addButton.setBounds(550, 600, 150, 50);
        add(addButton);

        addButton.addActionListener(e -> {
            String experiment = experimentField.getText().trim();
            String subject = subjectField.getText().trim();
            if (experiment.isEmpty() || subject.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Experiment and Subject cannot be empty!");
                return;
            }

            Date submissionDate = new Date(((java.util.Date) dateSpinner.getValue()).getTime());
            Time submissionTime = new Time(((java.util.Date) timeSpinner.getValue()).getTime());

            AssignmentComponent assignment = new AssignmentComponent(
                    experiment, subject, submissionDate, submissionTime
            );

            // Add to table
            tableModel.addRow(new Object[]{
                assignment.getExperimentName(),
                assignment.getSubjectName(),
                assignment.getSubmissionDate().toString(),
                assignment.getSubmissionTime().toString()
            });

            // Save to Pending.json
            fileStorage.addAssignment(assignment);

            // Clear input fields
            experimentField.setText("");
            subjectField.setText("");
        });

        //Deletion Button
        Button deleteButton = new Button("- Delete Selected");
        deleteButton.setBounds(380, 600, 150, 50);
        deleteButton.setFont(new Font("Arial",Font.PLAIN,15));
        add(deleteButton);

        deleteButton.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a row to delete!");
                return;
            }
            String exp = (String) tableModel.getValueAt(row, 0);
            fileStorage.removePendingAssignment(exp);
            tableModel.removeRow(row);
        });

        //Refresh Button
        Button refreshButton = new Button("Refresh");
        refreshButton.setFont(new Font("Arial",Font.PLAIN,15));
        refreshButton.setBounds(15,600, 100, 50);
        add(refreshButton);
    
        refreshButton.addActionListener(e ->{
            List<AssignmentComponent> pendingList2 = fileStorage.retrievePendingAssignments();
            tableModel.setRowCount(0);             
            for (AssignmentComponent a : pendingList2) {
                tableModel.addRow(new Object[]{
                    a.getExperimentName(),
                    a.getSubjectName(),
                    a.getSubmissionDate().toString(),
                    a.getSubmissionTime().toString()
                });
            }        
        });
        //Collect Button
        Button collectButton = new Button("Fetch Assignments");
        collectButton.setBounds(135,600,170,50);
        collectButton.setFont(new Font("Arial",Font.PLAIN,15));
        add(collectButton);
        collectButton.addActionListener(e->{
            JFrame popup = new JFrame("Moodle Signin");
            popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            popup.setSize(400, 200);
            popup.setResizable(false);
            popup.setLayout(null);
            JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setBounds(40, 30, 100, 25);
            usernameLabel.setForeground(Color.WHITE);
            popup.add(usernameLabel);

            JLabel passwordLabel  = new JLabel("Password:");
            passwordLabel.setBounds(40, 70, 100, 25);
            passwordLabel.setForeground(Color.WHITE);
            popup.add(passwordLabel);
            
            // Input fields
            JTextField usenameField = new JTextField();
            usenameField.setBounds(120, 30, 200, 25);
            popup.add(usenameField);
            
            JPasswordField passwordField = new JPasswordField();
            passwordField.setBounds(120, 70, 200, 25);
            popup.add(passwordField);

            //Login Button
            Button loginButton = new Button("Login");
            loginButton.setBounds(135,115,100,25);
            loginButton.setFont(new Font("Arial",Font.PLAIN,15));
            popup.add(loginButton);
        
            loginButton.addActionListener(ee -> {
                popup.dispose();
                JDialog loadingDialog = new JDialog(this, "Loading...", true);
                JLabel loadingLabel = new JLabel("Fetching assignments, please wait...");
                loadingLabel.setHorizontalAlignment(JLabel.CENTER);
                loadingDialog.add(loadingLabel);
                loadingDialog.setSize(300, 100);
                loadingDialog.setLocationRelativeTo(this);
                new Thread(() -> {
                    try {
                        String username = usenameField.getText();
                        String passwordString = new String(passwordField.getPassword());
                        ProcessBuilder process = new ProcessBuilder("py", "lib/moodle.py", username, passwordString);
                        process.redirectErrorStream(true);
                        Process p = process.start();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println("PYTHON: " + line);
                        }
                        int exitCode = p.waitFor();
                        System.out.println("Python finished with code: " + exitCode);
                        SwingUtilities.invokeLater(() -> refreshButton.doClick());

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        SwingUtilities.invokeLater(() -> loadingDialog.dispose());
                    }
                }).start();

                loadingDialog.setVisible(true);
            });
            // loginButton.addActionListener(ee-> {
            //     try {
            //         String passwordString = new String(passwordField.getPassword());                    
            //         System.out.println("Starting");
            //         ProcessBuilder process = new ProcessBuilder("py", "lib/moodle.py",usenameField.getText(),passwordString);
            //         process.redirectErrorStream(true);

            //         Process p = process.start();

            //         java.io.BufferedReader reader =
            //                 new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));

            //         String line;
            //         while ((line = reader.readLine()) != null) {
            //             System.out.println("PYTHON: " + line);
            //         }         
            //         int exitCode = p.waitFor();
            //         System.out.println("Python finished with code: " + exitCode);
            //         popup.dispose();
            //     } catch (Exception f) {
            //         f.printStackTrace();
            //     }
            // });

            popup.getContentPane().setBackground(new Color(41, 68, 90));
            popup.setIconImage(icon.getImage());
            popup.setVisible(true);
        });
        setVisible(true);
    }

}
