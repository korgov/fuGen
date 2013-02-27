package ru.korgov.intellij.fugen.properties.ui;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Author: Kirill Korgov (kirill@korgov.ru))
 * Date: 2/25/13 4:35 AM
 */
public final class ListenerAdapter {
    private final Runnable action;

    private final ChangeListener changeListener;
    private final DocumentListener documentListener;
    private final KeyListener keyListener;

    public ListenerAdapter(final Runnable action) {
        this.action = action;
        this.changeListener = buildChangeListener();
        this.documentListener = buildDocumentListener();
        this.keyListener = buildKeyListener();
    }


    public ChangeListener buildChangeListener() {
        return new ChangeListener() {
            @Override
            public void stateChanged(final ChangeEvent e) {
                action.run();
            }
        };
    }

    public DocumentListener buildDocumentListener() {
        return new DocumentListener() {
            @Override
            public void beforeDocumentChange(final DocumentEvent event) {
            }

            @Override
            public void documentChanged(final DocumentEvent event) {
                action.run();
            }
        };
    }

    public KeyListener buildKeyListener() {
        return new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                action.run();
            }
        };
    }

    public ChangeListener asChangeL() {
        return changeListener;
    }

    public DocumentListener asDocumentL() {
        return documentListener;
    }

    public KeyListener asKeyL() {
        return keyListener;
    }
}
