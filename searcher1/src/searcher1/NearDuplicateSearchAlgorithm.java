package searcher1;

import java.util.Set;

public interface NearDuplicateSearchAlgorithm {
	public Set<Pair<Integer, Integer>> execute(String text);
}
