package io.bsoa.rpc.rpc;

import java.util.List;

/**
 * Created by zhanggeng on 2016/12/22.
 */
public interface HelloService {

    public String sayHello(String name, int age);

    public TestObj getObj(TestObj obj, String s);

    public List<TestObj> getList(List<TestObj> obj, String s);

}
