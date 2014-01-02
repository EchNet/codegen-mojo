package org.terrane.codegen;

import java.io.File;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

/**
 * @goal generate-sources
 * @phase generate-sources
 */
public class CodegenMojo extends AbstractMojo
{
	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	MavenProject project;

	/**
	 * @parameter expression="${basedir}"
	 * @required
	 * @readonly
	 */
	String basedir;

	/**
	 * @parameter default-value="src/main/stubs"
	 * @required
	 */
	String sourceDirectory = "src/main/stubs";

	/**
	 * @parameter default-value=".yml"
	 * @required
	 */
	String sourceExtension = ".yml";

	/**
	 * @parameter default-value="target/generated-sources/terrane-codegen-mojo"
	 * @required
	 */
	String outputDirectory = "target/generated-sources/terrane-codegen-mojo";

	@Override
	public void execute()
	{
		Driver driver = new Driver();
		driver.setSourceDirectory(new File(new File(basedir), sourceDirectory));
		driver.setSourceExtension(sourceExtension);
		driver.setOutputDirectory(new File(new File(basedir), outputDirectory));
		driver.run();
		project.addCompileSourceRoot(driver.getOutputDirectory().getAbsolutePath());
	}
}
