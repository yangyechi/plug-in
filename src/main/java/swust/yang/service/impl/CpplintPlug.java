package swust.yang.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import swust.yang.entity.CpplintConfigInfo;
import swust.yang.entity.PluginInfo;
import swust.yang.entity.ResultMsg;
import swust.yang.service.IPlug;
import swust.yang.util.ConfigInfoManage;
import swust.yang.util.RunTask;
import swust.yang.util.SystemProperty;

public class CpplintPlug implements IPlug {
	
	@Override
	public ResultMsg singleExecute(String configInfo, String toolPath, 
								   String filePath, String logDir) {	
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

		// 日志格式
		String outputFormat = "--output=csv";
		
		// 文件分隔符
		String fileSeparator = SystemProperty.getFileSeparator();
			
		//工具路径(包含具体文件)
		toolPath = toolPath + fileSeparator + "nsiqcppstyle.py";
			
		// 规则文件路径
		String fileFilterPath = filePath.substring(0, filePath.lastIndexOf(fileSeparator) + 1) + "filefilter.txt";

		// 学生信息
		String studentInfo = filePath.substring(filePath.lastIndexOf(fileSeparator) + 1, filePath.lastIndexOf('.'));
		
		// 日志的命名格式
		String logName = studentInfo + ".log";

		// 日志存储位置（含文件名）
		String logPath = logDir + fileSeparator + logName;

		// 执行命令
		String executeCommand = 
				"python" + " " + toolPath + " " + outputFormat + " " + "-o" + " "
						+ logPath + " " + "-f" + " " + fileFilterPath + " " + filePath;
		System.out.println(executeCommand);

		// 将前端返回的包含配置信息的JSon字符串转换为对象
	    CpplintConfigInfo configObj =  JSonToObject(configInfo);	

		// 配置规则
		ConfigInfoManage.ruleConfig(configObj, fileFilterPath);

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

		// 分析执行日志
		float taskScore = 0.0f;
		try {
			taskScore = RunTask.getTaskScoreOfLint(logPath, 
												   ConfigInfoManage.getRulesAndScores(configObj));
		} catch (FileNotFoundException e) {
			System.err.println("作业执行失败！错误原因可能是：");
			System.err.println("1、日志路径不存在(" + logPath + ");");
			System.err.println("2、python2环境未配置;");
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
	public List<ResultMsg> batchExecute(String configInfo, String toolPath, 
										String srcDir, String logDir) {
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
		CpplintConfigInfo configObj = null;
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
			} else if (methodName.equals("getCheckExtendRules")) {
				try {
					String mName = "getScoreOf" + methodName.substring(8);
					try {
						rule = (String) m.invoke(configObj);
						currentScore = (Float) configObj.getClass().getMethod(mName).invoke(configObj);
						if (rule == null) {
							if (currentScore != null) {
								return "扩展检查项规则不能为空！";
							}
						} else {
							if (currentScore == null) {
								return "扩展检查项分数不能为空！";
							}
							if (currentScore == 0) {
								return "扩展检查项分数必须大于0且不能为特殊字符/字母！";
							}
							if (currentScore < 0 || currentScore > 100) {
								return "扩展检查项分数必须大于0且小于等于100！";
							}
							// 对扩展检查项手动输入的规则进行检查
							String ret = ConfigInfoManage.isRulesExist(rule.split(";"));
							if (!ret.equals("OK")) {
								return ret;
							}
							score += currentScore;
						}
					} catch (NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
						return "未知错误！请联系管理员！";
					}
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
		cpplintInfo.setName("cpplint-plug-1.0.jar");
		cpplintInfo.setAuthor("yangyechi");
		cpplintInfo.setDescription("Code specification check plug-in");
		cpplintInfo.setVersion("1.0");
		cpplintInfo.setClassName("swust.yang.service.impl.CpplintPlug");
		return cpplintInfo;
	}

	@Override
	public String getHtml(String preSetting) {
		String html = "";

		if (preSetting != null) {
			// 保存分数
			String scoreOfFuncAnnotation = "'' ", scoreOfFuncName = "'' ", scoreOfFuncParamtersNum = "'' ",
					scoreOfFuncStatLinesNum = "'' ", scoreOfVariableName = "'' ", scoreOfMacroName = "'' ",
					scoreOfNestedNum = "'' ", scoreOfUseGoto = "'' ", scoreOfLineLength = "'' ",
					scoreOfIdentationStyle = "'' ", scoreOfOperationSpace = "'' ", scoreOfKeyWordsUseBraces = "'' ",
					scoreOfExtendRules = "'' ";
			// 保存规则
			String checkFuncAnnotation = "", checkFuncName = "", checkFuncParamtersNum = "", checkFuncStatLinesNum = "",
					checkVariableName = "", checkMacroName = "", checkNestedNum = "", checkUseGoto = "",
					checkLineLength = "", checkIdentationStyleTab = "", checkIdentationStyleSpace = "",
					checkOperationSpace = "", checkKeyWordsUseBraces = "", checkExtendRules = "";
			// 将以前的配置信息(JSon字符串)转换为对象
			CpplintConfigInfo configObj = JSonToObject(preSetting);	    	

			// 获取以前的配置信息
			// 获取CpplintConfigInfo类的所有公共方法
			Method[] methods = configObj.getClass().getMethods();
			// 遍历获取的公共方法数组
			for (Method m : methods) {
				// 寻找方法名开头为getScoreOf的公共方法
				String mName = m.getName();
				String s = "";
				if (mName.startsWith("getScoreOf")) {
					switch (mName) {
					case "getScoreOfFuncAnnotation":
						s = ConfigInfoManage.analysisConfigInfo(configObj, mName);
						if (s != null && !s.equals("null")) {
							checkFuncAnnotation = " checked";
							scoreOfFuncAnnotation = '"' + Float.valueOf(s).toString() + '"' + " ";
						}
						break;
					case "getScoreOfFuncName":
						s = ConfigInfoManage.analysisConfigInfo(configObj, mName);
						if (s != null && !s.equals("null")) {
							checkFuncName = " checked";
							scoreOfFuncName = '"' + Float.valueOf(s).toString() + '"' + " ";
						}
						break;
					case "getScoreOfFuncParamtersNum":
						s = ConfigInfoManage.analysisConfigInfo(configObj, mName);
						if (s != null && !s.equals("null")) {
							checkFuncParamtersNum = " checked";
							scoreOfFuncParamtersNum = '"' + Float.valueOf(s).toString() + '"' + " ";
						}
						break;
					case "getScoreOfFuncStatLinesNum":
						s = ConfigInfoManage.analysisConfigInfo(configObj, mName);
						if (s != null && !s.equals("null")) {
							checkFuncStatLinesNum = " checked";
							scoreOfFuncStatLinesNum = '"' + Float.valueOf(s).toString() + '"' + " ";
						}
						break;
					case "getScoreOfVariableName":
						s = ConfigInfoManage.analysisConfigInfo(configObj, mName);
						if (s != null && !s.equals("null")) {
							checkVariableName = " checked";
							scoreOfVariableName = '"' + Float.valueOf(s).toString() + '"' + " ";
						}
						break;
					case "getScoreOfMacroName":
						s = ConfigInfoManage.analysisConfigInfo(configObj, mName);
						if (s != null && !s.equals("null")) {
							checkMacroName = " checked";
							scoreOfMacroName = '"' + Float.valueOf(s).toString() + '"' + " ";
						}
						break;
					case "getScoreOfNestedNum":
						s = ConfigInfoManage.analysisConfigInfo(configObj, mName);
						if (s != null && !s.equals("null")) {
							checkNestedNum = " checked";
							scoreOfNestedNum = '"' + Float.valueOf(s).toString() + '"' + " ";
						}
						break;
					case "getScoreOfUseGoto":
						s = ConfigInfoManage.analysisConfigInfo(configObj, mName);
						if (s != null && !s.equals("null")) {
							checkUseGoto = " checked";
							scoreOfUseGoto = '"' + Float.valueOf(s).toString() + '"' + " ";
						}
						break;
					case "getScoreOfLineLength":
						s = ConfigInfoManage.analysisConfigInfo(configObj, mName);
						if (s != null && !s.equals("null")) {
							checkLineLength = " checked";
							scoreOfLineLength = '"' + Float.valueOf(s).toString() + '"' + " ";
						}
						break;
					case "getScoreOfIdentationStyle":
						s = ConfigInfoManage.analysisConfigInfo(configObj, mName);
						if (s != null && !s.equals("null")) {
							String[] array = s.split("\\+");
							if (array[1].equals("tab")) {
								checkIdentationStyleTab = " checked";
							} else {
								checkIdentationStyleSpace = " checked";
							}
							scoreOfIdentationStyle = '"' + Float.valueOf(array[0]).toString() + '"' + " ";
						}
						break;
					case "getScoreOfOperationSpace":
						s = ConfigInfoManage.analysisConfigInfo(configObj, mName);
						if (s != null && !s.equals("null")) {
							checkOperationSpace = " checked";
							scoreOfOperationSpace = '"' + Float.valueOf(s).toString() + '"' + " ";
						}
						break;
					case "getScoreOfKeyWordsUseBraces":
						s = ConfigInfoManage.analysisConfigInfo(configObj, mName);
						if (s != null && !s.equals("null")) {
							checkKeyWordsUseBraces = " checked";
							scoreOfKeyWordsUseBraces = '"' + Float.valueOf(s).toString() + '"' + " ";
						}
						break;
					case "getScoreOfExtendRules":
						s = ConfigInfoManage.analysisConfigInfo(configObj, mName);
						if (s != null && !s.equals("null")) {
							String[] array = s.split("\\+");
							checkExtendRules = array[1];
							scoreOfExtendRules = '"' + Float.valueOf(array[0]).toString() + '"' + " ";
						}
						break;
					}
				}
			}
			// 前端页面初始化
			html = "<div id = \"cpplint\" >\r\n" + "		总分\r\n"
					+ "		<input type=\"text\" name=\"totalScore\" value=" + '"'
					+ configObj.getTotalScore().toString() + '"' + " " + "placeholder=\"大于0小于等于100\" >\r\n"
					+ "	    <br />\r\n" + "		\r\n" + "		检查函数注释\r\n"
					+ "		<input type=\"checkbox\" name=\"checkFuncAnnotation\" value=\"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\""
					+ checkFuncAnnotation + ">\r\n" + "		<input type=\"text\" name=\"scoreOfFuncAnnotation\" value="
					+ scoreOfFuncAnnotation + "placeholder=\"检查项分数设置\">\r\n" + "	    <br />\r\n" + "	    \r\n"
					+ "	        检查函数命名\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkFuncName\" value=\"~ RULE_3_3_A_start_function_name_with_lowercase_unix\""
					+ checkFuncName + ">\r\n" + "	    <input type=\"text\" name=\"scoreOfFuncName\" value="
					+ scoreOfFuncName + "placeholder=\"检查项分数设置\">\r\n" + "	    <br />   \r\n" + "	      \r\n"
					+ "	        检查函数参数个数\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkFuncParamtersNum\" value=\"~ RULE_6_1_E_do_not_use_more_than_5_paramters_in_function\""
					+ checkFuncParamtersNum + ">\r\n"
					+ "	    <input type=\"text\" name=\"scoreOfFuncParamtersNum\" value=" + scoreOfFuncParamtersNum
					+ "placeholder=\"检查项分数设置\">\r\n" + "	    <br />   \r\n" + "	      \r\n"
					+ "	         检查函数内部语句行数\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkFuncStatLinesNum\" value=\"~ RULE_6_1_G_write_less_than_200_lines_for_function\""
					+ checkFuncStatLinesNum + ">\r\n"
					+ "	    <input type=\"text\" name=\"scoreOfFuncStatLinesNum\" value=" + scoreOfFuncStatLinesNum
					+ "placeholder=\"检查项分数设置\">\r\n" + "	    <br />   \r\n" + "	      \r\n" + "	        检查变量命名\r\n"
					+ "	    <input type=\"checkbox\" disabled name=\"checkVariableName\" value=\"\"" + checkVariableName
					+ ">\r\n" + "	    <input type=\"text\" name=\"scoreOfVariableName\" value=" + scoreOfVariableName
					+ " placeholder=\"检查项分数设置\">\r\n" + "	    <br />\r\n" + "	       \r\n" + "	       检查宏常量命名\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkMacroName\" value=\"~ RULE_6_5_B_do_not_use_lowercase_for_macro_constants\""
					+ checkMacroName + ">\r\n" + "	    <input type=\"text\" name=\"scoreOfMacroName\" value="
					+ scoreOfMacroName + " placeholder=\"检查项分数设置\">\r\n" + "	    <br />   \r\n" + "	       \r\n"
					+ "	       检查嵌套次数\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkNestedNum\" value=\"~ RULE_A_3_avoid_too_deep_block\""
					+ checkNestedNum + ">\r\n" + "	    <input type=\"text\" name=\"scoreOfNestedNum\" value="
					+ scoreOfNestedNum + " placeholder=\"检查项分数设置\">\r\n" + "	    <br />\r\n" + "	 \r\n"
					+ "	        检查是否使用goto语句\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkUseGoto\" value=\"~ RULE_7_2_B_do_not_use_goto_statement\""
					+ checkUseGoto + ">\r\n" + "	    <input type=\"text\" name=\"scoreOfUseGoto\" value="
					+ scoreOfUseGoto + " placeholder=\"检查项分数设置\">\r\n" + "	    <br />\r\n" + "	\r\n"
					+ "	       检查每行代码长度\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkLineLength\" value=\"~ RULE_4_4_A_do_not_write_over_120_columns_per_line\""
					+ checkLineLength + ">\r\n" + "	    <input type=\"text\" name=\"scoreOfLineLength\" value="
					+ scoreOfLineLength + " placeholder=\"检查项分数设置\">\r\n" + "	    <br />\r\n" + "	\r\n"
					+ "	        检查缩进格式\r\n"
					+ "	    <input type=\"checkbox\" id = \"identationOfTab\" name=\"checkIdentationStyle\" value=\"~ RULE_4_1_A_A_use_tab_for_indentation\" onclick = \"check()\""
					+ checkIdentationStyleTab + ">tab\r\n"
					+ "	    <input type=\"checkbox\" id = \"identationOfSpace\" name=\"checkIdentationStyle\" value=\"~ RULE_4_1_A_B_use_space_for_indentation\" onclick = \"check()\""
					+ checkIdentationStyleSpace + ">space\r\n"
					+ "	    <input type=\"text\" name=\"scoreOfIdentationStyle\" value=" + scoreOfIdentationStyle
					+ " placeholder=\"检查项分数设置\">\r\n" + "	    <br />\r\n" + "	       \r\n"
					+ "	        检查操作符周围空格\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkOperationSpace\" value=\"~ RULE_4_2_A_A_space_around_operator\""
					+ checkOperationSpace + ">\r\n" + "	    <input type=\"text\" name=\"scoreOfOperationSpace\" value="
					+ scoreOfOperationSpace + " placeholder=\"检查项分数设置\">\r\n" + "	    <br />\r\n" + "	        \r\n"
					+ "	        检查关键词是否使用大括号\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkKeyWordsUseBraces\" value=\"~ RULE_4_5_B_use_braces_even_for_one_statement\""
					+ checkKeyWordsUseBraces + ">\r\n"
					+ "	    <input type=\"text\" name=\"scoreOfKeyWordsUseBraces\" value=" + scoreOfKeyWordsUseBraces
					+ " placeholder=\"检查项分数设置\">\r\n" + "	    <br />\r\n" + "	        \r\n" + "	        扩展检查项\r\n"
					+ "	    <input type=\"text\" name=\"scoreOfExtendRules\" value=" + scoreOfExtendRules
					+ " placeholder=\"扩展检查项分数设置\">\r\n" + "	    <br />\r\n"
					+ "	    <textarea name=\"checkExtendRules\" placeholder=\"请输入完整的规则,规则间用英文分号分割.\" rows=\"4\" cols=\"75\">"
					+ checkExtendRules + "</textarea>\r\n" + "	    <br />\r\n"
					+ "	    <font size=\"3\" color=\"#FF0000\">检查项说明:</font>\r\n" + "	    <br />\r\n"
					+ "	    <font size=\"2\">1、检查函数注释：函数必须提供doxygen格式的注释；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">2、检查函数命名：函数的命名必须以小写字母开头；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">3、检查函数参数个数：函数参数不能超过5个；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">4、检查函数内部语句行数：函数内部代码不能超过200行；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">5、检查变量命名：变量的命名必须以小写字母开头；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">6、检查宏常量命名：宏常量命名所用的字母必须全是大写字母；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">7、检查嵌套次数：代码的循环、选择嵌套深度不能超过3；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">8、检查是否使用goto语句：代码中不能使用goto语句；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">9、检查每行代码长度：每一行代码不能超过120字符；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">10、检查缩进格式：代码的缩进只能用tab/space；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">11、检查操作符周围空格：操作符两侧添加0/1个空格(代码排版)；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">12、检查关键词是否使用大括号：当关键词内部只有一条语句时，也必须使用一对大括号包裹；</font>\r\n"
					+ "		<br />\r\n" + "		<font size=\"2\">13、扩展检查项：根据需求添加其他检查项，其他检查项</font>\r\n"
					+ "		<a href=\"https://github.com/yangyechi/DiplomaProject/blob/master/%E4%BB%A3%E7%A0%81%E8%A7%84%E8%8C%83%E6%80%A7%E6%A3%80%E6%9F%A5_%E6%89%80%E6%9C%89%E6%A3%80%E6%9F%A5%E9%A1%B9%E5%AF%B9%E5%BA%94%E8%A7%84%E5%88%99.docx\" title=\"所有检查项文档说明\">文档</a>\r\n"
					+ "		<br />\r\n" + "	</div>\r\n" + "  	<script >\r\n" + "  		function check(){\r\n"
					+ "  			if(document.getElementById('identationOfTab').checked){\r\n"
					+ "  				document.getElementById(\"identationOfSpace\").disabled = true;\r\n"
					+ "  			}\r\n"
					+ "  			else if(document.getElementById('identationOfSpace').checked){\r\n"
					+ "  				document.getElementById(\"identationOfTab\").disabled = true;\r\n"
					+ "  			}\r\n" + "  			else{\r\n"
					+ "  				document.getElementById(\"identationOfSpace\").disabled = false;\r\n"
					+ "  				document.getElementById(\"identationOfTab\").disabled = false;\r\n"
					+ "  			}\r\n" + "  		}\r\n" + "  	</script>";

		} else {
			html = "<div id = \"cpplint\" >\r\n" + "		总分\r\n"
					+ "		<input type=\"text\" name=\"totalScore\" value=\"\"  placeholder=\"大于0小于等于100\" >\r\n"
					+ "	    <br />\r\n" + "		\r\n" + "		检查函数注释\r\n"
					+ "		<input type=\"checkbox\" name=\"checkFuncAnnotation\" value=\"~ RULE_5_3_A_provide_doxygen_function_comment_on_function_in_impl\" >\r\n"
					+ "		<input type=\"text\" name=\"scoreOfFuncAnnotation\" value=\"\" placeholder=\"检查项分数设置\">\r\n"
					+ "	    <br />\r\n" + "	    \r\n" + "	        检查函数命名\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkFuncName\" value=\"~ RULE_3_3_A_start_function_name_with_lowercase_unix\" >\r\n"
					+ "	    <input type=\"text\" name=\"scoreOfFuncName\" value=\"\" placeholder=\"检查项分数设置\">\r\n"
					+ "	    <br />   \r\n" + "	      \r\n" + "	        检查函数参数个数\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkFuncParamtersNum\" value=\"~ RULE_6_1_E_do_not_use_more_than_5_paramters_in_function\" >\r\n"
					+ "	    <input type=\"text\" name=\"scoreOfFuncParamtersNum\" value=\"\" placeholder=\"检查项分数设置\">\r\n"
					+ "	    <br />   \r\n" + "	      \r\n" + "	         检查函数内部语句行数\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkFuncStatLinesNum\" value=\"~ RULE_6_1_G_write_less_than_200_lines_for_function\" >\r\n"
					+ "	    <input type=\"text\" name=\"scoreOfFuncStatLinesNum\" value=\"\" placeholder=\"检查项分数设置\">\r\n"
					+ "	    <br />   \r\n" + "	      \r\n" + "	        检查变量命名\r\n"
					+ "	    <input type=\"checkbox\" disabled name=\"checkVariableName\" value=\"\" >\r\n"
					+ "	    <input type=\"text\" name=\"scoreOfVariableName\" value=\"\" placeholder=\"检查项分数设置\">\r\n"
					+ "	    <br />\r\n" + "	       \r\n" + "	       检查宏常量命名\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkMacroName\" value=\"~ RULE_6_5_B_do_not_use_lowercase_for_macro_constants\" >\r\n"
					+ "	    <input type=\"text\" name=\"scoreOfMacroName\" value=\"\" placeholder=\"检查项分数设置\">\r\n"
					+ "	    <br />   \r\n" + "	       \r\n" + "	       检查嵌套次数\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkNestedNum\" value=\"~ RULE_A_3_avoid_too_deep_block\" >\r\n"
					+ "	    <input type=\"text\" name=\"scoreOfNestedNum\" value=\"\" placeholder=\"检查项分数设置\">\r\n"
					+ "	    <br />\r\n" + "	 \r\n" + "	        检查是否使用goto语句\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkUseGoto\" value=\"~ RULE_7_2_B_do_not_use_goto_statement\" >\r\n"
					+ "	    <input type=\"text\" name=\"scoreOfUseGoto\" value=\"\" placeholder=\"检查项分数设置\">\r\n"
					+ "	    <br />\r\n" + "	\r\n" + "	       检查每行代码长度\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkLineLength\" value=\"~ RULE_4_4_A_do_not_write_over_120_columns_per_line\" >\r\n"
					+ "	    <input type=\"text\" name=\"scoreOfLineLength\" value=\"\" placeholder=\"检查项分数设置\">\r\n"
					+ "	    <br />\r\n" + "	\r\n" + "	        检查缩进格式\r\n"
					+ "	    <input type=\"checkbox\" id = \"identationOfTab\" name=\"checkIdentationStyle\" value=\"~ RULE_4_1_A_A_use_tab_for_indentation\" onclick = \"check()\">tab\r\n"
					+ "	    <input type=\"checkbox\" id = \"identationOfSpace\" name=\"checkIdentationStyle\" value=\"~ RULE_4_1_A_B_use_space_for_indentation\" onclick = \"check()\">space\r\n"
					+ "	    <input type=\"text\" name=\"scoreOfIdentationStyle\" value=\"\" placeholder=\"检查项分数设置\">\r\n"
					+ "	    <br />\r\n" + "	       \r\n" + "	        检查操作符周围空格\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkOperationSpace\" value=\"~ RULE_4_2_A_A_space_around_operator\" >\r\n"
					+ "	    <input type=\"text\" name=\"scoreOfOperationSpace\" value=\"\" placeholder=\"检查项分数设置\">\r\n"
					+ "	    <br />\r\n" + "	        \r\n" + "	        检查关键词是否使用大括号\r\n"
					+ "	    <input type=\"checkbox\" name=\"checkKeyWordsUseBraces\" value=\"~ RULE_4_5_B_use_braces_even_for_one_statement\" >\r\n"
					+ "	    <input type=\"text\" name=\"scoreOfKeyWordsUseBraces\" value=\"\" placeholder=\"检查项分数设置\">\r\n"
					+ "	    <br />\r\n" + "	        \r\n" + "	        扩展检查项\r\n"
					+ "	    <input type=\"text\" name=\"scoreOfExtendRules\" value=\"\" placeholder=\"扩展检查项分数设置\">\r\n"
					+ "	    <br />\r\n"
					+ "	    <textarea name=\"checkExtendRules\" placeholder=\"请输入完整的规则,规则间用英文分号分割.\" rows=\"4\" cols=\"75\"></textarea>\r\n"
					+ "	    <br />\r\n" + "	    <font size=\"3\" color=\"#FF0000\">检查项说明:</font>\r\n"
					+ "	    <br />\r\n" + "	    <font size=\"2\">1、检查函数注释：函数必须提供doxygen格式的注释；</font>\r\n"
					+ "		<br />\r\n" + "		<font size=\"2\">2、检查函数命名：函数的命名必须以小写字母开头；</font>\r\n"
					+ "		<br />\r\n" + "		<font size=\"2\">3、检查函数参数个数：函数参数不能超过5个；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">4、检查函数内部语句行数：函数内部代码不能超过200行；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">5、检查变量命名：变量的命名必须以小写字母开头；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">6、检查宏常量命名：宏常量命名所用的字母必须全是大写字母；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">7、检查嵌套次数：代码的循环、选择嵌套深度不能超过3；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">8、检查是否使用goto语句：代码中不能使用goto语句；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">9、检查每行代码长度：每一行代码不能超过120字符；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">10、检查缩进格式：代码的缩进只能用tab/space；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">11、检查操作符周围空格：操作符两侧添加0/1个空格(代码排版)；</font>\r\n" + "		<br />\r\n"
					+ "		<font size=\"2\">12、检查关键词是否使用大括号：当关键词内部只有一条语句时，也必须使用一对大括号包裹；</font>\r\n"
					+ "		<br />\r\n" + "		<font size=\"2\">13、扩展检查项：根据需求添加其他检查项，其他检查项</font>\r\n"
					+ "		<a href=\"https://github.com/yangyechi/DiplomaProject/blob/master/%E4%BB%A3%E7%A0%81%E8%A7%84%E8%8C%83%E6%80%A7%E6%A3%80%E6%9F%A5_%E6%89%80%E6%9C%89%E6%A3%80%E6%9F%A5%E9%A1%B9%E5%AF%B9%E5%BA%94%E8%A7%84%E5%88%99.docx\" title=\"所有检查项文档说明\">文档</a>\r\n"
					+ "		<br />\r\n" + "	</div>\r\n" + "  	<script >\r\n" + "  		function check(){\r\n"
					+ "  			if(document.getElementById('identationOfTab').checked){\r\n"
					+ "  				document.getElementById(\"identationOfSpace\").disabled = true;\r\n"
					+ "  			}\r\n"
					+ "  			else if(document.getElementById('identationOfSpace').checked){\r\n"
					+ "  				document.getElementById(\"identationOfTab\").disabled = true;\r\n"
					+ "  			}\r\n" + "  			else{\r\n"
					+ "  				document.getElementById(\"identationOfSpace\").disabled = false;\r\n"
					+ "  				document.getElementById(\"identationOfTab\").disabled = false;\r\n"
					+ "  			}\r\n" + "  		}\r\n" + "  	</script>";
		}
		return html;
	}

	private CpplintConfigInfo JSonToObject(String configInfo) {
		JSONObject jsonObject = JSONObject.fromObject(configInfo);
		CpplintConfigInfo configObj = 
				(CpplintConfigInfo)JSONObject.toBean(jsonObject,CpplintConfigInfo.class);
		return configObj;
	}
}
