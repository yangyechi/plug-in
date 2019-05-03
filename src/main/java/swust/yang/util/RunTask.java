package swust.yang.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RunTask {

	/**
	 * @description 运行传入的命令
	 * @param executeCommand 待运行命令
	 * @throws IOException If IO error occur
	 */
	public static void runCommand(String executeCommand) throws IOException {
		Process p = null;
		if (SystemProperty.getOSName().equals("Linux")) {
			String[] cmd = new String[]{"/bin/sh", "-c",executeCommand};
			p = Runtime.getRuntime().exec(cmd);
		} else {
			p = Runtime.getRuntime().exec("cmd /c " + executeCommand);
		}
		
		// 关闭输入流(即关闭shell/cmd输入缓冲区)
		if (p != null) {
			p.getOutputStream().close();
		}
		
		//存储读取的数据
		String data = null;
		
		//读取存储正常流输出缓冲区数据
		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		while ((data = input.readLine()) != null) {
			System.out.println(data);
		}
		if (input != null) {
			input.close();
		}
		
		//读取存储错误流的输出缓冲区
		BufferedReader errorInput = 
				new BufferedReader(new InputStreamReader(p.getErrorStream()));
		while ((data = errorInput.readLine()) != null) {
			System.out.println(data);
		}
		if (errorInput != null) {
			errorInput.close();
		}
		
		//销毁执行命令的进程
		p.destroy();
		p = null;
	}

	/**
	 * 
	 * @description 解析作业运行日志,根据日志计算该作业本次检查的得分。
	 * 				规则：违反了一次设置的检查项扣一分(扣完为止,即不会出现负分)，若该检查项错误超过5个,
	 * 				则把该检查项对应的分数扣完。最后对各检查项所得分数进行累加即为本次检查的总分。
	 * @param logPath 作业运行日志路径(包含文件后缀名)
	 * @param map     存储选择的检查项及对应分数(key-value)
	 * @return 返回作业得分
	 * @throws IOException           If an I/O error occurs
	 * @throws FileNotFoundException If log_path not found
	 * 
	 */
	public static float getTaskScoreOfLint(String logPath, Map<String, Float> map)
			throws FileNotFoundException, IOException {
		
		// 对需要用到的变量初始化
		float score = 0.0f;
		int flag = 0;
		
		// map<检查项-错误个数>初始化
		Map<String,Integer> errorMap = new HashMap<String,Integer>();
		for(String item : map.keySet()) {
			errorMap.put(item, 0);
		}
		
		//分析日志
		try (BufferedReader read = new BufferedReader(new FileReader(logPath))) {
			//跳过日志第一行
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
						// 获取该类错误个数
						int num = errorMap.get(rule) + 1;
						// 更新该类错误个数
						errorMap.put(rule, num);
						currentScore--;
						// 如果该类错误个数大于5或者该检查项得分已经小于0，那么该检查项得分置为0
						if (currentScore < 0 || num > 5) {
							currentScore = 0.0f;
						}
						map.put(rule, currentScore);					
					}
				}
			}
			
			//如果flag为0(表示当前日志没有错误)，则重写日志
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
		
		//若此时是批量执行阶段且日志有错，则对汇总结果进行更新、覆盖
		if(ResultsSummarOfLint.getFlag() && flag != 0) {
			for(Entry<String,Integer> item : errorMap.entrySet()) {
				if(item.getValue() > 0) {
					ResultsSummarOfLint.upateErrorNum(item.getKey(), item.getValue());
				}
			}			
		}
		
		return score;
	}

	/**
	 * 
	 * @description 解析作业运行日志,根据日志计算该作业本次检查的得分。
	 * 				规则：违反了一次设置的检查项扣一分(扣完为止,即不会出现负分)，若该检查项错误超过5个,
	 * 				则把该检查项对应的分数扣完。最后对各检查项所得分数进行累加即为本次检查的总分。
	 * @param logPath 作业运行日志路径(包含文件后缀名)
	 * @param map     存储选择的检查项及对应分数(key-value)
	 * @return 返回作业得分
	 * @throws IOException           If an I/O error occurs
	 * @throws FileNotFoundException If log_path not found
	 * 
	 */
	public static float getTaskScoreOfCheck(String logPath, Map<String, Float> map)
			throws FileNotFoundException, IOException {
		
		//对需要用到的变量初始化
		float score = 0.0f;
		int flag = 0;
		String log = null;
		
		// map<检查项-错误个数>初始化
		Map<String,Integer> errorMap = new HashMap<String,Integer>();
		for(String item : map.keySet()) {
			errorMap.put(item, 0);
		}
		
		//分析日志
		try (BufferedReader read = new BufferedReader(new FileReader(logPath))) {
			while ((log = read.readLine()) != null) {
				int beginIndex = 0, endIndex = 0;
				/*
				if (SystemProperty.getOSName().equals("Linux")) {
					beginIndex = log.indexOf('(') + 1;
					endIndex = log.indexOf(')');
				} else {
					beginIndex = log.indexOf(' ', log.indexOf(')')) + 1;
					endIndex = log.indexOf(':', beginIndex);
				}
				*/
				//日志处理
				beginIndex = log.indexOf(' ', log.indexOf(')')) + 1;
				endIndex = log.indexOf(':', beginIndex);
				log = log.substring(beginIndex, endIndex);
				
				//获取该检查项设置的分数
				Float currentScore = map.get(log);
				if (currentScore == null) {
					if(map.containsKey("style")) {
						if(log.equals("warning") ||log.equals("performance") 
								|| log.equals("portability")) {
							//获取该类检查项当前剩余得分
							Float s = map.get("style");
							// 获取该类错误个数
							int num = errorMap.get("style") + 1;
							// 更新该类错误个数
							errorMap.put("style", num);
							s--;
							// 如果该类错误个数大于5或者该检查项得分已经小于0，那么该检查项得分置为0
							if(s < 0 || num > 5) {
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
					// 获取该类错误个数
					int num = errorMap.get(log) + 1;
					// 更新该类错误个数
					errorMap.put(log, num);
					currentScore--;
					// 如果该类错误个数大于5或者该检查项得分已经小于0，那么该检查项得分置为0
					if (currentScore < 0 || num > 5) {
						currentScore = 0.0f;
					}
					map.put(log, currentScore);	
				}
				flag = 1;
			}
			//如果flag为0(表示当前日志没有错误)，则重写日志
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
		
		//若此时是批量执行阶段且日志有错，则对汇总结果进行更新、覆盖
		if(ResultsSummarOfCheck.getFlag() && flag != 0) {
			for(Entry<String,Integer> item : errorMap.entrySet()) {
				if(item.getValue() > 0) {
					ResultsSummarOfCheck.upateErrorNum(item.getKey(), item.getValue());
				}
			}			
		}
		
		return score;
	}
}
