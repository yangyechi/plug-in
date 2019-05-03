package swust.yang.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import net.sf.json.JSONObject;
import swust.yang.entity.CodeRecheckConfigInfo;
import swust.yang.entity.PluginInfo;
import swust.yang.entity.ResultMsg;
import swust.yang.service.IPlug;
import swust.yang.util.Resource;
import swust.yang.util.RunTask;
import swust.yang.util.SystemProperty;

public class CodeRecheckPlug implements IPlug {

	@Override
	public ResultMsg singleExecute(String configInfo, String toolPath, String filePath, String logDir) {
		ResultMsg msg = new ResultMsg();
		msg.setCode(-1);
		msg.setMessage("只能进行批量查重不能进行单个查重！所以该函数请不要调用！");
		return msg;
	}

	@Override
	public List<ResultMsg> batchExecute(String configInfo, String toolPath, String srcDir, String logDir) {
		// 执行结果集合
		List<ResultMsg> msgs = null;
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
		
		//传入的配置信息转为对象
		CodeRecheckConfigInfo configObj = JSonToObject(configInfo);
		
		//当前系统环境下文件分隔符
		String separator = SystemProperty.getFileSeparator();
		
		//查重阈值
		String thresholdValue = configObj.getThresholdValue().toString();
		
		//日志路径
		String time = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
					  .format(new Date(System.currentTimeMillis()));
		String logName = srcDir.substring(srcDir.lastIndexOf(separator) + 1) + 
						 "_" + time + "(" + System.currentTimeMillis() + ")" + ".log";
		String logPath = logDir + separator + logName;
		
		if(SystemProperty.getOSName().equals("Linux")) {
			//工具路径
			toolPath = toolPath + separator + "./sim_c";
		} else {
			//工具路径
			toolPath = toolPath + separator + "sim_c.exe";
		}
		
		
		//待执行的命令
		String command = toolPath + " " + "-a" + " " + "-t" + " " + thresholdValue + " " + "-p" + " " 
						 + "-T" + " " + "-o" + " " + logPath + " " + "-R" + " " + srcDir;
		System.out.println(command);
				
		//进行查重
		try {
			RunTask.runCommand(command);
		} catch (IOException e) {
			System.err.println("作业执行失败！错误原因可能是：");
			System.err.println("1、执行命令错误(" + command + ");");
			System.err.println("2、传入的工具路径不是所需工具的根目录.");
			e.printStackTrace();
			return null;
		}
		
		//分析日志。得出结果
		try {
			msgs = analysisLog(logPath);
		} catch (FileNotFoundException e) {
			System.err.println("日志不存在：" + logPath);
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 创建存放html图表的目录结构
		String htmlDir = srcDir + separator + "recheckHtml";
		String jsDir = htmlDir + separator + "js";
		String imgDir = htmlDir + separator + "img";
		String cssDir = htmlDir + separator + "css";
		new File(htmlDir).mkdir();
		new File(jsDir).mkdir();
		new File(imgDir).mkdir();
		new File(cssDir).mkdir();
		
		//待查重的作业数量计算
		String[] fileList = srcFile.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".cpp") || name.endsWith(".c")) {
					return true;
				}
				return false;
			}
		});
		int taskNum = fileList.length;
		
		//查重不通过的作业数量
		int failTaskNum = msgs.size();
		
		//查重通过的作业数量
		int passTaskNum = taskNum - failTaskNum;
		
		//生成查重结果图表
		String errorType = "var errorType = ['查重通过','查重未通过']";
		
		StringBuilder errorMap = new StringBuilder();
		errorMap.append("var errorMap = [");
		errorMap.append("{value:" + passTaskNum + ", name:'" + "查重通过" + "'},");
		errorMap.append("{value:" + failTaskNum + ", name:'" + "查重未通过" + "'}]");
		
		//数据合并
		String data = errorType.toString() + "\n" + errorMap.toString();
		
		//源文件路径
		String srcPath = null;
		//目的文件路径
		String destPath = null;	
		
		try {
			//把数据写入指定路径的array.js文件中
			destPath = jsDir + separator + "array.js";
			Resource.writeToFile(data, destPath);
		} catch (IOException e) {
			System.err.println("目的文件路径不存在：" + destPath);
			e.printStackTrace();
		}
		
		// 把jar包下面的html和js文件写入指定目录
		// 复制js文件到指定目录
		try {
			srcPath = System.getProperty("user.dir") + "\\src\\main\\resources\\echarts.min.js";
			// srcPath = "/resources/echarts.min.js"
			destPath = jsDir + separator + "echarts.min.js";
			Resource.writeFromJar(srcPath, destPath, "produce");
			// Resource.writeFromJar(srcPath, destPath, "jar");
		} catch (IOException e) {
			System.err.println("目的文件路径不存在：" + destPath);
			e.printStackTrace();
		}
		
		// 复制html文件到指定目录
		try {
			// pie.html
			srcPath = System.getProperty("user.dir") + "\\src\\main\\resources\\pie_recheck.html";
			// srcPath = "/resources/pie_recheck.html"
			destPath = htmlDir + separator + "pie.html";
			Resource.writeFromJar(srcPath, destPath, "produce");
			// Resource.writeFromJar(srcPath, destPath, "jar");
		} catch (IOException e) {
			System.err.println("目的文件路径不存在：" + destPath);
			e.printStackTrace();
		}
		
		return msgs;
	}

	@Override
	public String checkConfigInfo(String configInfo) {
		// 将前端返回的包含配置信息的JSon字符串转换为对象
		CodeRecheckConfigInfo configObj = null;
		try {
			configObj = JSonToObject(configInfo);
		} catch (net.sf.json.JSONException e) {
			e.printStackTrace();
			return "JSon格式错误！";
		}
		
		Integer thresholdValue = configObj.getThresholdValue();
		if(thresholdValue == null) {
			return "阈值不能为空！";
		} else {
			if(thresholdValue < 20 || thresholdValue > 100) {
				return "阈值必须大于等于20且小于等于100";
			}
			if(thresholdValue == 0) {
				return "阈值(20-100)且不能为特殊字符";
			}
		}
		return "OK";
	}

	@Override
	public PluginInfo getPluginInfo() {
		PluginInfo codeRecheckInfo = new PluginInfo();
		codeRecheckInfo.setName("coderecheck-plug-1.0.jar");
		codeRecheckInfo.setAuthor("yangyechi");
		codeRecheckInfo.setDescription("Code recheck plug-in");
		codeRecheckInfo.setVersion("1.0");
		codeRecheckInfo.setClassName("swust.yang.service.impl.CodeRecheckPlug");
		return codeRecheckInfo;
	}

	@Override
	public String getHtml(String preSetting) {
		String html = "";
		if(preSetting == null) {
			html = "<div id = \"codeRecheck\">\r\n" + 
					"			阈值\r\n" + 
					"			<input type=\"number\" name=\"thresholdValue\" value=\"\" min=\"20\" max=\"100\"  placeholder=\"20-100\" />\r\n" + 
					"			<font size=\"2\">/*查重的标准。相似度(百分数)小于阈值的作业通过查重检查，反之则不通过*/</font>\r\n" + 
					"</div>";
		} else {
			CodeRecheckConfigInfo config = JSonToObject(preSetting);
			html = "<div id = \"codeRecheck\">\r\n" + 
					"			阈值\r\n" + 
					"			<input type=\"number\" name=\"thresholdValue\" value=" + '"' + config.getThresholdValue() + '"' + " min=\"20\" max=\"100\"  placeholder=\"20-100\" />\r\n" + 
					"			<font size=\"2\">/*查重的标准。相似度(百分数)小于阈值的作业通过查重检查，反之则不通过*/</font>\r\n" + 
					"</div>";
		}
		return html;
	}

	private CodeRecheckConfigInfo JSonToObject(String configInfo) {
		JSONObject jsonObject = JSONObject.fromObject(configInfo);
		CodeRecheckConfigInfo configObj = 
				(CodeRecheckConfigInfo)JSONObject.toBean(jsonObject,CodeRecheckConfigInfo.class);
		return configObj;
	}
	
	private List<ResultMsg> analysisLog(String logPath) throws FileNotFoundException, IOException {
		List<ResultMsg> msgs = new ArrayList<ResultMsg>();
		int i = 0;
		try (BufferedReader read = new BufferedReader(new FileReader(logPath))){
  					String readLine = null;
  					while((readLine = read.readLine()) != null) {
  						i++;
  						if(i > 2) {
  							//学生信息
  							String stu = readLine.substring(readLine.indexOf("/") + 1, readLine.indexOf(".c"));
  							//相识度
  							String similarity = readLine.substring(readLine.indexOf("for ") + 4, 
  																	readLine.indexOf("%") - 1) + "%";
  							//一条检查结果
  							ResultMsg msg = new ResultMsg();
  							msg.setStudentInfor(stu);
  							msg.setValue(similarity);
  							msg.setCode(0);
  							msg.setMessage("OK");
  							//把结果添加进结果集
  							msgs.add(msg);
  						}
  					}
  					
  			}
		return msgs;
	}
}
