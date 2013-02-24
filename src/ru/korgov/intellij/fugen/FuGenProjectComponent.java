package ru.korgov.intellij.fugen;


import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.korgov.intellij.fugen.properties.PersistentStateProperties;
import ru.korgov.intellij.fugen.properties.ui.PropertiesWindow;

import javax.swing.*;

/**
 * Author: Kirill Korgov (kirill@korgov.ru))
 * Date: 20.02.13 2:33
 */

public class FuGenProjectComponent implements Configurable, ProjectComponent {

    private PropertiesWindow propertiesWindow;
    private final PersistentStateProperties properties;

    public FuGenProjectComponent(final Project project) {
        properties = PersistentStateProperties.getInstance(project);
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "FuGen";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return null;
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Override
    public JComponent createComponent() {
        if (propertiesWindow == null) {
            propertiesWindow = new PropertiesWindow();
        }
        reset();
        return propertiesWindow.getMainPanel();
    }

    @Override
    public boolean isModified() {
        return propertiesWindow.isModified(properties);
    }

    @Override
    public void apply() throws ConfigurationException {
        propertiesWindow.saveCurrentSettings(properties);
    }

    @Override
    public void reset() {
        propertiesWindow.loadCurrentProperties(properties);
    }

    @Override
    public void disposeUIResources() {
        propertiesWindow = null;
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "FuGenProjectComponent";
    }

    @Override
    public void projectOpened() {
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }
}
