package ru.korgov.intellij.fugen.properties.ui;

import com.intellij.codeInsight.template.JavaCodeContextType;
import com.intellij.codeInsight.template.impl.TemplateContext;
import com.intellij.codeInsight.template.impl.TemplateEditorUtil;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.uiDesigner.core.GridConstraints;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;

/**
 * Author: Kirill Korgov (korgov@yandex-team.ru))
 * Date: 02.03.13 2:53
 */
public class UIUtils {

    private static final GridConstraints DEFAULT_CONSTRAINTS = new GridConstraints(0, 0, 1, 1,
            GridConstraints.ANCHOR_CENTER,
            GridConstraints.FILL_BOTH,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW,
            null, null, null, 0, true);

    public static GridConstraints getDefaultConstraints() {
        return DEFAULT_CONSTRAINTS;
    }

    public static ActionGroup wrapAction(final String text, final AnAction action) {
        return new ActionGroup(text, false) {
            @NotNull
            @Override
            public AnAction[] getChildren(@Nullable final AnActionEvent e) {
                return Arrays.asList(action).toArray(new AnAction[1]);
            }
        };
    }

    public static EditorEx createEditor(final boolean isViewer, final boolean isTemplate) {
        final EditorFactory editorFactory = EditorFactory.getInstance();
        final Document document = editorFactory.createDocument("");
        final EditorEx editor = (EditorEx) editorFactory.createEditor(document, null, JavaFileType.INSTANCE, isViewer);
        editor.getSettings().setLineNumbersShown(false);
        editor.getSettings().setVirtualSpace(false);
        editor.getSettings().setWhitespacesShown(true);
        editor.getSettings().setAdditionalLinesCount(0);
        editor.getScrollPane().setAutoscrolls(false);


        if (isTemplate) {
            final TemplateContext contextByType = new TemplateContext();
            contextByType.setEnabled(new JavaCodeContextType.Statement(), true);
            TemplateEditorUtil.setHighlighter(editor, contextByType);
        }

        return editor;
    }


    public static void setTextFafety(final EditorEx editor, final String text) {
        final JComponent component = editor.getComponent();
        final CaretModel caretModel = editor.getCaretModel();
        final JScrollPane scrollPane = editor.getScrollPane();
        final Document document = editor.getDocument();
        final JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        final JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();

        final Dimension oldPrefSize = component.getPreferredSize();
        final int caretOffset = caretModel.getOffset();
        final int horizontalScrollValue = horizontalScrollBar.getValue();
        final int verticalScrollValue = verticalScrollBar.getValue();

        document.setText(text);

        horizontalScrollBar.setValue(Math.min(horizontalScrollBar.getMaximum(), horizontalScrollValue));
        verticalScrollBar.setValue(Math.min(verticalScrollBar.getMaximum(), verticalScrollValue));
        caretModel.moveToOffset(Math.min(caretOffset, text.length()));
        component.setPreferredSize(oldPrefSize);
    }

    public static String join(final List<String> allVarNames, final String sep) {
        final StringBuilder sb = new StringBuilder(allVarNames.size() * 20);
        for (final String varName : allVarNames) {
            sb.append(sep).append(varName);
        }
        return sb.substring(sep.length());
    }


    private UIUtils() {
    }
}
