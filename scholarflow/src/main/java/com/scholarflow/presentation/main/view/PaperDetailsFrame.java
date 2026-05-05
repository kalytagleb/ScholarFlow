package com.scholarflow.presentation.main.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.scholarflow.business.model.Paper;
import com.scholarflow.business.service.Translator;
import com.scholarflow.presentation.common.StatusBadge;

public final class PaperDetailsFrame {
    private final JFrame frame;
    private final Paper paper;
    private final Translator translator;

    public PaperDetailsFrame(final Paper paper, final Translator translator) {
        this.paper = Objects.requireNonNull(paper);
        this.translator = Objects.requireNonNull(translator);
        this.frame = new JFrame(translator.translate("details.title"));
        this.setupUI();
    }

    private void setupUI() {
        frame.setSize(700, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(30, 30, 30, 30));

        root.add(createHeaderSection(), BorderLayout.NORTH);
        root.add(createContentSection(), BorderLayout.CENTER);
        root.add(createFooterSection(), BorderLayout.SOUTH);

        frame.add(root);
    }

    private JPanel createHeaderSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("<html><body style='width: 450px'>" + paper.title() + "</body></html>");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(44, 62, 80));

        StatusBadge badge = new StatusBadge(
            paper.status(),
            translator.translate("status." + paper.status().name().toLowerCase())
        );

        panel.add(title, BorderLayout.CENTER);
        panel.add(badge, BorderLayout.EAST);
        return panel;
    }

    private JPanel createContentSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        if (paper.doi().isPresent()) {
            JLabel doiLabel = new JLabel("DOI: " + paper.doi().get());
            doiLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            doiLabel.setForeground(new Color(52, 152, 219));
            panel.add(doiLabel);
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
        }

        JLabel absHeader = new JLabel(translator.translate("details.abstract"));
        absHeader.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        panel.add(absHeader);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextArea area = new JTextArea(paper.paperAbstract());
        area.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setBackground(new Color(248, 249, 250));
        area.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(236, 240, 241)));
        panel.add(scroll);

        return panel;
    }

    private JPanel createFooterSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);

        JButton closeButton = new JButton(translator.translate("common.close"));
        closeButton.addActionListener(e -> frame.dispose());

        panel.add(closeButton);
        return panel;
    }

    public void open() {
        frame.setVisible(true);
    }
}
