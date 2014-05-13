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
import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link FirestarterConfig}.
 */
public class FirestarterConfigTest
{
  /**
   * Tests that the JSON deserialises correctly.
   */
  @Test
  public void testFromJson()
  {
    final FirestarterConfig config = FirestarterConfig.fromJson(reader("/FirestarterConfigTest.json"));

    assertThat(config.getName(), is("test"));
    assertThat(config.getParameters().size(), is(2));

    assertThat(config.getParameters().keySet().containsAll(ImmutableSet.of("VERSION", "VARIANT")), is(true));
    assertThat(config.getParameters().values().containsAll(ImmutableSet.of("0.0.1-SNAPSHOT", "Z")), is(true));

    assertThat(config.getJvms().size(), is(2));

    VmConfig vm;

    vm = config.getJvms().get(0);
    assertThat(vm.getName(), is("TestJvm1"));
    assertThat(vm.getHeap(), is(128));
    assertThat(vm.getJar(), is("target1-${VERSION}.jar"));
    assertThat(vm.getArguments().isEmpty(), is(true));

    vm = config.getJvms().get(1);
    assertThat(vm.getName(), is("TestJvm2"));
    assertThat(vm.getHeap(), is(64));
    assertThat(vm.getJar(), is("target2-${VERSION}-${VARIANT}.jar"));
    assertThat(vm.getArguments().size(), is(4));
    assertThat(vm.getArguments().get(0), is("-switch"));
    assertThat(vm.getArguments().get(1), is("value"));
    assertThat(vm.getArguments().get(2), is("-variant"));
    assertThat(vm.getArguments().get(3), is("${VARIANT}"));
  }

  private static BufferedReader reader(final String resource)
  {
    return new BufferedReader(new InputStreamReader(FirestarterConfig.class.getResourceAsStream(resource), Charsets.UTF_8));
  }
}