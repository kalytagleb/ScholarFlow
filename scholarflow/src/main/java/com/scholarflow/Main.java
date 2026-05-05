package com.scholarflow;

import javax.swing.SwingUtilities;

import com.scholarflow.business.service.FieldService;
import com.scholarflow.business.service.InteractionService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.business.service.UserService;
import com.scholarflow.business.service.PaperService;
import com.scholarflow.business.service.ReviewService;
import com.scholarflow.data.connection.DatabaseConfig;
import com.scholarflow.data.connection.DatabaseConnectionPool;
import com.scholarflow.data.jdbc.JdbcFieldRepository;
import com.scholarflow.data.jdbc.JdbcPaperCommentRepository;
import com.scholarflow.data.jdbc.JdbcPaperLikeRepository;
import com.scholarflow.data.jdbc.JdbcUserRepository;
import com.scholarflow.data.jdbc.JdbcPaperRepository;
import com.scholarflow.data.jdbc.JdbcPaperVersionRepository;
import com.scholarflow.data.jdbc.JdbcReviewAssignmentRepository;
import com.scholarflow.data.jdbc.JdbcReviewRepository;
import com.scholarflow.data.jdbc.JdbcStatusHistoryRepository;
import com.scholarflow.presentation.login.view.LoginFrame;

public final class Main {
    public static void main(String[] args) {
        try {
            DatabaseConfig config = new DatabaseConfig("config/database.properties");

            DatabaseConnectionPool pool = new DatabaseConnectionPool(config);

            PaperService paperService = new PaperService(
                new JdbcPaperRepository(pool),
                new JdbcPaperVersionRepository(pool),
                new JdbcStatusHistoryRepository(pool)
            );
            UserService userService = new UserService(new JdbcUserRepository(pool));
            FieldService fieldService = new FieldService(new JdbcFieldRepository(pool));
            InteractionService interactionService = new InteractionService(
                new JdbcPaperCommentRepository(pool),
                new JdbcPaperLikeRepository(pool), 
                new JdbcPaperRepository(pool)
            );
            ReviewService reviewService = new ReviewService(
                new JdbcReviewAssignmentRepository(pool), 
                new JdbcReviewRepository(pool), 
                new JdbcPaperRepository(pool)
            );

            Translator translator = new Translator("en");

            SwingUtilities.invokeLater(() -> {
                new LoginFrame(userService, reviewService, fieldService, paperService, interactionService, translator).open();
            });
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}