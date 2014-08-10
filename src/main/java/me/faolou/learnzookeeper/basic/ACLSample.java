package me.faolou.learnzookeeper.basic;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.ServerCnxn;
import org.apache.zookeeper.server.auth.AuthenticationProvider;

/**
 * Access Control List
 * Created by zhangkai on 8/10/14.
 */
public class ACLSample {

    public static void main(String[] args) throws Exception {

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
        ZooKeeper zk = new ZooKeeper("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183", 3000, watcher);


    }
}

class NameStartsWithAAuthenticationProvider implements AuthenticationProvider{

    @Override
    public String getScheme() {
        return "nswa";
    }

    @Override
    public KeeperException.Code handleAuthentication(ServerCnxn cnxn, byte[] authData) {
        String name = new String(authData);
        if (StringUtils.isBlank(name)) {
            return KeeperException.Code.AUTHFAILED;
        }
        cnxn.getAuthInfo().add(new Id(getScheme(), name));
        return KeeperException.Code.OK;
    }

    @Override
    public boolean matches(String id, String aclExpr) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(aclExpr)) {
            return false;
        }
        return StringUtils.startsWith(id, "A");
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public boolean isValid(String id) {
        return StringUtils.isNotBlank(id) && id.length() > 1;
    }
}
