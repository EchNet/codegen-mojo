package org.terrane.codegen.samples;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Verify that ConfigurationException was generated properly.
 */
public class ConfigurationExceptionTest
{
	@Test
    public void testConstructDefault() throws Exception
    {
		try {
			throw new ConfigurationException();
		}
		catch (ConfigurationException e) {
			assertNull(e.getMessage());
		}
    }

	@Test
    public void testConstructWithMessage() throws Exception
    {
		try {
			throw new ConfigurationException("hello");
		}
		catch (ConfigurationException e) {
			assertEquals("hello", e.getMessage());
		}
    }
}
