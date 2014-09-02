package me.faolou.learnzookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.data.Stat;

import java.util.List;

/**
 * @author Walter Zhang
 */
public class CuratorCRUDSample {

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        TestingServer server = new TestingServer();
        CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), retryPolicy);
        client.start();

        //创建节点
        client.create().forPath("/CuratorNode", "test curator".getBytes());
        //判断是否创建成功(存在性)
        Stat stat = client.checkExists().forPath("/CuratorNode");
        System.out.println("CuratorNode " + stat != null ? "exists" : "not exists");
        System.out.println(new String(client.getData().forPath("/CuratorNode")));

        //创建子节点
        client.create().forPath("/CuratorNode/ChildNode","curator child".getBytes());

        //获取子节点
        List<String> children = client.getChildren().forPath("/CuratorNode");
        System.out.println("Children are : " + children);

        //修改子节点数据
        client.setData().forPath("/CuratorNode/ChildNode", "curator little child".getBytes());
        System.out.println(new String(client.getData().forPath("/CuratorNode/ChildNode")));

        client.delete().forPath("/CuratorNode/ChildNode");
        client.delete().forPath("/CuratorNode");
        client.close();
        server.stop();
    }
}
