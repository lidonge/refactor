package free.servpp.refactor.servitization.ui;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;

public class TargetClass {
    private PsiField aggField;

    public PsiField getAggField() {
        return aggField;
    }

    public void setAggField(PsiField aggField) {
        this.aggField = aggField;
    }

    private PsiMethod aggGetMethod;

    public PsiMethod getAggGetMethod() {
        return aggGetMethod;
    }

    public void setAggGetMethod(PsiMethod aggGetMethod) {
        this.aggGetMethod = aggGetMethod;
    }

    private PsiMethod aggSetMethod;

    public PsiMethod getAggSetMethod() {
        return aggSetMethod;
    }

    public void setAggSetMethod(PsiMethod aggSetMethod) {
        this.aggSetMethod = aggSetMethod;
    }

    private PsiFile extractedFile;
    private PsiClass origClass;

    public PsiFile getExtractedFile() {
        return extractedFile;
    }

    public void setExtractedFile(PsiFile extractedFile) {
        this.extractedFile = extractedFile;
    }

    private Project project;

    public Project getProject() {
        return project;
    }

    public TargetClass(Project project) {
        this.project = project;
    }

    public PsiClass getOrigClass() {
        return origClass;
    }

    public void setOrigClass(PsiClass origClass) {
        this.origClass = origClass;
    }
}