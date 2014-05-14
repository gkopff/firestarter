package com.fatboyindustrial.firestarter;

import java.nio.file.Path;
import java.util.Optional;

/**
 * A strategy for locating jar files.
 */
public interface JarLocator
{
  /**
   * Locates the jar of the specified filename.
   * @param filename The filename.
   * @return The path to the jar file, or absent.
   */
  public Optional<Path> locate(String filename);
}
