package me.faolou.learnzookeeper.recipe.cache.tree;

import org.apache.commons.lang3.RandomUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

public class TLClientUpdate {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String zookeeperConnectionString = "127.0.0.1:2181";
                    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
                    CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
                    client.start();

                    Stat stat = client.checkExists().forPath(TreeListener.C_PATH);
                    if (stat != null) {
                        client.setData().forPath(TreeListener.C_PATH, RandomUtils.nextBytes(10));
                    }

                    client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
