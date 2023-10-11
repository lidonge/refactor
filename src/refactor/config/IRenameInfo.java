package refactor.config;

public interface IRenameInfo {
	static final String NS = "namespace";
	static final String OD = "oldId";
	static final String ND = "newId";
	static final String TB = "table";
	String getNamespace();

	String getOldId();

	String getNewId();

	boolean isTable();


}