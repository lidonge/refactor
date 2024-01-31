package free.servpp.refactor.servitization.ui;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.search.searches.ReferencesSearch;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lidong@date 2024-01-30@version 1.0
 */
public abstract class CallerMethodInfo {
    /**
     * If a method "doSomething(Type param1...)" which code block inside call the get/set method,
     * the method "doSomething" is the key of the map, and value CallExpressionInfo contains all changes to the method.
     */
    protected Map<PsiMethod, CallExpressionInfo> refMethodExpressionInfos = new HashMap<>();
    protected TargetClass targetClass;
    protected Project project;

    public CallerMethodInfo(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }

    public TargetClass getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(TargetClass targetClass) {
        this.targetClass = targetClass;
    }

    protected void findCallExpressInfo(PsiMethod psiMethodExtracted) {
        ReferencesSearch.search(psiMethodExtracted).forEach(psiReference -> {
            PsiReferenceExpression psiReferenceExpression = (PsiReferenceExpression) psiReference.getElement();
            PsiMethod method = PsiUtil.getMethod(psiReferenceExpression);
            if (method != null) {
                CallExpressionInfo callExpressionInfo = getCallExpressionInfo(method);
                callExpressionInfo.addReference(psiReferenceExpression);
            }
        });
    }

    private CallExpressionInfo getCallExpressionInfo(PsiMethod method) {
        CallExpressionInfo callExpressionInfo = refMethodExpressionInfos.get(method);
        if (callExpressionInfo == null) {
            callExpressionInfo = new CallExpressionInfo(project, method, targetClass, isRoot());
            refMethodExpressionInfos.put(method, callExpressionInfo);
        }
        return callExpressionInfo;
    }

    protected abstract boolean isRoot();


    public PsiFile getExtractedFile() {
        return targetClass.getExtractedFile();
    }

    public PsiField getAggField() {
        return targetClass.getAggField();
    }

    public PsiMethod getAggGetMethod() {
        return targetClass.getAggGetMethod();
    }

    public PsiMethod getAggSetMethod() {
        return targetClass.getAggSetMethod();
    }
}
