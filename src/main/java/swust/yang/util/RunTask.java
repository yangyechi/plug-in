package swust.yang.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class RunTask {

	/**
	 * @description 运行传入的命令
	 * @param executeCommand 待运行命令
	 * @throws IOException If IO error occur
	 */
	public static void runCommand(String executeCommand) throws IOException {
		Process p = null;
		if (SystemProperty.getOSName().equals("Linux")) {
			//待测试
			p = Runtime.getRuntime().exec(executeCommand);
		} else {
			p = Runtime.getRuntime().exec("cmd /c " + executeCommand);
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		// 关闭流释放资源
		if (p != null) {
			p.getOutputStream().close();
		}
		String readLine = null;
		while ((readLine = br.readLine()) != null) {
			System.out.println(readLine);
		}
		if (br != null) {
			br.close();
		}
		p.destroy();
		p = null;
	}

	/**
	 * 
	 * @description 解析作业运行日志,根据日志计算该作业本次检查的得分。规则：违反了一次设置的检查项扣一分，扣完为止。
	 * @param logPath 作业运行日志路径(包含文件后缀名)
	 * @param map     存储选择的检查项及对应分数(key-value)
	 * @return 返回作业得分(四舍五入取整)
	 * @throws IOException           If an I/O error occurs
	 * @throws FileNotFoundException If log_path not found
	 * 
	 */
	public static float getTaskScoreOfLint(String logPath, Map<String, Float> map)
			throws FileNotFoundException, IOException {
		float score = 0.0f;
		int flag = 0;
		try (BufferedReader read = new BufferedReader(new FileReader(logPath))) {
			String rule = read.readLine();
			while (rule != null) {
				// 读取日志,每次读一行
				rule = read.readLine();
				if (rule != null) {
					flag = 1;
					rule = "~ " + rule.substring(rule.lastIndexOf("RULE"), rule.lastIndexOf(','));
					Float currentScore = map.get(rule);
					if (currentScore == null) {
						System.err.println("错误！在map中未找到日志中出现的rule:" + rule);
					} else {
						currentScore--;
						if (currentScore < 0) {
							currentScore = 0.0f;
						}
						map.put(rule, currentScore);
					}
				}
			}
			if (flag == 0) {
				BufferedWriter write = new BufferedWriter(new FileWriter(logPath));
				write.write("Great! Check it out!");
				write.flush();
				write.close();
			}
		}
		// 遍历map,获取value值,得到该学生本次检查的最终得分
		for (Float item : map.values()) {
			score += item;
		}
		return (float) Math.round(score);
	}

	public static float getTaskScoreOfCheck(String logPath, Map<String, Float> map)
			throws FileNotFoundException, IOException {
		float score = 0.0f;
		int flag = 0;
		String log = null;
		try (BufferedReader read = new BufferedReader(new FileReader(logPath))) {
			while ((log = read.readLine()) != null) {
				int beginIndex = 0, endIndex = 0;
				if (SystemProperty.getOSName().equals("Linux")) {
					beginIndex = log.indexOf('(') + 1;
					endIndex = log.indexOf(')');
				} else {
					beginIndex = log.indexOf(' ', log.indexOf(')')) + 1;
					endIndex = log.indexOf(':', beginIndex);
				}
				log = log.substring(beginIndex, endIndex);
				Float currentScore = map.get(log);
				if (currentScore == null) {
					if(map.containsKey("style")) {
						if(log.equals("warning") ||log.equals("performance") 
								|| log.equals("portability")) {
							Float s = map.get("style");
							s--;
							if(s < 0) {
								s = 0.0f;
							}
							map.put("style", s);
						}
						else {
							System.err.println("错误！在map中未找到日志中出现的错误关键词：" + log);
						}
					}
					else {
						System.err.println("错误！在map中未找到日志中出现的错误关键词：" + log);
					}		
				} else {
					currentScore--;
					if (currentScore < 0) {
						currentScore = 0.0f;
					}
					map.put(log, currentScore);
				}
				flag = 1;
			}
			if (flag == 0) {
				BufferedWriter write = new BufferedWriter(new FileWriter(logPath));
				write.write("Great! Check it out!");
				write.flush();
				write.close();
			}
		}
		// 遍历map,获取value值,得到该学生本次检查的最终得分
		for (Float item : map.values()) {
			score += item;
		}
		return (float) Math.round(score);
	}
}
