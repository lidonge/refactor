package refactor;

import java.util.List;

import com.intellij.openapi.project.Project;
import refactor.config.Config;
import refactor.config.IConfig;
import refactor.config.IRenameInfo;

public class BatchRefactor implements IBatchRefactor {
	static final String CONF = "/batchRefactor.yml";
	static final String INFOS = "renameInfos";
	static final String PRJ = "project";
	static final String SRC = "src";
	IRefactor refactor;
	List<IRenameInfo> infos;
	public BatchRefactor(Project project, String ymlFile )  {
		IConfig conf = Config.getConfig(ymlFile);

		refactor = new JavaRefactor(project, conf.getSrc());
		infos = conf.getRenameInfos();
	}

	@Override
	public IRefactor _getRefactor() {
		return refactor;
	}

	@Override
	public List<IRenameInfo> _getRenameInfos() {
		return infos;
	}

}
