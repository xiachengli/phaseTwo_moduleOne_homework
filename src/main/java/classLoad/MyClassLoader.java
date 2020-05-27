package classLoad;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 自定义类加载器，加载指定目录下的class文件
 */
public class MyClassLoader extends ClassLoader{

    //加载的Class
    private Class cls;
    //指定的目录
    private String rootDir;

    public MyClassLoader(String rootDir){
        this.rootDir = rootDir;
    }

    public Class<?> getClassByName(String name) {
        //查看已加载的类
        Class result = findLoadedClass(name);
        if (null == result) {
            //自行加载
            byte[] classData = getClassData(name);
            result = defineClass(name,classData,0,classData.length);
        }
        return result;
    }

    /**
     * IO流读取字节码信息
     * @param name
     * @return
     */
    private byte[] getClassData(String name) {
        String path = rootDir+name.replace(".","/")+".class";
        InputStream inputStream = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try {
            inputStream = new FileInputStream(path);
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1){
                bos.write(buffer,0,len);
            }
            return bos.toByteArray();
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }finally {
            if (null != inputStream){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
