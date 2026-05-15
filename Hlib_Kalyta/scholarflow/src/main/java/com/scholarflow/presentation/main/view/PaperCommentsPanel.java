package com.scholarflow.presentation.main.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.scholarflow.business.model.PaperComment;
import com.scholarflow.business.service.Translator;

public final class PaperCommentsPanel extends JPanel {
    private final Translator translator;
    private final JPanel commentsContainer;
    private final JTextArea inputArea = new JTextArea(3, 20);
    private final JButton postButton;

    public PaperCommentsPanel(final Translator translator) {
        this.translator = translator;
        this.postButton = new JButton(translator.translate("comments.post_button"));

        this.setLayout(new BorderLayout(0, 10));
        this.setBackground(Color.WHITE);
        this.setBorder(new EmptyBorder(20, 0, 0, 0));

        this.commentsContainer = new JPanel();
        this.commentsContainer.setLayout(new BoxLayout(commentsContainer, BoxLayout.Y_AXIS));
        this.commentsContainer.setBackground(Color.WHITE);

        this.setupUI();
    }

    private void setupUI() {
        JLabel header = new JLabel(translator.translate("comments.title"));
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        this.add(header, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(new Color(248, 249, 250));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputPanel.add(inputScroll, BorderLayout.CENTER);
        inputPanel.add(postButton, BorderLayout.EAST);

        JPanel mainContent = new JPanel(new BorderLayout(0, 15));
        mainContent.setBackground(Color.WHITE);
        mainContent.add(inputPanel, BorderLayout.NORTH);
        mainContent.add(commentsContainer, BorderLayout.CENTER);

        this.add(mainContent, BorderLayout.CENTER);
    }

    public void displayComments(List<PaperComment> comments) {
        commentsContainer.removeAll();
        for (PaperComment c : comments) {
            commentsContainer.add(createCommentBubble(c));
            commentsContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        commentsContainer.revalidate();
        commentsContainer.repaint();
    }

    private JPanel createCommentBubble(PaperComment comment) {
        JPanel bubble = new JPanel(new BorderLayout(5, 5));
        bubble.setBackground(new Color(240, 242, 245));
        bubble.setBorder(new EmptyBorder(8, 12, 8, 12));
        bubble.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel authorLabel = new JLabel("User ID: " + comment.userId().toString().substring(0, 8));
        authorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JTextArea contentLabel = new JTextArea(comment.content());
        contentLabel.setEditable(false);
        contentLabel.setBackground(new Color(240, 242, 245));
        contentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contentLabel.setLineWrap(true);

        bubble.add(authorLabel, BorderLayout.NORTH);
        bubble.add(contentLabel, BorderLayout.CENTER);

        return bubble;
    }

    public String commentText() {
        return inputArea.getText();
    }

    public void clearInput() {
        inputArea.setText("");
    }

    public void onPostComment(Runnable action) {
        postButton.addActionListener(e -> action.run());
    }
}
