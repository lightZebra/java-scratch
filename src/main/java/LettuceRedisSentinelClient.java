import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.Utf8StringCodec;
import io.lettuce.core.masterslave.MasterSlave;
import io.lettuce.core.masterslave.StatefulRedisMasterSlaveConnection;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LettuceRedisSentinelClient {
	public static void main(String[] args) {

		RedisURI redisURI = RedisURI.Builder.sentinel("localhost", 30010, "master-1")
				.withSentinel("localhost", 30011)
				.withSentinel("localhost", 30012)
				.build();

		RedisClient redisClient = RedisClient.create();
		StatefulRedisMasterSlaveConnection<String, String> connection = MasterSlave
				.connect(redisClient, new Utf8StringCodec(), redisURI);

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
