package me.faolou.learnzookeeper.recipe.cache.node;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class NodeListener {
    public static final String C_PATH = "/TestNode";
    public static final String CHARSET = "UTF-8";

    public static void main(String[] args) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        String zookeeperConnectionString = "127.0.0.1:2181";
                        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
                        CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
                        client.start();

                        final NodeCache nodeCache = new NodeCache(client, C_PATH);
                        nodeCache.getListenable().addListener(new NodeCacheListener() {
                            @Override
                            public void nodeChanged() throws Exception {
                                System.out.println("================== catch node data change ==================");
                                ChildData childData = nodeCache.getCurrentData();
                                if(childData == null){
                                    System.out.println("===delete, path=" + C_PATH + ", childData=" + childData);
                                }else{
                                    System.out.println("===update or add, path=" + C_PATH + ", childData=" + new String(childData.getData(), CHARSET));
                                }
                            }
                        });
                        nodeCache.start();

                        Thread.sleep(Integer.MAX_VALUE);
                        client.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}