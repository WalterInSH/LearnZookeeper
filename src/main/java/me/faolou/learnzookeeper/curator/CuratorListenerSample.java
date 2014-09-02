package me.faolou.learnzookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.api.UnhandledErrorListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.WatchedEvent;

/**
 * CuratorListener receives notifications about errors and background events
 *
 * UnhandledErrorListener
 *
 * @author Walter Zhang
 */
public class CuratorListenerSample {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        TestingServer server = new TestingServer();
        CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), retryPolicy);
        client.start();

        CuratorListener listener = new CuratorListener() {
            private int count = 0;

            @Override
            public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {
                System.out.println(count++ + ":" + " " +
                        event.getPath() + " " +
                        event.getType() + " " +
                        event.getStat());
            }
        };

        UnhandledErrorListener unhandledErrorListener = new UnhandledErrorListener() {
            @Override
            public void unhandledError(String message, Throwable e) {
                System.out.println(message);
                e.printStackTrace();
            }
        };

        client.getCuratorListenable().addListener(listener);
        client.getUnhandledErrorListenable().addListener(unhandledErrorListener);

        client.create().inBackground().forPath("/TestListenerNode");

        client.delete().forPath("/TestListenerNode");
        client.delete().inBackground().forPath("/TestListenerNode2");
        client.close();
        server.stop();
    }
}
