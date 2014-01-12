package org.terrane.codegen;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.terrane.codegen.jamon.JamonCodegenTemplate;
import org.terrane.codegen.yaml.YamlCodegenPlan;
import org.terrane.io.FileVisitor;

/**
 * @goal generate-sources
 * @phase generate-sources
 */
public class CodegenMojo
	extends AbstractMojo
	implements Mojo, CodegenProjectContext
{
	private final static String SOURCE_PATTERN = ".*\\.yml";

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
	 * @parameter default-value="src/main/codegen"
	 * @required
	 */
	String sourceDirectory = "src/main/codegen";

	/**
	 * @parameter default-value="target/generated-sources/codegen-mojo"
	 * @required
	 */
	String outputDirectory = "target/generated-sources/codegen-mojo";

	/**
	 * @parameter default-value=".java"
	 * @required
	 */
	String outputExtension = ".java";

	@Override
	public void execute()
		throws MojoFailureException
	{
		try {
			run();
		}
		catch (Exception e) {
			throw new MojoFailureException("Codegen failure", e);
		}
		project.addCompileSourceRoot(getOutputDirectory().getAbsolutePath());
	}

	@Override
	public File getSourceDirectory()
	{
		return new File(new File(basedir), sourceDirectory);
	}

	@Override
	public File getOutputDirectory()
	{
		return new File(new File(basedir), outputDirectory);
	}

	@Override
	public String getOutputExtension()
	{
		return outputExtension;
	}

	@Override
	public CodegenTemplate createTemplate(String templateType, String templateClassName)
		throws Exception
	{
		return new JamonCodegenTemplate(templateClassName);
	}

	private void run()
		throws Exception
	{
		getLog().debug("traverse " + sourceDirectory);
		new FileVisitor<Exception>(getSourceDirectory(), SOURCE_PATTERN) {
			@Override
			protected void visit(File file) throws Exception {
				getLog().debug("handle source file " + file);
				handleSourceFile(file);
			}
		}.traverse();
	}

	private void handleSourceFile(File file)
		throws Exception
	{
		CodegenPlan plan = new YamlCodegenPlan(this, file);
		while (plan.hasNextProduction()) {
			CodegenPlan.Production prod = plan.nextProduction();
			File outputFile = new File(getOutputDirectory(), prod.getOutputPath());
			outputFile.getParentFile().mkdirs();
			Writer output = new FileWriter(outputFile);
			try {
				prod.getTemplate().expand(prod.getArguments(), output);
			}
			finally {
				output.close();
			}
		}
	}
}
