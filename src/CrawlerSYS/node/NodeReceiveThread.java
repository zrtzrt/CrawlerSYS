package CrawlerSYS.node;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.zaxxer.hikari.HikariDataSource;

import CrawlerSYS.main.MainReceiveThread;

import net.minidev.json.JSONObject;

public class NodeReceiveThread extends Thread{
	private Socket sk = null;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;
	private ExecutorService pool;
	private Logger logger = Logger.getLogger(this.getClass());
	private HikariDataSource ds = new HikariDataSource();
	
	public NodeReceiveThread(Socket sk) {
		super();
		this.sk = sk;
		try {
			sk.setSendBufferSize(1024*1024);
			sk.setReceiveBufferSize(1024*1024);
			in = new ObjectInputStream(sk.getInputStream());
			out = new ObjectOutputStream(sk.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
		}
	}


	public void run() {
		while(sk!=null&&!sk.isClosed()){
			JSONObject m = null;
			try {
				m = (JSONObject) in.readObject();
			} catch (IOException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
				break;
//				try {
//					sk.shutdownInput();
//					in=null;
////					out.close();
////					sk.close();
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//				e.printStackTrace();logger.error("Exception",e);
//				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
			}
			if(m!=null){
				if (((String) m.get("method")).equalsIgnoreCase("end")) {
					ds.close();
					try {
						System.out.println("node shutdown");
						logger.info("node shutdown");
//						sk.shutdownInput();
						in.close();
						out.close();
						sk.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
					}
				}else
				if (((String) m.get("method")).equalsIgnoreCase("get")) {
//					System.out.println(m);
					int spider = (Integer) m.get("thread");
					if(pool==null)
						pool = Executors.newFixedThreadPool(spider);
					NodeAfterReceive sar = new NodeAfterReceive(m,out,pool,ds);
					sar.start();
				}
			}
		}
	}
}
