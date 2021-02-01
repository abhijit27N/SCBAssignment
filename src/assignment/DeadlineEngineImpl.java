package assignment;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

public class DeadlineEngineImpl implements DeadlineEngine {
	// KeeUping the Scheduled Deadlines sorted makes retrieval faster wrt passed deadline.
	// It might be good idea to keep this size bounded, and backing with an Unbounded Queue
	private TreeMap<Long, Long> map = new TreeMap<Long, Long>();

	// Error'ed deadlines
	private HashSet<Long> errors = new HashSet<Long>();
	private static Long id = 0L;

	@Override
	public long schedule(long deadlineMs) {
		map.put(deadlineMs, ++id);
		return id;
	}

	@Override
	public boolean cancel(long requestId) {
		Long id = map.remove(requestId);
		if (id == null) // Already Cancelled / Processed / Error'ed
			return false;
		return true;
	}

	@Override
	public int poll(long nowMs, Consumer<Long> handler, int maxPoll) {		

		// Sanpshot of Expired Deadlines wrt to Deadline passed
		// This does not necessarily have be an sorted collection, keeping it sorted has no overhead either
		TreeMap<Long, Long> polledMap = map.entrySet().stream()
				                                      .filter(k -> k.getKey() < nowMs)
				                                      .limit(maxPoll)
				                                      .collect(TreeMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
		
		HashSet<Long> errorSet = new HashSet<Long>();

		polledMap.forEach((k, v) -> {
				try {
					handler.accept(v);
				} catch (Exception e) { // Error processing single event should not disturb entire batch.
					System.out.println("Exception thrown processing id: " + v);					
					errorSet.add(v);
				}
				map.remove(k);  // Serviced Deadlines can be removed
			} );
		errorSet.addAll(errorSet);
		return polledMap.size() - errorSet.size();			
	}

	@Override
	public int size() {
		return map.size();
	}

	public Set<Long> errors() {
		return errors;
	}	

}
