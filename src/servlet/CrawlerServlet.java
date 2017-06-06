package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.HashMap;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.Enumeration;
//import java.util.LinkedHashSet;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import CrawlerSYS.crawler.DefaultConfig;
import CrawlerSYS.node.CrawlerServer;
import CrawlerSYS.utils.WebCrawler;

public class CrawlerServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public CrawlerServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String method = request.getParameter("method");
		if (method.equalsIgnoreCase("startSocket")) {
			String p = request.getParameter("port");
			int port = DefaultConfig.socketPort;
			if(p!=null)
				port= Integer.parseInt(p);
			new WebSocket(port).start();
		}
		if (method.equalsIgnoreCase("startServer")) {
			String p = request.getParameter("port");
			int port = 6543;
			if(p!=null)
				port= Integer.parseInt(p);
			new CrawlerServer(port).start();
		}
		if (method.equalsIgnoreCase("console")) {
			request.getRequestDispatcher("/pages/console.jsp").forward(request, response);
		}
		if (method.equalsIgnoreCase("test")) {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println("ok");
			out.flush();
			out.close();
		}
		if (method.equalsIgnoreCase("get")) {
			String url = request.getParameter("url");
			Map<String,String> header = new HashMap<String,String>();
//			Enumeration e =request.getHeaderNames();                //通过枚举类型获取请求文件的头部信息集
//			//遍历头部信息集
//			    while(e.hasMoreElements()){          
//			//取出信息名                   
//			        String name=(String)e.nextElement();
//			//取出信息值              
//			        String value=request.getHeader(name);
//			        header.put(name, value);
//			    }
			header.put("User-Agent", request.getHeader("User-Agent"));
			String html = WebCrawler.getConnect(url, header, null).get().html();
			html=html.replaceAll("(?i)(<SCRIPT)[\\s\\S]*?((</SCRIPT>)|(/>))", "")
					.replaceAll("(?i)(<LINK)[\\s\\S]*?>", "").replaceAll("(?i)(href)=\".+?\"", "href=\"#\"");
			response.setContentType("text/html");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.println(html);
			out.flush();
			out.close();
		}
//		if (method.equalsIgnoreCase("get")) {
//			String mainNode = request.getParameter("main");
//			String urlArr = request.getParameter("url");
//			String xpathArr = request.getParameter("xpath");
//			List<String> url = Arrays.asList(urlArr);
//			List<String> xpath = Arrays.asList(xpathArr);
//			int getLink = Integer.parseInt(request.getParameter("link"));
//			int spider = Integer.parseInt(request.getParameter("thead"));
//			String linkpath = null, table = null;
//			List<String> lable = null;
//			List<Future> fList = new ArrayList<Future>();
//			LinkedHashSet<String> backLink = new LinkedHashSet<String>();
//			List<List<String>> backRes = new ArrayList<List<String>>();
//			if (getLink==1)
//				linkpath = request.getParameter("linkpath");
//			String then = request.getParameter("then");
//			if (then.equalsIgnoreCase("save")) {
//				table = request.getParameter("table");
//				lable = Arrays.asList(request.getParameter("lable"));
//			}
//			PrintWriter out = response.getWriter();  
//			
//			if(spider>0 && spider<11){
//				ExecutorService pool = Executors.newFixedThreadPool(spider);
////				String[][] u = StringHelper.spArr(url.toArray(new String[url.size()]), spider);
////				List<spider> sl = new ArrayList<spider>();
//				for (int i = 0; i < spider; i++) {
//					spider s = new spider(url.get(i),xpath);
//					if (getLink==1){
//						s.setRtLink(true);
//						if(StringHelper.validateString(linkpath))
//							s.setLinkpath(linkpath);
//					}
//					if (then.equalsIgnoreCase("save")) {
//						s.setTable(request.getParameter("table"));
//						if(lable!=null&&lable.size()>1)
//							s.setLable((String[])lable.toArray());
//						else s.setLable((String[])xpath.toArray());
//					}
//					Future fut = pool.submit(s);
//					fList.add(fut);
////					sl.add(s);
//				}
//				for (int i = 0; i < spider; i++) {
//					callbackEntity cb = null;
//					try {
//						cb = (callbackEntity) fList.get(i).get();
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (ExecutionException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					webSpider.send("http://"+mainNode+"/?mothod=ok&index="+i);
//					if (getLink==1){
//						for (int j = 0; j < cb.getLink().size(); j++) {
//							backLink.add(cb.getLink().get(j));
//						}
//					}
//					if (then.equalsIgnoreCase("save")) {
//						for (int j = 0; j < cb.getRes().size(); j++) {
//							backRes.add(cb.getRes());
//						}
//					}
//					
//				}
//				out.write("<msg>finish"+url.size()+"</msg>");
//				out.write("<msg>link"+backLink.toString()+"</msg>");
//				out.write("<msg>res"+backRes.toString()+"</msg>");
//			}
//		}
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the POST method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	
	public void init(ServletConfig config) throws ServletException {  
        System.out.println("Log4JInitServlet 正在初始化 log4j日志设置信息");  
        String log4jLocation = config.getInitParameter("log4j-properties-location");  

        ServletContext sc = config.getServletContext();  

        if (log4jLocation == null) {  
            System.err.println("*** 没有 log4j-properties-location 初始化的文件, 所以使用 BasicConfigurator初始化");  
            BasicConfigurator.configure();  
        } else {  
            String webAppPath = sc.getRealPath("/");  
            String log4jProp = webAppPath + log4jLocation;  
            File yoMamaYesThisSaysYoMama = new File(log4jProp);  
            if (yoMamaYesThisSaysYoMama.exists()) {  
                System.out.println("使用: " + log4jProp+"初始化日志设置信息");  
                PropertyConfigurator.configure(log4jProp);  
            } else {  
                System.err.println("*** " + log4jProp + " 文件没有找到， 所以使用 BasicConfigurator初始化");  
                BasicConfigurator.configure();  
            }  
        }  
        super.init(config);  
    }  
	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
		new CrawlerServer(DefaultConfig.serverPort).start();
		try {
			new WebSocket(DefaultConfig.socketPort).start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
