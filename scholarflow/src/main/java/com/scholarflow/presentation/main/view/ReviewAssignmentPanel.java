package com.scholarflow.presentation.main.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.Objects;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.scholarflow.business.service.Translator;
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

        this.setLayout(new GridBagLayout());
        this.setBackground(Color.WHITE);
        this.setBorder(new EmptyBorder(20, 20, 20, 20));

        this.setupUI();
    }

    private void setupUI() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);

        // Title
        JLabel title = new JLabel(translator.translate("review.assign_title"));
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(title);
        container.add(Box.createRigidArea(new Dimension(0, 20)));

        // Reviewer's Choice
        container.add(createLabel(translator.translate("review.select_reviewer")));
        container.add(reviewerCombo);
        container.add(Box.createRigidArea(new Dimension(0, 15)));

        // Deadlines
        container.add(createLabel(translator.translate("review.deadline_label") + " (YYYY-MM-DD)"));
        deadlineField.setMaximumSize(new Dimension(280, 35));
        deadlineField.setPreferredSize(new Dimension(280, 35));
        container.add(deadlineField);
        container.add(Box.createRigidArea(new Dimension(0, 20)));

        // Errors and button
        errorLabel.setForeground(new Color(231, 76, 60));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        container.add(errorLabel);
        container.add(Box.createRigidArea(new Dimension(0, 10)));
        container.add(assignBtn);

        this.add(container, new GridBagConstraints());
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(127, 140, 141));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
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
