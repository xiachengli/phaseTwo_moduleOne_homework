package mapper;

import container.StandardHost;

import java.util.HashMap;
import java.util.Map;

public class Mapper {

    //虚拟主机host -> context -> wrapper
    public Map<String, StandardHost> hostMap = new HashMap<>();

}
