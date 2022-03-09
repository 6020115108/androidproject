package org.ayo.oss;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by cowthan on 2018/7/26.
 *
 * 一个按生命周期管理元素的仓库
 *
 * 元素可以是一组callback，每个callback绑定一个tag，可根据tag添加和删除
 *
 *
 */

public class MyLifeCircleRepository<T> {

    public interface Function<T>{
        void handle(T t);
    }

    private LinkedHashMap<Object, T> elements = new LinkedHashMap<>(30, 0.8f, false);

    public MyLifeCircleRepository<T> add(Object tag, T t){
        elements.put(tag, t);
        return this;
    }

    public MyLifeCircleRepository<T> remove(Object tag){
        elements.remove(tag);
        return this;
    }

    public void walk(Function<T> function){
        for (Map.Entry<Object, T> entry: elements.entrySet()){
            function.handle(entry.getValue());
        }
    }

}
