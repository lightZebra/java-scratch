import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.io.IOException;
import java.util.HashSet;

import static java.util.Arrays.asList;

public class MainJedisRedisClient {

	public static void main(String[] args) {
		final HashSet<HostAndPort> hostAndPorts = new HashSet<>(asList(
				new HostAndPort("localhost", 30001),
				new HostAndPort("localhost", 30002),
				new HostAndPort("localhost", 30003)
		));
		try (JedisCluster jedisCluster = new JedisCluster(hostAndPorts)) {
			int count = 0;
			while (true) {
				try {
					jedisCluster.set("bar" + count, String.valueOf(count));
					System.out.println("inserted: " + count++);
					Thread.sleep(1000L);
				} catch (Exception e) {
					System.out.println("inner");
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			System.out.println("outer");
			e.printStackTrace();
		}
	}
}
