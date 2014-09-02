
package me.faolou.learnzookeeper.basic;
import org.apache.curator.test.TestingServer;
import org.apache.zookeeper.*;

/**
 *
 * Created by zhangkai on 8/10/14.
 */
public class WatcherSample {

    public static void main(String[]args) throws Exception{

        Watcher watcher = new Watcher() {
            private int count = 0;
            @Override
            public void process(WatchedEvent event) {
                System.out.println(count++ + ":" + " " +
                        event.getPath() + " " +
                        event.getType() + " " +
                        event.getState());
            }
        };
        TestingServer server = new TestingServer();
        ZooKeeper zk = new ZooKeeper(server.getConnectString(),3000, watcher);

        // set a watcher
        zk.exists("/testNode",true);
        // 这一行执行后会有NodeCreated事件
        zk.create("/testNode", "cat".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        // 删除了这个节点,再创建一遍,发现没有事件. 因为Watcher是"一次性的"!之前24行加的watcher已经在26行触发
        zk.delete("/testNode",-1);
        zk.create("/testNode", "cat".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        //触发一次修改事件
        zk.exists("/testNode",true);
        zk.setData("/testNode", "dog".getBytes(), -1);

        //触发一次子节点事件,该watcher可以通过 exists("/testNode/child",true) 或者getChildren("/testNode",true)增加,
        //但是两者产生的事件不一样,一个是NodeCreated,后者是NodeChildrenChanged
        //该watcher不能通过exists("/testNode",true) 增加
        zk.getChildren("/testNode",true);
        zk.create("/testNode/child","small_dog".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        //触发一次删除事件
        zk.exists("/testNode/child",true);
        zk.delete("/testNode/child",-1);

        zk.delete("/testNode",-1);
        zk.close();
        server.stop();
    }
}
