package CrawlerSYS.node;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.zaxxer.hikari.HikariDataSource;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;

import CrawlerSYS.crawler.DefaultConfig;
import CrawlerSYS.entity.CrawlerReturnEntity;
//import CrawlerSYS.utils.DBHelper;
import CrawlerSYS.utils.StringHelper;
import CrawlerSYS.utils.WebCrawler;

public class AutoNewsCrawler  implements Callable{
	
	private boolean rtLink = false;
	private int sleepTime = 500;
	private String url,linkpath = "//a/@href",table;
	private Map<String,String> header = null,cookie = null;
	private Logger logger = Logger.getLogger(AutoNewsCrawler.class);  
	private HikariDataSource ds;
	
	public AutoNewsCrawler(String u){
		this.url = u;
	}
	
	@Override
	public Object call() throws Exception {
//		List<String> res = new ArrayList<String>();
		String[] lable = new String[]{"url","title","content"},res = new String[3];
		res[0]=url;
			InputStream inputStream;
			Connection con = WebCrawler.getConnect(url, header, cookie);
			try {
				inputStream = new ByteArrayInputStream(con.execute().bodyAsBytes());
				TextDocument doc = new BoilerpipeSAXInput(new InputSource(inputStream))  
					.getTextDocument();
				CommonExtractors.ARTICLE_EXTRACTOR.process(doc);
				res[1] = doc.getTitle();
				res[2] = doc.getContent();
//				System.out.println("title:" + doc.getTitle());
				logger.info("title:" + doc.getTitle());
			} catch (IOException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			} catch (BoilerpipeProcessingException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			} catch (SAXException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			}
			if(table==null)
				table = url.split("://", 2)[1].split("/", 2)[0];
			String sql=StringHelper.sqlINSERT(table, lable, res);
			java.sql.Connection sCon = null;
			try {
				sCon = ds.getConnection();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();logger.error("Exception",e);
			}
			try {
				System.out.println(sql);
				sCon.createStatement().executeUpdate(sql);
				sCon.close();
			} catch (SQLException ex) {
				System.err.println("table not find. create new table");
				logger.error("table not find. create new table");
				String sqlC;
				int[] lenght = new int[]{DefaultConfig.urlSize,DefaultConfig.titleSize,DefaultConfig.textSize};
				sqlC = StringHelper.sqlCREATE(table, lable,lenght);
				System.out.println(sqlC);
				try {
					sCon.createStatement().executeUpdate(sqlC);
					sCon.createStatement().executeUpdate(sql);
					sCon.close();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
//					DBHelper.closeConn();
e.printStackTrace();logger.error("Exception",e);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
//					DBHelper.closeConn();
e.printStackTrace();logger.error("Exception",e);
//					DBHelper.closeConn();
				}
//				DBHelper.closeConn();
			}
			CrawlerReturnEntity back = new CrawlerReturnEntity();
			if(rtLink){
				List<String> xpath = new ArrayList<String>();
				xpath.add(linkpath);
				List<String> link = new ArrayList<String>();
				try {
					link = WebCrawler.getByXpath(con.get(), xpath, 0).get(0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
				}
				back.setLink(link);
				String head = url.split(":", 2)[0],
						webroot = url.split("://", 2)[1].split("/", 2)[0];
//				System.out.println(head+"://"+webroot);
				for (int i = 0; i < back.getLink().size(); i++) {
					String 	t = back.getLink().get(i).toLowerCase().trim();
//					if(t.startsWith("javascript")||t.startsWith("#")||t==null||t.length()<5)
					if(t.startsWith("/")){
//						link.remove(i--);
//						link.add(head+":"+t);
						back.getLink().set(i, head+"://"+webroot+t);
					}else
					if(!t.startsWith("http"))
						back.getLink().set(i, head+":"+t);
//					if(t.contains("?"))
//						t=t.split("\\?", 2)[0];
					if(t.endsWith("jpg")||t.endsWith("png")||t.endsWith("gif")||
							t.contains("#")||t.contains("javascript")||t.contains("return"))
						back.getLink().remove(i--);
				}
			}
			return back;
		}

	public boolean isRtLink() {
		return rtLink;
	}

	public void setRtLink(boolean rtLink) {
		this.rtLink = rtLink;
	}

	public int getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

//	public String getDbip() {
//		return dbip;
//	}
//
//	public void setDbip(String dbip) {
//		this.dbip = dbip;
//	}
//
//	public String getUser() {
//		return user;
//	}
//
//	public void setUser(String user) {
//		this.user = user;
//	}
//
//	public String getPwd() {
//		return pwd;
//	}
//
//	public void setPwd(String pwd) {
//		this.pwd = pwd;
//	}

	public String getLinkpath() {
		return linkpath;
	}

	public void setLinkpath(String linkpath) {
		this.linkpath = linkpath;
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

	public HikariDataSource getDs() {
		return ds;
	}

	public void setDs(HikariDataSource ds) {
		this.ds = ds;
	}

}
