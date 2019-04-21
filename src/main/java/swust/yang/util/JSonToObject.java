package swust.yang.util;

import net.sf.json.JSONObject;
import swust.yang.entity.CpplintConfigInfo;

public class JSonToObject {
	
	public static CpplintConfigInfo JSonStrToObject(String configInfo) {
	      JSONObject jsonObject = JSONObject.fromObject(configInfo);
	      CpplintConfigInfo configObj = 
	    		  (CpplintConfigInfo)JSONObject.toBean(jsonObject,CpplintConfigInfo.class);
	      return configObj;
	}

}
