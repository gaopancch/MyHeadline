package com.lxsj.myheadline.helpclass;

import com.letvugc.component.core.common.request.LetvGetRequest;
import com.letvugc.component.core.http.policy.HttpRequestPolicy;

public class DataGetRequest extends LetvGetRequest {
	private String index=null;
	
	public void setParam(String param){
		index=param;
	}
	
	@Override
	public Object parser(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	public HttpRequestPolicy getRequestPolicy() {
		return new HttpRequestPolicy(5000,1,10000);
	}

	@Override
	public String getUrl() throws Exception {
		// TODO Auto-generated method stub
		return ServerConstants.BASE_URL+index;
	}

}
