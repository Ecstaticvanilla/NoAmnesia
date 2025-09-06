/*
* Display a window with text fields (JTextField) and date input.
* Provide Save button → calls FileStorage.save().
* Show list of existing assignments.
* Notify user if input is invalid. 
*/

package ui;

import java.awt.*;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JMenu;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

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
public class Form extends JFrame{

    private static int i  = 0;

    public static void main(String[] args) {
    
        //Create Frame 
        JFrame form = new JFrame();
        form.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        form.setVisible(true);
        form.setSize(900,700);
        form.setResizable(false);
        form.setTitle("NoAmnesia");
        form.setLayout(null);
    
        //IMPORTANT
        ImageIcon icon = new ImageIcon(ui.Form.class.getResource("/resources/icon.png"));
        form.setIconImage(icon.getImage());
        form.getContentPane().setBackground(new Color(41, 68, 90));

        JLabel temp = new JLabel();
        temp.setText(String.valueOf(i));
        temp.setForeground(new Color(0xFFFDD0));
        temp.setFont(new Font("Times New Roman",Font.PLAIN,20));
        temp.setBounds(50,25,800,50);
        form.add(temp);

        Button addButton = new Button("+");
        addButton.setBounds(825, 600, 50, 50);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource() == addButton){
                    i += 1;
                    temp.setText(String.valueOf(i));
                }
            }
        });
        form.add(addButton);


    }
}

