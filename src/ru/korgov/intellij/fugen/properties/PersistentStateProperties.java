package ru.korgov.intellij.fugen.properties;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StorageScheme;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;

/**
 * Author: Kirill Korgov (kirill@korgov.ru))
 * Date: 20.02.13 3:48
 */
@State(
        name = PersistentStateProperties.NAME,
        storages = {
                @Storage(id = "default", file = "$PROJECT_FILE$"),
                @Storage(id = "dir", file = "$PROJECT_CONFIG_DIR$" + "/" + PersistentStateProperties.NAME + ".xml", scheme = StorageScheme.DIRECTORY_BASED)
        }
)
public class PersistentStateProperties implements PersistentStateComponent<PersistentStateProperties>, PropertiesState {
    public static final String NAME = "FuGenConfiguration";

    public static PersistentStateProperties getInstance(final Project project) {
        return ServiceManager.getService(project, PersistentStateProperties.class);
    }

    private String fuClassName = Constants.DEFAULT_FU_CLASS_NAME;
    private String fuFieldTemplate = Constants.DEFAULT_FU_TEMPLATE;
    private String fuMethodTemplate = Constants.DEFAULT_FU_METHOD_TEMPLATE;

    private boolean fieldTemplateEnabled = true;
    private boolean methodTemplateEnabled = false;

    @Override
    public boolean isFieldTemplateEnabled() {
        return fieldTemplateEnabled;
    }

    public void setFieldTemplateEnabled(final boolean fieldTemplateEnabled) {
        this.fieldTemplateEnabled = fieldTemplateEnabled;
    }

    @Override
    public boolean isMethodTemplateEnabled() {
        return methodTemplateEnabled;
    }

    public void setMethodTemplateEnabled(final boolean methodTemplateEnabled) {
        this.methodTemplateEnabled = methodTemplateEnabled;
    }

    @Override
    public String getFuClassName() {
        return fuClassName;
    }

    public void setFuClassName(final String fuClassName) {
        this.fuClassName = fuClassName;
    }

    @Override
    public String getFuFieldTemplate() {
        return fuFieldTemplate;
    }

    @Override
    public String getFuMethodTemplate() {
        return fuMethodTemplate;
    }

    public void setFuMethodTemplate(final String fuMethodTemplate) {
        this.fuMethodTemplate = fuMethodTemplate;
    }

    public void setFuFieldTemplate(final String fuFieldTemplate) {
        this.fuFieldTemplate = fuFieldTemplate;
    }

    @Override
    @SuppressWarnings("ReturnOfThis")
    public PersistentStateProperties getState() {
        return this;
    }

    @Override
    public void loadState(final PersistentStateProperties state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public static PersistentStateProperties getDefaultInstance() {
        return new PersistentStateProperties();
    }

    @SuppressWarnings({"OverlyComplexMethod", "ControlFlowStatementWithoutBraces", "NonFinalFieldReferenceInEquals", "RedundantIfStatement"})
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PersistentStateProperties that = (PersistentStateProperties) o;

        if (fieldTemplateEnabled != that.fieldTemplateEnabled) return false;
        if (methodTemplateEnabled != that.methodTemplateEnabled) return false;
        if (fuClassName != null ? !fuClassName.equals(that.fuClassName) : that.fuClassName != null) return false;
        if (fuFieldTemplate != null ? !fuFieldTemplate.equals(that.fuFieldTemplate) : that.fuFieldTemplate != null)
            return false;
        if (fuMethodTemplate != null ? !fuMethodTemplate.equals(that.fuMethodTemplate) : that.fuMethodTemplate != null)
            return false;

        return true;
    }

    @SuppressWarnings("NonFinalFieldReferencedInHashCode")
    @Override
    public int hashCode() {
        int result = fuClassName != null ? fuClassName.hashCode() : 0;
        result = 31 * result + (fuFieldTemplate != null ? fuFieldTemplate.hashCode() : 0);
        result = 31 * result + (fuMethodTemplate != null ? fuMethodTemplate.hashCode() : 0);
        result = 31 * result + (fieldTemplateEnabled ? 1 : 0);
        result = 31 * result + (methodTemplateEnabled ? 1 : 0);
        return result;
    }
}
