package ru.korgov.intellij.fugen.properties.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.korgov.intellij.fugen.FuLiveTester;
import ru.korgov.intellij.fugen.properties.Constants;
import ru.korgov.intellij.fugen.properties.GeneratorPropertiesState;
import ru.korgov.intellij.fugen.properties.PersistentStateProperties;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Kirill Korgov (kirill@korgov.ru)
 * Date: 02.12.12
 */
public class PropertiesWindow {

    private JPanel mainPanel;
    private JTextField fuClassTextField;
    private JPanel examplePane;
    private JPanel fieldTemplatePane;
    private JLabel varsHelpLabel;
    private JPanel methodTemplatePane;
    private JCheckBox staticMethodTemplateCheckBox;
    private JCheckBox staticFieldTemplateCheckBox;
    private JPanel toolbarPanel;
    private JList generatorsList;
    private JSplitPane mainSplitPane;
    private JTabbedPane templatesTabbedPane;
    private final EditorEx exampleViewer;
    private final EditorEx fieldTemplateEditor;
    private final EditorEx methodTemplateEditor;

    private final FuLiveTester fuLiveTester = new FuLiveTester("MyClass", "id", "long", "getId");

    private final DefaultListModel generatorsListModel = new DefaultListModel();

    private final List<GeneratorPropertiesState> generatorsProps = new ArrayList<GeneratorPropertiesState>();

    private
    @Nullable
    GeneratorPropertiesState currentGenerator = null;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public PropertiesWindow() {
        exampleViewer = UIUtils.createEditor(true, false);
        examplePane.add(exampleViewer.getComponent(), UIUtils.getDefaultConstraints());
        fieldTemplateEditor = UIUtils.createEditor(false, true);
        fieldTemplatePane.add(fieldTemplateEditor.getComponent(), UIUtils.getDefaultConstraints());
        methodTemplateEditor = UIUtils.createEditor(false, true);
        methodTemplatePane.add(methodTemplateEditor.getComponent(), UIUtils.getDefaultConstraints());

        varsHelpLabel.setText("<html>" + UIUtils.join(Constants.getAllVarNames(), ", ") + "</html>");

        final ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, createActions(), true);
        toolbarPanel.add(actionToolbar.getComponent(), UIUtils.getDefaultConstraints());

//        updateExampleText();
        addListeners();

        generatorsList.setModel(generatorsListModel);

