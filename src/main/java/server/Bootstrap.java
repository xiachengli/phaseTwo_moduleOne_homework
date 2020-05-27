package server;

import container.StandardHost;
import mapper.Mapper;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Minicat的主类
 */
public class Bootstrap {

    Mapper mapper = new Mapper();

    int port = 8080;

    /**
     * Minicat启动需要初始化展开的一些操作
     */
    public void start() throws Exception {

        // 加载解析相关的配置，server.xml
        loadServlet();


        // 定义一个线程池
        int corePoolSize = 10;
        int maximumPoolSize =50;
        long keepAliveTime = 100L;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();


        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler
        );

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("=====>>>Minicat start on port：" + port);

        System.out.println("=========>>>>>>使用线程池进行多线程改造");
        /*
            多线程改造（使用线程池）
         */
        while(true) {

            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket,mapper);
            //requestProcessor.start();
            threadPoolExecutor.execute(requestProcessor);
        }



    }

    /**
     * 加载解析server.xml，初始化Servlet
     */
    private void loadServlet() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("server.xml");
        SAXReader saxReader = new SAXReader();

        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();

            List<Element> serviceNodes = rootElement.selectNodes("//Service");
            for (int i = 0; i < serviceNodes.size(); i++) {
                Element element =  serviceNodes.get(i);
                //Connector连接器(一个service可对应多个connector，但此处仅仅考虑一个connector)
                Element connectorElement = (Element) element.selectSingleNode("Connector");
                //获取端口,默认8080
                String port = connectorElement.attributeValue("port") == null ? "8080" : connectorElement.attributeValue("port");

                //Engine servlet引擎
                Element engineElement = (Element) element.selectSingleNode("Engine");
                //Host
                List<Element> hostElements = (List<Element>)engineElement.selectNodes("//Host");
                for (Element e : hostElements) {
                    //name
                    String name = e.attributeValue("name");
                    //appBase
                    String appBase = e.attributeValue("appBase");

                    String key = name + ":" + port;
                    mapper.hostMap.put(key,new StandardHost(name,appBase));
                }
            }



        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }


    /**
     * Minicat 的程序启动入口
     * @param args
     */
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            // 启动Minicat
            bootstrap.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
