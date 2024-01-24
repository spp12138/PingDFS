package pingDfsServer.server;

public class FileServer {
	
	{
		System.out.println("FileServer被调用 1");
	}
	
	static {
		System.out.println("FileServer被加载");
	}
	
	public FileServer() {
		System.out.println("FileServer被调用 2");
	}
	// http://127.0.0.1:8999/FileServer/exec?a=77777&b=2&c=3
	public String exec(String a, String b , String c ) {
		return a+b+c;
	}

	
}
