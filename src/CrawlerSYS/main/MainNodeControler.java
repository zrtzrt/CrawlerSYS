package CrawlerSYS.main;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.minidev.json.JSONObject;

import CrawlerSYS.crawler.DefaultConfig;
import CrawlerSYS.utils.StringHelper;

public class MainNodeControler implements Runnable{
	
	private boolean relink,isOver = false;
	private int urlLimit=DefaultConfig.urlLimit,thread=DefaultConfig.thread,
			allLimit=DefaultConfig.allLimit,sleeptime=DefaultConfig.sleepTime;
	private String then = DefaultConfig.then,table,dbip,linkpath,user,pwd,dispose,autoR="http.+";
	private String[] node = DefaultConfig.node, lable;
	private Map<String,String> header = null,cookie = null;
	private List<String> urlRegex = new ArrayList<String>();
	private List<List<String>> xpath = new ArrayList<List<String>>();
	
	
	private int indexOfNode=0,send;
	private long startTime;
	private int[] sumUrl;
	private Socket[] sList;
	private ObjectOutputStream[] outList;
	private ObjectInputStream[] inList;
	private org.java_websocket.WebSocket conn;
	private LinkedHashSet<String> url = new LinkedHashSet<String>();
	private List<String> newUrl = new ArrayList<String>(),ban = new ArrayList<String>();
	private List<List<String>> res;
	private Logger logger = Logger.getLogger(MainNodeControler.class);
	
	public MainNodeControler(List<String> url, String urlRegex, int limit, String[] node){
		for (int i = 0; i < url.size(); i++) {
			this.url.add(url.get(i).toLowerCase());
		}
		setNode(node);
		sumUrl = new int[node.length];
		outList = new ObjectOutputStream[node.length];
		inList = new ObjectInputStream[node.length];
		sList = new Socket[node.length];
		autoR = urlRegex;
		then = "auto";
		allLimit = limit;
		logger.info("new MainNodeControler("+url+",auto,"+urlRegex+","+limit+","+node[0]+")");
	}
	public MainNodeControler(List<String> url, List<String> xpath, String[] node){
		for (int i = 0; i < url.size(); i++) {
			this.url.add(url.get(i).toLowerCase());
		}
		this.xpath.add(xpath);
		setNode(node);
		sumUrl = new int[node.length];
		outList = new ObjectOutputStream[node.length];
		inList = new ObjectInputStream[node.length];
		sList = new Socket[node.length];
//		System.out.println("new MainNodeControler("+url+","+xpath+","+node[0]+")");
		logger.info("new MainNodeControler("+url+","+xpath+","+node[0]+")");
	}
	public MainNodeControler(String[] url, String[] xpath, String[] node){
		for (int i = 0; i < url.length; i++) {
			this.url.add(url[i].toLowerCase());
		}
		List<String> x = new ArrayList<String>();
		for (int i = 0; i < xpath.length; i++) {
			x.add(xpath[i]);
		}
		this.xpath.add(x);
		setNode(node);
		sumUrl = new int[node.length];
		outList = new ObjectOutputStream[node.length];
		inList = new ObjectInputStream[node.length];
		sList = new Socket[node.length];
		logger.info("new MainNodeControler("+url+","+xpath+","+node[0]+")");
//		System.out.println("new MainNodeControler("+url+","+xpath+","+node+")");
	}
	public void run() {
		startTime = new Date().getTime();
//		System.out.println("MainNodeControler start------------"+node.length);
		logger.info("MainNodeControler start------------"+node.length);
//		System.out.println(urlRegex.get(0));
		String[][] u = StringHelper.spArr(url.toArray(new String[url.size()]), node.length);
//		if(then.contains("save")||then.contains("auto")){
//			if(!(dbip.matches(".+:[0-9]+/.+")&&
//					user!=null&&pwd!=null)){
//				System.err.println("JDBC config missed");
//				logger.error("JDBC config missed");
//				return;
//			}
//		}
//		for (int i = 1; i < xpath.size(); i++) {
//			if(!(xpath.get(i)!=null&&xpath.get(i).size()>1&&xpath.get(i).get(0).length()>0)){
//				ban.add(urlRegex.get(i-1).toLowerCase());
//			}
//		}
		for (int i = 0; i < node.length; i++) {
//			String res = WebCrawler.getJson("http://"+node.get(i)+":8080/CrawlerServlet?method=get&url="+u[i]+"&xpath="+xpath, null);
			send(u[i],i);
		}
	}
		
