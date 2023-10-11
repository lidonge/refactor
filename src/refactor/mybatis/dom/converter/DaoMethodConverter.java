package refactor.mybatis.dom.converter;

import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.ConvertContext;
import refactor.mybatis.dom.model.Mapper;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import refactor.mybatis.utils.JavaUtils;
import refactor.mybatis.utils.MapperUtils;

/**
 * @author yanglin
 */
public class DaoMethodConverter extends ConverterAdaptor<PsiMethod> {

    @Nullable
    @Override
    public PsiMethod fromString(@Nullable @NonNls String id, ConvertContext context) {
        Mapper mapper = MapperUtils.getMapper(context.getInvocationElement());
        return JavaUtils.findMethod(context.getProject(), MapperUtils.getNamespace(mapper), id).orNull();
    }

}