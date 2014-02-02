package ru.korgov.intellij.fugen.properties;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StorageScheme;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;

import java.util.ArrayList;
import java.util.List;

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

    private List<GeneratorPropertiesState> properties = Constants.getDefaultProperties();

    public List<GeneratorPropertiesState> getProperties() {
        return new ArrayList<>(properties);
    }

    public void setProperties(final List<GeneratorPropertiesState> properties) {
        this.properties = doFullCopy(properties);
    }

    private List<GeneratorPropertiesState> doFullCopy(final List<GeneratorPropertiesState> properties) {
        final List<GeneratorPropertiesState> res = new ArrayList<>(properties.size());
        for (final GeneratorPropertiesState property : properties) {
            res.add(property.copy());
        }
        return res;
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

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PersistentStateProperties that = (PersistentStateProperties) o;

        if (properties != null ? !properties.equals(that.properties) : that.properties != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return properties != null ? properties.hashCode() : 0;
    }
}
