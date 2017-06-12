package servlet;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.alibaba.fastjson.JSONPath;

import CrawlerSYS.crawler.Crawler;
import CrawlerSYS.main.MainNodeControler;

public class WebSocket extends WebSocketServer {
	public WebSocket(InetSocketAddress address) { 
		super(address);
		System.out.println("地址"+address); 
	}
	int e=0;
	int l=0;
	public WebSocket(int port) throws UnknownHostException {
		super(new InetSocketAddress(port)); 
		System.out.println("端口"+port); 
	}
	/** * 触发关闭事件 */ 
	@Override 
	public void onClose(org.java_websocket.WebSocket conn, int message, String reason, boolean remote) {
		
	} /** * 触发异常事件 */
	@Override 
	public void onError(org.java_websocket.WebSocket conn, Exception message) {
		System.out.println("Socket异常:"+message.toString());
		e++; 
	} /** * 客户端发送消息到服务器时触发事件 */ 
	@Override 
	public void onMessage(org.java_websocket.WebSocket conn, String message) {
		System.out.println(message);
		if(message!=null){
			List<String> url =(List<String>) JSONPath.read(message,"$.urls.url");
//			System.out.println(url);
			List<String> node =(List<String>) JSONPath.read(message,"$.nodes.node");
			String then = (String)JSONPath.read(message,"$.then");
			List<String> xpath = null ;
			if (then==null||!then.equalsIgnoreCase("auto")) {
				xpath =(List<String>) JSONPath.read(message,"$.xpaths.xpath");
			}
			List<String> header =(List<String>) JSONPath.read(message,"$.header.key");
			List<String> cookie =(List<String>) JSONPath.read(message,"$.cookie.key");
			List<String> headerV =(List<String>) JSONPath.read(message,"$.header.val");
			List<String> cookieV =(List<String>) JSONPath.read(message,"$.cookie.val");
			
			String timer = (String) JSONPath.read(message,"$.timer");
			int inv = 0;
			String t = (String) JSONPath.read(message,"$.interval");
			if(t!=null)
				inv=Integer.parseInt(t);
			
			if(node==null||node.get(0).length()==0){
				node=new ArrayList<String>();
				node.add("127.0.0.1:6543");
			}
			MainNodeControler n = new MainNodeControler(url,xpath,node.toArray(new String[0]));
			n.setConn(conn);
			t = (String) JSONPath.read(message,"$.thread");
			if(t!=null)
				n.setThread(Integer.parseInt(t));
			t = (String) JSONPath.read(message,"$.urlLimit");
			if(t!=null)
				n.setUrlLimit(Integer.parseInt(t));
//			t = (Boolean) JSONPath.read(message,"$.relink");
			boolean relink=false;
//			if(t!=null)
			if(JSONPath.read(message,"$.relink")!=null)
				relink = (Boolean) JSONPath.read(message,"$.relink");
			n.setRelink(relink);
			t = (String) JSONPath.read(message,"$.stime");
			if(t!=null)
				n.setSleeptime(Integer.parseInt(t));
			t = (String) JSONPath.read(message,"$.allLimit");
			if(t!=null)
				n.setAllLimit(Integer.parseInt(t));
			if(then!=null)
				n.setthen(then);
			if (relink)
				n.setLinkpath((String) JSONPath.read(message,"$.linkpath"));
			if (then!=null&&(then.contains("save")||then.contains("auto"))) {
				String table = (String) JSONPath.read(message,"$.table"),dbip = (String) JSONPath.read(message,"$.dbip"),
						user = (String) JSONPath.read(message,"$.user"),pwd = (String) JSONPath.read(message,"$.pw");
				List<String> lable = (List<String>) JSONPath.read(message,"$.lables.lable");
				n.setTable(table);
				n.setDbip(dbip);
				n.setUser(user);
				n.setPwd(pwd);
				if(lable!=null)
					n.setLable(lable.toArray(new String[1]));
			}
			Crawler c = new Crawler(n);
			c.header(header,headerV).cookie(cookie,cookieV);
			if(timer!=null)
				c.fixTimeRun(timer);
			else if(inv>0)
				c.periodRun(inv);
			else c.start();
			
		} 
	} /** * 触发连接事件 */
	@Override 
	public void onOpen(org.java_websocket.WebSocket conn, ClientHandshake handshake) {
		System.out.println("有人连接Socket conn:"+conn);
		l++; 
	}
}
