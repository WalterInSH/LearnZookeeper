package me.faolou.learnzookeeper.basic;

import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.*;

/**
 * 演示 Node操作的API
 * Created by zhangkai on 8/3/14.
 */
public class CRUD {

    public static void main(String[] args) throws Exception {
        // 创建一个与服务器的连接
        TestingServer server = new TestingServer();
        ZooKeeper zk = new ZooKeeper(server.getConnectString(),
                3000, new Watcher() {
            public void process(WatchedEvent event) {
            }
        });
        // 创建一个目录节点,数据为'RootData'(另外的两个参数请参看ACL和Ephemeral/Sequence Nodes)
        zk.create("/testRootPath", "RootData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT);
        // 取出子目录节点列表
        System.out.println(new String(zk.getData("/testRootPath", false, null)));

        // 创建一个子目录节点
        zk.create("/testRootPath/testChildPathA", "ChildDataA".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        zk.create("/testRootPath/testChildPathB", "ChildDataB".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        System.out.println(zk.getChildren("/testRootPath", true));

        // 修改子目录节点数据,修改时需要制定修改的数据版本,-1表示任何版本
        zk.setData("/testRootPath/testChildPathA", "modifyChildDataA".getBytes(), -1);

        //判断一个节点是否存在,如果存在返回节点状态,否则返回null
        System.out.println("目录节点状态：[" + zk.exists("/testRootPath", true) + "]");


        // 删除nodes,需要先删除child node
        zk.delete("/testRootPath/testChildPathA", -1);
        zk.delete("/testRootPath/testChildPathB", -1);
        zk.delete("/testRootPath", -1);
        // 关闭连接
        zk.close();

        server.stop();
    }
}
