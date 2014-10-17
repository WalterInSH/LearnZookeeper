package me.faolou.learnzookeeper.curator.realtest;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import static java.lang.Thread.sleep;

/**
 *
 *
 * @author jack.zhang
 * @since 2014/10/16
 */
public class CuratorCreator {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
        client.start();

//        client.create().forPath("/TestWatcherNode");

        client.create().forPath("/TestWatcherNode/3");
//        client.create().forPath("/TestWatcherNode/1", "fuck".getBytes());

        client.setData().forPath("/TestWatcherNode/1", "fuck1".getBytes());

        byte[] bytes = client.getData().forPath("/TestWatcherNode/1");
        System.out.println(new String(bytes));

//        client.create().creatingParentsIfNeeded().inBackground().forPath("/TestListenerNode/1");
//
//        client.delete().forPath("/TestListenerNode/1");
//        client.delete().inBackground().forPath("/TestListenerNode2");
        client.close();

    }
}
