// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package free.servpp.refactor.servitization.ui;

import com.intellij.lang.StdLanguages;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.impl.source.tree.java.PsiPackageStatementImpl;
import com.intellij.psi.search.searches.ReferenceSearcher;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

final class PsiToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        PsiToolWindowContent toolWindowContent = new PsiToolWindowContent(toolWindow);
        Content content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private static class PsiToolWindowContent {
        private final JPanel contentPanel = new JPanel();
        private final JList fieldList = new JList();
        private ToolWindow toolWindow;
        private PsiFile currentPsiFile;
        private PsiPackageStatement currentPsiPackage;


        public PsiToolWindowContent(ToolWindow toolWindow) {
            this.toolWindow = toolWindow;
            contentPanel.setLayout(new BorderLayout(0, 20));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
            contentPanel.add(createPsiFieldsPanel(), BorderLayout.NORTH);
            contentPanel.add(createControlsPanel(toolWindow), BorderLayout.CENTER);
        }

        @NotNull
        private JPanel createPsiFieldsPanel() {
            JPanel psiFieldsPanel = new JPanel();
            psiFieldsPanel.add(fieldList);
            return psiFieldsPanel;
        }

        @NotNull
        private JPanel createControlsPanel(ToolWindow toolWindow) {
            JPanel controlsPanel = new JPanel();
            JButton refresh = new JButton("Refresh");
            refresh.addActionListener(e -> refreshFieldList());
            controlsPanel.add(refresh);
            JButton extract = new JButton("Extract");
            extract.addActionListener(e -> extractFields());
            controlsPanel.add(extract);
            JButton hideToolWindowButton = new JButton("Hide");
            hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
            controlsPanel.add(hideToolWindowButton);
            return controlsPanel;
        }

        private void extractFields() {
            ExtractFieldsInfo extractFieldsInfo = new ExtractFieldsInfo(toolWindow.getProject(), "Extracted",
                    currentPsiPackage.getText(), fieldList.getSelectedValuesList(), currentPsiFile.getLanguage());
            extractFieldsInfo.generateReferenceInfo();
            WriteCommandAction.runWriteCommandAction(toolWindow.getProject(), () -> {
                extractFieldsInfo.refactor(currentPsiFile.getContainingDirectory());
            });

//      WriteCommandAction.runWriteCommandAction(toolWindow.getProject(),()->{psiMethodList.get(0).delete();});
//      psiFile = (PsiFile) CodeStyleManager.getInstance(toolWindow.getProject()).reformat(psiFile);
//      currentPsiFile.getContainingDirectory().add(psiFile);
        }

        private PsiMethod changeMethodParameters(PsiMethod psiMethod, PsiReferenceExpression expression) {
            PsiElement qualifer = expression.getQualifier();
            JvmParameter theParameter = null;
            for (JvmParameter jvmParameter : psiMethod.getParameters()) {
                if (jvmParameter.getName().equals(qualifer)) {//the reference is a parameter
                    theParameter = jvmParameter;
                    break;
                }
            }
            if (theParameter == null) {//the reference is a local var
                return null;
            }
//      PsiElementFactory.getInstance(toolWindow.getProject()).createParameter()
            return psiMethod;
        }

        private PsiFile getFile(PsiElement psiElement) {
            while (psiElement != null) {
                if (psiElement instanceof PsiFile)
                    return (PsiFile) psiElement;
                psiElement = psiElement.getParent();
            }
            return null;
        }

        private PsiMethod getMethod(PsiElement psiElement) {
            while (psiElement != null) {
                if (psiElement instanceof PsiMethod)
                    return (PsiMethod) psiElement;
                psiElement = psiElement.getParent();
            }
            return null;
        }

        private void refreshFieldList() {
            FileEditor editor = FileEditorManager.getInstance(toolWindow.getProject()).getSelectedEditor();
            currentPsiFile = PsiManager.getInstance(toolWindow.getProject()).findFile(editor.getFile());
            if (editor == null || currentPsiFile == null) {
                return;
            }
            Vector<PsiField> fields = new Vector<>();
            for (PsiElement psiElement : currentPsiFile.getChildren()) {
                if (psiElement instanceof PsiClass) {
                    PsiClass cls = (PsiClass) psiElement;
                    for (PsiField field : cls.getAllFields())
                        fields.add(field);
                } else if (psiElement instanceof PsiPackageStatement) {
                    currentPsiPackage = (PsiPackageStatement) psiElement;
                }
            }
            fieldList.setListData(fields);
        }


        public JPanel getContentPanel() {
            return contentPanel;
        }

    }

}
