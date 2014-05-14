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
import com.google.gson.Gson;

import java.io.Reader;
import java.util.List;
import java.util.Map;

/**
 * The outermost configuration object.
 */
public class FirestarterConfig
{
  /** The configuration name. */
  private String name;

  /** Variable substitution parameters. */
  private Map<String, String> parameters;

  /** VM details. */
  private List<VmConfig> jvms;

  /**
   * Creates a firestarter config.
   * @param reader The reader from which to source the JSON.
   * @return The firestarter configuration object.
   * @throws IllegalArgumentException If the configuration contains errors.
   */
  public static FirestarterConfig fromJson(final Reader reader) throws IllegalArgumentException
  {
    final Gson gson = new Gson();
    final FirestarterConfig cfg =  gson.fromJson(reader, FirestarterConfig.class);

    cfg.validate();
    return cfg;
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
   * Gets the variable substitution parameters.
   * @return The parameters.
   */
  public ImmutableMap<String, String> getParameters()
  {
    return ImmutableMap.copyOf(this.parameters);
  }

  /**
   * Gets the JVM configurations.
   * @return The JVM configurations.
   */
  public ImmutableList<VmConfig> getJvms()
  {
    return ImmutableList.copyOf(this.jvms);
  }

  /**
   * Validates the configuration.
   * @throws IllegalArgumentException If the configuration is invalid.
   */
  private void validate() throws IllegalArgumentException
  {
    Preconditions.checkArgument(this.name.indexOf(' ') == -1, "FirestarterConfig.name cannot contain spaces");

    this.jvms.stream().forEach(VmConfig::validate);
  }
}
