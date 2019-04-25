package swust.yang.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import swust.yang.entity.CppcheckConfigInfo;
import swust.yang.entity.PluginInfo;
import swust.yang.entity.ResultMsg;
import swust.yang.service.IPlug;
import swust.yang.util.ConfigInfoManage;
import swust.yang.util.RunTask;
import swust.yang.util.SystemProperty;

public class CppcheckPlug implements IPlug {

	@Override
	public ResultMsg singleExecute(String configInfo, String toolPath, String filePath, String logDir) {
		// 检查传入的toolPath是否存在
		File toolFile = new File(toolPath);
		if (!toolFile.exists()) {
			System.err.println("作业执行出错,传入的工具路径不存在(" + toolPath + ")");
			return null;
		}

		// 检查传入的toolPath是否是一个目录
		if (!toolFile.isDirectory()) {
			System.err.println("作业执行出错,传入的工具路径不是一个目录(" + toolPath + ")");
			return null;
		}

		// 检查传入的filePath是否存在
		File taskFile = new File(filePath);
		if (!taskFile.exists()) {
			System.err.println("作业执行出错,传入的作业路径不存在(" + filePath + ")");
			return null;
		}
		// 检查传入的filePath是否是一个目录
		if (taskFile.isDirectory()) {
			System.err.println("作业执行出错,传入的作业路径是一个目录(" + filePath + ")");
			return null;
		}

		// 检查传入的log_dir目录是否存在
		File logFile = new File(logDir);
		if (!logFile.exists()) {
			System.err.println("作业执行出错,传入的日志目录不存在(" + logDir + ")");
			return null;
		}

		// 检查传入的log_dir是否是一个目录
		if (!logFile.isDirectory()) {
			System.err.println("作业执行出错,传入的日志路径不是一个目录(" + logDir + ")");
			return null;
		}

		// 获取传入待执行作业文件名
		String taskSuffix = filePath.substring(filePath.lastIndexOf('.'));
		if (!taskSuffix.equals(".c") && !taskSuffix.equals(".cpp")) {
			System.err.println("作业执行出错,待检查的作业为不是一个c/cpp文件(" + filePath + ")");
			return null;
		}

		// 文件分隔符
		String fileSeparator = SystemProperty.getFileSeparator();

		// 工具路径(包含具体文件)
		if (SystemProperty.getOSName().equals("Linux")) {
			toolPath = toolPath + fileSeparator + "./cppcheck";
		} else {
			toolPath = toolPath + fileSeparator + "cppcheck.exe";
		}

		// 日志格式
		String outputFormat = "--template=vs";

		// 学生信息
		String studentInfo = filePath.substring(filePath.lastIndexOf(fileSeparator) + 1, filePath.lastIndexOf('.'));

		// 日志的命名格式
		String logName = studentInfo + ".log";

		// 日志存储位置（含文件名）
		String logPath = logDir + fileSeparator + logName;

		// 检查项初始化
		StringBuilder checkItem = new StringBuilder("--enable=");

		// 将前端返回的包含配置信息的JSon字符串转换为对象
		CppcheckConfigInfo configObj = JSonToObject(configInfo);

		// 获取配置的检查项及对应分数
		Map<String, Float> map = ConfigInfoManage.getRulesAndScores(configObj);

		// 提取检查项
		if(map.size() > 1) {
			for (String item : map.keySet()) {
				if (!item.equals("error")) {
					checkItem.append(item + ",");
				}
			}
			checkItem.setCharAt(checkItem.length() - 1, ' ');
		}
		else {
			checkItem = checkItem.delete(0, checkItem.length());
		}

		// 待执行的命令
		String executeCommand = toolPath + " " + outputFormat + " " + checkItem.toString() + filePath + " " + "2> "
				+ logPath;
		System.out.println(executeCommand);

		// 执行作业
		try {
			RunTask.runCommand(executeCommand);
		} catch (IOException e) {
			System.err.println("作业执行失败！错误原因可能是：");
			System.err.println("1、执行命令错误(" + executeCommand + ");");
			System.err.println("2、传入的工具路径不是所需工具的根目录.");
			e.printStackTrace();
			return null;
		}

		// 解析执行日志
		float taskScore = 0.0f;
		try {
			taskScore = RunTask.getTaskScoreOfCheck(logPath, map);
		} catch (FileNotFoundException e) {
			System.err.println("作业执行失败！错误原因可能是：");
			System.err.println("1、日志路径不存在(" + logPath + ");");
			System.err.println("2、cppcheck环境异常;");
			System.err.println("3、传入的工具路径不是所需工具的根目录.");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.err.println("作业执行失败！关闭文件IO出错!");
			e.printStackTrace();
			return null;
		}

		// 保存得分
		ResultMsg msg = new ResultMsg();
		msg.setStudentInfor(studentInfo);
		msg.setScore(taskScore);
		return msg;
	}

