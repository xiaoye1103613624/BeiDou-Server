package org.gms.net.server.coordinator.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages session initialization using remote host (ip address).
 */
public class SessionInitialization {
    /** 日志记录器 */
    private final static Logger log = LoggerFactory.getLogger(SessionInitialization.class);
    /** 最大初始化尝试次数 */
    private static final int MAX_INIT_TRIES = 2;
    /** 重试等待间隔（毫秒） */
    private static final long RETRY_DELAY_MILLIS = 1777;

    /** 处于初始化状态的远程主机集合 */
    private final Set<String> remoteHostsInInitState = new HashSet<>();
    /** 锁池，用于分段加锁 */
    private final List<Lock> locks = new ArrayList<>(100);

    SessionInitialization() {
        for (int i = 0; i < 100; i++) {
            locks.add(new ReentrantLock());
        }
    }

    /**
     * 根据远程主机哈希值获取对应的锁
     *
     * @param remoteHost 远程主机地址
     * @return 对应的分段锁
     */
    private Lock getLock(String remoteHost) {
        return locks.get(Math.abs(remoteHost.hashCode()) % 100);
    }

    /**
     * Try to initialize a session. Should be called <em>before</em> any session initialization procedure.
     *
     * @return InitializationResult.SUCCESS if initialization was successful.
     * If it was successful, finalize() needs to be called shortly after,
     * or else the initialization will be left hanging in a bad state,
     * which means any subsequent initialization from the same remote host will fail.
     */
    public InitializationResult initialize(String remoteHost) {
        final Lock lock = getLock(remoteHost);
        try {
            int tries = 0;
            while (true) {
                if (lock.tryLock()) {
                    try {
                        if (remoteHostsInInitState.contains(remoteHost)) {
                            return InitializationResult.ALREADY_INITIALIZED;
                        }

                        remoteHostsInInitState.add(remoteHost);
                    } finally {
                        lock.unlock();
                    }

                    break;
                } else {
                    if (tries++ == MAX_INIT_TRIES) {
                        return InitializationResult.TIMED_OUT;
                    }

                    Thread.sleep(RETRY_DELAY_MILLIS);
                }
            }
        } catch (Exception e) {
            log.error("Failed to initialize session.", e);
            return InitializationResult.ERROR;
        }

        return InitializationResult.SUCCESS;
    }

    /**
     * 完成初始化
     * 在会话初始化过程<em>之后</em>调用，释放该远程主机的初始化状态
     *
     * @param remoteHost 远程主机地址
     */
    public void finalize(String remoteHost) {
        final Lock lock = getLock(remoteHost);
        lock.lock();
        try {
            remoteHostsInInitState.remove(remoteHost);
        } finally {
            lock.unlock();
        }
    }
}
