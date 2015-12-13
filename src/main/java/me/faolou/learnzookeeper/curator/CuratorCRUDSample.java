package me.faolou.learnzookeeper.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.data.Stat;

import java.io.*;
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
        System.out.println("version:" + stat.getVersion());
        System.out.println("Aversion:" + stat.getAversion());
        System.out.println("Cversion:" + stat.getCversion());
        System.out.println("pxid:" + stat.getPzxid());
        System.out.println("cxid:" + stat.getCzxid());
        System.out.println("mxid:" + stat.getMzxid());
        System.out.println("ctime:" + stat.getCtime());
        System.out.println("mtime:" + stat.getMtime());
        System.out.println("childs:" + stat.getNumChildren());

        //写入值看看
        DataOutput stringWriter = new ObjectOutputStream(new ByteArrayOutputStream(1));
        stat.write(stringWriter);

        System.out.println(new String(client.getData().forPath("/CuratorNode")));

        //创建子节点
        client.create().forPath("/CuratorNode/ChildNode", "curator child".getBytes());

        //获取子节点
        List<String> children = client.getChildren().forPath("/CuratorNode");
        System.out.println("Children are : " + children);

        //修改子节点数据
        client.create().creatingParentsIfNeeded().forPath("/CuratorNode/ChildNode/1/1", "curator little child".getBytes());
        System.out.println("createParent:" + new String(client.getData().forPath("/CuratorNode/ChildNode/1/1")));

        //修改主节点数据
        client.setData().forPath("/CuratorNode", "fuck".getBytes());

        //判断是否创建成功(存在性)
        stat = client.checkExists().forPath("/CuratorNode");
        System.out.println("version:" + stat.getVersion());
        System.out.println("Aversion:" + stat.getAversion());
        System.out.println("Cversion:" + stat.getCversion());
        System.out.println("pxid:" + stat.getPzxid());
        System.out.println("cxid:" + stat.getCzxid());
        System.out.println("mxid:" + stat.getMzxid());
        System.out.println("ctime:" + stat.getCtime());
        System.out.println("mtime:" + stat.getMtime());
        System.out.println("childs:" + stat.getNumChildren());

        byte[] a = new byte[1];
        DataInput input = new DataInputStream(new ByteArrayInputStream(a));
        stat.readFields(input);
        System.out.println(a);

        client.delete().forPath("/CuratorNode/ChildNode");
        client.delete().forPath("/CuratorNode");
        client.close();
        server.stop();
    }
}
