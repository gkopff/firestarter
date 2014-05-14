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

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * A depth-first strategy for locating jar files.
 */
public class DepthFirstJarLocator implements JarLocator
{
  /** The starting point for all searches. */
  private final Path searchRoot;

  /**
   * Constructor.
   * @param searchRoot The starting point for all searches.
   */
  public DepthFirstJarLocator(final Path searchRoot)
  {
    this.searchRoot = Preconditions.checkNotNull(searchRoot, "searchRoot cannot be null");
  }

  /**
   * Locates the jar of the specified filename.
   * @param filename The filename.
   * @return The path to the jar file, or absent.
   */
  @Override
  public Optional<Path> locate(final String filename)
  {
    Preconditions.checkNotNull(filename, "filename cannot be null");

    return locate(this.searchRoot, filename);
  }

  /**
   * Locates the jar of the specified filename.
   * @param search The directory to search.
   * @param filename The filename.
   * @return The path to the jar file, or absent.
   */
  private static Optional<Path> locate(final Path search, final String filename)
  {
    final File root = search.toFile();

    // Find the files in this directory ...

    final List<File> files = ImmutableList.copyOf(root.listFiles(File::isFile));

    // ... and search for the jar file ...

    final Optional<Path> path = files.stream()
        .filter(file -> file.getName().equals(filename))
        .map(file -> file.getAbsoluteFile().toPath())
        .findFirst();

    if (path.isPresent())
    {
      return path;
    }

    // Find the directories in this directory ...

    final List<File> dirs = ImmutableList.copyOf(root.listFiles(File::isDirectory));

    // ... and search these directories ...

    for (final File dir : dirs)
    {
      final Optional<Path> result = locate(dir.getAbsoluteFile().toPath(), filename);
      if (result.isPresent())
      {
        return result;
      }
    }

    return Optional.empty();
  }
}
