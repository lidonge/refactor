package refactor.mybatis.alias;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author yanglin
 */
public class AliasClassReference extends PsiReferenceBase<XmlAttributeValue> {

    private Function<AliasDesc, String> function = new Function<AliasDesc, String>() {
        @Override
        public String apply(AliasDesc input) {
            return input.getAlias();
        }
    };

    public AliasClassReference(@NotNull XmlAttributeValue element) {
        super(element, true);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        XmlAttributeValue attributeValue = getElement();
        Project prj = attributeValue.getProject();
        String value = attributeValue.getValue();
        AliasFacade facade = AliasFacade.getInstance(prj);
        Optional<PsiClass> opt = facade.findPsiClass(attributeValue, value);
        return opt.orNull();
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        AliasFacade aliasFacade = AliasFacade.getInstance(getElement().getProject());
        Collection<String> result = Collections2.transform(aliasFacade.getAliasDescs(getElement()), function);
        return result.toArray(new String[result.size()]);
    }

}
