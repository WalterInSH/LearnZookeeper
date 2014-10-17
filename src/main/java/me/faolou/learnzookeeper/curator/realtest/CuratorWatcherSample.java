package me.faolou.learnzookeeper.curator.realtest;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.WatchedEvent;

import static java.lang.Thread.sleep;

/**
 * watcher 封装了zk原本的watcher 可以跨进程使用, 但是注意, 无法在 inbackground的情况下触发watcher
 * @author Walter Zhang
 */
public class CuratorWatcherSample {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        final CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
        client.start();

        final CuratorWatcher watcher = new CuratorWatcher() {
            private int count = 0;

            @Override
            public void process(WatchedEvent event) throws Exception {
                System.out.println(count++ + ":" + " " +
                        event.getPath() + " " +
                        event.getType() + " " +
                        event.getState());
                client.checkExists().usingWatcher(this).forPath("/TestWatcherNode/1");
                client.checkExists().usingWatcher(this).forPath("/TestWatcherNode/3");
            }
        };

        client.checkExists().usingWatcher(watcher).forPath("/TestWatcherNode/1");
        client.checkExists().usingWatcher(watcher).forPath("/TestWatcherNode/3");

        sleep(50000);

    }
}
