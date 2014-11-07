package me.faolou.learnzookeeper.curator.realtest;

import me.faolou.learnzookeeper.curator.ByteUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * watcher 封装了zk原本的watcher 可以跨进程使用, 但是注意, 无法在 inbackground的情况下触发watcher
 *
 * @author Walter Zhang
 */
public class CuratorWatcherSample {

    public static final String TEST_DATA_STORE = "/test_data/store";

    public static Map<String, Integer> integerMap;

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        final CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
        client.start();

        final CuratorWatcher childWatcher = new CuratorWatcher() {
            @Override
            public void process(WatchedEvent event) throws Exception {
                if (event.getType().equals(Watcher.Event.EventType.NodeDataChanged) || event.getType().equals(Watcher.Event.EventType.NodeCreated)) {
                    String toObject = (String) ByteUtils.toObject(client.getData().usingWatcher(this).forPath(event.getPath()));
                    System.out.println("will update:" + toObject);
                }
            }
        };

        Stat stat = client.checkExists().forPath(TEST_DATA_STORE);
        if (stat == null) {
            /*为空则设置watcher*/
            final CuratorWatcher initWatcher = new CuratorWatcher() {
                @Override
                public void process(WatchedEvent event) throws Exception {
                /*只要是结点创建或者改变的事件, 那么就去监听它的子节点*/
                    if (event.getType().equals(Watcher.Event.EventType.NodeDataChanged) || event.getType().equals(Watcher.Event.EventType.NodeCreated)) {
                        Map<String, Integer> maps = (Map<String, Integer>) ByteUtils.toObject(client.getData().forPath("/test_data/store"));
                        for (String id : maps.keySet()) {
                            client.checkExists().usingWatcher(childWatcher).forPath("/test_data/store/" + id);
                        }
                    }
                }
            };

            client.checkExists().usingWatcher(initWatcher).forPath(TEST_DATA_STORE);
        } else {
            integerMap = (Map<String, Integer>) ByteUtils.toObject(client.getData().forPath("/test_data/store"));
            for (String id : integerMap.keySet()) {
                if (client.checkExists().forPath("/test_data/store/" + id) != null) {
                    /*每次获得数据的时候, 都再次的设置watcher*/
                    String str = (String) ByteUtils.toObject(client.getData().usingWatcher(childWatcher).forPath("/test_data/store/" + id));
                    System.out.println("init: " + str); //这就是需要后期处理的子节点数据
                }
            }
        }

        sleep(5000000);
    }
}
