package com.scholarflow.presentation.main.controller;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.scholarflow.business.model.Field;
import com.scholarflow.business.model.User;
import com.scholarflow.business.service.FieldService;
import com.scholarflow.business.service.PaperService;
import com.scholarflow.business.service.Translator;
import com.scholarflow.presentation.main.view.PaperSubmissionPanel;

public final class PaperSubmissionController {
    private final User author;
    private final PaperService paperService;
    private final FieldService fieldService;
    private final Translator translator;
    private final PaperSubmissionPanel view;

    // Callback. After submission we should back to list
    private final Runnable onSuccess;

    private List<Field> Fields;

    public PaperSubmissionController(
        final User author,
        final PaperService paperService,
        final FieldService fieldService,
        final Translator translator,
        final PaperSubmissionPanel view,
        final Runnable onSuccess
    ) {
        this.author = Objects.requireNonNull(author);
        this.paperService = Objects.requireNonNull(paperService);
        this.fieldService = Objects.requireNonNull(fieldService);
        this.translator = Objects.requireNonNull(translator);
        this.view = Objects.requireNonNull(view);
        this.onSuccess = Objects.requireNonNull(onSuccess);
        
        this.init();
    }

    private void init() {
        this.view.onSubmit(this::handleSubmit);
        this.loadFields();
    }

    private void loadFields() {
        new SwingWorker<List<Field>, Void>() {
            @Override
            protected List<Field> doInBackground() {
                return fieldService.getAllFields();
            }

            @Override
            protected void done() {
                try {
                    Fields = get();

                    List<String> names = Fields.stream()
                        .map(f -> f.localizedName(translator.currentLanguage()))
                        .collect(Collectors.toList());
                    
                    view.setFieldNames(names);
                } catch (Exception e) {
                    view.displayError("Failed to load fields");
                }
            }
        }.execute();
    }

    private void handleSubmit() {
        final String title = view.title();
        final String paperAbstract = view.paperAbstract();
        final String keywords = view.keywords();
        final String selectedFieldName = view.selectedFieldName();

        if (title.isBlank() || paperAbstract.isBlank() || selectedFieldName == null) {
            view.displayError(translator.translate("error.empty_fields"));
            return;
        }

        final UUID fieldId = Fields.stream()
            .filter(f -> f.localizedName(translator.currentLanguage()).equals(selectedFieldName))
            .findFirst()
            .flatMap(Field::id)
            .orElse(null);

        view.setLock(true);
        view.displayError(translator.translate("submit.submitting"));

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                paperService.createPaper(title, paperAbstract, keywords, fieldId, author);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(null, translator.translate("submit.success_message"));

                    onSuccess.run();
                } catch (Exception e) {
                    view.setLock(false);
                    String message = e.getCause().getMessage();
                    view.displayError(message);
                }
            }
        }.execute();
    }
}
