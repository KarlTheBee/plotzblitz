package de.karlthebee.spigot.plotsblitz.database;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.karlthebee.spigot.plotsblitz.util.Tuple;

/**
 * Not jet ready
 */
class DatabaseCache<T> {

	private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

	/**
	 * the default value on how long a request should be saved before getting
	 * deleted. A higher value leads to a lower mysql access rate but also to higher
	 * memory consumption and may lead to cache error (example : a plot is removed
	 * but the cache forgot to delete it) However there is no guarantee that after
	 * exactly MAX_CACHE_TIME the value will be deleted. It can be that the value
	 * can be up to 2 * MAX_CACHE_TIME Is in milliseconds
	 */
	private static final long MAX_CACHE_TIME = 60 * 1000;
	

	private Map<String, Tuple<Long, T>> map = new HashMap<>();

	public DatabaseCache() {
		service.scheduleWithFixedDelay(() -> updateCache(), MAX_CACHE_TIME, MAX_CACHE_TIME, TimeUnit.MILLISECONDS);
	}

	public void saveRequestResult(String s, T t) {
		map.put(s, new Tuple<Long, T>(System.currentTimeMillis(),
				Objects.requireNonNull(t, "A cached value cannot be null")));
	}

	/**
	 * @param s
	 * @return the request. is null if there is none or it's deleted
	 */
	public synchronized T getRequest(String s) {
		Tuple<Long, T> tuple = map.get(s);
		if (tuple.key + MAX_CACHE_TIME < System.currentTimeMillis()) {
			map.remove(s);
			return null;
		}
		return tuple.value;
	}

	private void updateCache() {
		for (String s : map.keySet()) {
			getRequest(s);
		}
	}

	public void clearCache() {
		map.clear();
	}

}
