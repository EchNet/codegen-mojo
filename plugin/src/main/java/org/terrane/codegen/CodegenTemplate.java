package org.terrane.codegen;

import java.io.Writer;

public interface CodegenTemplate
{
	/**
	 * @param args    template arguments
	 * @param output  output stream
	 */
	public void expand(Object[] args, Writer output)
		throws Exception;
}
