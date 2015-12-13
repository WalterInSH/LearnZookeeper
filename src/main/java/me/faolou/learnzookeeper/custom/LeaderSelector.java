package me.faolou.learnzookeeper.custom;

import com.google.common.base.Preconditions;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 分布式节点Leader选举器, 一般用于限制多个jvm instance同一时间只能做一件事.
 *
 * @author zhangwenbin
 * @since 2015/12/11.
 */
public class LeaderSelector extends LeaderSelectorListenerAdapter implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeaderSelector.class);

    private final String name;
    private final org.apache.curator.framework.recipes.leader.LeaderSelector leaderSelector;
    private final AtomicInteger leaderCount = new AtomicInteger();
    private CuratorFramework client;
    private AtomicBoolean permit;
    private String ip;
    private Integer term;
    private TimeUnit timeUnit;

    /**
     * 创建Leader选举, 每个选举器有自己的client, 一般用于全局只有一个分布式锁的情况
     *
     * @param zkHosts  zk地址
     * @param path     选举path
     * @param name     选举人名字
     * @param term     任期
     * @param timeUnit 任期时间单位
     */
    public LeaderSelector(String zkHosts, String path, String name, Integer term, TimeUnit timeUnit) {
        this(CuratorFrameworkFactory.newClient(zkHosts, new ExponentialBackoffRetry(1000, 3)), path, name, term, timeUnit);
    }

    /**
     * 创建Leader选举, 多个选举器共享client, 一般用于全局有多个分布式锁的情况
     * @param client
     * @param path
     * @param name
     * @param term
     * @param timeUnit
     */
    public LeaderSelector(CuratorFramework client, String path, String name, Integer term, TimeUnit timeUnit) {
        try {
            this.ip = SystemUtils.findLocalAddressIp();
        } catch (SocketException e) {
            this.ip = "127.0.0.1";
        }
        this.name = name;
        this.permit = new AtomicBoolean();

        // 参与leader选举的client必须使用相同path
        Preconditions.checkNotNull(client, "client不可为空");
        this.leaderSelector = new org.apache.curator.framework.recipes.leader.LeaderSelector(client, path, this);

        // requeue:当该client被选为leader后，参与下次leader的选举，大部分应用需要自动执行requeue
        this.leaderSelector.autoRequeue();

        Preconditions.checkArgument(term > 0, "任期不得小于等于0");
        this.term = term;
        this.timeUnit = timeUnit;
    }

    public void start() {
        client.start();
        leaderSelector.start();
    }

    @Override
    public void close() throws IOException {
        CloseableUtils.closeQuietly(client);
        CloseableUtils.closeQuietly(leaderSelector);
    }

    public boolean getPermit() {
        return permit.get();
    }

    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {

        LOGGER.info("ip: {} 被选为 {} leader, 任期: {} {}", ip, name, term, timeUnit);
        LOGGER.info("ip: {} 成为 {} leader {} 次", ip, name, leaderCount.getAndIncrement());
        try {
            permit.compareAndSet(false, true);
            Thread.sleep(timeUnit.toMillis(term));
        } catch (InterruptedException e) {
            LOGGER.warn("name: {}, ip: {} 任期被中断", name, ip);
            Thread.currentThread().interrupt();
        } finally {
            permit.compareAndSet(true, false);
            LOGGER.info("name: {}, ip: {} 任期结束", name, ip);
        }
    }
}
