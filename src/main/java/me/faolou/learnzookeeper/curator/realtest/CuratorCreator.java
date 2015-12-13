package me.faolou.learnzookeeper.curator.realtest;

import com.google.common.collect.Maps;
import me.faolou.learnzookeeper.curator.ByteUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

import java.util.Map;

/**
 * @author jack.zhang
 * @since 2014/10/16
 */
public class CuratorCreator {
    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
        client.start();

        Map<String, Integer> map = Maps.newHashMap();
        for (int i = 0; i < 10; i++) {
            map.put(String.valueOf(i), i);
        }

       //模拟创建根节点
        if (client.checkExists().forPath("/test_data/store") == null) {
            client.create().creatingParentsIfNeeded().forPath("/test_data/store", ByteUtils.toByteArray(map));
        }


        //每个根节点插入或者更新数据
        for (int i = 0; i < 10; i++) {
            createOrUpdateNode(client, i);
        }


        //模拟一次数据变化
        client.setData().forPath("/test_data/store", ByteUtils.toByteArray(map));
        for (int i = 0; i < 10; i++) {
            createOrUpdateNode(client, i);
        }

        client.close();

    }

    private static void createOrUpdateNode(CuratorFramework client, int i) throws Exception {
        String path = "/test_data/store/" + String.valueOf(i);
        Stat stat = client.checkExists().forPath(path);
        if (stat == null) {
            client.create().forPath(path, ByteUtils.toByteArray(String.valueOf(i)));
        } else {
            client.setData().forPath(path, ByteUtils.toByteArray(String.valueOf(i)));
        }
    }
}
