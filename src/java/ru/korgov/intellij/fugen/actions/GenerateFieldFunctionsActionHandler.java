package ru.korgov.intellij.fugen.actions;

import com.intellij.codeInsight.generation.ClassMember;
import com.intellij.codeInsight.generation.GenerateMembersHandlerBase;
import com.intellij.codeInsight.generation.GenerationInfo;
import com.intellij.codeInsight.generation.PsiFieldMember;
import com.intellij.codeInsight.generation.PsiGenerationInfo;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.LanguageLevelProjectExtension;
import com.intellij.openapi.util.Condition;
import com.intellij.pom.java.LanguageLevel;
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
import ru.korgov.intellij.fugen.FuBuilder;
import ru.korgov.intellij.fugen.properties.PersistentStateProperties;
import ru.korgov.intellij.fugen.properties.PropertiesState;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Kirill Korgov (kirill@korgov.ru)
 * Date: 06.12.12
 */
public class GenerateFieldFunctionsActionHandler extends GenerateMembersHandlerBase {

    private final String actionText;
    private final int actionIndex;

    public GenerateFieldFunctionsActionHandler(final String actionText, final int actionIndex) {
        super("Generate " + actionText);
        this.actionText = actionText;
        this.actionIndex = actionIndex;
    }

    @Override
    protected ClassMember[] chooseOriginalMembers(final PsiClass aClass, final Project project) {
        if (aClass.isInterface()) {
            return ClassMember.EMPTY_ARRAY;
        }
        return super.chooseOriginalMembers(aClass, project);
    }

    @Override
    protected GenerationInfo[] generateMemberPrototypes(final PsiClass clazz, final ClassMember original) throws IncorrectOperationException {
        final List<GenerationInfo> out = new ArrayList<GenerationInfo>(3);
        final PropertiesState properties = getProperties(clazz);

        if (properties.isFieldTemplateEnabled() || properties.isMethodTemplateEnabled()) {
            if (original instanceof PsiFieldMember) {
                final PsiFieldMember psiFieldMember = (PsiFieldMember) original;
                final PsiField field = psiFieldMember.getElement();
                final PsiMethod fieldGetter = generateGetter(field);

                final FuBuilder fuBuilder = FuBuilder.getInstance(clazz, field, fieldGetter, properties);
                appendGetterIfNeed(out, clazz, fieldGetter, fuBuilder);
                appendFuFieldIfNeed(clazz, out, fuBuilder);
                appendFuMethodIfNeed(clazz, out, fuBuilder);
            }
        }

        return out.toArray(new GenerationInfo[out.size()]);
    }

    private void appendFuMethodIfNeed(final PsiClass clazz, final List<GenerationInfo> out, final FuBuilder fuBuilder) {
        addIfNotNull(out, tryGenerateFuMethod(clazz, fuBuilder));
    }

    private void appendFuFieldIfNeed(final PsiClass clazz, final List<GenerationInfo> out, final FuBuilder fuBuilder) {
        addIfNotNull(out, tryGenerateFuField(clazz, fuBuilder));
    }

    private void appendGetterIfNeed(final List<GenerationInfo> out, final PsiClass clazz, final PsiMethod fieldGetter, final FuBuilder fuBuilder) {
        if (fuBuilder.isNeedGetter()) {
            if (!methodExists(clazz, fieldGetter)) {
                out.add(new PsiGenerationInfo<PsiMethod>(fieldGetter));
            }
        }
    }

    private PropertiesState getProperties(final PsiClass aClass) {
        final PersistentStateProperties state = PersistentStateProperties.getInstance(aClass.getProject());
        final List<? extends PropertiesState> propertiesList = state.getProperties();
        return propertiesList.get(actionIndex);
    }

    @Nullable
    private PsiGenerationInfo<PsiField> tryGenerateFuField(final PsiClass clazz, final FuBuilder fuBuilder) {
        final String fuText = fuBuilder.buildFuFieldText();
        if (fuText != null && !fuText.isEmpty()) {
            final Project project = clazz.getProject();
            final PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
            final PsiField fieldFromText = elementFactory.createFieldFromText(fuText, clazz);
            final PsiField existsFieldFu = clazz.findFieldByName(fieldFromText.getName(), false);
            if (existsFieldFu == null) {
                return new PsiGenerationInfo<PsiField>(fieldFromText);
            }
        }
        return null;
    }

    @Nullable
    private PsiGenerationInfo<PsiMethod> tryGenerateFuMethod(final PsiClass clazz, final FuBuilder fuBuilder) {

        final Project project = clazz.getProject();
        final PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        final String fuMethodText = fuBuilder.buildFuMethodText();
        if (fuMethodText != null && !fuMethodText.isEmpty()) {
            final LanguageLevel languageLevel = LanguageLevelProjectExtension.getInstance(project).getLanguageLevel();
            final PsiMethod methodFromText = elementFactory.createMethodFromText(fuMethodText, clazz, languageLevel);
            final PsiMethod[] existsMethods = clazz.findMethodsByName(methodFromText.getName(), false);
            if (existsMethods.length == 0) {
                return new PsiGenerationInfo<PsiMethod>(methodFromText);
            }
        }
        return null;
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
        return "No fields have been found to generate " + actionText + " for";
    }

    protected String getNothingAcceptedMessage() {
        return "No fields without " + actionText + " were found";
    }

    @Nullable
    @Override
    @SuppressWarnings("RefusedBequest")
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
