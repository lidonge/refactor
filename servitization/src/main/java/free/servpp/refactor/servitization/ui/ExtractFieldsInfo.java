package free.servpp.refactor.servitization.ui;

import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lidong@date 2024-01-25@version 1.0
 */
public class ExtractFieldsInfo extends CallerMethodInfo {
    private String extractedClassName = "Extracted";
    private String packageName;
    private Language language;
    private List<PsiField> extractedFields;
    private List<PsiMethod> extractedMethods = new ArrayList<>();
//    private Map<PsiMethod, RefToMethod> referenceMap = new HashMap<>();

    public ExtractFieldsInfo(Project project, String extractedClassName, String packageName, List<PsiField> extractedFields, Language language) {
        super(project);
        this.extractedClassName = extractedClassName;
        this.packageName = packageName;
        this.extractedFields = extractedFields;
        this.language = language;
        this.targetClass = new TargetClass(project);
        this.targetClass.setOrigClass(PsiUtil.getPsiClass(extractedFields.get(0)));
    }

    @Override
    protected boolean isRoot() {
        return true;
    }

    private void findExtractedMethods(){
        for(PsiField psiField:extractedFields){
            ReferencesSearch.search(psiField).forEach(psiReference -> {
                PsiElement psiElement = psiReference.getElement();
                PsiMethod method = PsiUtil.getMethod(psiElement);
                if(method != null){
                    if(!extractedMethods.contains(method)){
                        extractedMethods.add(method);
                    }
                }
            });
        }
    }

    public void generateReferenceInfo(){
        findExtractedMethods();
        targetClass.setExtractedFile(PsiFileFactory.getInstance(project).createFileFromText(extractedClassName + ".java",
                language, getExtractedClassText()));
        PsiClass psiClass = PsiUtil.getPsiClass(targetClass.getExtractedFile());
        PsiElementFactory psiElementFactory = PsiElementFactory.getInstance(project);
        String fieldName = PsiUtil.makeFieldName(extractedClassName);
        targetClass.setAggField(psiElementFactory.createFieldFromText("private " + extractedClassName + " " + fieldName + ";", psiClass));
        targetClass.setAggGetMethod(psiElementFactory.createMethodFromText(
                "public " + extractedClassName + " " + PsiUtil.makeGetMethodName(fieldName) + "(){\n" +
                        "return " + fieldName + ";\n" +
                        "}",
                psiClass));
        targetClass.setAggSetMethod(psiElementFactory.createMethodFromText(
                "public void " + PsiUtil.makeSetMethodName(fieldName) + "(" + extractedClassName + " " + fieldName + "){\n" +
                        "this." + fieldName + "=" + fieldName + ";\n" +
                        "}",
                psiClass));
        //find all referenced methods, and its expressions
        for(PsiMethod psiMethod:extractedMethods){
            findCallExpressInfo(psiMethod);
        }
        //refactor the expressions and methods
        for (CallExpressionInfo info: refMethodExpressionInfos.values()){
            info.dealRefExpressions();
        }
    }

    private String getExtractedClassText(){
        String codes = packageName;
        codes += "\npublic class " + extractedClassName +"{";
        for(PsiField psiField:extractedFields){
            String text = psiField.getText();
            codes += "\n"+text;
        }
        for(PsiMethod psiMethod:extractedMethods){
            codes += "\n"+psiMethod.getText();
        }
//        WriteCommandAction.runWriteCommandAction(toolWindow.getProject(),()->{psiMethodList.get(0).delete();});

        codes += "\n}";
        return codes;
    }

    public Language getLanguage() {
        return language;
    }

    public void setExtractedFields(List<PsiField> extractedFields) {
        this.extractedFields = extractedFields;
    }

    public String getExtractedClassName() {
        return extractedClassName;
    }

    public void setExtractedClassName(String extractedClassName) {
        this.extractedClassName = extractedClassName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<PsiField> getExtractedFields() {
        return extractedFields;
    }

    public List<PsiMethod> getExtractedMethods() {
        return extractedMethods;
    }

    public void refactor(PsiDirectory containingDirectory) {
        //add new file
        containingDirectory.add(getExtractedFile());
        //remove old fields, and add new field
        int index = 0;
        for(PsiField psiField: extractedFields){
            if(index == 0){
                psiField.replace(targetClass.getAggField());
            }else {
                psiField.delete();
            }
            index++;
        }
        //remove old methods, and add new methods
        index = 0;
        for(PsiMethod psiMethod:extractedMethods){
            if(index == 0){
                psiMethod.replace(targetClass.getAggGetMethod());
            }else if(index == 1){
                psiMethod.replace(targetClass.getAggSetMethod());
            }else{
                psiMethod.delete();
            }
            index++;
        }
        //change references
        for(CallExpressionInfo callExpressionInfo:refMethodExpressionInfos.values()){
            callExpressionInfo.refactor();
        }
    }
}
