package refactor.config;

import java.util.Map;

public class RenameInfo implements IRenameInfo {
	private String namespace;
	private String oldId;
	private String newId;
	private boolean table;
	
	public RenameInfo(String namespace, String oldId, String newId, boolean table) {
		super();
		this.namespace = namespace;
		this.oldId = oldId;
		this.newId = newId;
		this.table = table;
	}
	
	public RenameInfo(Map<String,Object> m) {
		super();
		this.namespace = (String) m.get(NS);
		this.oldId = (String) m.get(OD);
		this.newId = (String) m.get(ND);
		this.table = (boolean) m.get(TB);
	}
	
	public RenameInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public String getOldId() {
		return oldId;
	}

	@Override
	public String getNewId() {
		return newId;
	}

	@Override
	public boolean isTable() {
		return table;
	}

	@Override
	public String toString() {
		return "RenameInfo [namespace=" + namespace + ", oldId=" + oldId + ", newId=" + newId + ", table=" + table
				+ "]";
	}

}
