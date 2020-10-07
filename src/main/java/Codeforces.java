import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Codeforces {

	public static void main(String[] args) {
		final Scanner scanner = new Scanner(System.in);
		final int n = scanner.nextInt();

		final int[] functions = new int[n];
		for (int i = 0; i < n; i++) {
			functions[i] = scanner.nextInt();
		}

		final List<List<Integer>> edges = IntStream.range(0, n)
				.mapToObj(i -> new ArrayList<Integer>())
				.collect(Collectors.toList());

		for (int edge = 1; edge < n; edge++) {
			final int parent = scanner.nextInt();
			edges.get(parent).add(edge);
		}

		final int[] dp = new int[n];
		dfs(0, functions, edges, dp);

		final long leafs = edges.stream().filter(List::isEmpty).count();

		System.out.println(leafs + 1 - dp[0]);
	}

  private static void dfs(int node, int[] functions, List<List<Integer>> edges, int[] dp) {
    if (edges.get(node).isEmpty()) {
      dp[node] = 1;
      return;
    }

    if (functions[node] == 0) {
      for (Integer child : edges.get(node)) {
        dfs(child, functions, edges, dp);
        dp[node] += dp[child];
      }
    } else {
      dp[node] = Integer.MAX_VALUE;
      for (Integer child : edges.get(node)) {
        dfs(child, functions, edges, dp);
        dp[node] = Math.min(dp[node], dp[child]);
      }
    }
  }
}
