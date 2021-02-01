package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import assignment.CacheImpl;

public class CacheTest {

	CacheImpl<String, String> cache = new CacheImpl<String, String>();

	@Rule
	public final ExpectedException exception = ExpectedException.none();

	@Test
	public void test_FunctionCalled_OnlyOnce() {

		assertEquals("v1", cache.get("k1", s -> "v1"));

		assertEquals("v1", cache.get("k1", s -> "v2"));
	}

	@Test
	public void test_NullKeyValue() {

		// No harm allowing ONE Null Key
		// It would still be an Unique entry, mapped to ONE Value
		assertEquals("vNull", cache.get(null, s -> "vNull"));
		assertEquals("vNull", cache.get(null, s -> "vNull-2"));

		// Absolutely fine to have multiple Null values
		assertNull(cache.get("K2", s -> null));
		assertNull(cache.get("K3", s -> null));
		assertNull(cache.get("K2", s -> null));
		assertNull(cache.get("K3", s -> null));

	}

	@Test
	public void test_NullFunction() {
		exception.expect(RuntimeException.class);
		exception.expectMessage("Null Function Passed");
		cache.get("k", null);
	}

	@Test
	public void test_FunctionThowingException() {
		exception.expect(RuntimeException.class);
		exception.expectMessage("Function throwing Exception");
		cache.get("k", s -> {
			throw new RuntimeException("Function Throwing Exception");
		});
	}
}
