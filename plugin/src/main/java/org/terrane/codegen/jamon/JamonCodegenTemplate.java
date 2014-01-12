package org.terrane.codegen.jamon;

import java.io.Writer;
import java.lang.reflect.Method;
import org.terrane.codegen.CodegenTemplate;

public class JamonCodegenTemplate
	implements CodegenTemplate
{
	private final static String RENDER_METHOD_NAME = "render";

	private Object templateObj;
	private Method renderMethod;

	public JamonCodegenTemplate(String className)
		throws Exception
	{
		Class<?> templateClass = Class.forName(className);
		this.templateObj = templateClass.newInstance();
		this.renderMethod = getRenderMethod(templateClass);
	}

	@Override
	public void expand(Object[] args, Writer outWriter)
		throws Exception
	{
		Object[] allArgs = new Object[args.length + 1];
		allArgs[0] = outWriter;
		System.arraycopy(args, 0, allArgs, 1, args.length);
		renderMethod.invoke(templateObj, allArgs);
	}

	private Method getRenderMethod(Class<?> templateClass)
	{
		for (Method method : templateClass.getMethods()) {
			if (method.getName().equals(RENDER_METHOD_NAME)) {
				return method;
			}
		}
		throw new IllegalArgumentException(templateClass + " does not have a render method.");
	}
}
