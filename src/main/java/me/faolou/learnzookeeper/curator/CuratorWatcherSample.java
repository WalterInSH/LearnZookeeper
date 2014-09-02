package me.faolou.learnzookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.WatchedEvent;

/**
 * @author Walter Zhang
 */
public class CuratorWatcherSample {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        TestingServer server = new TestingServer();
        CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), retryPolicy);
        client.start();

        CuratorWatcher watcher = new CuratorWatcher() {
            private int count = 0;

            @Override
            public void process(WatchedEvent event) throws Exception {
                System.out.println(count++ + ":" + " " +
                        event.getPath() + " " +
                        event.getType() + " " +
                        event.getState());
            }
        };

        client.checkExists().usingWatcher(watcher).forPath("/TestWatcherNode");
        client.create().forPath("/TestWatcherNode");

        client.delete().forPath("/TestWatcherNode");
        client.close();
        server.stop();
    }
}
