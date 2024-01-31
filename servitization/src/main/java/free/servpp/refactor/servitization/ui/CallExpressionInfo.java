package free.servpp.refactor.servitization.ui;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lidong@date 2024-01-26@version 1.0
 */
public class CallExpressionInfo extends CallerMethodInfo {
    /**
     * The method "doSomething"
     */
    private PsiMethod expressionMethod;
    /**
     * The old expression references, from global search.
     */
    private List<PsiReferenceExpression> references = new ArrayList<>();
    /**
     * Old expression change to new expression, key is old value is new.
     */
    private Map<PsiElement, PsiElement> changedExpressions = new HashMap<>();

    /**
     * The changed parameters of the method "doSomething",the key is name of new parameter,
     * value is a key-value pair which key is new and value old
     */
    private Map<String, Map.Entry<PsiParameter, PsiParameter>> changedParameters = new HashMap<>();
    /**
     * Some parameters should be removed.
     */
    private List<PsiParameter> removedParameters = new ArrayList<>();
    /**
     * Import the classes of the parameter.
     */
    private Map<PsiFile, List<PsiImportStatement>> newImports = new HashMap<>();
    private boolean parentIsRoot;

    public CallExpressionInfo(Project project, PsiMethod expressionMethod, TargetClass targetClass, boolean parentIsRoot) {
        super(project);
        this.expressionMethod = expressionMethod;
        this.targetClass = targetClass;
        this.parentIsRoot = parentIsRoot;
    }

    @Override
    public boolean isRoot() {
        return false;
    }

    public void dealRefExpressions() {
        boolean bNeedChangeParameter = isNeedChangeParameter();
        for (PsiReferenceExpression psiReferenceExpression : references) {
            changeMethodParameters(psiReferenceExpression, bNeedChangeParameter);
        }
        if (changedParameters.size() != 0) {//the method is changed, it should refactor recursively
            //find all referenced methods, and its expressions
            findCallExpressInfo(expressionMethod);
            //refactor the expressions and methods
            for (CallExpressionInfo info : refMethodExpressionInfos.values()) {
                info.dealRefExpressions();
            }

        }
    }

    private boolean isNeedChangeParameter() {
        PsiElement psiElement = parentIsRoot ? references.get(0).getQualifier() : getOrigExpression();
        return references.size() == PsiUtil.countPsiElement(expressionMethod,psiElement);
    }

    @Nullable
    private PsiExpression getOrigExpression() {
        PsiParameter psiParameter = getOrigParameter();
        PsiExpression theExpr = getExpression(psiParameter);
        return theExpr;
    }

    private PsiParameter getOrigParameter(){
        String origName = targetClass.getOrigClass().getName();
        for(PsiParameter psiParameter:expressionMethod.getParameterList().getParameters()){
            if(psiParameter.getTypeElement().getText().equals(origName)){
                return psiParameter;
            }
        }
        return null;
    }

    private PsiExpression getExpression(PsiParameter psiParameter) {
        PsiExpression theExpr = null;
        PsiMethodCallExpression psiMethodCallExpression = (PsiMethodCallExpression) references.get(0).getParent();
        for(PsiExpression psiExpression:psiMethodCallExpression.getArgumentList().getExpressions()){
            if(psiExpression.getText().equals(psiParameter.getName())){
                theExpr = psiExpression;
                break;
            }
        }
        return theExpr;
    }

