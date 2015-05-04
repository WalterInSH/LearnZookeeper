package me.faolou.learnzookeeper.recipe.cache.tree;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * ��ʼ��֮ǰ, �Ὣ������㼰�������ӽڵ����ݶ�����, �������ݳ�ʼ��
 * ״̬����NODE_ADD
 */
public class TreeListener {
    public static final String C_PATH = "/TestTree";
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

                        final TreeCache treeCache = new TreeCache(client, C_PATH);
                        treeCache.getListenable().addListener(new TreeCacheListener() {
                            @Override
                            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                                System.out.println("================== catch tree change ==================");
                                if(event.getData() == null){
                                    System.out.println("===init," + event.getType());
                                    return;
                                }

                                if(event.getData().getData() == null){
                                    System.out.println("===delete," + event.getType() + "," + event.getData().getPath());
                                }else{
                                    System.out.println("===update or add," + event.getType() + "," + event.getData().getPath() + "," + new String(event.getData().getData(), TreeListener.CHARSET));
                                }
                            }
                        });
                        treeCache.start();

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
