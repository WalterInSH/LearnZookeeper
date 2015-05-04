package me.faolou.learnzookeeper.recipe.cache.path;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.EnsurePath;

import java.util.List;

public class ChildrenListener {
    public static final String C_PATH = "/TestPath";
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

                        //ensure path of /test
                        new EnsurePath(C_PATH).ensure(client.getZookeeperClient());

                        final PathChildrenCache pathChildrenCache = new PathChildrenCache(client, C_PATH, true);
                        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
                            @Override
                            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                                System.out.println("================== catch children change ==================");
                                System.out.println("===" + event.getType() + "," + event.getData().getPath() + "," + event.getData().getData());
                                List<ChildData> childDataList = pathChildrenCache.getCurrentData();
                                if (childDataList != null && childDataList.size() > 0) {
                                    System.out.println("===all children as:");
                                    for (ChildData childData : childDataList) {
                                        System.out.println("==" + childData.getPath() + "," + new String(childData.getData(), "UTF-8"));
                                    }
                                }
                            }
                        });
                        pathChildrenCache.start();

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
