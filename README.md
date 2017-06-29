# CrawlerSYS

Light-weight High-performance Reliable Smart Distributed Crawler System [preview](http://cloudforwork.cn/crawler)  [中文文档](https://github.com/zrtzrt/CrawlerSYS/raw/master/docs/CrawlerSYS%E8%AE%BE%E8%AE%A1%E6%96%87%E6%A1%A3.docx)
<br /><br />
![crawlerPage](https://raw.githubusercontent.com/zrtzrt/CrawlerSYS/master/docs/img/crawler.JPG)

### describe
- #### Light-weight
  it's very small. Streamline the process to perfection. 
  1. Use java socket to Communication.
  2. Use web socket to monitor.
  3. Use jsoup to download.
  4. Use xsoup to get data.

- #### High-performance

  it's very fast. Because using HikariCP it can access database very fast.

   I crawler [damai](https://www.damai.cn/) all date using 10 thread working on 4Mbps network take me less than 30s.
    <video controls><sourc src="https://github.com/zrtzrt/CrawlerSYS/raw/master/docs/video/CrawlerSYS%20damai.mp4" type="video/mp4">
    [video](https://github.com/zrtzrt/CrawlerSYS/raw/master/docs/video/CrawlerSYS%20damai.mp4)
    </video>
- #### Reliable

  It's very accurate. It rarely  go wrong when deal with dig data.

  I have another project working on this [HeatPoint](cloudforwork.cn/heatpoint). You can see it's vary distinct.

- #### Smart

  It can automatic get data(only new or blog) from website you entry without training. 

## How to use

 1. You can git war to you tomcat Server.
 2. You can download jar and use it in your project.

## Demo

```java
List<String> xpath = new ArrayList<String>();  xpath.add("//a/@href");
List<String> url = new ArrayList<String>();  url.add("https://www.github.com");
new Crawler(url,xpath).run();   //get data by xpath
```



```java
List<String> url = new ArrayList<String>();  url.add("https://www.github.com");
new Crawler(url,"http.+",1000).run();  //automatic get title and content
```

## Thanks

This project dependent on several Open source project

[jsoup](https://github.com/jhy/jsoup) ,[xsoup](https://github.com/code4craft/xsoup) , [boilerpipe](https://github.com/kohlschutter/boilerpipe) , [HikariCP](https://github.com/brettwooldridge/HikariCP)

## API 

### Common 

#### Crawler(List\<String\> url, List\<String\> xpath)	Crawler
> Create new crawler by List of url and xpath.
#### Crawler(List\<String\> url, Map\<String,String\> xpath)		Crawler
> Create new crawler by List of url and Map of lable(key),xpath(value).
#### Crawler(List\<String\> url, String urlRegex, int limit)		Crawler	
> Create new auto crawler by List of url. Use urlRegex to limit url type.It will get title and content automatic without xpath.
#### run() 	void
> Start running in this thread. It will wait until all node finished.
#### start() 	void
> Start running in other thread.
#### periodRun(int timeoutInHours)		void
> Run in several hours.
#### fixTimeRun(String time)		void
> Run in fixed time like 12:00:00. Format: "HH:mm:ss"
#### distributed(String[] node)	Crawler
> Add node. Default: "127.0.0.1:6543". Format: "ip : port".
#### exception(String urlRegex, List\<String\> xpath)		Crawler
> Select special url and dispose with special xpath.
#### save(boolean asList, String table, String[] lable)		Crawler
> Data persistence. Save to your database (mysql).If asList is false only first date pre xpath.
#### jdbc(String ip,String user,String pw)	Crawler
> JDBC configuration. Default: "127.0.0.1:3306/crawler","root","root". Format: "ip : port /dataBaseName".
#### recursion(String linkpath, int limit)	Crawler
> Use breadth-first crawler. Default: "//a/@href",5000
#### limit(int limit)	Crawler
> The amount of data to send each time.Less limit more accurate monitoring but slower.Default: 30
#### thread(int thread)		Crawler
> Change crawler speed.Depend on your network bandwith.Recommend: 2~3 per Mbps.Default: 10
#### sleep(int sleep)		Crawler
> Slow down crawler to avoid connection time out error.Default: 0
#### custom(Dispose d)	        Crawler
> If you want to processing acquired data or use your own way to save date.you can implements  Dispose and use it as parameter.
 
<br />
  
### Less common
#### exceptions(String[] urlRegex, List\<List\<String\>\> xpath)	Crawler
> Add several exceptions at one times.
#### header(Map\<String,String\> header)	Crawler
> Add unify header for any url. Default:{"User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)"}
#### cookie(Map\<String,String\> cookie)		Crawler
> Add unify cookie for any url.
#### header(List\<String\> key,List\<String\> val)	Crawler
#### cookie(List\<String\> key,List\<String\> val)	Crawler
#### recursion(String linkpath)	Crawler
> If you use it without limit.It will never over.
#### jdbc(String ip,int port,String dbName,String user,String pw)	Crawler
#### save(boolean asList, String table)		Crawler
#### startServer()	Crawler
> Start crawler server by default port.
#### startServer(int port)		Crawler
> Default: 6543

 <br />
 
### Static
#### WebCrawler.getConnect(String url, Map\<String,String\> header, Map\<String,String\> cookie)		Connection
> Return org.jsoup.Connection for next step.
#### WebCrawler.send(String url)		void
> Send http request without response.
#### WebCrawler.get(String url, Map\<String,String\> header, String encode)	String
> Get json from http request.
#### WebCrawler.getByXpath(Document doc, String[] xpath, int sleepTime)		List\<List\<String\>\>
> Extraction data by xpath from Document.
#### WebCrawler.ignoreSsl()		void
> Ignore SSL certificate.To avoid Validation failure.
#### StringHelper.toInt(String str)	String
> Same as Integer.parseInt(String s) but more smart.
#### similarity(String str1,String str2)		double
> Return similarity between two String.
#### StringHelper.similarity(List\<String\> strList,String str,double filter)		String
> Return most similar String in List.If less than filter return 0.
#### StringHelper.sqlCREATE(String table,String[] lable,int[] length)		String
#### StringHelper.sqlSELECT(String table,String[] lable,String[] condition)	String
#### StringHelper.sqlINSERT(String table,String[] lable,String[] data)	String
#### StringHelper.sqlDate(Date date)		String

<br />

### Interface Dispose
#### resultDispose(CrawlerReturnEntity back,HikariDataSource ds)		CrawlerReturnEntity

|   name |           class           |             describe             |
| -----: | :-----------------------: | :------------------------------: |
|    url |          String           |             page url             |
|    res |    List\<List\<String\>\>     |       date by xpath order       |
| resMap | Map\<String, List\<String\>\> | you can use it if you add lables |
|   link |       List\<String\>        |       add link if you want       |

