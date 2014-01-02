package org.terrane.codegen;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class Driver
{
	private File sourceDirectory = new File(".");
	private String sourceExtension = ".yml";
	private File outputDirectory = new File(".");

	public static void main(String[] args)
	{
		Driver driver = new Driver();
		if (args.length > 0) {
			driver.sourceDirectory = new File(args[0]);
		}
		if (args.length > 1) {
			driver.sourceExtension = args[1];
		}
		if (args.length > 2) {
			driver.outputDirectory = new File(args[2]);
		}
		driver.run();
	}

	public void setSourceDirectory(File sourceDirectory)
	{
		if (sourceDirectory == null) throw new IllegalArgumentException();
		this.sourceDirectory = sourceDirectory;
	}

	public void setSourceExtension(String sourceExtension)
	{
		this.sourceExtension = sourceExtension;
	}

	public void setOutputDirectory(File outputDirectory)
	{
		this.outputDirectory = outputDirectory;
	}

	public File getOutputDirectory()
	{
		return outputDirectory;
	}

	public void run()
	{
		traverse(sourceDirectory);
	}

	private void traverse(File f)
	{
		if (f.isDirectory()) {
			File[] listing = f.listFiles();
			if (listing != null) {
				for (File child : listing) {
					traverse(child);
				}
			}
		}
		else if (f.getName().endsWith(sourceExtension)) {
			try {
				doFile(f);
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private void doFile(File f)
		throws IOException
	{
		Map<String,Object> map = loadProperties(f);
		Writer output = openOutputFile(f, map);
		try {
			expandTemplate(getSpecifiedTemplate(f, map), getArgs(map), output);
		}
		finally {
			output.close();
		}
	}

	private Map<String,Object> loadProperties(File f)
		throws IOException
	{
		Yaml yaml = new Yaml();
		InputStream input = new FileInputStream(f);
		try {
			return (Map<String,Object>) yaml.load(input);
		}
		finally {
			input.close();
		}
	}

	private Object getSpecifiedTemplate(File f, Map<String,Object> map)
	{
		if (!map.containsKey("templateClassName")) {
			throw new RuntimeException(f + ": no templateClassName");
		}
		String templateClassName = map.get("templateClassName").toString();
		return instantiateTemplateClass(templateClassName);
	}

	private Object instantiateTemplateClass(String templateClassName)
	{
		try {
			return Class.forName(templateClassName).newInstance();
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Object[] getArgs(Map<String,Object> map)
	{
		if (map.containsKey("args")) {
			Object value = map.get("args");
			Object[] args;
			if (value instanceof List) {
				args = ((List<?>) value).toArray(new Object[0]);
			}
			else if (value.getClass().isArray()) {
				args = new Object[Array.getLength(value)];
				for (int i = 0; i < args.length; ++i) {
					args[i] = Array.get(value, i);
				}
			}
			else {
				args = new Object[] { value.toString() };
			}
			return args;
		}
		return new Object[0];
	}

	private Writer openOutputFile(File f, Map<String,Object> map)
		throws IOException
	{
		String outputPath;
		if (map.containsKey("outputPath")) {
			outputPath = map.get("outputPath").toString();
		}
		else {
			outputPath = f.toString().substring(sourceDirectory.toString().length());
			outputPath = outputPath.substring(0, outputPath.length() - sourceExtension.length());
			outputPath += ".java";
		}
		File outputFile = new File(outputDirectory, outputPath);
		outputFile.getParentFile().mkdirs();
		return new FileWriter(outputFile);
	}

	private void expandTemplate(Object template, Object[] args, Writer outWriter)
	{
		try {
			Method renderMethod = getMethodByName(template.getClass(), "render");
			Object[] allArgs = new Object[args.length + 1];
			allArgs[0] = outWriter;
			System.arraycopy(args, 0, allArgs, 1, args.length);
			renderMethod.invoke(template, allArgs);
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Method getMethodByName(Class<?> templateClass, String methodName)
	{
		for (Method method : templateClass.getMethods()) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}
		throw new RuntimeException(templateClass + " does not have a " + methodName + " method.");
	}
}
