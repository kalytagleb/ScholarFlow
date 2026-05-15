package com.scholarflow.presentation.main.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.scholarflow.business.service.Translator;
import com.scholarflow.presentation.common.Card;
import com.scholarflow.presentation.common.PrimaryButton;
import com.scholarflow.presentation.common.StyleComboBox;

public final class ReviewAssignmentPanel extends JPanel {
    private final Translator translator;

    private final StyleComboBox<String> reviewerCombo = new StyleComboBox<>(new String[0]);
    private final JTextField deadlineField = new JTextField();
    private final PrimaryButton assignBtn;
    private final JLabel errorLabel = new JLabel(" ", SwingConstants.CENTER);

    public ReviewAssignmentPanel(final Translator translator) {
        this.translator = Objects.requireNonNull(translator);
        this.assignBtn = new PrimaryButton(translator.translate("review.assign_button"));

        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        this.setupUI();
    }

    private void setupUI() {
        Card card = new Card(20);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Title
        JLabel title = new JLabel(translator.translate("review.assign_title"));
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        addField(card, translator.translate("review.select_reviewer"), reviewerCombo);
        addField(card, translator.translate("review.deadline_label") + " (YYYY-MM-DD)", deadlineField);

        assignBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        assignBtn.setMaximumSize(new Dimension(280, 40));
        card.add(assignBtn);
        this.add(card, BorderLayout.CENTER);
    }

    private void addField(JPanel p, String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(new Color(127, 140, 141));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(l);
        p.add(Box.createRigidArea(new Dimension(0, 5)));

        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setMaximumSize(new Dimension(350, 35));
        field.setPreferredSize(new Dimension(350, 35));

        if (field instanceof JTextField) {
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 221, 225), 1),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
            ));
        }

        p.add(field);
        p.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    public void fillReviewers(List<String> names) {
        reviewerCombo.removeAllItems();
        names.forEach(reviewerCombo::addItem);
    }

    public String selectedReviewerName() {
        return (String) reviewerCombo.getSelectedItem();
    }

    public String deadlineText() {
        return deadlineField.getText();
    }

    public void onAssign(Runnable action) {
        assignBtn.addActionListener(e -> action.run());
    }

    public void displayError(String msg) {
        errorLabel.setText(msg);
    }

    public void setLock(boolean locked) {
        assignBtn.setEnabled(!locked);
    }
}
