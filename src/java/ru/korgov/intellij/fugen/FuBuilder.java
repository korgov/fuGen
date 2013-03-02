package ru.korgov.intellij.fugen;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTypesUtil;
import ru.korgov.intellij.fugen.properties.Constants;
import ru.korgov.intellij.fugen.properties.PropertiesState;

/**
 * Author: Kirill Korgov (kirill@korgov.ru))
 * Date: 2/24/13 8:52 PM
 */
public class FuBuilder {
    private String fuFieldTemplate;
    private String fuMethodTemplate;
    private String fuClassName;
    private String className;
    private String fieldName;
    private String fieldType;
    private String getterMethodName;

    public String buildFuFieldText() {
        return buildByTemplate(fuFieldTemplate);
    }

    public String buildFuMethodText() {
        return buildByTemplate(fuMethodTemplate);
    }

    public String buildByTemplate(final String template) {
        return template
                .replaceAll(Constants.Vars.FU_CLASS_NAME_VAR, fuClassName)
                .replaceAll(Constants.Vars.THIS_TYPE_VAR, className)
                .replaceAll(Constants.Vars.FIELD_TYPE_VAR, fieldType)
                .replaceAll(Constants.Vars.FIELD_GETTER_VAR, getterMethodName)
                .replaceAll(Constants.Vars.FIELD_NAME_VAR, fieldName)
                .replaceAll(Constants.Vars.FIELD_NAME_UPPER_VAR, upFirstChar(fieldName))
                .replaceAll(Constants.Vars.FIELD_NAME_ALL_BIG_VAR, buildConstFieldName(fieldName));
    }

    private static String buildConstFieldName(final String fieldName) {
        final int length = fieldName.length();
        final StringBuilder sb = new StringBuilder(length * 2);
        for (int i = 0; i < length; ++i) {
            final char ch = fieldName.charAt(i);
            if (Character.isUpperCase(ch) && i != 0) {
                sb.append("_");
            }
            sb.append(Character.toUpperCase(ch));
        }
        return sb.toString();
    }

    private static String upFirstChar(final String fieldName) {
        if (fieldName != null && !fieldName.isEmpty()) {
            return Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        }
        return "";
    }

    public FuBuilder setFuFieldTemplate(final String fuFieldTemplate) {
        this.fuFieldTemplate = fuFieldTemplate;
        return this;
    }

    public FuBuilder setFuClassName(final String fuClassName) {
        this.fuClassName = fuClassName;
        return this;
    }

    public FuBuilder setClassName(final String className) {
        this.className = className;
        return this;
    }

    public FuBuilder setFieldName(final String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public FuBuilder setFieldType(final String fieldType) {
        this.fieldType = PsiTypesUtil.boxIfPossible(fieldType);
        return this;
    }

    public FuBuilder setGetterMethodName(final String getterMethodName) {
        this.getterMethodName = getterMethodName;
        return this;
    }

    public static FuBuilder getInstance(final PsiClass clazz, final PsiField field, final PsiMethod getterMethod, final PropertiesState properties) {
        final String fieldName = field.getName();
        return new FuBuilder()
                .setClassName(clazz.getName())
                .setFieldName(fieldName)
                .setFieldType(field.getType().getCanonicalText())
                .setFuFieldTemplate(properties.getFuFieldTemplate())
                .setFuMethodTemplate(properties.getFuMethodTemplate())
                .setFuClassName(properties.getFuClassName())
                .setGetterMethodName(getterMethod.getName());
    }

    public FuBuilder setFuMethodTemplate(final String fuMethodTemplate) {
        this.fuMethodTemplate = fuMethodTemplate;
        return this;
    }
}

