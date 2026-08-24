package org.gms.net.server;

import org.gms.manager.ServerManager;
import org.gms.net.server.guild.Guild;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServerGuildDisbandConcurrencyTest {
    private static final int DISBANDING_GUILD_ID = Integer.MAX_VALUE - 101;
    private static final int UNRELATED_GUILD_ID = Integer.MAX_VALUE - 102;

    private Server server;
    private Map<Integer, Guild> guilds;

    @BeforeAll
    static void setUpApplicationContext() {
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBean(any(Class.class)))
                .thenAnswer(invocation -> {
                    Class<?> beanType = invocation.getArgument(0);
                    return mock(beanType);
                });
        new ServerManager().setApplicationContext(applicationContext);
    }

    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        server = Server.getInstance();
        Field guildsField = Server.class.getDeclaredField("guilds");
        guildsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Integer, Guild> reflectedGuilds = (Map<Integer, Guild>) guildsField.get(server);
        guilds = reflectedGuilds;
    }

    @AfterEach
    void tearDown() {
        synchronized (guilds) {
            guilds.remove(DISBANDING_GUILD_ID);
            guilds.remove(UNRELATED_GUILD_ID);
        }
    }

    @Test
    void disbandDoesNotHoldGlobalGuildMapLockDuringGuildWork() throws Exception {
        Guild disbandingGuild = mock(Guild.class);
        Guild unrelatedGuild = mock(Guild.class);
        CountDownLatch disbandEntered = new CountDownLatch(1);
        CountDownLatch allowDisbandToFinish = new CountDownLatch(1);
        when(disbandingGuild.disbandGuild()).thenAnswer(invocation -> {
            disbandEntered.countDown();
            assertTrue(allowDisbandToFinish.await(5, TimeUnit.SECONDS));
            return true;
        });
        synchronized (guilds) {
            guilds.put(DISBANDING_GUILD_ID, disbandingGuild);
            guilds.put(UNRELATED_GUILD_ID, unrelatedGuild);
        }

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            Future<Boolean> disbandResult = executor.submit(() -> server.disbandGuild(DISBANDING_GUILD_ID));
            assertTrue(disbandEntered.await(2, TimeUnit.SECONDS));

            Future<Guild> unrelatedLookup = executor.submit(() -> server.getGuild(UNRELATED_GUILD_ID));
            assertSame(unrelatedGuild, unrelatedLookup.get(2, TimeUnit.SECONDS));

            allowDisbandToFinish.countDown();
            assertTrue(disbandResult.get(2, TimeUnit.SECONDS));
            assertNull(server.getGuild(DISBANDING_GUILD_ID));
        } finally {
            allowDisbandToFinish.countDown();
            executor.shutdownNow();
        }
    }

    @Test
    void failedDisbandKeepsGuildRegisteredForRetry() {
        Guild disbandingGuild = mock(Guild.class);
        when(disbandingGuild.disbandGuild()).thenReturn(false);
        synchronized (guilds) {
            guilds.put(DISBANDING_GUILD_ID, disbandingGuild);
        }

        assertFalse(server.disbandGuild(DISBANDING_GUILD_ID));
        assertSame(disbandingGuild, server.getGuild(DISBANDING_GUILD_ID));
    }
}
