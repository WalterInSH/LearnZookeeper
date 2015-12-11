package me.faolou.learnzookeeper.custom;

import com.google.common.collect.Lists;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangwenbin
 * @since 2015/12/11.
 */
public class LeaderSelectorTest {
    public static void main(String[] args) throws Exception {
        TestingServer server = new TestingServer();
        int CLIENT_QTY = 3;
        String path = "/examples/leader";
        List<LeaderSelector> locksList = Lists.newArrayList();
        try {
            for (int i = 0; i < CLIENT_QTY; ++i) {
                LeaderSelector locks = new LeaderSelector(server.getConnectString(), path, "Client #" + i, 5, TimeUnit.SECONDS);
                locks.start();
                System.out.println(locks.getPermit());
            }
            System.out.println("Press enter/return to quit\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } finally {
            for (LeaderSelector locks : locksList) {
                CloseableUtils.closeQuietly(locks);
            }
            CloseableUtils.closeQuietly(server);
        }
    }

}