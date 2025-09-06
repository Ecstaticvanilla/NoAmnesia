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
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import backend.AssignmentComponent;
import backend.FileStorage;

//Misc Imports 
import java.awt.*;
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
        // ImageIcon icon = new ImageIcon(Form.class.getResource("/resources/icon.png"));
        // setIconImage(icon.getImage());

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
        setVisible(true);
    }

}
