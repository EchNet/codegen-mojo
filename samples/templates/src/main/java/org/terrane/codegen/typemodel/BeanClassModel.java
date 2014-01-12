package org.terrane.codegen.typemodel;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

public class BeanClassModel
	extends ClassModel
{
	private PropertyDescriptor[] properties;

	public BeanClassModel(Class<?> prototype)
	{
		super(prototype);
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(prototype);
			this.properties = beanInfo.getPropertyDescriptors();
		}
		catch (IntrospectionException e) {
			this.properties = new PropertyDescriptor[0];
		}
	}

	public PropertyDescriptor[] getPropertyDescriptors()
	{
		return properties;
	}
}
