package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.document.TextDocument;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput;

public class AutoFileReader {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		args = new String[2];
		args[0] = "D:\\360极速浏览器下载\\1_20170614100615_ldaon\\正文抽取-源数据";
		args[1] = "D:\\360极速浏览器下载\\1_20170614100615_ldaon\\正文抽取-test";
		
		BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;
		File inDir = new File(args[0]);
		File outDir = new File(args[1]);
		if(!outDir.exists()){
			if(outDir.getParentFile().exists()){
				if(outDir.mkdir())
					System.out.println("目录不存在,已成功创建");
				else System.err.println("目录创建失败");
			}else {
				System.err.println("父目录不存在");
				return;
			}
		}
		if (inDir.isDirectory()) {
            String[] filelist = inDir.list();
            for (int i = 0; i < filelist.length; i++) {
            	File file = new File(args[0] + "\\" + filelist[i]);
            	if(file.isFile()){
            		try {
						InputSource inputStream = new InputSource(new FileInputStream(file));
						inputStream.setEncoding("UTF-8");
						TextDocument doc = new BoilerpipeSAXInput(inputStream).getTextDocument();
						extractor.process(doc);  
//						System.out.println("title:" + doc.getTitle());
						File newFile = new File(args[1] + "\\" + filelist[i]);
						if(!newFile.exists()){
							if(newFile.createNewFile()){
								FileWriter fw = new FileWriter(newFile);
								BufferedWriter bw = new BufferedWriter(fw);
								String res = doc.getContent();
								if(res.contains("字体大小： 大 中 小"))
									res=res.split("字体大小： 大 中 小", 2)[1];
								bw.write(res);
								bw.flush();
								bw.close();
								fw.close();
								System.out.println(filelist[i]+"已完成，标题："+doc.getTitle());
							}else System.err.println(filelist[i]+"创建失败");
						}else System.err.println(filelist[i]+"已存在");
//						System.out.println("content:" + doc.getContent());
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BoilerpipeProcessingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            }
		}
	}
}
