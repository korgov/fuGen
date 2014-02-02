package ru.korgov.intellij.fugen.properties;

/**
 * Author: Kirill Korgov (kirill@korgov.ru))
 * Date: 2/24/13 10:24 PM
 */
public interface PropertiesState {
    String getFuFieldTemplate();

    String getFuMethodTemplate();

    boolean isFieldTemplateEnabled();

    boolean isMethodTemplateEnabled();

    String getGeneratorName();
}
