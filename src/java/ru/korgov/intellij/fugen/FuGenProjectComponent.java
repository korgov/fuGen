package ru.korgov.intellij.fugen;


import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.Anchor;
import com.intellij.openapi.actionSystem.Constraints;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.korgov.intellij.fugen.actions.GenerateFieldFunctionsAction;
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
        this.properties = PersistentStateProperties.getInstance(project);
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
        final GenerateFieldFunctionsAction coded0 = new GenerateFieldFunctionsAction("Coded0", 0);
        final GenerateFieldFunctionsAction coded1 = new GenerateFieldFunctionsAction("Coded1", 1);

        final DefaultActionGroup group = new DefaultActionGroup();
        group.addSeparator();
        group.add(coded0);
        group.add(coded1);
        group.addSeparator();
        final ActionManager actionManager = ActionManager.getInstance();
        actionManager.registerAction("FuGenActionGroup", group);
        actionManager.registerAction("FuGenActionCoded0", coded0);
        actionManager.registerAction("FuGenActionCoded1", coded1);

        final DefaultActionGroup genGroup = (DefaultActionGroup) actionManager.getAction("GenerateGroup");
        genGroup.add(group, new Constraints(Anchor.AFTER, "JavaGenerateGroup2"));
    }

    @Override
    public void disposeComponent() {
    }
}
