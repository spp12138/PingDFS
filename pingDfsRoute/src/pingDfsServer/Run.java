package pingDfsServer;

import java.io.IOException;

import pingDfsServer.http.BasicHttpServer;

public class Run {
	
	// bui~~ 启动！
	public static void main(String[] args) throws IOException {
		BasicHttpServer.startHttpServer();
	}

}
