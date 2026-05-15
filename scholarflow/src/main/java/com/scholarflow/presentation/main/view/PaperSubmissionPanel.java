package com.scholarflow.presentation.main.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.print.PrinterAbortException;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.scholarflow.business.service.Translator;
import com.scholarflow.presentation.common.PrimaryButton;
import com.scholarflow.presentation.common.StyleComboBox;

public final class PaperSubmissionPanel extends JPanel {
    private final Translator translator;

    private final JTextField titleField = new JTextField();
    private final JTextArea abstractArea = new JTextArea(8, 20);
    private final JTextField keywordsField = new JTextField();
    private final StyleComboBox<String> fieldCombo = new StyleComboBox<>(new String[0]);

    private final PrimaryButton submitBtn;
    private final JLabel errorLabel = new JLabel(" ", SwingConstants.CENTER);

    public PaperSubmissionPanel(final Translator translator) {
        this.translator = Objects.requireNonNull(translator);
        this.submitBtn = new PrimaryButton(translator.translate("submit.button"));

        this.setLayout(new GridBagLayout());
        this.setBackground(new Color(245, 246, 250));
        this.setupLayout();
    }

    private void setupLayout() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 221, 225), 1),
            new EmptyBorder(30, 40, 30, 40)
        ));

        JLabel header = new JLabel(translator.translate("submit.title"));
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(header);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        addLabeledField(card, translator.translate("table.paper_title"), titleField);

        JLabel absLabel = createLabel(translator.translate("details.abstract_label"));
        absLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(absLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));

        abstractArea.setLineWrap(true);
        abstractArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(abstractArea);
        scroll.setMaximumSize(new Dimension(400, 150));
        scroll.setAlignmentX(Component.CENTER_ALIGNMENT); 
        card.add(scroll);
        card.add(Box.createRigidArea(new Dimension(0, 15)));

        addLabeledField(card, translator.translate("submit.keywords_label"), keywordsField);
        
        JLabel fieldLabel = createLabel(translator.translate("table.paper.field"));
        fieldLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(fieldLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        
        fieldCombo.setMaximumSize(new Dimension(400, 35));
        fieldCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(fieldCombo);
        
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(errorLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT); 
        submitBtn.setMaximumSize(new Dimension(400, 45));
        card.add(submitBtn);

        this.add(card, new GridBagConstraints());
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(127, 140, 141));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void addLabeledField(JPanel panel, String text, JTextField field) {
        JLabel label = createLabel(text);
        label.setAlignmentX(Component.CENTER_ALIGNMENT); 
        panel.add(label);
        
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        field.setMaximumSize(new Dimension(400, 35));
        field.setPreferredSize(new Dimension(400, 35));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 221, 225), 1),
            javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    public String title() { 
        return titleField.getText(); 
    }
    public String paperAbstract() { 
        return abstractArea.getText(); 
    }
    public String keywords() { 
        return keywordsField.getText(); 
    }
    public String selectedFieldName() {
        return (String) fieldCombo.getSelectedItem(); 
    }

    public void setFieldNames(List<String> names) {
        fieldCombo.removeAllItems();
        names.forEach(fieldCombo::addItem);
    }

    public void onSubmit(Runnable action) { 
        submitBtn.addActionListener(e -> action.run()); 
    }
    public void displayError(String msg) { 
        errorLabel.setText(msg); 
        errorLabel.setForeground(new Color(231, 76, 60)); 
    }
    public void setLock(boolean locked) {
        submitBtn.setEnabled(!locked); 
    }
}
