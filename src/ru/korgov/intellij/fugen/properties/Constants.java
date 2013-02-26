package ru.korgov.intellij.fugen.properties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Author: Kirill Korgov (kirill@korgov.ru)
 * Date: 23.02.13 4:49
 */
public class Constants {

    public static final String DEFAULT_FU_CLASS_NAME = "ru.korgov.util.func.Function";
    public static final String DEFAULT_FU_CONST_PREFIX = "TO_";

    //todo: replace by vars

    public static final String DEFAULT_FU_TEMPLATE =
            "public static final $FuClass$<$ThisType$, $FieldType$> $FuConstName$ = new $FuClass$<$ThisType$, $FieldType$>() {\n" +
                    "    @Override\n" +
                    "    public $FieldType$ apply(final $ThisType$ arg) {\n" +
                    "        return arg.$fieldGetter$();\n" +
                    "    }\n" +
                    "};";

    public static final String DEFAULT_FU_METHOD_TEMPLATE =
            "public static $FuClass$<$ThisType$, $FieldType$> as$FieldName$() {\n" +
                    "    return $FuConstName$;\n" +
                    "}";


    public static class Vars {
        public static final String FU_CLASS = "FuClass";
        public static final String THIS_TYPE = "ThisType";
        public static final String FIELD_TYPE = "FieldType";
        public static final String FU_CONST_NAME = "FuConstName";
        public static final String FIELD_GETTER = "fieldGetter";
        public static final String FIELD_NAME = "fieldName";
        public static final String FIELD_NAME_BIG = "FieldName";

        private static final List<String> ALL_VARS = Arrays.asList(
                FU_CLASS,
                THIS_TYPE,
                FIELD_TYPE,
                FU_CONST_NAME,
                FIELD_GETTER,
                FIELD_NAME,
                FIELD_NAME_BIG
        );


        public static final String FU_CLASS_NAME_VAR = wrapRegexpVar(FU_CLASS);
        public static final String THIS_TYPE_VAR = wrapRegexpVar(THIS_TYPE);
        public static final String FIELD_TYPE_VAR = wrapRegexpVar(FIELD_TYPE);
        public static final String FU_CONST_NAME_VAR = wrapRegexpVar(FU_CONST_NAME);
        public static final String FIELD_GETTER_VAR = wrapRegexpVar(FIELD_GETTER);
        public static final String FIELD_NAME_VAR = wrapRegexpVar(FIELD_NAME);
        public static final String FIELD_NAME_UPPER_VAR = wrapRegexpVar(FIELD_NAME_BIG);

        private Vars() {
        }

        private static String wrapRegexpVar(final String name) {
            return "\\$" + name + "\\$";
        }
    }

    private Constants() {
    }

    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static List<String> getAllVarNames() {
        return Collections.unmodifiableList(Vars.ALL_VARS);
    }
}
