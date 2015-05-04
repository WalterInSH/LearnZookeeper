package me.faolou.learnzookeeper.recipe.cache.node;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class NLClientCreate {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String zookeeperConnectionString = "127.0.0.1:2181";
                    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
                    CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
                    client.start();

                    Stat stat = client.checkExists().forPath(NodeListener.C_PATH);
                    if (stat == null) {
                        client.create().withMode(CreateMode.PERSISTENT).forPath(NodeListener.C_PATH, "-1".getBytes(NodeListener.CHARSET));
                    }

                    client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
