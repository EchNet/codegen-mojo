package org.terrane.codegen.typemodel;

public class ClassModel
{
	String packageName;
	String name;

	public ClassModel(Class<?> type)
	{
		this(type.getName());
	}

	public ClassModel(String typeName)
	{
		int dot = typeName.lastIndexOf('.');
		if (dot > 0) {
			packageName = typeName.substring(0, dot);
			name = typeName.substring(dot + 1);
		}
		else {
			name = typeName;
		}
	}

	public String getPackageName()
	{
		return packageName;
	}

	public String getName()
	{
		return name;
	}

	public String getCanonicalName()
	{
		return (packageName != null && packageName.length() > 0 ? (packageName + ".") : "") + name;
	}
}
