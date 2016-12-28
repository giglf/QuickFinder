package util;


import java.io.File;
import java.util.concurrent.RecursiveAction;

/**
 * 用于使用ForkJoinPool进行多线程搜索<\br>
 * 但基本没有优化效果
 * @author giglf
 *
 */
public class RecursiveTraverser extends RecursiveAction {

	private static final long serialVersionUID = 1L;

	private File file;
	private DBManager dbManager;
	
	public RecursiveTraverser(File file, DBManager dbManager) {
		this.file = file;
		this.dbManager = dbManager;
	}
	
	@Override
	protected void compute() {
		try {
			File[] fileList = file.listFiles();
			for (File f : fileList) {
				if (f.isDirectory()) {
					RecursiveTraverser traverser = new RecursiveTraverser(f, dbManager);
					traverser.fork();
					traverser.join();
				} // else {
				String filename = f.getName();
				String suffix;
				if (filename.lastIndexOf('.') < 0)
					suffix = "";
				else
					suffix = filename.endsWith(".tar.gz") ?
							"tar.gz" : filename.substring(filename.lastIndexOf('.'));
				dbManager.insertPath(f.getAbsolutePath(), filename, suffix);
				// }
			}
		} catch(NullPointerException e){
			System.out.println("该文件不可访问："+ file.getAbsolutePath());
		}
	}
	
}
