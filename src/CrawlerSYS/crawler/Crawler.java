package CrawlerSYS.crawler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import CrawlerSYS.node.CrawlerServer;
import CrawlerSYS.node.Dispose;

import CrawlerSYS.main.MainNodeControler;

public class Crawler {
	private Logger logger = Logger.getLogger(this.getClass());  
	private MainNodeControler nc;
	
	public Crawler(MainNodeControler nc) {
		this.nc=nc;
	}
	public Crawler(List<String> url, List<String> xpath) {
		super();
		String[] node = DefaultConfig.node;
		nc = new MainNodeControler(url,xpath,node);
	}
	public Crawler(List<String> url, Map<String,String> xpath) {
		this(url,new ArrayList<String>(xpath.values()));
		nc.setLable(xpath.keySet().toArray(new String[xpath.size()]));
	}
	public Crawler(List<String> url, String urlRegex, int limit) {
		super();
		String[] node = DefaultConfig.node;
		nc = new MainNodeControler(url,urlRegex,limit,node);
	}
	public void run(){
		nc.run();
		while(!nc.isOver()){
			try {
				Thread.sleep(5*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			}
		}
	}
	public void start(){
		new Thread(nc).start();
	}
	
	public Crawler periodRun(int timeoutInHours){
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(nc,timeoutInHours,timeoutInHours,TimeUnit.HOURS);
		return this;
	}
	public Crawler fixTimeRun(String time){
		DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");  
	    DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");  
	    Date curDate = null;
		try {
			curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
		}
		long initDelay  = curDate.getTime() - System.currentTimeMillis();
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(nc,initDelay,1,TimeUnit.DAYS);
		return this;
	}
	
	public Crawler distributed(String[] node){
		for (String n : node) {
			if(n.matches("[0-9]+.[0-9]+.[0-9]+.[0-9]+:[0-9]+")){
				System.err.println("node form error");
				break;
			}
		}
		nc.setNode(node);
		return this;
	}
	public Crawler exception(String urlRegex, List<String> xpath){
//		if(xpath!=null&&xpath.size()>1&&xpath.get(0).length()>0){
			nc.getUrlRegex().add(urlRegex.toLowerCase());
			nc.getXpath().add(xpath);
//		}
		return this;
	}
	public Crawler exception(String urlRegex, String[] xpath){
		exception(urlRegex,Arrays.asList(xpath));
		return this;
	}
	public Crawler exceptions(String[] urlRegex, List<List<String>> xpath){
		for (int i = 0; i < urlRegex.length; i++) {
			exception(urlRegex[i],xpath.get(i));
		}
		return this;
	}
	public Crawler header(Map<String,String> header){
		nc.setHeader(header);
		return this;
	}
	public Crawler cookie(Map<String,String> cookie){
		nc.setCookie(cookie);
		return this;
	}
	public Crawler cookie(List<String> key,List<String> val) {
		if(key==null)
			return this;
		Map<String,String> cookie = new LinkedHashMap<String,String>();
		for (int i = 0; i < val.size(); i++) {
			cookie.put(key.get(i), val.get(i));
		}
		return cookie(cookie);
	}
	public Crawler header(List<String> key,List<String> val) {
		if(key==null)
			return this;
		Map<String,String> header = new LinkedHashMap<String,String>();
		for (int i = 0; i < val.size(); i++) {
			header.put(key.get(i), val.get(i));
		}
		return header(header);
	}
	public Crawler recursion(String linkpath){
		nc.setRelink(true);
		if(linkpath!=null)
			nc.setLinkpath(linkpath);
		return this;
	}
	public Crawler recursion(String linkpath, int limit){
		recursion(linkpath);
		nc.setAllLimit(limit);
		return this;
	}
	public Crawler jdbc(String ip,String user,String pw){
		nc.setDbip(ip);
		nc.setUser(user);
		nc.setPwd(pw);
		return this;
	}
	public Crawler jdbc(String ip,int port,String dbName,String user,String pw){
		jdbc(ip+":"+port+"/"+dbName,user,pw);
		return this;
	}
	public Crawler save(boolean asList, String table){
		if (asList) {
			nc.setthen("saveaslist");
		}else nc.setthen("save");
		nc.setTable(table);
		return this;
	}
	public Crawler save(boolean asList, String table, String[] lable){
		save(asList,table);
		nc.setLable(lable);
		return this;
	}
	public Crawler thread(int thread){
		nc.setThread(thread);
		return this;
	}
	public Crawler limit(int limit){
		nc.setUrlLimit(limit);
		return this;
	}
	public Crawler sleep(int sleep){
		nc.setSleeptime(sleep);
		return this;
	}
//	public Crawler custom(int type){
//		nc.setDispose(type);
//		return this;
//	}
	public Crawler custom(Dispose d){
		nc.setDispose(d.getClass().getName());
		return this;
	}
	public Crawler startServer(int port){
		new CrawlerServer(port).start();
		return this;
	}
	public Crawler startServer(){
		new CrawlerServer(DefaultConfig.serverPort).start();
		return this;
	}
}
