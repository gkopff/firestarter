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

import com.google.common.base.Charsets;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link Firestarter}.
 */
public class FirestarterTest
{
  /**
   * Tests that processing a VM configuration results in the correct command line string.
   */
  @Test
  public void testProcess() throws FileNotFoundException
  {
    final JarLocator locator = new StaticDirJarLocator(Paths.get("/home/yossarian/"));
    final FirestarterConfig config = FirestarterConfig.fromJson(reader("/FirestarterTest_Process.json"));

    final String line = Firestarter.process(locator, config.getName(), config.getParameters(), config.getJvms().get(0));
    final String expected =
        "java -server -Xms128M -Xmx128M -Dfirestarter.vmname=TestJvm1 -jar /home/yossarian/target1-0.0.1-SNAPSHOT.jar" +
        " -switch value -option verbose";

    assertThat(line, is(expected));
  }

  /**
   * Tests that the {@link FileNotFoundException} is raised if the jar file cannot be located.
   */
  @Test(expected = FileNotFoundException.class)
  public void testProcessJarFileNotFound() throws FileNotFoundException
  {
    final JarLocator locator = new AlwaysFailsJarLocator();
    final FirestarterConfig config = FirestarterConfig.fromJson(reader("/FirestarterTest_Process.json"));

    Firestarter.process(locator, config.getName(), config.getParameters(), config.getJvms().get(0));
  }

  /**
   * Gets a resource from the classpath.
   * @param resource The path of the resource.
   * @return A reader pointing to the resource.
   */
  private static BufferedReader reader(final String resource)
  {
    return new BufferedReader(new InputStreamReader(FirestarterConfig.class.getResourceAsStream(resource), Charsets.UTF_8));
  }
}