        mainSplitPane.updateUI();
    }

    private ActionGroup createActions() {
        final DefaultActionGroup group = new DefaultActionGroup();
        group.add(createAddAction());
        group.add(createDeleteAction());
        group.addSeparator();
        group.add(createMoveUpAction());
        group.add(createMoveDownAction());
        return group;
    }

    private AnAction createMoveUpAction() {
        return createMoveAction("Up", "Move selected action up", AllIcons.Actions.MoveUp, -1);
    }

    private AnAction createMoveDownAction() {
        return createMoveAction("Down", "Move selected action down", AllIcons.Actions.MoveDown, 1);
    }

    private ActionGroup createMoveAction(final String name, final String description, final Icon icon, final int delta) {
        return UIUtils.wrapAction(name, new AnAction(name, description, icon) {
            @Override
            public void actionPerformed(final AnActionEvent e) {
                final int selectedIndex = generatorsList.getSelectedIndex();
                if (selectedIndex > -1) {
                    final int newIndex = selectedIndex + delta;
                    if (newIndex >= 0 && newIndex < generatorsProps.size()) {

                        final GeneratorPropertiesState removedState = generatorsProps.remove(selectedIndex);
                        final Object removedLabel = generatorsListModel.remove(selectedIndex);

                        generatorsProps.add(newIndex, removedState);
                        generatorsListModel.add(newIndex, removedLabel);

                        generatorsList.setSelectedIndex(newIndex);
                    }
                }
            }
        });
    }

    private ActionGroup createDeleteAction() {
        return UIUtils.wrapAction("Delete", new AnAction("Delete", "Delete selected action", PlatformIcons.DELETE_ICON) {
            @Override
            public void actionPerformed(final AnActionEvent e) {
                final int selectedIndex = generatorsList.getSelectedIndex();
                if (selectedIndex > -1) {
                    final GeneratorPropertiesState selectedGenerator = generatorsProps.get(selectedIndex);
                    final int yesNoDialogAnswerCode = Messages.showYesNoDialog(toolbarPanel,
                            "Are you sure you want to delete \"" + selectedGenerator.getGeneratorName() + "\" action?",
                            "Delete Generation Action", Messages.getQuestionIcon()
                    );

                    if (isYesAnswer(yesNoDialogAnswerCode)) {
                        deleteGenerator(selectedIndex);
                    }
                }
            }

            private boolean isYesAnswer(final int yesNoDialogAnswerCode) {
                return yesNoDialogAnswerCode == 0;
            }
        });
    }

    private void deleteGenerator(final int index) {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                generatorsListModel.remove(index);
                final GeneratorPropertiesState removedGenerator = generatorsProps.remove(index);
                //noinspection ObjectEquality
                if (currentGenerator == removedGenerator) {
                    currentGenerator = null;
                    if (generatorsProps.isEmpty()) {
                        loadFromState(GeneratorPropertiesState.empty());
                    } else {
                        generatorsList.setSelectedIndex(index - 1);
                    }
                }
            }
        });
    }

    private ActionGroup createAddAction() {
        return UIUtils.wrapAction("Add", new AnAction("Add", "Add new generation action", PlatformIcons.ADD_ICON) {
            @Override
            public void actionPerformed(final AnActionEvent e) {
                final String name = Messages.showInputDialog(toolbarPanel, "Name: ", "Add Generation Action", null);
                if (name != null) {
                    addNewGenerator(name);
                }
            }
        });
    }

    private void addNewGenerator(final String name) {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                flushCurrentGeneratorState();
                generatorsProps.add(new GeneratorPropertiesState(name));
                generatorsListModel.addElement(name);
                generatorsList.setSelectedIndex(generatorsListModel.size() - 1);
            }
        });
    }

    private void addListeners() {

        final ListenerAdapter listener = new ListenerAdapter(new Runnable() {
            @Override
            public void run() {
                updateExampleText();
            }
        });

        fuClassTextField.addKeyListener(listener.asKeyL());
        fieldTemplateEditor.getDocument().addDocumentListener(listener.asDocumentL());
        methodTemplateEditor.getDocument().addDocumentListener(listener.asDocumentL());
        staticFieldTemplateCheckBox.addChangeListener(listener.asChangeL());
        staticMethodTemplateCheckBox.addChangeListener(listener.asChangeL());

        generatorsList.addListSelectionListener(getGeneratorSelectionListener());
    }

    private ListSelectionListener getGeneratorSelectionListener() {
        return new ListSelectionListener() {
            @Override
            public void valueChanged(final ListSelectionEvent e) {
                final int selectedIndex = generatorsList.getSelectedIndex();
                switchGeneratorTo(selectedIndex);
            }
        };
    }

    private void switchGeneratorTo(final int selectedIndex) {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                if (selectedIndex >= 0 && selectedIndex < generatorsProps.size()) {
                    flushCurrentGeneratorState();
                    currentGenerator = generatorsProps.get(selectedIndex);
                    loadCurrentGeneratorState();
                }
            }
        });
    }

    private void loadCurrentGeneratorState() {
        if (currentGenerator != null) {
            loadFromState(currentGenerator);
        }
    }

    private void loadFromState(final @NotNull GeneratorPropertiesState state) {
        fuClassTextField.setText(state.getFuClassName());
        UIUtils.setTextFafety(fieldTemplateEditor, state.getFuFieldTemplate());
        UIUtils.setTextFafety(methodTemplateEditor, state.getFuMethodTemplate());
        staticFieldTemplateCheckBox.setSelected(state.isFieldTemplateEnabled());
        staticMethodTemplateCheckBox.setSelected(state.isMethodTemplateEnabled());
        templatesTabbedPane.setSelectedIndex(state.isFieldTemplateEnabled() ? 0 : 1);
    }

    private void flushCurrentGeneratorState() {
        if (currentGenerator != null) {
            saveCurrentStateTo(currentGenerator);
        }
    }

    private void saveCurrentStateTo(final @NotNull GeneratorPropertiesState state) {
        state.setFuClassName(fuClassTextField.getText());
        state.setFuFieldTemplate(fieldTemplateEditor.getDocument().getText());
        state.setFuMethodTemplate(methodTemplateEditor.getDocument().getText());
        state.setFieldTemplateEnabled(staticFieldTemplateCheckBox.isSelected());
        state.setMethodTemplateEnabled(staticMethodTemplateCheckBox.isSelected());
    }

    private void updateExampleText() {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                final GeneratorPropertiesState state = GeneratorPropertiesState.empty();
                saveCurrentStateTo(state);
                final String text = fuLiveTester.buildTestText(state);
                UIUtils.setTextFafety(exampleViewer, text);
            }
        });
    }

    public void loadCurrentProperties(final PersistentStateProperties properties) {
        if (properties != null) {
            reInitGenerators(properties);
//            generatorsList.setSelectedIndex(0);
        }
    }

    private void reInitGenerators(final PersistentStateProperties properties) {
        currentGenerator = null;
        generatorsProps.clear();
        generatorsListModel.clear();

        for (final GeneratorPropertiesState generatorsProp : properties.getProperties()) {
            generatorsProps.add(generatorsProp.copy());
            generatorsListModel.addElement(generatorsProp.getGeneratorName());
        }
    }

    public void saveCurrentSettings(final PersistentStateProperties state) {
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                flushCurrentGeneratorState();
                state.setProperties(generatorsProps);
            }
        });
    }

    public boolean isModified(final PersistentStateProperties state) {
        final PersistentStateProperties currentState = PersistentStateProperties.getDefaultInstance();
        saveCurrentSettings(currentState);
        return !state.equals(currentState);
    }
}
