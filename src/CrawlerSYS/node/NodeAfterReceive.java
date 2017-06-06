package CrawlerSYS.node;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.zaxxer.hikari.HikariDataSource;

import net.minidev.json.JSONObject;
import CrawlerSYS.utils.StringHelper;
import CrawlerSYS.crawler.DefaultConfig;
import CrawlerSYS.entity.CrawlerReturnEntity;

public class NodeAfterReceive extends Thread{
	private JSONObject m = null;
	private ObjectOutputStream out = null;
	private ExecutorService pool;
	private Logger logger = Logger.getLogger(this.getClass());
	private HikariDataSource ds;
	
	public NodeAfterReceive(JSONObject m, ObjectOutputStream out, ExecutorService pool, HikariDataSource ds2) {
		super();
		this.m = m;
		this.out = out;
		this.pool = pool;
		ds=ds2;
	}

	public void run() {
		String[] url = (String[]) m.get("url");
		String[] urlR = (String[]) m.get("urlR");
		List<List<String>> xpath = (List<List<String>>) m.get("xpath");
		boolean getLink = (Boolean) m.get("relink");
		Map<String,String> header = (Map<String,String>) m.get("header"),
				cookie = (Map<String,String>) m.get("cookie");
		String linkpath = null, table = null, dbip = null, user = null, pwd = null;
		String[] lable = null;
		List<Future> fList = new ArrayList<Future>();
		LinkedHashSet<String> backLink = new LinkedHashSet<String>();
		List<List<List<String>>> backRes = new ArrayList<List<List<String>>>();
		List<String> err = new ArrayList<String>();
		if (getLink)
			linkpath = (String) m.get("linkpath");
		String then = (String) m.get("then");
		String dispose = (String) m.get("dispose");
		int stime = (Integer) m.get("stime");
		if (then.contains("save")||then.contains("auto")) {
			dbip = (String) m.get("dbip");
			user = (String) m.get("user");
			pwd = (String) m.get("pwd");
			table = (String) m.get("table");
//			System.out.println(dbip+user+pwd+table);
			
			ds.setJdbcUrl("jdbc:mysql://"+dbip+"?"+DefaultConfig.dbArgs);
			ds.setUsername(user);
			ds.setPassword(pwd);
		}
		else{
			System.out.println("useing DefaultConfig");
			ds.setJdbcUrl("jdbc:mysql://"+DefaultConfig.dbip+"?"+DefaultConfig.dbArgs);
			ds.setUsername(DefaultConfig.user);
			ds.setPassword(DefaultConfig.password);
		}
		lable = (String[]) m.get("lable");
		String ip = null;
		try {
			ip=InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
		}
		
//				String[][] u = StringHelper.spArr(url.toArray(new String[url.size()]), spider);
//				List<spider> sl = new ArrayList<spider>();
//		System.out.println(then+then.contains("auto"));
		if(!then.contains("auto")){
			for (int i = 0; i < url.length; i++) {
				SingleUrlCrawler s = null;
	//			System.out.println(urlR[0]);
				if(urlR!=null){
					boolean f = false;
					for (int j = urlR.length-1; j >= 0; j--) {
						if(url[i].matches(urlR[j])){
							s = new SingleUrlCrawler(url[i],xpath.get(j+1));
	//						System.out.println(urlR[j]);
							f = true;
							break;
						}
	//					System.out.println(urlR[j]+" not match");
					}
					if(!f)
						s = new SingleUrlCrawler(url[i],xpath.get(0));
				}else s = new SingleUrlCrawler(url[i],xpath.get(0));
				s.setHeader(header);
				s.setCookie(cookie);
				s.setDispose(dispose);
				if (getLink){
					s.setRtLink(true);
					if(StringHelper.validateString(linkpath))
						s.setLinkpath(linkpath);
				}
				s.setthen(then);
				s.setSleepTime(stime);
				s.setDs(ds);
				if (then.equalsIgnoreCase("save")||then.equalsIgnoreCase("saveaslist")) {
	//						s.setSave(true);
	//				s.setDbip(dbip);
	//				s.setUser(user);
	//				s.setPwd(pwd);
					s.setTable(table);
				}
				if(lable!=null&&lable.length>1)
					s.setLable(lable);
				//else s.setLable(xpath.get(0).toArray(new String[0]));
				Future fut = pool.submit(s);
				if(fut!=null)
					fList.add(fut);
	//					sl.add(s);
			}
		}else{
			for (int i = 0; i < url.length; i++) {
//				System.out.println("news");
				AutoNewsCrawler s = new AutoNewsCrawler(url[i]);
				s.setHeader(header);
				s.setCookie(cookie);
				if (getLink){
					s.setRtLink(true);
					if(StringHelper.validateString(linkpath))
						s.setLinkpath(linkpath);
				}
				s.setSleepTime(stime);
//							s.setSave(true);
//				s.setDbip(dbip);
//				s.setUser(user);
//				s.setPwd(pwd);
				s.setDs(ds);
				if(table!=null)
					s.setTable(table);
				//else s.setLable(xpath.get(0).toArray(new String[0]));
				Future fut = pool.submit(s);
				if(fut!=null)
					fList.add(fut);
//						sl.add(s);
			}
		}
		for (int i = 0; i < fList.size(); i++) {
			CrawlerReturnEntity cb = null;
			try {
				cb = (CrawlerReturnEntity) fList.get(i).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			}
//					WebCrawler.send("http://"+mainNode+"/?mothod=ok&index="+i);
			try {
				JSONObject t = new JSONObject();
				t.put("done", ip);
				if(t!=null&&ip!=null){
					out.writeObject(t);
					out.flush();
				}
			} catch (Exception e) {
e.printStackTrace();logger.error("Exception",e);
			}
			if(cb!=null)
				for (int j = 0; j < cb.getLink().size(); j++) {
					backLink.add(cb.getLink().get(j));
				}
			else {
				System.err.println("--------CrawlerReturnEntity==null---------return");
				logger.error("--------CrawlerReturnEntity==null---------return");
				return;
			}
			if (!then.equalsIgnoreCase("save")&&!then.equalsIgnoreCase("saveaslist")&&!then.contains("auto")) {
				for (int j = 0; j < cb.getRes().size(); j++) {
					backRes.add(cb.getRes());
				}
			}
			if(cb.getErr()!=null)
				for (int j = 0; j < cb.getErr().size(); j++) {
					err.add(cb.getErr().get(j));
				}
			
		}
		JSONObject json=new JSONObject();
		json.put("finish", ip);
		if (backLink.size()>0) {
			json.put("link", backLink.toArray(new String[0]));
		}
		if (!then.equalsIgnoreCase("save")&&!then.equalsIgnoreCase("saveaslist")) {
			json.put("res", backRes);
		}
		if (err.size()>0){
			json.put("err", err);
			System.err.println(err);
			logger.error(err);
		}
		try {
			//System.out.println(json);
			out.writeObject(json);
			out.flush();
//			if(!getLink){
//				out.close();
//			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
		}
		
	}
}
