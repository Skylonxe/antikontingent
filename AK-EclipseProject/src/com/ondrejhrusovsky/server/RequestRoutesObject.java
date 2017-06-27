package com.ondrejhrusovsky.server;

public class RequestRoutesObject {
	public String requestId;
	
	public String from;
	public String to;
	public String through;
	public String date;
	public String time;
	
	public RequestRoutesObject() {
    }
	
	public RequestRoutesObject(String requestId, String from, String to, String through, String date, String time) {
        super();
        
        this.requestId = requestId;
        this.from = from;
        this.to = to;
        this.through = through;
        this.date = date;
        this.time = time;
    }
	
	public String toString()
	{
		return requestId + " " + from + " " + through + " " + to + " " + date + " " + time;
	}
}


