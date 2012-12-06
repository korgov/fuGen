package ru.korgov.intellij.fugen;

import com.intellij.codeInsight.generation.actions.BaseGenerateAction;

/**
 * Author: Kirill Korgov (korgov@yandex-team.ru)
 * Date: 05.12.12
 */

public class GenerateFieldFunctionsAction extends BaseGenerateAction {
    public GenerateFieldFunctionsAction() {
        super(new GenerateFieldFunctionsActionHandler());
    }
}
