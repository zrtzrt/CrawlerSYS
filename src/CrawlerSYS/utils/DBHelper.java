package CrawlerSYS.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;


public class DBHelper {
	/*
	 * 加载驱动，创建连接，定义变量，执行查询，数据加工，关闭连接
	 * 其中1-3步在DBConnection中，4-6在DAO层中
	 */
	private static String mySqlDriver="com.mysql.jdbc.Driver";
	private static String url = "jdbc:mysql://127.0.0.1:3306/showinfo?useUnicode=true&characterEncoding=UTF-8";
//	private static String url = "jdbc:mysql://119.29.223.149:3306/heatpoint?useUnicode=true&characterEncoding=UTF-8";
	public static Connection conn=null;
	public static PreparedStatement ps = null; 
    public static Statement st=null;
	public static ResultSet rs=null;
	private static Logger logger = Logger.getLogger(DBHelper.class);  

	public static Connection getConn(){
		try{
		   	 Class.forName(mySqlDriver).newInstance();//1 加载驱动 
		   	 conn= DriverManager.getConnection(url, "root", "root");
//		   	 conn= DriverManager.getConnection(url, "root", "ig8ZZezcPjCE");
		   	System.out.println("数据库连接成功！");
		}catch(Exception e){
			System.out.println("数据库连接失败！");
e.printStackTrace();logger.error("Exception",e);
		}
		return conn;
	}
	public static Connection getConn(String ip,int port,String dbName,String user,String pw){
		try{
		   	 Class.forName(mySqlDriver).newInstance();//1 加载驱动
		   	 url = "jdbc:mysql://"+ip+":"+port+"/"+dbName+"?useUnicode=true&characterEncoding=UTF-8";
		   	 conn= DriverManager.getConnection(url, user, pw);
		   	System.out.println("数据库连接成功！");
		}catch(Exception e){
			System.out.println("数据库连接失败！");
e.printStackTrace();logger.error("Exception",e);
		}
		return conn;
	}
	
	public static void closeConn(){
		try{
			if(rs!=null) rs.close();
			if(st!=null) st.close();
			if(ps!=null) ps.close();
			if(conn!=null) conn.close();
		}catch(Exception e){
e.printStackTrace();logger.error("Exception",e);
		}
	}
	
	public static void insert(String sql){
		try {
			DBHelper.getConn().createStatement().executeUpdate(sql);
			DBHelper.closeConn();
		} catch (SQLException e) {
			DBHelper.closeConn();
e.printStackTrace();logger.error("Exception",e);
		}
	}
}
