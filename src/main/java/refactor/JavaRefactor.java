package refactor;


import com.intellij.openapi.project.Project;

import java.io.File;

public class JavaRefactor implements IRefactor {
	Project project;
	String srcPath;
	public JavaRefactor(Project project,String srcPath) {
		this.project =  project;
		this.srcPath = srcPath;
	}

	@Override
	public Project _getProject() {
		return project;
	}

	@Override
	public String _getJavaPath(String name) {

		return srcPath + File.separatorChar + name.replace('.', File.separatorChar)+".java";
	}

}
