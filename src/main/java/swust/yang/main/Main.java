package swust.yang.main;

import java.io.IOException;
import java.util.List;

import swust.yang.entity.ResultMsg;
import swust.yang.service.IPlug;
import swust.yang.service.impl.CodeRecheckPlug;

public class Main {

	public static void main(String[] args) throws IOException {
		IPlug recheck = new CodeRecheckPlug();
		String configInfo = "{\r\n" + 
				"	\"thresholdValue\": \"80\"\r\n" + 
				"}";
		String toolPath = "E:\\sim";
		String srcDir = "E:\\simTest";
		String logDir = "E:\\simLog";
		List<ResultMsg> list = recheck.batchExecute(configInfo, toolPath, srcDir, logDir);
		for(ResultMsg item : list) {
			System.out.println(item.getStudentInfor());
			System.out.println(item.getValue());
		}
	}

}
