package refactor.mybatis.reference;

import com.google.common.base.Optional;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.xml.XmlAttributeValue;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import refactor.mybatis.dom.MapperBacktrackingUtils;
import refactor.mybatis.utils.JavaUtils;

/**
 * @author yanglin
 */
public class PsiFieldReferenceSetResolver extends ContextReferenceSetResolver<XmlAttributeValue, PsiField> {

    protected PsiFieldReferenceSetResolver(XmlAttributeValue from) {
        super(from);
    }

    @NotNull
    @Override
    public String getText() {
        return getElement().getValue();
    }

    @NotNull
    @Override
    public Optional<PsiField> resolve(@NotNull PsiField current, @NotNull String text) {
        PsiType type = current.getType();
        if (type instanceof PsiClassReferenceType && !((PsiClassReferenceType) type).hasParameters()) {
            PsiClass clazz = ((PsiClassReferenceType) type).resolve();
            if (null != clazz) {
                return JavaUtils.findSettablePsiField(clazz, text);
            }
        }
        return Optional.absent();
    }

    @NotNull
    @Override
    public Optional<PsiField> getStartElement(@Nullable String firstText) {
        Optional<PsiClass> clazz = MapperBacktrackingUtils.getPropertyClazz(getElement());
        return clazz.isPresent() ? JavaUtils.findSettablePsiField(clazz.get(), firstText) : Optional.<PsiField>absent();
    }

}