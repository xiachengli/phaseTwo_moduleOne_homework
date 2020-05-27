package server;

import container.StandardContext;
import container.StandardHost;
import container.StandardWrapper;
import mapper.Mapper;

import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

public class RequestProcessor extends Thread {

    private Socket socket;
    private Mapper mapper;

    public RequestProcessor(Socket socket, Mapper mapper) {
        this.socket = socket;
        this.mapper = mapper;
    }

    @Override
    public void run() {
        try{
            InputStream inputStream = socket.getInputStream();

            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());

            String key = request.getUrl();
            //String key = name + ":" + port
            StandardHost host = mapper.hostMap.get("localhost:8080");
            //String key = /contextName;
            String[] url = request.getUrl().split("/");
            StandardContext context = host.contextMap.get("/"+url[1]);
            //String key = /uri
            StandardWrapper wrapper = context.standardWrapperMap.get("/"+url[2]);

            // 静态资源处理
            if(wrapper == null) {
                response.outputHtml(request.getUrl());
            }else{
                // 动态资源servlet请求
                HttpServlet httpServlet = wrapper.servlet;
                httpServlet.service(request,response);
            }

            socket.close();

        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
