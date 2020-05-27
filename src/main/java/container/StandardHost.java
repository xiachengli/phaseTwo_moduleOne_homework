package container;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class StandardHost {

    //虚拟主机名称
    private String name;

    //webapps所在目录
    private String appBase;

    //部署的web应用上下文
    public Map<String,StandardContext> contextMap = new HashMap<>();

    public StandardHost(String name, String appBase) {
        this.name = name;
        this.appBase = appBase;

        //由host去加载web.xml
        loadWebXml(appBase);
    }

    /**
     * 加载部署的应用的web.xml文件
     * @param appBase
     */
    private void loadWebXml(String appBase) {
        File file = new File(appBase);
        if (file.exists()) {
            File[] list = file.listFiles();
            for (File temp : list) {
                //上下文名称
                String contextName = temp.getName();
                //递归获取web.xml的绝对路径
                File context = new File(temp.getAbsolutePath()+"\\web.xml");
                if (context.exists()) {
                    contextMap.put("/"+contextName,new StandardContext(appBase+"/"+contextName+"/",context));
                }
            }
        }

    }


}
