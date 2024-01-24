package refactor.mybatis.dom.description;

import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import refactor.mybatis.dom.model.Mapper;
import refactor.mybatis.utils.DomUtils;
import com.intellij.openapi.module.Module;
/**
 * @author yanglin
 */
public class MapperDescription extends DomFileDescription<Mapper> {

    public MapperDescription() {
        super(Mapper.class, "mapper");
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        return DomUtils.isMybatisFile(file);
    }

}
