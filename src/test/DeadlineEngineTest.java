package test;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import assignment.DeadlineEngineImpl;

public class DeadlineEngineTest {

	DeadlineEngineImpl cache = new DeadlineEngineImpl();

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	LocalDateTime dl = LocalDateTime.now();

	long dl1 = dl.plusNanos(1000000).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(); // now + 1 ms
	long dl2 = dl.plusNanos(2000000).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(); // + 2 ms
	long dl3 = dl.plusNanos(3000000).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(); // + 3 ms
	long dl4 = dl.plusNanos(4000000).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(); // + 4 ms
	long dl5 = dl.plusNanos(5000000).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(); // + 5 ms
	long dl6 = dl.plusNanos(6000000).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(); // + 6 ms

	long nowMs = dl.plusNanos(10000000).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(); // + 10 ms [Supplied
																									// Deadline]

	@Test
	public void test_poll_all_deadlines_expired_1() {
		cache.schedule(dl1);
		cache.schedule(dl2);
		cache.schedule(dl3);
		cache.schedule(dl4);
		cache.schedule(dl5);
		cache.schedule(dl6);

		// All deadlines expired, but max_count > total expired
		assertEquals(6, cache.poll(nowMs, e -> e.toString(), 10));
	}

	@Test
	public void test_poll_all_deadlines_expired_2() {
		cache.schedule(dl1);
		cache.schedule(dl2);
		cache.schedule(dl3);
		cache.schedule(dl4);
		cache.schedule(dl5);
		cache.schedule(dl6);

		// All deadlines expired, but max_count < total expired
		assertEquals(2, cache.poll(nowMs, e -> e.toString(), 2));
	}

	@Test
	public void test_poll_all_deadlines_expired_3() {
		cache.schedule(dl1);
		cache.schedule(dl2);
		cache.schedule(dl3);
		cache.schedule(dl4);
		cache.schedule(dl5);
		cache.schedule(dl6);

		// All deadlines expired, but max_count = 0
		assertEquals(0, cache.poll(nowMs, e -> e.toString(), 0));
	}

	@Test
	public void test_poll_partial_deadlines_expired() {
		cache.schedule(dl1);
		cache.schedule(dl2);
		cache.schedule(dl3);
		cache.schedule(dl4);
		cache.schedule(dl5);
		cache.schedule(dl6);

		nowMs = dl.plusNanos(3500000).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(); // + 3.5 ms
		// Only 3 expired wrt to passed deadline
		assertEquals(3, cache.poll(nowMs, e -> e.toString(), 10));
	}

	@Test
	public void test_HandlerThrowingException() {
		cache.schedule(dl1);
		cache.schedule(dl2);

		assertEquals(0, cache.poll(nowMs, e -> {throw new RuntimeException();}, 10)); 

	}
}