	@Override
	public List<ResultMsg> batchExecute(String configInfo, String toolPath, String srcDir, String logDir) {
		// 执行结果集合
		List<ResultMsg> msgs = new ArrayList<ResultMsg>();

		// 实例化指定目录的文件对象
		File srcFile = new File(srcDir);
		File logFile = new File(logDir);
		File toolFile = new File(toolPath);

		// 检查传入的toolPath是否存在
		if (!toolFile.exists()) {
			System.err.println("批量检查作业失败！传入的工具路径不存在(" + toolPath + ")");
			return null;
		}

		// 检查传入的toolPath是否是一个目录
		if (!toolFile.isDirectory()) {
			System.err.println("批量检查作业失败！传入的工具路径不是一个目录(" + toolPath + ")");
			return null;
		}

		// 检查作业目录否存在
		if (!srcFile.exists()) {
			System.err.println("批量检查作业失败！传入的作业目录不存在(" + srcDir + ")");
			return null;
		}

		// 检查日志目录是否存在
		if (!logFile.exists()) {
			System.err.println("批量检查作业失败！传入的日志目录不存在(" + logDir + ")");
			return null;
		}

		// 检查作业目录是否为一个文件目录
		if (!srcFile.isDirectory()) {
			System.err.println("批量检查作业失败！传入的作业路径不是一个目录(" + srcDir + ")");
			return null;
		}
		if (!logFile.isDirectory()) {
			System.err.println("批量检查作业失败！传入的日志路径不是一个目录(" + logDir + ")");
			return null;
		}

		// 获取该文件目录下的所有文件名
		String[] fileList = srcFile.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".cpp") || name.endsWith(".c")) {
					return true;
				}
				return false;
			}
		});
		for (String item : fileList) {
			// 待检查的作业路径
			String filePath = srcDir + SystemProperty.getFileSeparator() + item;
			System.out.println("file path:" + filePath);
			// 每次执行结果
			ResultMsg ret = null;
			ret = singleExecute(configInfo, toolPath, filePath, logDir);
			if (ret != null) {
				msgs.add(ret);
			} else {
				ResultMsg msg = new ResultMsg();
				msg.setStudentInfor(item.substring(0, item.lastIndexOf('.')));
				msg.setScore(0.0f);
				msgs.add(msg);
			}
		}
		return msgs;
	}

	@Override
	public String checkConfigInfo(String configInfo) {
		// 将前端返回的包含配置信息的JSon字符串转换为对象
		CppcheckConfigInfo configObj = null;
		try {
			configObj = JSonToObject(configInfo);
		} catch (net.sf.json.JSONException e) {
			e.printStackTrace();
			return "JSon格式错误！";
		}
		// 获取CpplintConfigInfo类的所有公共方法
		Method[] methods = configObj.getClass().getMethods();
		// 总分
		Float totalScore = null;
		// 规则
		String rule = null;
		// 规则对应分数
		Float currentScore = null;
		// 分数累计
		float score = 0.0f;
		// 遍历获取的公共方法数组
		for (Method m : methods) {
			String methodName = m.getName();
			if (methodName.equals("getTotalScore")) {
				try {
					totalScore = (Float) m.invoke(configObj);
					if (totalScore == null) {
						return "总分不能为空！";
					}
					if (totalScore == 0) {
						return "总分必须大于0且不能为特殊字符/字母！";
					}
					if (totalScore < 0 || totalScore > 100) {
						return "总分必须大于0且小于等于100！";
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
					return "未知错误！请联系管理员！";
				}
			} else if (methodName.equals("getScoreOfError")) {
				try {
					currentScore = (Float) m.invoke(configObj);
					if (currentScore == null) {
						return "错误消息检查项分数不能为空！";
					}
					if (currentScore == 0) {
						return "错误消息检查项分数必须大于0且不能为特殊字符/字母！";
					}
					if (currentScore < 0 || currentScore > 100) {
						return "错误消息检查项分数必须大于0且小于等于100！";
					}
					score += currentScore;
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
					return "未知错误！请联系管理员！";
				}
			} else if (methodName.startsWith("getCheck")) {
				String mName = "getScoreOf" + methodName.substring(8);
				try {
					rule = (String) m.invoke(configObj);
					currentScore = (Float) configObj.getClass().getMethod(mName).invoke(configObj);
					if (rule == null) {
						if (currentScore != null) {
							return "每一项检查项及其对应分数必须同时为空或者同时不为空！";
						}
					} else {
						if (currentScore == null) {
							return "每一项检查项及其对应分数必须同时为空或者同时不为空！";
						}
						if (currentScore == 0) {
							return "检查项分数必须大于0且不能为特殊字符/字母！";
						}
						if (currentScore < 0 || currentScore > 100) {
							return "检查项分数必须大于0且小于等于100！";
						}
						score += currentScore;
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
					return "未知错误！请联系管理员！";
				}
			}
		}
		if (score != totalScore) {
			return "总分与各检查项分数之和不相等，请检查！";
		}
		return "OK";
	}

	@Override
	public PluginInfo getPluginInfo() {
		PluginInfo cpplintInfo = new PluginInfo();
		cpplintInfo.setName("cppcheck-plug-1.0.jar");
		cpplintInfo.setAuthor("yangyechi");
		cpplintInfo.setDescription("Code static check plug-in");
		cpplintInfo.setVersion("1.0");
		cpplintInfo.setClassName("swust.yang.service.impl.CppcheckPlug");
		return cpplintInfo;
	}

	@Override
	public String getHtml(String preSetting) {
		String html = "";
		if(preSetting == null) {
			html = "<div id = \"cppcheck\" >\r\n" + 
					"		总分\r\n" + 
					"		<input type=\"text\" name=\"totalScore\" value=\"\" placeholder=\"大于0小于等于100\" >\r\n" + 
					"	    <br />\r\n" + 
					"		\r\n" + 
					"		启用错误消息(默认开启)\r\n" + 
					"		<input type=\"text\" name=\"scoreOfError\" value=\"\" placeholder=\"检查项分数设置\">\r\n" + 
					"	    <br />\r\n" + 
					"	    \r\n" + 
					"	        启用警告消息\r\n" + 
					"	    <input type=\"checkbox\" name=\"checkWarning\" value=\"warning\">\r\n" + 
					"	    <input type=\"text\" name=\"scoreOfWarning\" value=\"\" placeholder=\"检查项分数设置\">\r\n" + 
					"	    <br />   \r\n" + 
					"	      \r\n" + 
					"	        启用风格警告消息\r\n" + 
					"	    <input type=\"checkbox\" name=\"checkStyle\" value=\"style\">\r\n" + 
					"	    <input type=\"text\" name=\"scoreOfStyle\" value=\"\" placeholder=\"检查项分数设置\">\r\n" + 
					"	    <br />   \r\n" + 
					"	      \r\n" + 
					"	        启用可移植性警告消息\r\n" + 
					"	    <input type=\"checkbox\" name=\"checkPortability\" value=\"portability\">\r\n" + 
					"	    <input type=\"text\" name=\"scoreOfPortability\" value=\"\" placeholder=\"检查项分数设置\">\r\n" + 
					"	    <br />   \r\n" + 
					"	      \r\n" + 
					"	        启用性能警告消息\r\n" + 
					"	    <input type=\"checkbox\" name=\"checkPerformance\" value=\"performance\">\r\n" + 
					"	    <input type=\"text\" name=\"scoreOfPerformance\" value=\"\"  placeholder=\"检查项分数设置\">\r\n" + 
					"	    <br />\r\n" + 
					"	     \r\n" + 
					"	    <font size=\"3\" color=\"#FF0000\">检查项说明:</font>\r\n" + 
					"	    <br />\r\n" + 
					"	    <font size=\"2\">1、错误消息：代码中的错误项（编译器检查不出来的BUG），如：数组越界、内存泄漏等；</font>\r\n" + 
					"		<br />\r\n" + 
					"		<font size=\"2\">2、警告消息：为了避免产生bug而提供的编程改进意见，如：代码可能出现空指针异常等；</font>\r\n" + 
					"		<br />\r\n" + 
					"		<font size=\"2\">3、风格警告消息：风格有关问题的代码清理，如：冗余代码、常量性等；</font>\r\n" + 
					"		<br />\r\n" + 
					"		<font size=\"2\">4、可移植性警告消息：提示跨平台时容易出现的问题，如：64 位的可移植性、不同的编译器中代码运行结果不同等；</font>\r\n" + 
					"		<br />\r\n" + 
					"		<font size=\"2\">5、性能警告消息：建议可优化的代码（这些建议只是基于常识，即使修复这些消息，也不确定会得到任何可测量的性能提升）；</font>\r\n" + 
					"		<br />\r\n" + 
					"		\r\n" + 
					"	</div>";
		} else {
			// 保存分数
			String scoreOfWarning = "'' ", scoreOfStyle = "'' ", 
				   scoreOfPortability = "'' ", scoreOfPerformance = "'' ";
			// 保存规则
			String checkWarning = "", checkStyle = "", checkPortability = "", checkPerformance = "";
			
			// 将以前的配置信息(JSon字符串)转换为对象
			CppcheckConfigInfo configObj = JSonToObject(preSetting);
			
			// 获取以前的配置信息
			// 获取CppcheckConfigInfo类的所有公共方法
			Method[] methods = configObj.getClass().getMethods();
			
			// 遍历获取的公共方法数组
			for (Method m : methods) {
				// 寻找方法名开头为getScoreOf的公共方法
				String mName = m.getName();
				String s = "";
				if (mName.startsWith("getScoreOf")) {
					switch (mName) {
					case "getScoreOfWarning":
						s = ConfigInfoManage.analysisConfigInfo(configObj, mName);
						if (s != null && !s.equals("null")) {
							checkWarning = " checked";
							scoreOfWarning = '"' + Float.valueOf(s).toString() + '"' + " ";
						}
						break;
					case "getScoreOfStyle":
						s = ConfigInfoManage.analysisConfigInfo(configObj, mName);
						if (s != null && !s.equals("null")) {
							checkStyle = " checked";
							scoreOfStyle = '"' + Float.valueOf(s).toString() + '"' + " ";
						}
						break;
					case "getScoreOfPortability":
						s = ConfigInfoManage.analysisConfigInfo(configObj, mName);
						if (s != null && !s.equals("null")) {
							checkPortability = " checked";
							scoreOfPortability = '"' + Float.valueOf(s).toString() + '"' + " ";
						}
						break;
					case "getScoreOfPerformance":
						s = ConfigInfoManage.analysisConfigInfo(configObj, mName);
						if (s != null && !s.equals("null")) {
							checkPerformance = " checked";
							scoreOfPerformance = '"' + Float.valueOf(s).toString() + '"' + " ";
						}
						break;
					}
				}
			}
			html = "<div id = \"cppcheck\" >\r\n" + 
					"		总分\r\n" + 
					"		<input type=\"text\" name=\"totalScore\" value=" + '"'+ configObj.getTotalScore().toString() + '"' + " placeholder=\"大于0小于等于100\" >\r\n" + 
					"	    <br />\r\n" + 
					"		\r\n" + 
					"		启用错误消息(默认开启)\r\n" + 
					"		<input type=\"text\" name=\"scoreOfError\" value=" + '"'+ configObj.getScoreOfError().toString() + '"' +  " placeholder=\"检查项分数设置\">\r\n" + 
					"	    <br />\r\n" + 
					"	    \r\n" + 
					"	        启用警告消息\r\n" + 
					"	    <input type=\"checkbox\" name=\"checkWarning\" value=\"warning\"" + checkWarning + ">\r\n" + 
					"	    <input type=\"text\" name=\"scoreOfWarning\" value=" + scoreOfWarning + "placeholder=\"检查项分数设置\">\r\n" + 
					"	    <br />   \r\n" + 
					"	      \r\n" + 
					"	        启用风格警告消息\r\n" + 
					"	    <input type=\"checkbox\" name=\"checkStyle\" value=\"style\"" + checkStyle + ">\r\n" + 
					"	    <input type=\"text\" name=\"scoreOfStyle\" value=" + scoreOfStyle + "placeholder=\"检查项分数设置\">\r\n" + 
					"	    <br />   \r\n" + 
					"	      \r\n" + 
					"	        启用可移植性警告消息\r\n" + 
					"	    <input type=\"checkbox\" name=\"checkPortability\" value=\"portability\"" + checkPortability + ">\r\n" + 
					"	    <input type=\"text\" name=\"scoreOfPortability\" value="+ scoreOfPortability + "placeholder=\"检查项分数设置\">\r\n" + 
					"	    <br />   \r\n" + 
					"	      \r\n" + 
					"	        启用性能警告消息\r\n" + 
					"	    <input type=\"checkbox\" name=\"checkPerformance\" value=\"performance\"" + checkPerformance + ">\r\n" + 
					"	    <input type=\"text\" name=\"scoreOfPerformance\" value=" + scoreOfPerformance + "placeholder=\"检查项分数设置\">\r\n" + 
					"	    <br />\r\n" + 
					"	     \r\n" + 
					"	    <font size=\"3\" color=\"#FF0000\">检查项说明:</font>\r\n" + 
					"	    <br />\r\n" + 
					"	    <font size=\"2\">1、错误消息：代码中的错误项（编译器检查不出来的BUG），如：数组越界、内存泄漏等；</font>\r\n" + 
					"		<br />\r\n" + 
					"		<font size=\"2\">2、警告消息：为了避免产生bug而提供的编程改进意见，如：代码可能出现空指针异常等；</font>\r\n" + 
					"		<br />\r\n" + 
					"		<font size=\"2\">3、风格警告消息：风格有关问题的代码清理，如：冗余代码、常量性等；</font>\r\n" + 
					"		<br />\r\n" + 
					"		<font size=\"2\">4、可移植性警告消息：提示跨平台时容易出现的问题，如：64 位的可移植性、不同的编译器中代码运行结果不同等；</font>\r\n" + 
					"		<br />\r\n" + 
					"		<font size=\"2\">5、性能警告消息：建议可优化的代码（这些建议只是基于常识，即使修复这些消息，也不确定会得到任何可测量的性能提升）；</font>\r\n" + 
					"		<br />\r\n" + 
					"		\r\n" + 
					"	</div>";
		}
		return html;
	}

	private CppcheckConfigInfo JSonToObject(String configInfo) {
		JSONObject jsonObject = JSONObject.fromObject(configInfo);
		CppcheckConfigInfo configObj = (CppcheckConfigInfo) JSONObject.toBean(jsonObject, CppcheckConfigInfo.class);
		return configObj;
	}
}
