/*
 * Copyright 2014 Greg Kopff
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.fatboyindustrial.firestarter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;

import java.util.List;
import java.util.Map;

/**
 * Configuration information for a single JVM.
 */
public class VmConfig
{
  /** The minimum memory size (in MB) for a VM. */
  public static final int MIN_VM_SIZE = 64;

  /** One megabyte. */
  @SuppressWarnings("MagicNumber")
  private static final int MEGABYTES = 1024 * 1024;

  /** The VM name. */
  private final String name;

  /** The heap size in MB. */
  private final int heap;

  /** The jar file. */
  private final String jar;

  /** Command line arguments. */
  private final ImmutableList<String> arguments;

  /** JVM properties. */
  private final ImmutableSortedMap<String, String> properties;

  /**
   * Constructor.
   * @param name The VM name.
   * @param heap The heap size in MB.
   * @param jar The jar file.
   * @param arguments Command line arguments.
   * @param properties JVM properties.
   */
  public VmConfig(final String name,
                  final int heap,
                  final String jar,
                  final List<String> arguments,
                  final Map<String, String> properties)
  {
    Preconditions.checkNotNull(name, "name cannot be null");
    Preconditions.checkNotNull(jar, "jar cannot be null");
    Preconditions.checkNotNull(arguments, "arguments cannot be null");
    Preconditions.checkNotNull(properties, "properties cannot be null");

    Preconditions.checkArgument(name.indexOf(' ') == - 1, "VmConfig.name cannot contain spaces");
    Preconditions.checkArgument(heap >= MIN_VM_SIZE, "VmConfig.heap must be >= " + MIN_VM_SIZE + " but was: " + heap);
    Preconditions.checkArgument(jar.indexOf(' ') == - 1, "VmConfig.jar cannot contain spaces");

    this.name = name;
    this.heap = heap;
    this.jar = jar;
    this.arguments = ImmutableList.copyOf(arguments);
    this.properties = ImmutableSortedMap.copyOf(properties);
  }

  /**
   * Creates a VM config from the given HOCON configuration.
   * @param name The configuration name.
   * @param vmConfig The configuration.
   * @return The VM config.
   * @throws IllegalArgumentException If the configuration is invalid.
   */
  public static VmConfig fromConfig(final String name, final Config vmConfig) throws IllegalArgumentException
  {
    Preconditions.checkNotNull(name, "name cannot be null");
    Preconditions.checkNotNull(vmConfig, "vmConfig cannot be null");

    return new VmConfig(
        name,
        (int) (vmConfig.getBytes("heap") / MEGABYTES),
        vmConfig.getString("jar"),
        vmConfig.getStringList("args"),
        vmConfig.hasPath("properties")
            ? Maps.transformValues(vmConfig.getObject("properties").unwrapped(), String::valueOf)
            : ImmutableMap.of());
  }

  /**
   * Gets the VM name.
   * @return The VM name.
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * Gets the heap size.
   * @return The heap in MB.
   */
  public int getHeap()
  {
    return this.heap;
  }

  /**
   * Gets the jar file name.
   * @return The jar file name.
   */
  public String getJar()
  {
    return this.jar;
  }

  /**
   * Gets the command line arguments.
   * @return The command line arguments.
   */
  public ImmutableList<String> getArguments()
  {
    return this.arguments;
  }

  /**
   * Gets the JVM properties.
   * @return The JVM properties.
   */
  public ImmutableSortedMap<String, String> getProperties()
  {
    return this.properties;
  }
}
