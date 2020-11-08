package tech.dttp.block.logger.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.util.Pair;
public class ContainerUtils {
    public static <T> Map<T, Integer> getTransactions(List<Pair<T, Integer>> transactions) {
    Map<T, Integer> compressed = new HashMap<>();

    transactions.forEach(pair -> {
      compressed.put(
        pair.getLeft(),
        compressed.getOrDefault(pair.getLeft(), 0) + pair.getRight()
      );
    });

    return compressed;
  }
}
