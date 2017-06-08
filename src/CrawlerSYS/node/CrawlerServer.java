package CrawlerSYS.node;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

import org.apache.log4j.Logger;

import CrawlerSYS.crawler.DefaultConfig;

public class CrawlerServer extends Thread{
	private ServerSocket server = null;
	private Logger logger = Logger.getLogger(CrawlerServer.class);  
	
	public CrawlerServer(int port) {
		super();
		try {
			server = new ServerSocket(port,100);
			System.out.println("ServerSocket start");
			logger.info("ServerSocket start");
		} catch (IOException e) {
			// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			try {
				server.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			}
		}
	}
	public CrawlerServer() {
		this(DefaultConfig.serverPort);
	}
	public void run() {
		while(!server.isClosed()){
				Socket sk = null;
				try {
					sk = server.accept();
				} catch (IOException e) {
					// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
				}
				if(sk!=null&&!sk.isClosed()){
					NodeReceiveThread nrt= new NodeReceiveThread(sk);
					nrt.start();
				}	
		}
		System.out.println("server closed");	
		logger.info("server closed");
	}
	public void close() {
		try {
			server.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();logger.error("Exception",e);
		}
	}
	
}
