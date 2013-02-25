package ru.korgov.intellij.fugen.properties.ui;

import com.intellij.codeInsight.template.JavaCodeContextType;
import com.intellij.codeInsight.template.impl.TemplateContext;
import com.intellij.codeInsight.template.impl.TemplateEditorUtil;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.uiDesigner.core.GridConstraints;
import ru.korgov.intellij.fugen.properties.Constants;
import ru.korgov.intellij.fugen.properties.PersistentStateProperties;
import ru.korgov.intellij.fugen.properties.PropertiesState;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Author: Kirill Korgov (kirill@korgov.ru)
 * Date: 02.12.12
 */
public class PropertiesWindow {
    private static final GridConstraints DEFAULT_CONSTRAINTS = new GridConstraints(0, 0, 1, 1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
            null, null, null, 0, true);

    private JPanel mainPanel;
    private JTextField fuClassTextField;
    private JTextField fuPrefixTextField;
    private JPanel exampleScrollPane;
    private JPanel fieldTemplatePane;
    private JLabel varsHelpLabel;
    private JPanel methodTemplatePane;
    private JCheckBox staticMethodTemplateCheckBox;
    private JCheckBox staticFieldTemplateCheckBox;
    private final Editor exampleViewer;
    private final Editor fieldTemplateEditor;
    private final Editor methodTemplateEditor;

    private final FuLiveTester fuLiveTester = new FuLiveTester("MyClass", "id", "long", "getId");
    private final Project project;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public PropertiesWindow(final Project project) {
        this.project = project;
        exampleViewer = createEditor(true, false);
        exampleScrollPane.add(exampleViewer.getComponent(), DEFAULT_CONSTRAINTS);
        fieldTemplateEditor = createEditor(false, true);
        fieldTemplatePane.add(fieldTemplateEditor.getComponent(), DEFAULT_CONSTRAINTS);
        methodTemplateEditor = createEditor(false, true);
        methodTemplatePane.add(methodTemplateEditor.getComponent(), DEFAULT_CONSTRAINTS);

        varsHelpLabel.setText("<html>" + join(Constants.getAllVarNames(), ", ") + "</html>");

        updateExamplerText();
        addListeners();
    }

    private String join(final List<String> allVarNames, final String sep) {
        final StringBuilder sb = new StringBuilder(allVarNames.size() * 20);
        for (final String varName : allVarNames) {
            sb.append(sep).append(varName);
        }
        return sb.substring(sep.length());
    }

    private Editor createEditor(final boolean isViewer, final boolean isTemplate) {
        final EditorFactory editorFactory = EditorFactory.getInstance();
        final Document document = editorFactory.createDocument("");

        final Editor editor = editorFactory.createEditor(document, project, JavaFileType.INSTANCE, isViewer);
        editor.getSettings().setLineNumbersShown(false);
        editor.getSettings().setVirtualSpace(false);
        editor.getSettings().setWhitespacesShown(true);

        if (isTemplate) {
            final TemplateContext contextByType = new TemplateContext();
            contextByType.setEnabled(new JavaCodeContextType.Statement(), true);
            TemplateEditorUtil.setHighlighter(editor, contextByType);
        }

        return editor;
    }

    private void addListeners() {

        final ListenerAdapter listener = new ListenerAdapter(new Runnable() {
            @Override
            public void run() {
                updateExamplerText();
            }
        });

        fuClassTextField.addKeyListener(listener.asKeyL());
        fuPrefixTextField.addKeyListener(listener.asKeyL());
        fieldTemplateEditor.getDocument().addDocumentListener(listener.asDocumentL());
        methodTemplateEditor.getDocument().addDocumentListener(listener.asDocumentL());
        staticFieldTemplateCheckBox.addChangeListener(listener.asChangeL());
        staticMethodTemplateCheckBox.addChangeListener(listener.asChangeL());

    }

    private void updateExamplerText() {
        final PersistentStateProperties state = PersistentStateProperties.getDefaultInstance();
        saveCurrentSettings(state);
        final String text = fuLiveTester.buildTestText(state);
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                setTextFafety(exampleViewer, text);
            }
        });
    }

    public void loadCurrentProperties(final PropertiesState properties) {
        if (properties != null) {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                @Override
                public void run() {
                    fuClassTextField.setText(properties.getFuClassName());
                    fuPrefixTextField.setText(properties.getFuConstNamePrefix());

                    setTextFafety(fieldTemplateEditor, properties.getFuFieldTemplate());
                    setTextFafety(methodTemplateEditor, properties.getFuMethodTemplate());
                    exampleViewer.getScrollingModel().scrollVertically(0);

                    staticFieldTemplateCheckBox.setSelected(properties.isFieldTemplateEnabled());
                    staticMethodTemplateCheckBox.setSelected(properties.isMethodTemplateEnabled());
                }
            });
        }
    }

    private void setTextFafety(final Editor editor, final String text) {
        final JComponent component = editor.getComponent();
        final Dimension oldPrefSize = new Dimension(component.getPreferredSize());
        editor.getDocument().setText(text);
        component.setPreferredSize(oldPrefSize);
    }

    public void saveCurrentSettings(final PersistentStateProperties properties) {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                properties.setFuClassName(fuClassTextField.getText());
                properties.setFuConstNamePrefix(fuPrefixTextField.getText());
                properties.setFuFieldTemplate(fieldTemplateEditor.getDocument().getText());
                properties.setFuMethodTemplate(methodTemplateEditor.getDocument().getText());

                properties.setFieldTemplateEnabled(staticFieldTemplateCheckBox.isSelected());
                properties.setMethodTemplateEnabled(staticMethodTemplateCheckBox.isSelected());
            }
        });
    }

    public boolean isModified(final PersistentStateProperties state) {
        final PersistentStateProperties currentState = PersistentStateProperties.getDefaultInstance();
        saveCurrentSettings(currentState);
        return !state.equals(currentState);
    }
}