    private void changeMethodParameters(PsiReferenceExpression psiReferenceExpression, boolean bNeedChangeParameter) {
        PsiElementFactory psiElementFactory = PsiElementFactory.getInstance(targetClass.getProject());
        if (!bNeedChangeParameter) {//the reference is a local var
            //make new psiReferenceExpression of local var
            PsiElement psiNewElement = null;
            PsiElement psiOldElement = psiReferenceExpression;
            if (parentIsRoot) {
                String text = psiReferenceExpression.getText();
                int endIndex = text.lastIndexOf(".");
                text = text.substring(0, endIndex) + "." + targetClass.getAggGetMethod().getName() + "()" + text.substring(endIndex);
                psiNewElement = psiElementFactory.createExpressionFromText(text, expressionMethod);
            } else {
                psiOldElement = psiOldElement.getParent();
                psiNewElement = createNewStatement(psiOldElement, psiElementFactory);
            }
//            PsiReferenceExpression psiReferenceExpressionNew = (PsiReferenceExpression) psiElementFactory.createExpressionFromText(text, expressionMethod);
            changedExpressions.put(psiOldElement, psiNewElement);
        } else {
//            String qualifer = psiReferenceExpression.getQualifier().getText();
            final PsiParameter parameterOld = getOrigParameter();
            //change old parameter, if the parameter is defined, remove the parameter
            String paramNewName = targetClass.getAggField().getName();
            Map.Entry<PsiParameter, PsiParameter> parameterPair = changedParameters.get(paramNewName);
            if (parameterPair == null) {
                PsiClass psiClass = PsiUtil.getPsiClass(targetClass.getExtractedFile());
                addImport(psiElementFactory.createImportStatement(psiClass), PsiUtil.getFile(parameterOld));
                PsiParameter parameterNew = psiElementFactory.createParameter(paramNewName,
                        psiElementFactory.createType(psiClass));
                parameterPair = new Map.Entry<PsiParameter, PsiParameter>() {
                    @Override
                    public PsiParameter getKey() {
                        return parameterNew;
                    }

                    @Override
                    public PsiParameter getValue() {
                        return parameterOld;
                    }

                    @Override
                    public PsiParameter setValue(PsiParameter value) {
                        return parameterOld;
                    }
                };
                changedParameters.put(paramNewName, parameterPair);
            } else {
                removedParameters.add(parameterOld);
            }
            //make new psiReferenceExpression
            PsiElement psiNewElement = null;
            PsiElement psiOldElement = psiReferenceExpression;
            if (parentIsRoot) {
                String text = psiReferenceExpression.getText().replace(parameterOld.getName(), paramNewName);
                psiNewElement = psiElementFactory.createExpressionFromText(text, expressionMethod);
            }else{
                psiOldElement = psiOldElement.getParent();
                String text = psiOldElement.getText();
                String paraName = getOrigParameter().getName();
                text = text.replace(paraName, paramNewName);
                psiNewElement = psiElementFactory.createStatementFromText(text, expressionMethod);
            }
            changedExpressions.put(psiOldElement, psiNewElement);
        }
    }

    @NotNull
    private PsiElement createNewStatement(PsiElement psiReferenceExpression, PsiElementFactory psiElementFactory) {
        PsiElement psiNewElement;
        String text = psiReferenceExpression.getText();
        String paraName = getOrigParameter().getName();
        text = text.replace(paraName, paraName + "." + targetClass.getAggGetMethod().getName() + "()");
        psiNewElement = psiElementFactory.createStatementFromText(text, expressionMethod);
        return psiNewElement;
    }

    private void addImport(PsiImportStatement importStatement, PsiFile file) {
        List<PsiImportStatement> imports = newImports.get(file);
        if (imports == null) {
            imports = new ArrayList<>();
            newImports.put(file, imports);
        }
        imports.add(importStatement);
    }

    public Map<PsiElement, PsiElement> getChangedExpressions() {
        return changedExpressions;
    }

    public Map<String, Map.Entry<PsiParameter, PsiParameter>> getChangedParameters() {
        return changedParameters;
    }

    public List<PsiParameter> getRemovedParameters() {
        return removedParameters;
    }

    public Map<PsiFile, List<PsiImportStatement>> getNewImports() {
        return newImports;
    }

    public PsiMethod getExpressionMethod() {
        return expressionMethod;
    }

    public void setExpressionMethod(PsiMethod expressionMethod) {
        this.expressionMethod = expressionMethod;
    }

    public List<PsiReferenceExpression> getReferences() {
        return references;
    }

    public void addReference(PsiReferenceExpression reference) {
        this.references.add(reference);
    }

    public void refactor() {
        //replace changed parameters, and remove old parameters
        for (Map.Entry<PsiParameter, PsiParameter> pair : changedParameters.values()) {
            pair.getValue().replace(pair.getKey());
        }
        for (PsiParameter psiParameter : removedParameters) {
            psiParameter.delete();
        }
        //replace changed expressions
        for (PsiElement key : changedExpressions.keySet()) {
            PsiElement value = changedExpressions.get(key);
            key.replace(value);
        }

        //add imports
        for (PsiFile psiFile : newImports.keySet()) {
            List<PsiImportStatement> importStatements = newImports.get(psiFile);
            for (PsiImportStatement psiImportStatement : importStatements) {
                ((PsiJavaFile) psiFile).getImportList().add(psiImportStatement);
            }
        }
        //change references
        for(CallExpressionInfo callExpressionInfo:refMethodExpressionInfos.values()){
            callExpressionInfo.refactor();
        }
    }
}
