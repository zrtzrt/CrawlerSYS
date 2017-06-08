package CrawlerSYS.crawler;

public class DefaultConfig {
	public static String dbip = "127.0.0.1:3306/test";
	public static String dbArgs = "useUnicode=true&characterEncoding=UTF-8";
	public static String user = "root";
	public static String password = "root";
	public static String then = "show";
	public static int urlLimit = 15;
	public static int thread = 5;
	public static int allLimit = 5000;
	public static int sleepTime = 0;
	public static int serverPort = 6543;
	public static int socketPort = 6544;
	public static int lableSize = 200;
	public static int urlSize = 50;
	public static int titleSize = 100;
	public static int textSize = 10000;
	public static String[] node = {"127.0.0.1:6543"};
}
