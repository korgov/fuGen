package ru.korgov.intellij.fugen.actions;

import com.intellij.codeInsight.generation.actions.BaseGenerateAction;

/**
 * Author: Kirill Korgov (kirill@korgov.ru)
 * Date: 05.12.12
 */

public class GenerateFieldFunctionsAction extends BaseGenerateAction {
    public GenerateFieldFunctionsAction() {
        super(new GenerateFieldFunctionsActionHandler());
    }
}
