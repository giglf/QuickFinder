package ui;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ForkJoinPool;

import javax.swing.JFrame;
import javax.swing.JTextField;

import util.DBManager;
import util.DBManagerPool;
import util.RecursiveTraverser;
import util.Traverser;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

public class Finder {

	public static final String DIR_SEPERATE = System.getProperties().getProperty("file.separator");
	
	private JFrame frmQuickfinder;
	private JTextField editText;
	private JButton searchButton;
	private JButton updateButton;
	private JList resultShow;
	private JCheckBox isMultiThread;
	
	private DefaultListModel<String> listModel;
	
	private DBManager dbManager;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Finder window = new Finder();
					window.frmQuickfinder.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Finder() {
		dbManager = new DBManager();
		listModel = new DefaultListModel<>();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmQuickfinder = new JFrame();
		frmQuickfinder.setTitle("QuickFinder");
		frmQuickfinder.setBounds(100, 100, 1000, 600);
		frmQuickfinder.setResizable(false);
		frmQuickfinder.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmQuickfinder.getContentPane().setLayout(null);
		//监听关闭事件
		frmQuickfinder.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e){
				dbManager.terminate(); //当窗口关闭时关闭数据库的连接
			}
		});
		
		editText = new JTextField();
		editText.setBounds(10, 10, 748, 21);
		frmQuickfinder.getContentPane().add(editText);
		editText.setColumns(10);
		
		searchButton = new JButton("Search");
		searchButton.setBounds(770, 10, 75, 21);
		frmQuickfinder.getContentPane().add(searchButton);
		searchButton.addActionListener(new ActionListener() {
			//搜索按钮监听事件
			@Override
			public void actionPerformed(ActionEvent arg0) {
				searchDataBase(editText.getText());
			}
		});
		
		updateButton = new JButton("Update");
		updateButton.setBounds(850, 10, 75, 21);
		frmQuickfinder.getContentPane().add(updateButton);
		updateButton.addActionListener(new ActionListener() {
			//更新按钮监听事件
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(editText.getText().equals("")){
					updateDataBase(".", isMultiThread.isSelected()); //无输入时默认更新当前目录下的路径
				} else{
					updateDataBase(editText.getText(), isMultiThread.isSelected());
				}
			}
		});
		
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem openItem = new JMenuItem("open");
		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					if(Desktop.isDesktopSupported()){
						Desktop desktop = Desktop.getDesktop();
						if(desktop.isSupported(Desktop.Action.OPEN)){
							desktop.open(new File(resultShow.getSelectedValue().toString()));
						}
					}
				}catch (Exception err) {
					err.printStackTrace();
				}
			}
		});
		popupMenu.add(openItem);
		
		JMenuItem copyItem = new JMenuItem("copy");
		copyItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int canCopy = chooser.showOpenDialog(frmQuickfinder.getContentPane());
				if(canCopy == JFileChooser.APPROVE_OPTION){
					File target = chooser.getSelectedFile();
					File source = new File(resultShow.getSelectedValue().toString());
					boolean success = copyAll(source.getAbsolutePath(), target.getAbsolutePath());
					if(success){
						JOptionPane.showMessageDialog(null, "Copy Successed", "Success", JOptionPane.WARNING_MESSAGE);
					} else{
						JOptionPane.showMessageDialog(null, "Copy Failed", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		popupMenu.add(copyItem);
		
		resultShow = new JList(listModel);
		resultShow.setFont(new Font("宋体", 1, 13));
		resultShow.setBounds(10, 41, 978, 510);
		frmQuickfinder.getContentPane().add(resultShow);
		resultShow.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e){
				if(e.isMetaDown() && resultShow.getSelectedIndex()!=-1){
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		
		
		//用于启用是否使用多线程更新系统目录
		isMultiThread = new JCheckBox("多线程");
		isMultiThread.setBounds(925, 10, 65, 21);
		frmQuickfinder.getContentPane().add(isMultiThread);
		
		JMenuBar menuBar = new JMenuBar();
		frmQuickfinder.setJMenuBar(menuBar);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		//菜单，“帮助”界面
		JMenuItem mntmHelping = new JMenuItem("Helping");
		mnHelp.add(mntmHelping);
		mntmHelping.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame helpingFrame = new JFrame("Helping");
				helpingFrame.getContentPane().setLayout(null);
				helpingFrame.setResizable(false);
				helpingFrame.setBounds(20, 20, 350, 150);
				helpingFrame.setVisible(true);
				JTextArea show = new JTextArea("目录更新：\n"
											 + "\t点击Update默认Update当前目录信息\n"
											 + "\t亦可输入想要Update目录的路径进行Update\n"
											 + "\t通过选择框按钮选择是否使用多线程更新目录\n"
											 + "文件搜索：\n"
											 + "\t输入文件名或绝对路径搜索\n"
											 + "\t通过通配符*进行搜索");
				show.setEditable(false);
				show.setBounds(0, 0, 350, 150);
				helpingFrame.getContentPane().add(show);
			}
		});
		//菜单，“关于”界面
		JMenuItem mntmIntroduction = new JMenuItem("About");
		mnHelp.add(mntmIntroduction);
		mntmIntroduction.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame aboutFrame = new JFrame("About");
				aboutFrame.getContentPane().setLayout(null);
				aboutFrame.setResizable(false);
				aboutFrame.setBounds(20, 20, 350, 150);
				aboutFrame.setVisible(true);
				JTextArea show = new JTextArea("项目信息：\n"
											 + "\t这是一个通用的文件快速搜索工具\n\n"
											 + "\t组长：林泳聪 （giglf）\n"
											 + "\t组员：张旭、伍佳会、张琦、王菁\n");
				show.setEditable(false);
				show.setBounds(0, 0, 350, 150);
				aboutFrame.getContentPane().add(show);
			}
		});
	}
	
	//从数据库中选择
	private void searchDataBase(String searchInfo){
		ResultSet resultSet = dbManager.selectPath(searchInfo);
		listModel.clear();
		try {
			while(resultSet.next()){
				listModel.addElement(resultSet.getString("filedir"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//更新数据库的操作
	private void updateDataBase(String path, boolean isMulti){
		dbManager.dropTable(); //若更新需要完全扫一遍目录查看有哪些文件修改过，还不如删掉表重建
		dbManager.createTable();
		updateButton.setEnabled(false); //使按钮不可用
		if(isMulti){
			DBManagerPool dbManagerPool = new DBManagerPool(10);  //数据库池包含10个DBManager
			RecursiveTraverser rTraverser = new RecursiveTraverser(new File(path), dbManagerPool);
			ForkJoinPool process = new ForkJoinPool(10);  //开启十个线程
			process.invoke(rTraverser);
			dbManagerPool.terminateAll();
		} else{
			Traverser traverser = new Traverser(path, dbManager);
			traverser.traverseAndUpdate();
		}
		updateButton.setEnabled(true); //更新完毕，使按钮可用
 	}
	
	//复制所有文件，用于文件夹复制
	private boolean copyAll(String source, String target){
		boolean ret = false;
		
		File dir = new File(source);
		File targetDir = new File(target + DIR_SEPERATE + dir.getName());
		if(dir.isDirectory()){
			if(!targetDir.exists()){
				targetDir.mkdirs();
			}
			File[] fileList = dir.listFiles();
			for(File f : fileList){
				if(f.isDirectory()){
					ret = copyAll(f.getAbsolutePath(), targetDir.getAbsolutePath() + DIR_SEPERATE + f.getName());
					if(!ret) return false; //一次复制失败直接返回失败
				} else{
					ret = copyFile(f.getAbsolutePath(), targetDir.getAbsolutePath() + DIR_SEPERATE + f.getName());
					if(!ret) return false;
				}
			}
		} else{
			ret = copyFile(source, target + DIR_SEPERATE + dir.getName());
		}
		return ret;
	}
	
	//文件复制
	private boolean copyFile(String source, String target){
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			inputStream = new FileInputStream(source);
			outputStream = new FileOutputStream(target);
			byte[] buffer = new byte[4000];
			int byteRead = 0;
			while ((byteRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, byteRead);
			}
			inputStream.close();
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	
}
