package com.scholarflow;

import javax.swing.SwingUtilities;

import com.scholarflow.business.service.AuditService;
import com.scholarflow.business.service.FieldService;
import com.scholarflow.business.service.InteractionService;
import com.scholarflow.business.service.PaperService;
import com.scholarflow.business.service.ReviewService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.business.service.UserService;
import com.scholarflow.business.service.XmlService;
import com.scholarflow.data.connection.DatabaseConfig;
import com.scholarflow.data.connection.DatabaseConnectionPool;
import com.scholarflow.data.jdbc.JdbcAuditLogRepository;
import com.scholarflow.data.jdbc.JdbcFieldRepository;
import com.scholarflow.data.jdbc.JdbcPaperCommentRepository;
import com.scholarflow.data.jdbc.JdbcPaperLikeRepository;
import com.scholarflow.data.jdbc.JdbcPaperRepository;
import com.scholarflow.data.jdbc.JdbcPaperVersionRepository;
import com.scholarflow.data.jdbc.JdbcReviewAssignmentRepository;
import com.scholarflow.data.jdbc.JdbcReviewRepository;
import com.scholarflow.data.jdbc.JdbcStatusHistoryRepository;
import com.scholarflow.data.jdbc.JdbcUserRepository;
import com.scholarflow.presentation.login.view.LoginFrame;

public final class Main {
    public static void main(final String[] args) {
        try {
            final DatabaseConfig config = new DatabaseConfig("config/database.properties");
            final DatabaseConnectionPool pool = new DatabaseConnectionPool(config);

            final PaperService paperService = new PaperService(
                new JdbcPaperRepository(pool),
                new JdbcPaperVersionRepository(pool),
                new JdbcStatusHistoryRepository(pool)
            );
            final UserService userService = new UserService(new JdbcUserRepository(pool));
            final FieldService fieldService = new FieldService(new JdbcFieldRepository(pool));
            final InteractionService interactionService = new InteractionService(
                new JdbcPaperCommentRepository(pool),
                new JdbcPaperLikeRepository(pool),
                new JdbcPaperRepository(pool)
            );
            final ReviewService reviewService = new ReviewService(
                new JdbcReviewAssignmentRepository(pool),
                new JdbcReviewRepository(pool),
                new JdbcPaperRepository(pool)
            );
            final AuditService auditService = new AuditService(new JdbcAuditLogRepository(pool));
            final XmlService xmlService = new XmlService(fieldService);

            final Translator translator = new Translator("en");

            SwingUtilities.invokeLater(() ->
                new LoginFrame(
                    userService,
                    reviewService,
                    fieldService,
                    paperService,
                    interactionService,
                    translator,
                    auditService,
                    xmlService
                ).open()
            );
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
