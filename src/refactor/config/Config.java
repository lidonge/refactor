package refactor.config;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Config implements IConfig {
	static final String INFOS = "renameInfos";
	static final String PRJ = "project";
	static final String SRC = "src";
	private String project;
	private String src;

	private List<IRenameInfo> renameInfos;
	public static Config getConfig(String ymalFile) {
		FileInputStream fin = null;
		File f = new File(ymalFile);
		Config conf = null;
		try {
			fin = new FileInputStream(f);
			Yaml yaml = new Yaml();
			LinkedHashMap maps = yaml.load(fin);
			List<Map> infos = (List<Map>) maps.get(INFOS);
			List<IRenameInfo> _infos = new ArrayList<IRenameInfo>();
			for(Map<String,Object> m: infos) {
				IRenameInfo info = new RenameInfo(m);
				_infos.add(info);
			}

			conf = new Config();
			conf.setProject((String) maps.get(PRJ));
			conf.setSrc((String) maps.get(SRC));
			conf.setRenameInfos(_infos);
			System.out.println(conf);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				fin.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return conf;
	}
	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	@Override
	public String getProject() {
		return project;
	}

	@Override
	public void setProject(String project) {
		this.project = project;
	}

	@Override
	public List<IRenameInfo> getRenameInfos() {
		return renameInfos;
	}

	@Override
	public void setRenameInfos(List<IRenameInfo> renameInfos) {
		this.renameInfos = renameInfos;
	}

	@Override
	public String toString() {
		return "Config [project=" + project + ", src=" + src + ", renameInfos=" + renameInfos + "]";
	}
	
}
