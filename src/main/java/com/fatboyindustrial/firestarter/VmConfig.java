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
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Configuration information for a single JVM.
 */
public class VmConfig
{
  /** The minimum memory size (in MB) for a VM. */
  public static final int MIN_VM_SIZE = 64;

  /** The VM name. */
  private String name;

  /** The heap size in MB. */
  private int heap;

  /** The jar file. */
  private String jar;

  /** Command line arguments. */
  @SerializedName("args")
  private List<String> arguments;

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
    return ImmutableList.copyOf(this.arguments);
  }

  /**
   * Validates the configuration.
   * @throws IllegalArgumentException If the configuration is invalid.
   */
  protected void validate() throws IllegalArgumentException
  {
    Preconditions.checkArgument(this.name.indexOf(' ') == - 1, "VmConfig.name cannot contain spaces");
    Preconditions.checkArgument(this.heap >= MIN_VM_SIZE, "VmConfig.heap must be >= " + MIN_VM_SIZE);
    Preconditions.checkArgument(this.jar.indexOf(' ') == - 1, "VmConfig.jar cannot contain spaces");
  }
}
