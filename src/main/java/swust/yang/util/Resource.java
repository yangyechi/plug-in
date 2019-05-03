package swust.yang.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Resource {

	/**
	 * 
	 * @param srcPath 源文件路径
	 * @param destPath 目的文件路径
	 * @param type 用于辨别是生产环境还是部署环境(jar包)，部署环境下传入的参数为"jar"，生产环境下参数为"produce"
	 * @throws IOException If an IO error occur
	 */
	public static void writeFromJar(String srcPath,String destPath,String type) throws IOException{
		//部署环境下从jar包读取资源
		if(type.equals("jar")) {
        	//读写数据
			try (InputStream input = Resource.class.getResourceAsStream(srcPath); 
		         BufferedReader read = new BufferedReader(new InputStreamReader(input));
				 BufferedWriter write = new BufferedWriter(new FileWriter(destPath))){
					String readLine = null;
					while((readLine = read.readLine()) != null) {
						readLine = readLine + "\n";
						write.write(readLine);
					}
					write.flush();
			}
        } else {
        	try (BufferedReader read = new BufferedReader(new FileReader(srcPath));
   				 BufferedWriter write = new BufferedWriter(new FileWriter(destPath))){
   					String readLine = null;
   					while((readLine = read.readLine()) != null) {
   						readLine = readLine + "\n";
   						write.write(readLine);
   					}
   					write.flush();
   			}
        }
    }
	
	/**
	 * 
	 * @param data 待写入文件的数据
	 * @param destPath 目的文件所在路径
	 * @throws IOException If an IO error occur
	 */
	public static void writeToFile(String data,String destPath) throws IOException {
		try (BufferedWriter write = new BufferedWriter(new FileWriter(destPath))){
  				write.write(data);
  				write.flush();
  		}
	}
}
