import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;

import java.util.Collection;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.Arrays.asList;

public class LettuceRedisClusterClient {
	public static void main(String[] args) {

		Collection<RedisURI> uris = asList(
				RedisURI.create("localhost", 30001),
				RedisURI.create("localhost", 30002),
				RedisURI.create("localhost", 30003)
		);

		RedisClusterClient redisClient = RedisClusterClient.create(uris);
		StatefulRedisClusterConnection<String, String> connection = redisClient.connect();

		ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
				.enableAdaptiveRefreshTrigger(ClusterTopologyRefreshOptions.RefreshTrigger.MOVED_REDIRECT, ClusterTopologyRefreshOptions.RefreshTrigger.PERSISTENT_RECONNECTS)
				.adaptiveRefreshTriggersTimeout(5, TimeUnit.SECONDS)
				.build();

		redisClient.setOptions(ClusterClientOptions.builder()
				.topologyRefreshOptions(topologyRefreshOptions)
				.build());

		Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(new Runnable() {
			private int count = 0;

			@Override
			public void run() {
				try {
					connection.sync().set("testKey" + count, String.valueOf(count));
					System.out.println("inserted: " + count++);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1, 1, TimeUnit.SECONDS);

		new Scanner(System.in).nextLine();

		connection.close();
		redisClient.shutdown();
	}

	/*
	RedisURI redisURI = RedisURI.Builder.sentinel("localhost", 30010, "master-1")
				.build();

		RedisClient redisClient = RedisClient.create();
		StatefulRedisMasterSlaveConnection<String, String> connection = MasterSlave
				.connect(redisClient, new Utf8StringCodec(), redisURI);

		Executors.newScheduledThreadPool(1).scheduleWithFixedDelay(new Runnable() {
			private int count = 0;

			@Override
			public void run() {
				try {
					connection.sync().set("testKey", String.valueOf(count++));
					System.out.println("inserted: " + count);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1, 1, TimeUnit.SECONDS);

		new Scanner(System.in).nextLine();

		connection.close();
		redisClient.shutdown();
	 */
}
