/**
 * Logback: the generic, reliable, fast and flexible logging framework.
 * 
 * Copyright (C) 2000-2009, QOS.ch
 * 
 * This library is free software, you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation.
 */
package ch.qos.logback.classic.spi;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * An almost trivial no fuss implementation of a class loader following the
 * child-first delegation model.
 * 
 * @author Ceki G�lc�
 */
public class LocalFirstClassLoader extends URLClassLoader {

  public LocalFirstClassLoader(URL[] urls) {
    super(urls);
  }

  public LocalFirstClassLoader(URL[] urls, ClassLoader parent) {
    super(urls, parent);
  }

  public void addURL(URL url) {
    super.addURL(url);
  }

  public Class<?> loadClass(String name) throws ClassNotFoundException {
    return loadClass(name, false);
  }

  /**
   * We override the parent-first behavior established by java.lang.Classloader.
   * 
   * The implementation is surprisingly straightforward.
   */
  protected Class<?> loadClass(String name, boolean resolve)
      throws ClassNotFoundException {

    // First, check if the class has already been loaded
    Class c = findLoadedClass(name);

    // if not loaded, search the local (child) resources
    if (c == null) {
      try {
        c = findClass(name);
      } catch (ClassNotFoundException cnfe) {
        // ignore
      }
    }

    // if we could not find it, delegate to parent
    // Note that we don't attempt to catch any ClassNotFoundException
    if (c == null) {
      if (getParent() != null) {
        c = getParent().loadClass(name);
      } else {
        c = getSystemClassLoader().loadClass(name);
      }
    }

    if (resolve) {
      resolveClass(c);
    }

    return c;
  }
}
