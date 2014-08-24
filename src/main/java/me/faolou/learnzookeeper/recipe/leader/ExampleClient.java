package me.faolou.learnzookeeper.recipe.leader;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.recipes.leader.LeaderSelector;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhangkai on 8/24/14.
 */
public class ExampleClient extends LeaderSelectorListenerAdapter implements Closeable {
    private final String name;
    private final LeaderSelector leaderSelector;
    private final AtomicInteger leaderCount = new AtomicInteger();

    public ExampleClient(CuratorFramework client, String path, String name) {
        this.name = name;

        // 参与leader选举的client必须使用相同path
        leaderSelector = new LeaderSelector(client, path, this);

        // requeue:当该client被选为leader后，参与下次leader的选举，大部分应用需要自动执行requeue
        leaderSelector.autoRequeue();
    }

    public void start() throws IOException {
        // the selection for this instance doesn't start until the leader selector is started
        // leader selection is done in the background so this call to leaderSelector.start() returns immediately
        leaderSelector.start();
    }

    @Override
    public void close() throws IOException {
        leaderSelector.close();
    }

    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
        // 当该client被选为leader，这个方法会被执行，当方法执行完毕，自动放弃领导权

        final int waitSeconds = (int) (5 * Math.random()) + 1;

        System.out.println(name + " 被选为了leader. 休息 " + waitSeconds + " 秒...");
        System.out.println(name + " 成为leader " + leaderCount.getAndIncrement() + " 次.");
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(waitSeconds));
        } catch (InterruptedException e) {
            System.err.println(name + " was interrupted.");
            Thread.currentThread().interrupt();
        } finally {
            System.out.println(name + " relinquishing leadership.\n");
        }
    }

}
