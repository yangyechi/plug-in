package swust.yang.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class GccTools {

	/**
	 * 
	 * @param command 执行命令
	 * @param outputFileName 编译之后生成的可执行文件名(包含后缀名)
	 * @param fileDir 待编译的文件所在目录
	 * @param logPath 日志路径(包含文件名及其后缀名)
	 * @return
	 * @throws IOException
	 */
	public static String compile(String command, String outputFileName, String fileDir, String logPath)
			throws IOException {
		// 编译源文件
		File srcDir = new File(fileDir);
		Process p = Runtime.getRuntime().exec(command, null, srcDir);

		// 关闭流释放资源
		if (p != null) {
			p.getOutputStream().close();
		}
		BufferedReader br = null;
		String readLine = null;

		// 打印输出缓冲区数据
		br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((readLine = br.readLine()) != null) {
			System.out.println(readLine);
		}
		if (br != null) {
			br.close();
		}
	
		// 打印错误缓冲区数据
		br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		BufferedWriter write = null;
		while ((readLine = br.readLine()) != null) {
			if (write == null) {
				write = new BufferedWriter(new FileWriter(logPath));
			}
			write.write(readLine);
		}
	
		//编译出错标志,初始化为true
		boolean error = true;
		
		// 关闭文件IO
		if (write != null) {
			// 如果错误缓冲区有数据,那么获取作业目录下的后缀名为.exe的文件名
			String[] file_list = srcDir.list(new FilenameFilter() {
				String tag = ".exe";
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(tag);
				}
			});
			//检查是否编译成功
			for(String fileName : file_list) {
				if(fileName.equals(outputFileName)) {
					error = false;
					break;
				}
			}
			write.flush();
			write.close();
		}
		else {
			error = false;
		}
		
		if (br != null) {
			br.close();
		}

		// 销毁子进程
		p.destroy();
		p = null;

		if(error) {
			return "Compile Error";
		}
		return "OK";
	}
	
	
	public static String execute(List<String> testCase,long timeout,
			String file_path, String log_dir) {
		return "Accept";
	}
}
