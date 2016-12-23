package io.bsoa.rpc.rpc;

import java.util.List;

/**
 * Created by zhanggeng on 2016/12/22.
 */
public class HelloServiceImpl implements HelloService {
    public String sayHello(String name, int age) {
        return "hello" + name;
    }

    public TestObj getObj(TestObj obj, String s) {
        obj.setName(obj.getName() + " server");
        return obj;
    }

    public List<TestObj> getList(List<TestObj> obj, String s) {
        return obj;
    }
}
