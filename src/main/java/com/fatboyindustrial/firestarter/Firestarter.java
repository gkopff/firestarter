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
import com.typesafe.config.ConfigFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Main application class. <p>
 *
 * Java command lines are output to standard out.
 */
public class Firestarter
{
  /** The environment variable name that points to the root search directory. */
  private static final String FS_ROOT = "FS_ROOT";

  /**
   * Main method.
   * @param args Command line arguments: JSON file.
   */
  public static void main(final String[] args)
  {
    if (args.length != 1)
    {
      usage();
      System.exit(1);
    }

    final String dotConf = args[0];

    try (final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dotConf), Charsets.UTF_8)))
    {
      final FirestarterConfig cfg = FirestarterConfig.fromConfig(ConfigFactory.parseReader(br));
      final JarLocator locator = new DepthFirstJarLocator(
          getEnvironmentVariable(FS_ROOT)
              .map(Paths::get)
              .orElseThrow(() -> new IOException(FS_ROOT + " is not set")));

      for (final VmConfig vm : cfg.getJvms())
      {
        System.out.println(process(locator, cfg.getName(), vm));
      }
    }
    catch (FileNotFoundException e)
    {
      System.err.println("File not found; " + e);
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
   * @param vm The VM details.
   * @return The command line.
   * @throws FileNotFoundException If the jar file cannot be found.
   */
  @VisibleForTesting
  protected static String process(final JarLocator locator,
                                  @SuppressWarnings("UnusedParameters") final String configName,
                                  final VmConfig vm) throws FileNotFoundException
  {
    final List<String> cmd = new ArrayList<>();

    cmd.add("java");
    cmd.add("-server");
    cmd.add("-XX:+UseConcMarkSweepGC");
    cmd.add("-XX:+HeapDumpOnOutOfMemoryError");

    final Optional<String> fsRoot = getEnvironmentVariable(FS_ROOT);
    fsRoot.ifPresent(root -> cmd.add(String.format("-XX:HeapDumpPath=%s", root)));

    cmd.add(String.format("-Xms%dM", vm.getHeap()));
    cmd.add(String.format("-Xmx%dM", vm.getHeap()));

    cmd.add(String.format("-Dfirestarter.vmname=%s", vm.getName()));

    cmd.addAll(
        vm.getProperties().entrySet().stream()
            .map(entry -> String.format("\"-D%s=%s\"", entry.getKey(), entry.getValue()))
            .collect(Collectors.toList()));

    cmd.add("-jar");
    cmd.add(
        locator.locate(vm.getJar())
            .map(Path::toString)
            .orElseThrow(() -> new FileNotFoundException(vm.getJar())));

    cmd.addAll(vm.getArguments());

    return cmd.stream().collect(Collectors.joining(" "));
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
    System.err.println("fs.sh <config>");
    System.err.println("Environment variable '" + FS_ROOT + "' must be set to the jar search root directory.");
  }
}
