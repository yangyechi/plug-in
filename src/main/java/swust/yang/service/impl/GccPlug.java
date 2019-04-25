package swust.yang.service.impl;

import java.io.File;
import java.util.List;

import swust.yang.entity.PluginInfo;
import swust.yang.entity.ResultMsgOfGcc;
import swust.yang.service.IIDEPlug;

public class GccPlug implements IIDEPlug {

	@Override
	public ResultMsgOfGcc singleExecute(List<String> testCase,long timeout,String file_path, String log_dir){
		// 检查传入的file_path是否存在
		File task_path = new File(file_path);
		if (!task_path.exists() || task_path.isDirectory()) {
			System.err.println("作业路径为 :" + file_path);
			
		}

		// 检查传入的log_dir目录是否存在
		File log_file = new File(log_dir);
		if (!log_file.exists()) {
			System.err.println("日志目录为 :" + log_dir);
			
		}
		if (!log_file.isDirectory()) {
			System.err.println("日志目录为 :" + log_dir);
			
		}

		// 获取传入待执行作业文件名
		String task_suffix = file_path.substring(file_path.lastIndexOf('.'));
		if (!task_suffix.equals(".c") && !task_suffix.equals(".cpp")) {
			System.err.println("待检查的作业为 :" + file_path);
			
		}
	    //获取文件目录路径
	    String path = file_path.substring(file_path.indexOf('\\') + 1,file_path.lastIndexOf('\\'));
	    
	    //获取学生信息
	    String studentInfo = file_path.substring(file_path.lastIndexOf('\\') + 1,file_path.lastIndexOf('.'));
		
	    //获取日志名
	    String logName = log_dir + "\\" + studentInfo + ".log";
	    
	    //生成编译命令
	    
	    //编译
	    
	    //运行
	    
	    return null;
	}

	@Override
	public List<ResultMsgOfGcc> batchExecute(List<String> testCase, long timeout, String src_dir, String log_dir) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginInfo getPluginInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
