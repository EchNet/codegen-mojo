package org.terrane.codegen;

public interface CodegenPlan
{
	public boolean hasNextProduction();
	public Production nextProduction();

	public static interface Production
	{
		/**
		 * The template to expand.
		 */
		public CodegenTemplate getTemplate()
			throws Exception;

		/**
		 * Arguments to the template.
		 */
		public Object[] getArguments()
			throws Exception;

		/**
		 * The output path, relative to the project output directory.
		 */
		public String getOutputPath();
	}
}
