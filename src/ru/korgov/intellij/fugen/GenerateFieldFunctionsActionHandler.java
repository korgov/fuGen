package ru.korgov.intellij.fugen;

import com.intellij.codeInsight.generation.ClassMember;
import com.intellij.codeInsight.generation.GenerateMembersHandlerBase;
import com.intellij.codeInsight.generation.GenerationInfo;
import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.codeInsight.generation.PsiGenerationInfo;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Kirill Korgov (korgov@yandex-team.ru)
 * Date: 06.12.12
 */
public class GenerateFieldFunctionsActionHandler extends GenerateMembersHandlerBase {

    public GenerateFieldFunctionsActionHandler() {
        super("Generate functions");
    }

    @Override
    protected ClassMember[] chooseOriginalMembers(final PsiClass aClass, final Project project) {
        if (aClass.isInterface()) {
            return ClassMember.EMPTY_ARRAY;
        }
        return super.chooseOriginalMembers(aClass, project);
    }

    @Override
    protected GenerationInfo[] generateMemberPrototypes(final PsiClass aClass, final ClassMember original) throws IncorrectOperationException {
        final List<GenerationInfo> out = new ArrayList<GenerationInfo>(2);
        if (original instanceof PsiFieldMember) {
            final PsiFieldMember psiFieldMember = (PsiFieldMember) original;
            final PsiField field = psiFieldMember.getElement();
            final PsiMethod getterMethod = generateGetter(field);
            if (!methodExists(aClass, getterMethod)) {
                out.add(new PsiGenerationInfo<PsiMethod>(getterMethod));
            }
            addIfNotNull(out, tryGenerateFunction(aClass, field, getterMethod));
        }
        return out.toArray(new GenerationInfo[out.size()]);
    }

    @Nullable
    private PsiGenerationInfo<PsiField> tryGenerateFunction(final PsiClass clazz, final PsiField field, final PsiMethod getterMethod) {

        final String fuConstantName = createFuConstantName(field);
        final PsiField existsFieldFu = clazz.findFieldByName(fuConstantName, false);
        if (existsFieldFu == null) {
            final PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(clazz.getProject());
            final String fieldTypeText = field.getType().getCanonicalText();
            final String fuClassName = "ru.korgov.util.func.Function";
            final String className = clazz.getQualifiedName();
            final String fuGenericType = "<" + className + ", " + fieldTypeText + ">";
            final String getterMethodName = getterMethod.getName();
            final String fuFullType = fuClassName + fuGenericType;
            return new PsiGenerationInfo<PsiField>(
                    elementFactory.createFieldFromText(
                            "public static final " + fuFullType + " " +
                                    fuConstantName + " = new " + fuFullType + "() {\n" +
                                    "    @Override\n" +
                                    "    public " + fieldTypeText + " apply(final " + className + " arg) {\n" +
                                    "        return arg." + getterMethodName + "();\n" +
                                    "    }\n" +
                                    "};",
                            clazz
                    )
            );
        }
        return null;
    }

    private String createFuConstantName(final PsiField field) {
        final String fieldName = field.getName();
        final StringBuilder sb = new StringBuilder("TO_");
        final int length = fieldName.length();
        for (int i = 0; i < length; ++i) {
            final char ch = fieldName.charAt(i);
            if (Character.isUpperCase(ch) && i != 0) {
                sb.append("_");
            }
            sb.append(Character.toUpperCase(ch));
        }
        return sb.toString();
    }

    public PsiMethod generateGetter(final PsiField field) {
        return PropertyUtil.generateGetterPrototype(field);
    }

    private static boolean methodExists(final PsiClass aClass, final PsiMethod template) {
        final PsiMethod existing = aClass.findMethodBySignature(template, false);
        return existing != null;
    }

    private void addIfNotNull(final List<GenerationInfo> out, final GenerationInfo info) {
        if (info != null) {
            out.add(info);
        }
    }

    @Override
    protected String getNothingFoundMessage() {
        return "No fields have been found to generate functions for";
    }

    protected String getNothingAcceptedMessage() {
        return "No fields without function were found";
    }

    @Nullable
    @Override
    protected ClassMember[] chooseOriginalMembers(final PsiClass aClass, final Project project, final Editor editor) {
        final ClassMember[] allMembers = getAllOriginalMembers(aClass);
        if (allMembers == null) {
            HintManager.getInstance().showErrorHint(editor, getNothingFoundMessage());
            return null;
        }
        if (allMembers.length == 0) {
            HintManager.getInstance().showErrorHint(editor, getNothingAcceptedMessage());
            return null;
        }
        return chooseMembers(allMembers, false, false, project, editor);
    }

    @Override
    @Nullable
    protected ClassMember[] getAllOriginalMembers(final PsiClass aClass) {
        final PsiField[] fields = withoutStatic(aClass.getFields());
        if (fields.length == 0) {
            return null;
        }

        final List<ClassMember> members = ContainerUtil.findAll(wrap(fields), new Condition<ClassMember>() {
            @Override
            public boolean value(final ClassMember member) {
                try {
                    return generateMemberPrototypes(aClass, member).length > 0;
                } catch (final IncorrectOperationException ignored) {
                    return false;
                }
            }
        });
        return members.toArray(new ClassMember[members.size()]);
    }

    private PsiField[] withoutStatic(final PsiField[] fields) {
        final List<PsiField> out = new ArrayList<PsiField>(fields.length);
        for (final PsiField field : fields) {
            final PsiModifierList modifierList = field.getModifierList();
            if (modifierList == null || !modifierList.hasModifierProperty("static")) {
                out.add(field);
            }
        }
        return out.toArray(new PsiField[out.size()]);
    }

    private ClassMember[] wrap(final PsiField[] fields) {
        final List<ClassMember> out = new ArrayList<ClassMember>(fields.length);
        for (final PsiField field : fields) {
            out.add(new PsiFieldMember(field));
        }
        return out.toArray(new ClassMember[out.size()]);
    }

}
