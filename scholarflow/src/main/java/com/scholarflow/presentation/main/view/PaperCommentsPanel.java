package com.scholarflow.presentation.main.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.BorderFactory;
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
import com.scholarflow.presentation.common.Card;
import com.scholarflow.presentation.common.PrimaryButton;

public final class PaperCommentsPanel extends JPanel {
    private final Translator translator;
    private final JPanel commentsContainer;
    private final JTextArea inputArea = new JTextArea(3, 20);
    private final PrimaryButton postButton;

    public PaperCommentsPanel(final Translator translator) {
        this.translator = translator;
        this.postButton = new PrimaryButton(translator.translate("comments.post_button"));

        this.setLayout(new BorderLayout(0, 10));
        this.setBackground(Color.WHITE);
        this.setBorder(new EmptyBorder(20, 0, 0, 0));

        this.commentsContainer = new JPanel();
        this.commentsContainer.setLayout(new BoxLayout(commentsContainer, BoxLayout.Y_AXIS));
        this.commentsContainer.setBackground(Color.WHITE);

        this.setupUI();
    }

    private void setupUI() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setOpaque(false);

        JPanel inputRow = new JPanel(new BorderLayout(15, 0));
        inputRow.setOpaque(false);
        inputRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createLineBorder(new Color(220, 221, 225), 1));

        postButton.setPreferredSize(new Dimension(100, 40));
        postButton.setBackground(new Color(41, 128, 185));
        postButton.setForeground(Color.WHITE);

        inputRow.add(inputScroll, BorderLayout.CENTER);
        inputRow.add(postButton, BorderLayout.EAST);

        JLabel title = new JLabel(translator.translate("comments.title"));
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.add(title);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(inputRow);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(commentsContainer);

        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    postButton.doClick();
                }
            }  
        });
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

        JLabel authorLabel = new JLabel(comment.authorName());
        authorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        authorLabel.setForeground(new Color(41, 128, 185));

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
