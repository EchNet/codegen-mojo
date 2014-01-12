package org.terrane.codegen.yaml;

import java.lang.reflect.Array;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.terrane.codegen.CodegenPlan;
import org.terrane.codegen.CodegenProjectContext;
import org.terrane.codegen.CodegenTemplate;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

public class YamlCodegenPlan implements CodegenPlan
{
	private CodegenProjectContext project;
	private File sourceFile;
	private Map<String,Object> map;
	private List<?> prodList;
	private int index;

	public YamlCodegenPlan(CodegenProjectContext project, File sourceFile)
		throws IOException
	{
		this.project = project;
		this.sourceFile = sourceFile;
		this.map = load(sourceFile);
		this.prodList = (List<?>) map.get("produces");
	}

	private Map<String,Object> load(File sourceFile)
		throws IOException
	{
		Yaml yaml = new Yaml();
		Reader input = new FileReader(sourceFile);
		try {
			return (Map<String,Object>) yaml.load(input);
		}
		catch (YAMLException e) {
			throw fixYAMLException(e);
		}
		finally {
			input.close();
		}
	}

	private YAMLException fixYAMLException(YAMLException e) 
	{
		// Snakeyaml lacks a method of expressing the source file name in its
		// error messages.  Compensate.
		if (e.getMessage().indexOf("'reader'") > 0) {
			e = new YAMLException(e.getMessage().replace("'reader'", sourceFile.toString()), e.getCause());
		}
		return e;
	}

	@Override
	public boolean hasNextProduction()
	{
		return index < (prodList == null ? 1 : prodList.size());
	}

	@Override
	public Production nextProduction()
	{
		Production production;
		if (prodList == null) {
			production = new BaseProduction(this.map) {
				@Override
				protected String getOutputClassName()
				{
					String fileName = sourceFile.getName();
					int dot = fileName.lastIndexOf('.');
					return dot < 0 ? fileName : fileName.substring(0, dot);
				}
			};
		}
		else {
			production = new BaseProduction((Map<String,Object>) prodList.get(index)) {
				@Override
				protected String getOutputClassName()
				{
					String outputClassName = (String) prodMap.get("output");
					if (outputClassName == null) {
						throw new RuntimeException("output field required");
					}
					return outputClassName;
				}
			};
		}
		++index;
		return production;
	}

	abstract private class BaseProduction implements Production
	{
		Map<String,Object> prodMap;

		BaseProduction(Map<String,Object> prodMap)
		{
			this.prodMap = prodMap;
		}

		@Override
		public CodegenTemplate getTemplate()
			throws Exception
		{
			if (!map.containsKey("template")) {
				throw new Exception(sourceFile + ": template is required");
			}
			Object templateObj = map.get("template");
			String templateClassName;
			if (templateObj instanceof String) {
				templateClassName = (String) templateObj;
			}
			else if (templateObj instanceof Map) {
				Map<String,Object> templateMap = (Map<String,Object>) templateObj;
				if (!templateMap.containsKey("class")) {
					throw new Exception(sourceFile + ": template requires class");
				}
				templateClassName = templateMap.get("class").toString();
			}
			else {
				throw new Exception(sourceFile + ": template: wrong type");
			}

			return project.createTemplate("???", templateClassName);
		}

		@Override
		public Object[] getArguments()
			throws Exception
		{
			Object value = prodMap.get("arguments");
			if (value == null) {
				throw new Exception("arguments field required");
			}

			if (value instanceof List) {
				List<?> list = (List<?>) value;
				Object[] args = new Object[list.size()];
				for (int i = 0; i < args.length; ++i) {
					args[i] = processArgument(list.get(i));
				}
				return args;
			}
			return new Object[] { processArgument(value) };
		}

		@Override
		public String getOutputPath()
		{
			return new File(getPackagePath(), getOutputClassName() + project.getOutputExtension()).toString();
		}

		abstract protected String getOutputClassName();
	}

	private File getPackagePath()
	{
		int prefixLength = project.getSourceDirectory().toString().length();
		String src = sourceFile.toString().substring(prefixLength);
		return new File(src).getParentFile();
	}

	private Object processArgument(Object argument)
		throws Exception
	{
		if (argument instanceof Map) {
			Map<?,?> map = (Map<?,?>) argument;
			if (map.keySet().equals(Collections.singleton("$java"))) {
				return Class.forName((String) map.get("$java"));
			}
		}
		return argument;
	}
}
