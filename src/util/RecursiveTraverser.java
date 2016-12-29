package util;


import java.io.File;
import java.util.concurrent.RecursiveAction;

/**
 * 用于使用ForkJoinPool进行多线程搜索<\br>
 * 优化效果微弱
 * @author giglf
 *
 */
public class RecursiveTraverser extends RecursiveAction {

	private static final long serialVersionUID = 1L;

	private File file;
	private DBManager dbManager;
	private DBManagerPool dbManagerPool; //通过数据库池维护已建的数据库连接
	
	public RecursiveTraverser(File file, DBManagerPool pool) {
		this.file = file;
		this.dbManagerPool = pool;
		this.dbManager = dbManagerPool.getDBManager();
	}
	
	@Override
	protected void compute() {
		try {
			File[] fileList = file.listFiles();
			for (File f : fileList) {
				if (f.isDirectory()) { //遇到文件夹开启新的子线程进行遍历
					RecursiveTraverser traverser = new RecursiveTraverser(f, dbManagerPool);
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
			dbManagerPool.returnDBManager(dbManager);
		} catch(NullPointerException e){
			System.out.println("该文件不可访问："+ file.getAbsolutePath());
		}
	}
	
}
