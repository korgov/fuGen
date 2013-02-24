package ru.korgov.intellij.fugen.properties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Author: Kirill Korgov (korgov@yandex-team.ru))
 * Date: 23.02.13 4:49
 */
public class Constants {

    public static final String DEFAULT_FU_CLASS_NAME = "ru.korgov.util.func.Function";
    public static final String DEFAULT_FU_METHOD_NAME = "apply";
    public static final String DEFAULT_FU_CONST_PREFIX = "TO_";

    public static final String DEFAULT_FU_TEMPLATE =
            "public static final $FuClass$<$ThisType$, $FieldType$> $FuConstName$ = new $FuClass$<$ThisType$, $FieldType$>() {\n" +
                    "    @Override\n" +
                    "    $FieldType$ $fuMethod$(final $ThisType$ arg) {\n" +
                    "        return arg.$fieldGetter$();\n" +
                    "    }\n" +
                    "};";

    public static final String FU_CLASS_NAME_VAR = wrapRegexpVar("FuClass");
    public static final String THIS_TYPE_VAR = wrapRegexpVar("ThisType");
    public static final String FIELD_TYPE_VAR = wrapRegexpVar("FieldType");
    public static final String FU_CONST_NAME_VAR = wrapRegexpVar("FuConstName");
    public static final String FU_METHOD_VAR = wrapRegexpVar("fuMethod");
    public static final String FIELD_GETTER_VAR = wrapRegexpVar("fieldGetter");
    public static final String FIELD_NAME_VAR = wrapRegexpVar("fieldName");
    public static final String FIELD_NAME_UPPER_VAR = wrapRegexpVar("FieldName");

    private static final List<Pattern> ALL_VARS_AS_PATTERNS = Arrays.asList(
            Pattern.compile(FU_CLASS_NAME_VAR),
            Pattern.compile(THIS_TYPE_VAR),
            Pattern.compile(FIELD_TYPE_VAR),
            Pattern.compile(FU_CONST_NAME_VAR),
            Pattern.compile(FU_METHOD_VAR),
            Pattern.compile(FIELD_GETTER_VAR),
            Pattern.compile(FIELD_NAME_VAR),
            Pattern.compile(FIELD_NAME_UPPER_VAR)
    );

    private Constants() {
    }

    private static String wrapRegexpVar(final String name) {
        return "\\$" + name + "\\$";
    }

    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static List<Pattern> getAllVars() {
        return Collections.unmodifiableList(ALL_VARS_AS_PATTERNS);
    }
}
