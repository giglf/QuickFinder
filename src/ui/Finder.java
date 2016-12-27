package ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

public class Finder {

	private JFrame frame;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Finder window = new Finder();
					window.frame.setVisible(true);
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
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1000, 600);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(10, 10, 770, 21);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton searchButton = new JButton("Search");
		searchButton.setBounds(790, 9, 85, 23);
		frame.getContentPane().add(searchButton);
		
		JButton updateButton = new JButton("Update");
		updateButton.setBounds(889, 9, 85, 23);
		frame.getContentPane().add(updateButton);
		
		JList resultShow = new JList();
		resultShow.setBounds(10, 41, 964, 510);
		frame.getContentPane().add(resultShow);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
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
											 + "文件搜索：\n"
											 + "\t输入文件名或绝对路径搜索\n"
											 + "\t通过通配符*进行搜索");
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
				show.setBounds(0, 0, 350, 150);
				aboutFrame.getContentPane().add(show);
			}
		});
	}
}
