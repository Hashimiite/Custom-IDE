package src.gui;

import javax.swing.*;
import java.awt.*;

public class StatusBar extends JPanel {
    private final JLabel status;
    
    public StatusBar() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        
        status = new JLabel("Ready");
        add(status, BorderLayout.WEST);
    }
    
    public void setStatus(String message) {
        status.setText(message);
    }
}