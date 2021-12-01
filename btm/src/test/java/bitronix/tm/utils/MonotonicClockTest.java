package bitronix.tm.utils;

import junit.framework.TestCase;

/**
 * Test for MonotonicClock
 */
public class MonotonicClockTest extends TestCase {
	
	/**
	 * MonotonicClock should increment always
	 * (the increment may not be 1 always as granularity of system clock maybe > 1ms)
	 */
	public void testIncrement() {
		long systemMillis = System.currentTimeMillis();
		long lastCurrentTimeMillis = MonotonicClock.currentTimeMillis();
		// Check on second
		int i = 0;
		int sameMilli=0;
		
		// Test the increment for one second:
		while (System.currentTimeMillis() < systemMillis + 1000) {
			long currentTimeMillis = MonotonicClock.currentTimeMillis();
			if (currentTimeMillis == lastCurrentTimeMillis) {
				// same milli
				sameMilli++;
			} else {
				assertTrue("Expected always growing increment but found (in iteration "+i+"): lastCurrentTimeMillis="+lastCurrentTimeMillis+ " > currentTimeMillis="+currentTimeMillis, 
						lastCurrentTimeMillis < currentTimeMillis);
			}
			i++;
		}
		assertNotSame("Expected same millis in the loop above",0,sameMilli);
	}

	/**
	 * bitronix.tm.resource.common.XAPool.shrink()
	 * needs to know the seconds since last releaseTime,
	 * so MonotonicClock needs to return values corresponding to "real" time when asking at different timestamps.
	 * 
	 * (Due to design the starting offset MonotonicClock.currentTimeMillis() and System.currentTimeMillis() varies)
	 * 
	 * @throws InterruptedException 
	 */
	public void testContinues() throws InterruptedException {

		long lastCurrentTimeMillis = MonotonicClock.currentTimeMillis();

		Thread.sleep(1000);

		long now = MonotonicClock.currentTimeMillis();
		
		long duration= now-lastCurrentTimeMillis;

		assertTrue("Should be around one second (1000 ms) but was "+duration, 700<duration && duration< 1300);
	}
}
