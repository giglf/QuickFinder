package util;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

/**
 * BFS traverse all file.</br> 
 * And update it to databases.
 */
public class Traverser {

	private DBManager dbManager;
	private File file;
	private Queue<File> directory;
	
	public Traverser(String filepath, DBManager dbManager) {
		this.dbManager = dbManager;
		file = new File(filepath);
		directory = new LinkedList<>();
	}
	
	//通过队列广搜遍历文件夹，避免递归爆栈
	public void traverseAndUpdate(){
		directory.add(file);
		while(!directory.isEmpty()){
			File currentFile = directory.poll();
			try {
				File[] fileList = currentFile.listFiles();
				for (File f : fileList) {
					if (f.isDirectory()) {
						directory.add(f);
					} //else {
					String filename = f.getName();
					String suffix;
					if (filename.lastIndexOf('.') < 0)
						suffix = "";
					else
						suffix = filename.endsWith(".tar.gz") ? "tar.gz"
								: filename.substring(filename.lastIndexOf('.'));
					dbManager.insertPath(f.getAbsolutePath(), filename, suffix);
					// }
				}
			} catch(NullPointerException e){
				//如System Volume Information这种文件夹不可访问
				System.out.println("该文件不可访问："+ currentFile.getAbsolutePath());
			}
		}
	}
	

}
