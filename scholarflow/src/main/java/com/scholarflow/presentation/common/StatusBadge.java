package com.scholarflow.presentation.common;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.scholarflow.business.model.enums.PaperStatus;

public final class StatusBadge extends JLabel {
    private final PaperStatus status;

    public StatusBadge(final PaperStatus status, final String translatedText) {
        super(translatedText, SwingConstants.CENTER);
        this.status = status;

        this.setOpaque(false);
        this.setFont(new Font("Segoe UI", Font.BOLD, 11));
        this.setBorder(new EmptyBorder(4, 12, 4, 12));

        this.setForeground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackgroundForStatus(status));
        
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        g2.dispose();
        super.paintComponent(g); 
    }

    private Color getBackgroundForStatus(PaperStatus status) {
        return switch (status) {
            case DRAFT -> new Color(149, 165, 166); // Gray
            case SUBMITTED -> new Color(52, 152, 219);   // Blue
            case UNDER_REVIEW -> new Color(241, 196, 15); // Yellow
            case ACCEPTED -> new Color(46, 204, 113);     // Gree
            case REJECTED -> new Color(231, 76, 60);      // Red
            case MINOR_REVISION, MAJOR_REVISION -> new Color(155, 89, 182); // Purple
            case RESUBMITTED -> new Color(26, 188, 156);  // Biruze
        };
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(d.width + 10, d.height);
    }
}
