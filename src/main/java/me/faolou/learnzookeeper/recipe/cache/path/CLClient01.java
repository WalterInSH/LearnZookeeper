package me.faolou.learnzookeeper.recipe.cache.path;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.Random;

public class CLClient01 {
    public static final String C_PATH_SUB = ChildrenListener.C_PATH + "/dog";

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String zookeeperConnectionString = "127.0.0.1:2181";
                    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
                    CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
                    client.start();

                    Random random = new Random();
                    Thread.sleep(1000 * random.nextInt(3));

                    Stat stat = client.checkExists().forPath(C_PATH_SUB);
                    if(stat == null){
                        client.create().withMode(CreateMode.EPHEMERAL).forPath(C_PATH_SUB, "dogData".getBytes(ChildrenListener.CHARSET));
                    }

                    Thread.sleep(1000 * random.nextInt(3));
                    client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
