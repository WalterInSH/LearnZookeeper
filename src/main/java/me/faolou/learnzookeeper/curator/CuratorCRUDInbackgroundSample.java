package me.faolou.learnzookeeper.curator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.data.Stat;

import static java.lang.Thread.sleep;

/**
 * 1. getData如果不存在ZNode, 则抛KeeperException$NoNodeException
 * 使用inbackground则不抛异常
 * <p/>
 * 2. create如果创建不存在的父节点下的子节点, 也会抛异常,
 * 使用inbackground则不抛,
 * 使用creatingParentsIfNeeded则可以成功创建
 * <p/>
 * 3. 尝试获取inbackground crete或者set的数据, 则会失败
 *
 * @author jack.zhang
 */
public class CuratorCRUDInbackgroundSample {

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        TestingServer server = new TestingServer();

        //使用local
//        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", retryPolicy);
        CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), retryPolicy);
        client.start();

       /*测试一下 getdata from nonexist node */
        try {
            byte[] bytes = client.getData().forPath("/nonexist"); //直接异常
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        byte[] bytes = client.getData().inBackground().forPath("/nonexist");//不会报异常
        System.out.println(ArrayUtils.isEmpty(bytes));


        /*测试一下create结点*/
        String s = client.create().creatingParentsIfNeeded().forPath("/node/1", "test".getBytes()); //成功创建
        System.out.println(s);

        /*多级目录创建不支持*/
        s = client.create().creatingParentsIfNeeded().forPath("/node/2/2", "test".getBytes()); //无法成功
        System.out.println(s);

        Stat stat1 = client.checkExists().forPath("/node/2/2");
        System.out.println(stat1==null);

        /*测试一下 inbackground的是否可以马上获取data*/
        byte[] bytes1 = client.getData().inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                System.out.println("fuck");

            }
        }).forPath("/node/1");

        client.setData().inBackground().forPath("/node", "fuck".getBytes());
        /*后台线程利用future做的, sleep一会他就做完了*/
        sleep(500);
        byte[] bytes2 = client.getData().forPath("/node");
        System.out.println(new String(bytes2) + new String(bytes1));

        /*exist没啥好说的*/
        Stat stat = client.checkExists().forPath("/node/1");
        System.out.println(stat);

        client.delete().guaranteed().forPath("/node/1");

        stat = client.checkExists().forPath("/node/1");
        System.out.println(stat);

        client.close();
        server.stop();
    }
}
