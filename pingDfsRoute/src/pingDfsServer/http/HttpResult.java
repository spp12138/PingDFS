package pingDfsServer.http;

import pingDfsServer.http.HttpMessageParser.Request;

public abstract class HttpResult {

	public abstract String result(Request httpRequest);
	
}
