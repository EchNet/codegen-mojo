package org.terrane.codegen;

import java.io.File;

public interface CodegenProjectContext
{
	public File getSourceDirectory();
	public File getOutputDirectory();
	public String getOutputExtension();

	public CodegenTemplate createTemplate(String templateType, String templateClassName)
		throws Exception;
}
