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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Main application class. <p>
 *
 * Java command lines are output to standard out.
 */
public class Firestarter
{
  /**
   * Main method.
   * @param args Command line arguments: JSON file.
   */
  public static void main(final String[] args)
  {
    if (args.length != 2)
    {
      usage();
      System.exit(1);
    }

    final String rootVar = args[0];
    final String json = args[1];

    try (final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(json), Charsets.UTF_8)))
    {
      final FirestarterConfig cfg = FirestarterConfig.fromJson(br);
      final JarLocator locator = new DepthFirstJarLocator(
          getEnvironmentVariable(rootVar)
              .map(Paths::get)
              .orElseThrow(() -> new IOException(rootVar + " is not set")));

      for (final VmConfig vm : cfg.getJvms())
      {
        System.out.println(process(locator, cfg.getName(), cfg.getParameters(), vm));
      }
    }
    catch (FileNotFoundException e)
    {
      System.err.println("Input file not found");
      System.exit(1);
    }
    catch (IOException e)
    {
      System.err.println("i/o exception: " + e);
      System.exit(1);
    }
  }

  /**
   * Process a single VM.
   * @param locator The jar locator.
   * @param configName The overarching configuration name.
   * @param params The parameter substitution keys and values.
   * @param vm The VM details.
   * @return The command line.
   * @throws FileNotFoundException If the jar file cannot be found.
   */
  @VisibleForTesting
  protected static String process(final JarLocator locator,
                                  final String configName,
                                  final Map<String, String> params,
                                  final VmConfig vm) throws FileNotFoundException
  {
    final List<String> cmd = new ArrayList<>();

    cmd.add("java");
    cmd.add("-server");

    cmd.add(String.format("-Xms%dM", vm.getHeap()));
    cmd.add(String.format("-Xmx%dM", vm.getHeap()));

    cmd.add(String.format("-Dfirestarter.vmname=%s", resolve(params, vm.getName())));

    cmd.add("-jar");
    cmd.add(locator.locate(resolve(params, vm.getJar()))
                .map(Path::toString)
                .orElseThrow(() -> new FileNotFoundException(resolve(params, vm.getJar()))));

    cmd.addAll(vm.getArguments());

    return cmd.stream().collect(Collectors.joining(" "));
  }

  /**
   * Performs parameter substitution for the given text.
   * @param params The parameter substitution keys and values.
   * @param text The text to transform.
   * @return The text with any parameters substituted with their values.
   */
  private static String resolve(final Map<String, String> params, final String text)
  {
    String transformed = text;

    for (final Map.Entry<String, String> entry : params.entrySet())
    {
      transformed = transformed.replace("${" + entry.getKey() + "}", entry.getValue());
    }

    return transformed;
  }

  /**
   * Gets the named environment variable.
   * @param var The variable name.
   * @return The variable's value.
   */
  private static Optional<String> getEnvironmentVariable(final String var)
  {
    return Optional.ofNullable(System.getenv(var));
  }

  /**
   * Prints usage information to standard error.
   */
  private static void usage()
  {
    System.err.println("fs.sh <root env name> <json>");
  }
}
