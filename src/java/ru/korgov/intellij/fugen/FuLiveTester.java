package ru.korgov.intellij.fugen;

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
        fuBuilder.setFuFieldTemplate(state.getFuFieldTemplate())
                .setFuMethodTemplate(state.getFuMethodTemplate())
                .setFuFieldEnabled(state.isFieldTemplateEnabled())
                .setFuMethodEnabled(state.isMethodTemplateEnabled())
                .setStripPrefixEnabled(state.isStripPrefixEnabled())
                .setPrefixStripPattern(state.getStripPrefixPattern());

        return buildTestText(fuBuilder.buildFuFieldText(), fuBuilder.buildFuMethodText());
    }

    private String buildTestText(final String fuFieldText, final String fuMethodText) {
        return "public class " + className + " {\n" +
                "    private " + fieldType + " " + fieldName + ";\n\n" +
                "    public " + fieldType + " " + getterMethodName + "() {\n" +
                "        return " + fieldName + ";\n" +
                "    }\n\n" +
                "    " + (fuFieldText.replaceAll("\\n", "\n    ") + "\n\n" +
                        "    " + fuMethodText.replaceAll("\\n", "\n    ")).trim() +
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
