package CrawlerSYS.node;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.jsoup.Connection;

import com.zaxxer.hikari.HikariDataSource;

import CrawlerSYS.crawler.DefaultConfig;
import CrawlerSYS.entity.CrawlerReturnEntity;
import CrawlerSYS.utils.StringHelper;
import CrawlerSYS.utils.WebCrawler;

public class SingleUrlCrawler  implements Callable{
	private boolean rtLink = false;
	private int sleepTime = 500;
	private String url,linkpath = "//a/@href", table,then,spacialUrl,dispose;
	private String[] lable;
	private List<String> xpath = new ArrayList<String>(),link = new ArrayList<String>(),err = new ArrayList<String>();
	private Map<String,String> header = null,cookie = null;
	private Logger logger = Logger.getLogger(this.getClass());  
	private HikariDataSource ds;
	
	public SingleUrlCrawler(String u, List<String> xpath){
		this.url = u;
		setXpath(xpath);
	}
	
	public CrawlerReturnEntity call() {
//		System.out.println("befor"+xpath);
		List<List<String>> res = new ArrayList<List<String>>();
//		for (int i = 0; i < url.size(); i++) {
//			int len = xpath.size();
			if(xpath==null||xpath.size()==0||xpath.get(0).length()==0)
				return null;
			if (rtLink&&!xpath.get(xpath.size()-1).equals(linkpath)){
				xpath.add(linkpath);
			}
			Connection con = WebCrawler.getConnect(url, header, cookie);
				try {
					res = WebCrawler.getByXpath(con.get(), xpath, 0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
				}
//			else {
//				String r = WebCrawler.get(url,header);
//				String[] ra = r.split("\n");
//				for (int i = 0; i < ra.length; i++) {
//					if(ra[i].length()>1000){
//						ra[i].replaceAll("<+>", "").replace("&nbsp;", " ");
//					}	
//				}
//			}
			if (rtLink){
//				link = new ArrayList<String>(Arrays.asList(res.get(res.size()-1)
//				.substring(1, res.get(res.size()-1).length()-1).split(",")));
				link = res.get(res.size()-1);
//				System.out.println(link);
//				while(res.size()!=len)
//				int end = res.size()-1;
				res.remove(res.size()-1);
				if(xpath.get(xpath.size()-1).equals(linkpath))
					xpath.remove(xpath.size()-1);
			}
//			System.out.println("after"+xpath);
			CrawlerReturnEntity back;
			if(dispose!=null&&lable!=null){
				Map<String,List<String>> resMap = new LinkedHashMap<String,List<String>>();
				for (int i = 0; i < res.size(); i++) {
					resMap.put(lable[i], res.get(i));
				}
				back = new CrawlerReturnEntity(url,resMap,link,err);
			}else
				back = new CrawlerReturnEntity(url,res,link,err);
			if(dispose!=null){
//				back=CustomCrawler.resultDispose(back,dispose);
				try {
					Class cla = Class.forName(dispose);
					back=(CrawlerReturnEntity) cla.getDeclaredMethod(
							"resultDispose", back.getClass(),ds.getClass()).invoke(cla.newInstance(), back,ds);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
return back;
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
				}
			}
			String head = url.split(":", 2)[0],
					webroot = url.split("://", 2)[1].split("/", 2)[0];
//			System.out.println(head+"://"+webroot);
			for (int i = 0; i < back.getLink().size(); i++) {
				String 	t = back.getLink().get(i).toLowerCase().trim();
//				if(t.startsWith("javascript")||t.startsWith("#")||t==null||t.length()<5)
				if(t.startsWith("/")){
//					link.remove(i--);
//					link.add(head+":"+t);
					back.getLink().set(i, head+"://"+webroot+t);
				}else
				if(!t.startsWith("http"))
					back.getLink().set(i, head+":"+t);
//				if(t.contains("?"))
//					t=t.split("\\?", 2)[0];
				if(t.endsWith("jpg")||t.endsWith("png")||t.endsWith("gif")||
						t.contains("#")||t.contains("javascript")||t.contains("return"))
					back.getLink().remove(i--);
			}
//			System.out.println(back.getLink());
			if (then.equalsIgnoreCase("save")||then.equalsIgnoreCase("saveaslist")) {
				if(then.equalsIgnoreCase("save")){
//					CustomCrawler.saveProcess(sql,dispose);
					String[] rlist = new String[xpath.size()];
					for (int i = 0; i < back.getRes().size(); i++) {
						rlist[i]=back.getRes().get(i).get(0);
					}
					saveProcess(rlist);
				} else {
					for (int i = 0; i < back.getRes().get(0).size(); i++) {
						String[] rlist = new String[xpath.size()];
						for (int j = 0; j < rlist.length; j++) {
							rlist[j]=back.getRes().get(j).get(i);
							saveProcess(rlist);
						}
					}
				}
				back.setRes(null);
			}
//			System.out.println(back.getRes());
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			}
//		}
			return back;
	}
	private void saveProcess(String[] res){
		if(dispose!=null){
//			back=CustomCrawler.resultDispose(back,dispose);
			try {
				Class cla = Class.forName(dispose);
				if(!(Boolean) cla.getDeclaredMethod("saveProcess", String[].class).invoke(cla.newInstance(), res))
					save(res);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			}
		}else save(res);
	}
	private void save(String[] res){
		if(lable==null)
			lable=xpath.toArray(new String[0]);
//		System.out.println(lable.length);
		String sql=StringHelper.sqlINSERT(table, lable, res);
//		System.out.println(sql);
		java.sql.Connection con = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();logger.error("Exception",e);
		}
		try {
//			Statement cs=DBHelper.getConn(dbip.split(":",2)[0], 
//					Integer.parseInt(dbip.split(":",2)[1].split("/", 2)[0]), 
//					dbip.split(":",2)[1].split("/", 2)[1], user, pwd)
//					.createStatement();
//			cs.executeUpdate(sql);
//			DBHelper.closeConn();
			con.createStatement().executeUpdate(sql);
			con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();logger.error("Exception",ex);
//			System.err.println("table not find. create new table");
//			logger.error("table not find. create new table");
			String sqlC;
			int[] lenght = new int[lable.length];
			for (int i = 0; i < lenght.length; i++) {
				lenght[i]=DefaultConfig.lableSize;
			}
			if(table!=null)
				sqlC = StringHelper.sqlCREATE(table, lable,lenght);
			else sqlC = StringHelper.sqlCREATE(url.split("://", 2)[1].split("/", 2)[0],lable,lenght);
			System.out.println(sqlC);
			try {
//				Statement cs=DBHelper.getConn(dbip.split(":",2)[0], 
//						Integer.parseInt(dbip.split(":",2)[1].split("/", 2)[0]),
//						dbip.split(":",2)[1].split("/", 2)[1], user, pwd)
//						.createStatement();
//				cs.execute(sqlC);
//				cs.executeUpdate(sql);
				con.createStatement().executeUpdate(sqlC);
				con.createStatement().executeUpdate(sql);
				con.close();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
//				DBHelper.closeConn();
e.printStackTrace();logger.error("Exception",e);
				err.add(ex.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
//				DBHelper.closeConn();
e.printStackTrace();logger.error("Exception",e);
				err.add(ex.toString());
//				DBHelper.closeConn();
			}
//			DBHelper.closeConn();
		}
		
	}
	
	
	public String getthen() {
		return then;
	}
	public void setthen(String then) {
		this.then = then;
	}
	public String getLinkpath() {
		return linkpath;
	}
	public void setLinkpath(String linkpath) {
		this.linkpath = linkpath;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String[] getLable() {
		return lable;
	}
	public void setLable(String[] lable) {
		this.lable = lable;
	}
	public List<String> getLink() {
		return link;
	}
	public void setLink(List<String> link) {
		this.link = link;
	}
	public boolean isRtLink() {
		return rtLink;
	}
	public void setRtLink(boolean rtLink) {
		this.rtLink = rtLink;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Map<String, String> getHeader() {
		return header;
	}
	public void setHeader(Map<String, String> header) {
		this.header = header;
	}
	public Map<String, String> getCookie() {
		return cookie;
	}
	public void setCookie(Map<String, String> cookie) {
		this.cookie = cookie;
	}
	public List<String> getXpath() {
		return xpath;
	}
	public void setXpath(List<String> xpath) {
		this.xpath = xpath;
	}
	public int getSleepTime() {
		return sleepTime;
	}
	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}
//	public String getDbip() {
//		return dbip;
//	}
//	public void setDbip(String dbip) {
//		this.dbip = dbip;
//	}
//	public String getUser() {
//		return user;
//	}
//	public void setUser(String user) {
//		this.user = user;
//	}
//	public String getPwd() {
//		return pwd;
//	}
//	public void setPwd(String pwd) {
//		this.pwd = pwd;
//	}
	public List<String> getErr() {
		return err;
	}
	public void setErr(List<String> err) {
		this.err = err;
	}
	public String getSpacialUrl() {
		return spacialUrl;
	}
	public void setSpacialUrl(String spacialUrl) {
		this.spacialUrl = spacialUrl;
	}
	public String getDispose() {
		return dispose;
	}
	public void setDispose(String dispose) {
		this.dispose = dispose;
	}

	public HikariDataSource getDs() {
		return ds;
	}

	public void setDs(HikariDataSource ds) {
		this.ds = ds;
	}
}
