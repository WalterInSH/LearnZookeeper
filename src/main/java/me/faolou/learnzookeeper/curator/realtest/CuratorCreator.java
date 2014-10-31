package me.faolou.learnzookeeper.curator.realtest;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

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

        client.delete().inBackground().forPath("/TestWatcherNode/3");
        client.delete().inBackground().forPath("/TestWatcherNode/1");

        client.create().creatingParentsIfNeeded().forPath("/TestWatcherNode/3", null); //创建的时候不设置任何值, 保存的data是client local ip

        byte[] bytes1 = client.getData().forPath("/TestWatcherNode/3");

        System.out.println(bytes1);



        client.create().forPath("/TestWatcherNode/1", "fuck".getBytes());
        client.setData().forPath("/TestWatcherNode/1", "fuck1".getBytes());

        byte[] bytes = client.getData().forPath("/TestWatcherNode/1");
        System.out.println(new String(bytes));


        client.delete().forPath("/TestWatcherNode/3");
        client.delete().forPath("/TestWatcherNode/1");
        client.delete().forPath("/TestWatcherNode");

        client.close();

    }
}
