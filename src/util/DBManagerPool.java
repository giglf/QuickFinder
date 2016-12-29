package util;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 数据库池</br>
 * 用于管理多线程更新时使用的数据库</br>
 * 避免重复建立数据库连接
 */
public class DBManagerPool {

	private Queue<DBManager> dbManagers;
	private int maxDBManager;
	
	public DBManagerPool(int initNum) {
		dbManagers = new LinkedList<>();
		maxDBManager = initNum;
		for(int i=0;i<maxDBManager;i++){
			dbManagers.add(new DBManager());
		}
	}
	
	//获得要使用的数据库连接
	public synchronized DBManager getDBManager(){
		while(isEmpty()){}
		return dbManagers.poll();
	}
	
	//返回用完的数据库连接到池中
	public synchronized void returnDBManager(DBManager dbManager){
		dbManagers.add(dbManager);
	}
	
	public boolean isEmpty(){
		return dbManagers.isEmpty();
	}
	
	public void terminateAll(){
		for(DBManager db : dbManagers){
			db.terminate();
		}
	}
	
}
