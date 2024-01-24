package pingDfsServer.http;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import pingDfsServer.http.HttpMessageParser.Request;

public class HttpTask implements Runnable {
    private Socket socket;

    public HttpTask(Socket socket) throws SocketException {
    	socket.setSoTimeout(3000*10);
        this.socket = socket;
    }

    @Override
    public void run() {

        if (socket == null) {
            throw new IllegalArgumentException("socket不允许为空，请初始化socket！");
        }

        try {
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter out = new PrintWriter(outputStream);

            Request httpRequest = HttpMessageParser.parse2request(socket.getInputStream());
            try {
                // 根据请求结果进行响应
            	String invoke = invoke(httpRequest);
                String httpRes = HttpMessageParser.buildResponse(httpRequest, invoke);
                out.print(httpRes);
            } catch (Exception e) {
                String httpRes = HttpMessageParser.buildResponse(httpRequest, e.toString());
                out.print(httpRes);
                e.printStackTrace();
            }
            out.flush();
        } catch (URISyntaxException e1) {
			e1.printStackTrace();
			System.err.println("装载请求参数失败！");
		} catch (IOException e1) {
			e1.printStackTrace();
			System.err.println("获取Socket输入流失败");
		} finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
	public String invoke(Request httpRequest) throws Exception {
    	URI uri = httpRequest.getUri();
    	String uriPath = uri.getPath();
		if(StringUtils.isBlank(uriPath) || uriPath.equals("/")) {
			throw new Exception("请求失败，禁止请求根目录！");
		}
		//解析URI 获取请求的 class和method
		String p1 = uriPath.substring(1);
		String className = "pingDfsServer.server."+p1.substring(0,p1.indexOf("/"));
		String p2 = p1.substring(p1.indexOf("/")+1);
		String methodName = "";
		if(p2.indexOf("/") > -1) {
			methodName = p2.substring(0,p2.indexOf("/"));
		}else {
			methodName = p2;
		}
		
		Class<?> clz = Class.forName(className);
		if(clz == null) 
			throw new Exception("未找到Class ： " + className);
		
		Method[] methods = clz.getMethods();
		for (Method method : methods) {
		    if (method.getName().equals(methodName)) {
//		        System.out.println("匹配成功，开始执行反射方法，methodName:" + method.getName());
		    	try {
		    		List<Object> params = new ArrayList<>();
		    		String query = uri.getQuery();
		    		if(StringUtils.isNotBlank(query)) {
		    			String[] split = query.split("&");
		    			for (int i = 0; i < split.length; i++) {
		    				String[] param = split[i].split("=");
		    				if(param.length < 2) {
		    					params.add("");
		    				}else {
		    					params.add(param[1]);
		    				}
						}
		    		}
		    		return String.valueOf(method.invoke(clz.newInstance(),params.toArray()));
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("请求参数匹配失败"+e.getMessage());
				}
		    }
		}
		throw new Exception("未找到Method ： " + methodName);
	}
	
	public  void getPath() throws IOException {
		String property = System.getProperty("java.class.path");
		File f = new File(property);
		String parent = f.getParent();
		System.out.println("2 >>> "+parent);
	}
    
}

