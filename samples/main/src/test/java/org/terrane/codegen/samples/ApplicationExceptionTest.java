package org.terrane.codegen.samples;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Verify that ApplicationException was generated properly.
 */
public class ApplicationExceptionTest
{
	@Test
    public void testConstructDefault() throws Exception
    {
		try {
			throw new ApplicationException();
		}
		catch (ApplicationException e) {
			assertNull(e.getMessage());
		}
    }

	@Test
    public void testConstructWithMessage() throws Exception
    {
		try {
			throw new ApplicationException("hello");
		}
		catch (ApplicationException e) {
			assertEquals("hello", e.getMessage());
		}
    }
}
