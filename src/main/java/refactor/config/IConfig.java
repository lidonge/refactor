package refactor.config;

import java.util.List;

public interface IConfig {

	String getProject();

	void setProject(String project);

	List<IRenameInfo> getRenameInfos();

	void setRenameInfos(List<IRenameInfo> renameInfos);

	String getSrc();

	void setSrc(String src);
}