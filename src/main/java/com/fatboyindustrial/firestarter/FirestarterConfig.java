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
import com.typesafe.config.Config;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The outermost configuration object.
 */
public class FirestarterConfig
{
  /** The configuration name. */
  private final String name;

  /** VM details. */
  private final ImmutableList<VmConfig> jvms;

  /**
   * Constructor.
   * @param name The configuration name.
   * @param jvms The JVM configuration details.
   */
  public FirestarterConfig(final String name, final List<VmConfig> jvms)
  {
    Preconditions.checkNotNull(name, "name cannot be null");
    Preconditions.checkNotNull(jvms, "jvms cannot be null");

    Preconditions.checkArgument(name.indexOf(' ') == -1, "FirestarterConfig.name cannot contain spaces");

    this.name = name;
    this.jvms = ImmutableList.copyOf(jvms);
  }

  /**
   * Creates a firestarter config from the given HOCON configuration.
   * @param hocon The HOCON configuration.
   * @return The firestarter config.
   * @throws IllegalArgumentException If the configuration is invalid.
   */
  public static FirestarterConfig fromConfig(final Config hocon) throws IllegalArgumentException
  {
    Preconditions.checkNotNull(hocon, "hocon cannot be null");

    final Config resolved = hocon.resolve();
    final Config jvms = resolved.getConfig("jvms");

    final List<String> vmKeys = jvms.entrySet().stream()
        .map(Map.Entry::getKey)
        .map(str -> str.substring(0, str.indexOf('.')))
        .distinct()
        .collect(Collectors.toList());

    return new FirestarterConfig(
        resolved.getString("name"),
        vmKeys.stream()
            .map(key -> VmConfig.fromConfig(key, jvms.getConfig(key)))
            .collect(Collectors.toList()));
  }

  /**
   * Gets the configuration name.
   * @return The name.
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * Gets the JVM configurations.
   * @return The JVM configurations.
   */
  public ImmutableList<VmConfig> getJvms()
  {
    return this.jvms;
  }
}
