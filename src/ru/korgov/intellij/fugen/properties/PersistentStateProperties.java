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
public class PersistentStateProperties implements PersistentStateComponent<PersistentStateProperties> {
    public static final String NAME = "FuGenConfiguration";

    public static PersistentStateProperties getInstance(final Project project) {
        return ServiceManager.getService(project, PersistentStateProperties.class);
    }

    private String fuClassName = Constants.DEFAULT_FU_CLASS_NAME;
    private String fuMethodName = Constants.DEFAULT_FU_METHOD_NAME;
    private String fuConstNamePrefix = Constants.DEFAULT_FU_CONST_PREFIX;
    private String fuTemplate = Constants.DEFAULT_FU_TEMPLATE;

    public String getFuClassName() {
        return fuClassName;
    }

    public void setFuClassName(final String fuClassName) {
        this.fuClassName = fuClassName;
    }

    public String getFuMethodName() {
        return fuMethodName;
    }

    public void setFuMethodName(final String fuMethodName) {
        this.fuMethodName = fuMethodName;
    }

    public String getFuConstNamePrefix() {
        return fuConstNamePrefix;
    }

    public void setFuConstNamePrefix(final String fuConstNamePrefix) {
        this.fuConstNamePrefix = fuConstNamePrefix;
    }

    public String getFuTemplate() {
        return fuTemplate;
    }

    public void setFuTemplate(final String fuTemplate) {
        this.fuTemplate = fuTemplate;
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

    public PersistentStateProperties getDefaultInstance() {
        return new PersistentStateProperties();
    }
}
