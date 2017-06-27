package com.ondrejhrusovsky.server;

public class RequestContingentDetailsObject {
	public String requestId;
	public int routeIdx;
	
	public RequestContingentDetailsObject() {
    }
	
	public RequestContingentDetailsObject(String requestId, int routeIdx) {
        super();
        
        this.requestId = requestId;
        this.routeIdx = routeIdx;
    }
}
