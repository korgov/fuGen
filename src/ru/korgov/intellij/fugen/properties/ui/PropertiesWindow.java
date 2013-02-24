package ru.korgov.intellij.fugen.properties.ui;

import ru.korgov.intellij.fugen.properties.Constants;
import ru.korgov.intellij.fugen.properties.PersistentStateProperties;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Kirill Korgov (kirill@korgov.ru)
 * Date: 02.12.12
 */
public class PropertiesWindow {
    private JPanel mainPanel;
    private JTextField fuClassTextField;
    private JTextField fuMethodTextField;
    private JTextPane fuTemplatePane;
    private JTextField fuPrefixTextField;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public PropertiesWindow() {
        setHighligter();
    }

    private void setHighligter() {
        final StyledDocument styledDocument = fuTemplatePane.getStyledDocument();
        final Style normalTextStyle = fuTemplatePane.addStyle("Normal", null);
        final Style boldTextStyle = fuTemplatePane.addStyle("Bold", null);
        StyleConstants.setBold(boldTextStyle, true);
        StyleConstants.setForeground(boldTextStyle, new Color(112, 0, 121));
        StyleConstants.setBold(normalTextStyle, false);

        fuTemplatePane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                final String text = fuTemplatePane.getText();
                styledDocument.setCharacterAttributes(0, text.length(), normalTextStyle, true);
                for (final Pattern pattern : Constants.getAllVars()) {
                    final Matcher matcher = pattern.matcher(text);
                    while (matcher.find()) {
                        matcher.start();
                        styledDocument.setCharacterAttributes(matcher.start(), matcher.group().length(), boldTextStyle, true);
                    }
                }
            }
        });
    }

    public void loadCurrentProperties(final PersistentStateProperties properties) {
        if (properties != null) {
            fuTemplatePane.setText(properties.getFuTemplate());
            fuClassTextField.setText(properties.getFuClassName());
            fuMethodTextField.setText(properties.getFuMethodName());
            fuPrefixTextField.setText(properties.getFuConstNamePrefix());
        }
    }

    public void saveCurrentSettings(final PersistentStateProperties properties) {
        properties.setFuClassName(fuClassTextField.getText());
        properties.setFuConstNamePrefix(fuPrefixTextField.getText());
        properties.setFuMethodName(fuMethodTextField.getText());
        properties.setFuTemplate(fuTemplatePane.getText());
    }

    public boolean isModified(final PersistentStateProperties state) {
        final PersistentStateProperties currentState = state.getDefaultInstance();
        saveCurrentSettings(currentState);
        return !state.equals(currentState);
    }
}
