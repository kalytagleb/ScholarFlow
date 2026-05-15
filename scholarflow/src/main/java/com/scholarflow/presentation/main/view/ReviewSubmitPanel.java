package com.scholarflow.presentation.main.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.scholarflow.business.model.enums.ReviewDecision;
import com.scholarflow.business.service.Translator;
import com.scholarflow.presentation.common.PrimaryButton;
import com.scholarflow.presentation.common.StyleComboBox;

public final class ReviewSubmitPanel extends JPanel {
    private final Translator translator;

    private final StyleComboBox<ReviewDecision> decisionCombo;
    private final JTextArea commentsArea = new JTextArea(6, 20);
    private final JTextArea privateNotesArea = new JTextArea(3, 20);
    private final PrimaryButton submitBtn;

    private final JLabel errorLabel = new JLabel(" ", SwingConstants.CENTER);

    public ReviewSubmitPanel(final Translator tr) {
        this.translator = Objects.requireNonNull(tr);
        this.decisionCombo = new StyleComboBox<>(ReviewDecision.values());
        this.submitBtn = new PrimaryButton(tr.translate("review.submit_button"));
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);
        this.setupUI();
    }

    private void setupUI() {
        this.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 221, 225)),
            translator.translate("review.submit_title"),
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14)
        ));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(15, 15, 15, 15));
        form.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Choose decision
        JLabel decisionLabel = createLabel(translator.translate("review.decision_label"));
        decisionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(decisionLabel);

        decisionCombo.setAlignmentX(Component.LEFT_ALIGNMENT); 
        form.add(decisionCombo);
        form.add(Box.createRigidArea(new Dimension(0, 15)));

        // Public comments
        JLabel commLabel = createLabel(translator.translate("review.comments_label"));
        commLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(commLabel);
        
        JScrollPane commScroll = new JScrollPane(commentsArea);
        commScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        commScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        form.add(commScroll);
        form.add(Box.createRigidArea(new Dimension(0, 15)));

        // Private notes
        JLabel privLabel = createLabel(translator.translate("review.private_notes_label"));
        privLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(privLabel);
        
        JScrollPane privScroll = new JScrollPane(privateNotesArea);
        privScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        privScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        form.add(privScroll);

        // Erros.
        form.add(Box.createRigidArea(new Dimension(0, 20)));
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnWrapper.setOpaque(false);
        btnWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnWrapper.add(submitBtn);
        form.add(btnWrapper);

        this.add(form, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(127, 140, 141));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public ReviewDecision selectedDecision() { 
        return (ReviewDecision) decisionCombo.getSelectedItem(); 
    }

    public String comments() { 
        return commentsArea.getText(); 
    }

    public String privateNotes() { 
        return privateNotesArea.getText(); 
    }
    
    public void onSubmit(Runnable action) { 
        submitBtn.addActionListener(e -> action.run()); 
    }

    public void displayError(String msg) { 
        errorLabel.setText(msg); 
    }

    public void setLock(boolean locked) { 
        submitBtn.setEnabled(!locked); 
    }
}
