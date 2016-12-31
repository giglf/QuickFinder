package util;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ForkJoinPool;


public class DBManager {
	
	
	private final static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private final static String DB_URL = "jdbc:mysql://localhost:3306/QuickFinder?useSSL=true";
	
	private final static String USER = "quickFinder";
	private final static String PASS = "thePasswordThatEasyKnow";
	
	private Connection connection;
	private Statement statement;
	public DBManager(){
		try {
			//加载jdbc驱动
			Class.forName(JDBC_DRIVER);
			
			//连接上数据库
			//System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			//System.out.println("Connected database successfully...");
			statement = connection.createStatement();
		} catch (Exception e) {
			System.err.println("Connecting to database failed...");
			e.printStackTrace();
		}
	}
	
	//建表
	public void createTable(){
		try {
			String sql = "CREATE TABLE IF NOT EXISTS FIleInfo(" +
						 "filedir VARCHAR(256) PRIMARY KEY, " + 
						 "filename VARCHAR(256), " + 
						 "suffix VARCHAR(20) )";
			
			statement.executeUpdate(sql);
			System.out.println("Create table success!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//插入文件路径数据
	public void insertPath(String filedir, String filename, String suffix){
		try {
			filedir = filedir.replace("\\", "/");//对windows中的路径名做出变换，因为具有\会被标为转义符
			//通过双引号括着传入数据，而不用单引号，避免文件名包含单引号导致异常匹配插入时抛异常
			String sql = "INSERT INTO FileInfo(filedir, filename, suffix)"
					+ "VALUES(\"" + filedir + "\",\"" + filename + "\",\"" + suffix + "\");";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//通过输入的sql查询语句进行查询操作
	public ResultSet selectPath(String searchInfo){
		searchInfo = searchInfo.replace('*', '%'); //换成用于数据库的通配符
		searchInfo = searchInfo.replace('\\', '/');
		String sql = "SELECT filedir FROM FileInfo WHERE "
				   + "filedir LIKE \"" + searchInfo + "\" "
				   + "OR filename LIKE \"" + searchInfo + "\" "
				   + "OR suffix LIKE \"" + searchInfo + "\";";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultSet;
	}
	
	//删除表
	public void dropTable(){
		try {
			statement.executeUpdate("DROP TABLE FileInfo;");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//结束数据库的链接
	public void terminate(){
		try {
			statement.close();
			connection.close();
			//System.out.println("Database closed。。。");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

//	public static void main(String[] args) {
//		DBManager dbManager = new DBManager();
//		dbManager.dropTable();
//		dbManager.createTable();
//		long start = System.currentTimeMillis();
//		Traverser traverser = new Traverser("E:\\someFile\\2016Grade3\\java大作业", dbManager);
//		traverser.traverseAndUpdate();
//		long end = System.currentTimeMillis();
//		System.out.println("Single Thread time elapse: " + (end - start) + "ms");
//
//		dbManager = new DBManager();
//		dbManager.dropTable();
//		dbManager.createTable();
//		DBManagerPool pool = new DBManagerPool(10);
//		start = System.currentTimeMillis();
//		RecursiveTraverser rTraverser = new RecursiveTraverser(new File("E:\\someFile\\2016Grade3\\java大作业"), pool);
//		ForkJoinPool process = new ForkJoinPool(10);
//		process.invoke(rTraverser);
//		end = System.currentTimeMillis();
//		System.out.println("Multiple Thread time elapse: " + (end - start) + "ms");
		
//	}
	
}
