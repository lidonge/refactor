package free.servpp.refactor.servitization.ui;

import com.intellij.lang.jvm.JvmParameter;
import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lidong@date 2024-01-25@version 1.0
 */
public class PsiUtil {
    static String GET = "get";
    static String SET = "set";
    public static String makeGetMethodName(String field) {
        return GET + makeClassName(field);
    }
    public static String makeSetMethodName(String field) {
        return SET + makeClassName(field);
    }
    public static String makeClassName(String field) {
        char[] chars = field.toCharArray();
        char c = Character.toUpperCase(chars[0]);
        chars[0] = c;

        return String.valueOf(chars);
    }
    public static String makeFieldName(String clsName) {
        char[] chars = clsName.toCharArray();
        char c = Character.toLowerCase(chars[0]);
        chars[0] = c;

        return String.valueOf(chars);
    }

    public static PsiFile getFile(PsiElement psiElement){
        while(psiElement != null){
            if(psiElement instanceof PsiFile)
                return (PsiFile) psiElement;
            psiElement = psiElement.getParent();
        }
        return null;
    }

    public static PsiMethod getMethod(PsiElement psiElement){
        while(psiElement != null){
            if(psiElement instanceof PsiMethod)
                return (PsiMethod) psiElement;
            psiElement = psiElement.getParent();
        }
        return null;
    }

    public static PsiClass getPsiClass(PsiFile psiFile){
        for(PsiElement psiElement:psiFile.getChildren()){
            if(psiElement instanceof PsiClass){
                return (PsiClass) psiElement;
            }
        }
        return null;
    }
    public static JvmParameter getJvmParameter(String qualifer, PsiMethod psiMethod) {
        JvmParameter theParameter = null;
        for (JvmParameter jvmParameter:psiMethod.getParameters()){
            if(jvmParameter.getName().equals(qualifer)){//the reference is a parameter
                theParameter = jvmParameter;
                break;
            }
        }
        return theParameter;
    }

    public static int countPsiElement(PsiElement src, PsiElement target){
        int count = 0;
        for(PsiElement child: src.getChildren()){
            if(child.getText().equals(target.getText()) && child.getClass() == target.getClass())
                count++;
            else
                count += countPsiElement(child,target);
        }
        return count;
    }

    public static List<PsiElement> searchPsiElements(PsiElement src, PsiElement target){
        List<PsiElement> ret = new ArrayList<>();
        for(PsiElement child: src.getChildren()){
            if(child.getText().equals(target.getText()) && child.getClass() == target.getClass())
                ret.add(child);
            else {
                ret.addAll(searchPsiElements(child,target));
            }
        }
        return ret;
    }
    public static PsiClass getPsiClass(PsiElement psiElement) {
        while(psiElement != null){
            if(psiElement instanceof PsiClass)
                return (PsiClass) psiElement;
            psiElement = psiElement.getParent();
        }
        return null;
    }
}
