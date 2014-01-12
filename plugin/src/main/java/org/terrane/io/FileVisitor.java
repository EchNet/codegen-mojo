package org.terrane.io;

import java.io.File;
import java.util.regex.Pattern;

abstract public class FileVisitor<E extends Throwable>
{
	private File base;
	private Pattern pattern;

	public FileVisitor(File base, String regexp)
	{
		this.base = base;
		this.pattern = Pattern.compile(regexp);
	}

	public void traverse()
		throws E
	{
		traverse(base);
	}

	private void traverse(File f)
		throws E
	{
		if (f.isDirectory()) {
			File[] listing = f.listFiles();
			if (listing != null) {
				for (File child : listing) {
					traverse(child);
				}
			}
		}
		else if (pattern.matcher(f.getName()).matches()) {
			visit(f);
		}
	}

	abstract protected void visit(File f)
		throws E;
}
