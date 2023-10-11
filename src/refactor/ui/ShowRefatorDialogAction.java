package refactor.ui;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.refactoring.listeners.RefactoringListenerManager;
import org.jetbrains.annotations.NotNull;

public class ShowRefatorDialogAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
//        MapperRefactoringProvider provider = new MapperRefactoringProvider();
//        RefactoringListenerManager.getInstance(project).addListenerProvider(provider);
        new RefatorDialog(project).show();

    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        System.out.println(e.getProject());
        e.getPresentation().setEnabledAndVisible(true);
    }

}
