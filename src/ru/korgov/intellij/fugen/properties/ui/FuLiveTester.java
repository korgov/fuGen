package ru.korgov.intellij.fugen.properties.ui;

import ru.korgov.intellij.fugen.FuBuilder;
import ru.korgov.intellij.fugen.properties.PropertiesState;

/**
 * Author: Kirill Korgov (kirill@korgov.ru))
 * Date: 2/24/13 10:12 PM
 */
public class FuLiveTester {
    private final String className;
    private final String fieldName;
    private final String fieldType;
    private final String getterMethodName;

    private final FuBuilder fuBuilder;

    public FuLiveTester(final String className, final String fieldName, final String fieldType, final String getterMethodName) {
        this.className = className;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.getterMethodName = getterMethodName;
        fuBuilder = initBuilder();
    }

    public String buildTestText(final PropertiesState state) {
        fuBuilder.setFuClassName(state.getFuClassName())
                .setFuFieldTemplate(state.getFuFieldTemplate())
                .setFuMethodTemplate(state.getFuMethodTemplate());


        final String fuFieldText = state.isFieldTemplateEnabled() ? fuBuilder.buildFuFieldText() : "";
        final String fuMethodText = state.isMethodTemplateEnabled() ? fuBuilder.buildFuMethodText() : "";
        return buildTestText(fuFieldText, fuMethodText);
    }

    private String buildTestText(final String fuFieldText, final String fuMethodText) {
        return "public class " + className + " {\n" +
                "    private " + fieldType + " " + fieldName + ";\n\n" +
                "    public " + fieldType + " " + getterMethodName + "() {\n" +
                "        return " + fieldName + ";\n" +
                "    }\n\n" +
                "    " + fuFieldText.replaceAll("\\n", "\n    ") +
                "\n\n" +
                "    " + fuMethodText.replaceAll("\\n", "\n    ") +
                "\n}";
    }

    private FuBuilder initBuilder() {
        return new FuBuilder()
                .setClassName(className)
                .setFieldType(fieldType)
                .setFieldName(fieldName)
                .setGetterMethodName(getterMethodName);
    }
}
