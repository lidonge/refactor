package refactor.mybatis.utils;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import refactor.Logger;
import refactor.mybatis.dom.model.*;
import refactor.mybatis.dom.model.Package;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * @author yanglin
 */
public final class MapperUtils {

    private MapperUtils() {
        throw new UnsupportedOperationException();
    }
    @NotNull
    @NonNls
    public static Collection<Mapper> findMappers(@NotNull Project project) {
        return DomUtils.findDomElements(project, Mapper.class);
    }

    @NotNull
    @NonNls
    public static Collection<Mapper> findMappers(@NotNull Project project, @NotNull String namespace) {

        List<Mapper> result = Lists.newArrayList();
        for (Mapper mapper : findMappers(project)) {
            if (getNamespace(mapper).equals(namespace)) {
                result.add(mapper);
            }
        }
        return result;
    }

    @NotNull
    public static Collection<Mapper> findMappers(@NotNull Project project, @NotNull PsiClass clazz) {
        return  findMappers(project, clazz.getQualifiedName()) ;
    }

    @NotNull
    @NonNls
    public static Mapper getMapper(@NotNull DomElement element) {
        Optional<Mapper> optional = Optional.fromNullable(DomUtil.getParentOfType(element, Mapper.class, true));
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new IllegalArgumentException("Unknown element");
        }
    }

    @NotNull
    @NonNls
    public static String getNamespace(@NotNull Mapper mapper) {
        String ns = mapper.getNamespace().getStringValue();
        return null == ns ? "" : ns;
    }

    @NotNull
    @NonNls
    public static String getNamespace(@NotNull DomElement element) {
        return getNamespace(getMapper(element));
    }

    @Nullable
    @NonNls
    public static <T extends IdDomElement> String getId(@NotNull T domElement) {
        return domElement.getId().getRawText();
    }

    @NonNls
    public static boolean isMapperWithSameNamespace(@Nullable Mapper mapper, @Nullable Mapper target) {
        return null != mapper && null != target && getNamespace(mapper).equals(getNamespace(target));
    }

    @NotNull
    @NonNls
    public static <T extends IdDomElement> String getIdSignature(@NotNull T domElement) {
        return getNamespace(domElement) + "." + getId(domElement);
    }

    @NotNull
    @NonNls
    public static <T extends IdDomElement> String getIdSignature(@NotNull T domElement, @NotNull Mapper mapper) {
        Mapper contextMapper = getMapper(domElement);
        String id = getId(domElement);
        if(id == null) {
            id = "";
        }
        String idsignature= getIdSignature(domElement);
        //getIdSignature(domElement)
        return isMapperWithSameNamespace(contextMapper, mapper) ?id :idsignature ;
    }

    public static void processConfiguredTypeAliases(@NotNull Project project, @NotNull Processor<TypeAlias> processor) {
        for (Configuration conf : getMybatisConfigurations(project)) {
            for (TypeAliases tas : conf.getTypeAliases()) {
                for (TypeAlias ta : tas.getTypeAlias()) {
                    String stringValue = ta.getAlias().getStringValue();
                    if (null != stringValue && !processor.process(ta)) {
                        return;
                    }
                }
            }
        }
    }

    private static Collection<Configuration> getMybatisConfigurations(Project project) {
        return DomUtils.findDomElements(project, Configuration.class);
    }

    public static void processConfiguredPackage(@NotNull Project project,
                                                @NotNull Processor<Package> processor) {
        for (Configuration conf : getMybatisConfigurations(project)) {
            for (TypeAliases tas : conf.getTypeAliases()) {
                for (Package pkg : tas.getPackages()) {
                    if (!processor.process(pkg)) {
                        return;
                    }
                }
            }
        }
    }
}