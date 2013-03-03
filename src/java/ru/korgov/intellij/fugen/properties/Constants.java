package ru.korgov.intellij.fugen.properties;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Author: Kirill Korgov (kirill@korgov.ru)
 * Date: 23.02.13 4:49
 */
public class Constants {

    public static final String DEFAULT_FU_CLASS_NAME = "ru.korgov.util.func.Function";

    public static final String DEFAULT_FU_FIELD_TEMPLATE =
            "public static final $" + Vars.FU_CLASS + "$<$" + Vars.THIS_TYPE + "$, $" + Vars.FIELD_TYPE + "$> TO_$" + Vars.FIELD_NAME_ALL_BIG + "$ = new $" + Vars.FU_CLASS + "$<$" + Vars.THIS_TYPE + "$, $" + Vars.FIELD_TYPE + "$>() {\n" +
                    "    @Override\n" +
                    "    public $" + Vars.FIELD_TYPE + "$ apply(final $" + Vars.THIS_TYPE + "$ arg) {\n" +
                    "        return arg.$" + Vars.FIELD_GETTER + "$();\n" +
                    "    }\n" +
                    "    //Add or change actions in Project Settings -> FuGen" +
                    "};";

    public static final String DEFAULT_FU_METHOD_TEMPLATE =
            "public static $" + Vars.FU_CLASS + "$<$" + Vars.THIS_TYPE + "$, $" + Vars.FIELD_TYPE + "$> as$" + Vars.FIELD_NAME_BIG + "$() {\n" +
                    "    return TO_$" + Vars.FIELD_NAME_ALL_BIG + "$;\n" +
                    "}";

    public static final String DEFAULT_GENERATOR_NAME = "Functions";

    public static List<GeneratorPropertiesState> getDefaultProperties() {
        return Arrays.asList(new GeneratorPropertiesState());
    }


    public static class Vars {
        public static final String FU_CLASS = "FuClass";
        public static final String THIS_TYPE = "ThisType";
        public static final String FIELD_TYPE = "FieldType";
        public static final String FIELD_GETTER = "fieldGetter";
        public static final String FIELD_NAME = "fieldName";
        public static final String FIELD_NAME_BIG = "FieldName";
        public static final String FIELD_NAME_ALL_BIG = "FIELD_NAME";

        private static final List<String> ALL_VARS = Arrays.asList(
                FU_CLASS,
                THIS_TYPE,
                FIELD_TYPE,
                FIELD_GETTER,
                FIELD_NAME,
                FIELD_NAME_BIG,
                FIELD_NAME_ALL_BIG
        );


        public static final String FU_CLASS_NAME_VAR = wrapRegexpVar(FU_CLASS);
        public static final String THIS_TYPE_VAR = wrapRegexpVar(THIS_TYPE);
        public static final String FIELD_TYPE_VAR = wrapRegexpVar(FIELD_TYPE);
        public static final String FIELD_GETTER_VAR = wrapRegexpVar(FIELD_GETTER);
        public static final String FIELD_NAME_VAR = wrapRegexpVar(FIELD_NAME);
        public static final String FIELD_NAME_UPPER_VAR = wrapRegexpVar(FIELD_NAME_BIG);
        public static final String FIELD_NAME_ALL_BIG_VAR = wrapRegexpVar(FIELD_NAME_ALL_BIG);

        private Vars() {
        }

        private static String wrapRegexpVar(final String name) {
            return "\\$" + name + "\\$";
        }
    }

    public static class Patterns {
        public static final Pattern FIELD_GETTER_VAR_P = Pattern.compile(Vars.FIELD_GETTER_VAR);

        private Patterns() {
        }
    }

    private Constants() {
    }

    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
    public static List<String> getAllVarNames() {
        return Collections.unmodifiableList(Vars.ALL_VARS);
    }
}
