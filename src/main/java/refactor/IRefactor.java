package refactor;

import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.rename.RenameProcessor;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.refactoring.rename.naming.AutomaticRenamerFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Optional;

public interface IRefactor {

	default PsiMethod readMethod(PsiClass unit, String name, PsiType[] tps){
		PsiMethod[] methods = unit.findMethodsByName(name,true);
		if(methods.length == 0) {
			Logger.log(Status.ERROR, "Can not find method " + name + " in class " + unit.getQualifiedName());
		}
		PsiMethod ret = null;
		for(PsiMethod method: methods) {
			PsiParameterList plist = method.getParameterList();
			int len = plist.getParametersCount();
			if(len != tps.length)
				continue;
			boolean bFit = true;
			for(int i = 0;i<len;i++){
				PsiParameter psiParam = plist.getParameter(i);
				if(!psiParam.getType().equals(tps[i])) {
					bFit = false;
					break;
				}
			}
			if(bFit){
				ret = method;
				break;
			}
		}
		return ret;
	}

	default PsiField readField(PsiClass unit, String name) {
		return unit.findFieldByName(name,true);
	}

	default void makeChangetoMethod(PsiMethod methodToRename, String newName){
		this.renameUnit(methodToRename,newName);
	}

	default void makeChangetoField(PsiField fieldToRename, String newName){
		this.renameUnit(fieldToRename,newName);
	}

	default void renameCompUnit(@NotNull PsiClass psiClass,	@NotNull String newName) {
		this.renameUnit(psiClass,newName);
	}
	default void renameUnit(@NotNull PsiElement psiElement,	@NotNull String newName) {
		Project project = this._getProject();
		RenamePsiElementProcessor elementProcessor = RenamePsiElementProcessor.forElement(psiElement);
		Logger.log("User Element Porpcesor:" +elementProcessor);
		elementProcessor.setToSearchInComments(psiElement, true);
		elementProcessor.setToSearchForTextOccurrences(psiElement, true);
		RenameProcessor processor = new RenameProcessor(project,
				psiElement,
				newName,
				GlobalSearchScope.projectScope(project),
				true,
				true);
//		for (AutomaticRenamerFactory factory : AutomaticRenamerFactory.EP_NAME.getExtensionList()) {
////			Logger.log("Find a factory:" + factory);
//			if (factory.isApplicable(psiElement) && factory.getOptionName() != null) {
//				processor.addRenamerFactory(factory);
//			}
//		}
		processor.setPreviewUsages(false);
		processor.run();
	}

	default PsiJavaFile lookUpJavaFile(String fileName) {
		Project project = this._getProject();
		String path = _getJavaPath(fileName);
		VirtualFile oldFile = LocalFileSystem.getInstance().findFileByPath(project.getBasePath()+path);
		Logger.log("look up java file path:" + project.getBasePath()+path +", oldFile:"  +oldFile);
		if (oldFile != null) {
			PsiFile psiFile = PsiManager.getInstance(project).findFile(oldFile);
			if (psiFile instanceof PsiJavaFile) {
				return (PsiJavaFile) psiFile;
			}
		}
		return null;
	}
	default PsiClass findUnit( String name){
		Logger.log(Status.INFO,"find unit:" +name);
		PsiJavaFile javaFile = this.lookUpJavaFile(name);
		PsiClass compUnit = javaFile.getClasses()[0];

		return compUnit;
	}

	String _getJavaPath(String name);

	Project _getProject();

}