	public void send(String[] url,int i){
		if(i>node.length){
			System.err.println("out of node list");
			logger.error("out of node list");
			return;
		}
		if(then.contains("auto")){
			for (int j = 0; j < url.length; j++) {
				if(!url[j].matches(autoR))
					return;
			}
		}
		send++;
//		sumUrl[i]+=url.length;
		Socket s = null;
		JSONObject json=new JSONObject();
		json.put("method", "get");
		json.put("thread", thread);
		json.put("url", url);
		json.put("xpath", xpath);
		json.put("header", header);
		json.put("cookie", cookie);
		json.put("urlR", urlRegex.toArray(new String[urlRegex.size()]));
		json.put("relink", relink);
		if (relink) {
			json.put("linkpath", linkpath);
		}
		json.put("then", then);
		json.put("dispose", dispose);
		json.put("stime", sleeptime);
		if(then.contains("save")||then.contains("auto")){
			json.put("dbip", dbip);
			json.put("user", user);
			json.put("pwd", pwd);
			json.put("table", table);
		}
		json.put("lable", lable);
		try {
			if(outList[i]==null){
//				System.out.println(node[i]);
				s = new Socket(node[i].split(":", 2)[0],Integer.parseInt(node[i].split(":", 2)[1]));
				s.setSoTimeout(10*1000);
				s.setReceiveBufferSize(1024*1024);
				s.setSendBufferSize(1024*1024);
				sList[i] = s;
				ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
				outList[i] = out;
			}
			outList[i].writeObject(json);
			outList[i].flush();
			if(inList[i]==null){
				ObjectInputStream in = new ObjectInputStream(s.getInputStream());
				inList[i] = in;
				MainReceiveThread read = new MainReceiveThread(this,i);
				read.start();
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
		}
	}
	
	public void over(){
		if (newUrl.size()>0) {
			send(getNewUrl().toArray(new String[getNewUrl().size()]), getIndexOfNode());
//			System.out.println(getNewUrl().size()+"@"+getNode()[getIndexOfNode()]+"\n"+getNewUrl());
			logger.info(getNewUrl().size()+"@"+getNode()[getIndexOfNode()]);
			if(getIndexOfNode()<getNode().length-1)
				setIndexOfNode(getIndexOfNode()+1);
			else setIndexOfNode(0);
			getNewUrl().clear();
			return;
		}
		isOver=true;
		int urlIndex=0;
		for (int j = 0; j < sumUrl.length; j++) {
			urlIndex+=sumUrl[j];
		}
		long endTime = new Date().getTime();
		System.out.println("got:"+urlIndex+",used Time:"+(endTime-startTime)/1000+"s");
		if(conn!=null)
			conn.close();
		JSONObject json=new JSONObject();
		json.put("method", "end");
		for (int i = 0; i < node.length; i++) {
			try {
				System.out.println(json);
				logger.info(json);
				outList[i].writeObject(json);
				outList[i].flush();
				outList[i].close();
				inList[i].close();
//				sList[i].close();
				outList[i]=null;
				inList[i]=null;
				sList[i]=null;
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			}
		}
//		if("auto".equalsIgnoreCase(then)&&res.size()>0){
//			LinkedHashSet<String> text = new LinkedHashSet<String>();
////			LinkedHashSet<Integer> size = new LinkedHashSet<Integer>();
////			List<List<List<String>>> rlist = new ArrayList<List<List<String>>>();
//			int max = 0;
//			for (int i = 0; i < res.size(); i++) {
//				int t = res.get(i).size();
//				if(t>max)
//					max=t;
//			}
//			List<List<String>> structure = new ArrayList<List<String>>();
//			for (int i = 0; i < max; i++) {
//				List<String> t = new ArrayList<String>();
//				if(res.get(i).size()<=i){
//					for (int j = 0; j < res.size(); j++) {
//						if(text.add(res.get(j).get(i)))
//							t.add(res.get(j).get(i));
//						else {
//							t.clear();
//							break;
//						}
//					}
//					if(t!=null)
//						structure.add(t);
//				}
//			}
//			
//			text.clear();
////			for (int i = 0; i < structure.size(); i++) {
////				if(text.add(structure.get(i)))
////					save
////			}
//		}
	}
	
	
	public String[] getNode() {
		return node;
	}
	public void setNode(String[] node2) {
		if(node2!=null)
			this.node = node2;
	}
	public LinkedHashSet<String> getUrl() {
		return url;
	}
	public void setUrl(LinkedHashSet<String> url) {
		this.url = url;
	}
	public String getAutoR() {
		return autoR;
	}
	public void setAutoR(String autoR) {
		this.autoR = autoR;
	}
	public Map<String, String> getHeader() {
		return header;
	}
	public void setHeader(Map<String, String> header) {
		this.header = header;
	}
	public void setCookie(Map<String, String> header) {
		this.cookie = header;
	}
	public Map<String, String> getCookie() {
		return cookie;
	}
	public List<List<String>> getXpath() {
		return xpath;
	}
	public void setXpath(List<List<String>> xpath) {
		this.xpath = xpath;
	}
	public int getUrlLimit() {
		return urlLimit;
	}
	public void setUrlLimit(int urlLimit) {
		this.urlLimit = urlLimit;
	}
	public int getThread() {
		return thread;
	}
	public void setThread(int thread) {
		this.thread = thread;
	}
	public String getthen() {
		return then;
	}
	public void setthen(String then) {
		this.then = then;
		if("auto".equalsIgnoreCase(then))
			res = new ArrayList<List<String>>();
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getDbip() {
		return dbip;
	}
	public void setDbip(String dbip) {
		this.dbip = dbip;
	}
	public String getLinkpath() {
		return linkpath;
	}
	public void setLinkpath(String linkpath) {
		this.linkpath = linkpath;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String[] getLable() {
		return lable;
	}
	public void setLable(String[] lable) {
		this.lable = lable;
	}
	public synchronized boolean isRelink() {
		return relink;
	}
	public synchronized void setRelink(boolean relink) {
		this.relink = relink;
	}
	public synchronized List<String> getNewUrl() {
		return newUrl;
	}
	public synchronized void setNewUrl(List<String> newUrl) {
		this.newUrl = newUrl;
	}
	public synchronized int getIndexOfNode() {
		return indexOfNode;
	}
	public synchronized void setIndexOfNode(int indexOfNode) {
		this.indexOfNode = indexOfNode;
	}
	public int[] getSumUrl() {
		return sumUrl;
	}
	public void setSumUrl(int[] sumUrl) {
		this.sumUrl = sumUrl;
	}
	public ObjectOutputStream[] getOutList() {
		return outList;
	}
	public void setOutList(ObjectOutputStream[] outList) {
		this.outList = outList;
	}
	public ObjectInputStream[] getInList() {
		return inList;
	}
	public void setInList(ObjectInputStream[] inList) {
		this.inList = inList;
	}
	public int getAllLimit() {
		return allLimit;
	}
	public void setAllLimit(int allLimit) {
		this.allLimit = allLimit;
	}
	public int getSleeptime() {
		return sleeptime;
	}
	public void setSleeptime(int sleeptime) {
		this.sleeptime = sleeptime;
	}
	public int getSend() {
		return send;
	}
	public void setSend(int send) {
		this.send = send;
	}
	public Socket[] getsList() {
		return sList;
	}
	public void setsList(Socket[] sList) {
		this.sList = sList;
	}
	public String getDispose() {
		return dispose;
	}
	public void setDispose(String dispose) {
		this.dispose = dispose;
	}
	public List<String> getUrlRegex() {
		return urlRegex;
	}
	public void setUrlRegex(List<String> urlRegex) {
		this.urlRegex = urlRegex;
	}
	public List<String> getBan() {
		return ban;
	}
	public void setBan(List<String> ban) {
		this.ban = ban;
	}
	public List<List<String>> getRes() {
		return res;
	}
	public void setRes(List<List<String>> res) {
		this.res = res;
	}
	public boolean isOver() {
		return isOver;
	}
	public org.java_websocket.WebSocket getConn() {
		return conn;
	}
	public void setConn(org.java_websocket.WebSocket conn) {
		this.conn = conn;
	}
}
