/*
 * Copyright (c) 2013 Mark Allen.
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

package com.revetkn.ios.analyzer;

import static java.lang.String.format;
import static java.util.Collections.emptySet;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author <a href="http://revetkn.com">Mark Allen</a>
 */
public class ArtworkAnalyzer {
  /** Suffixes for files which may reference images. */
  private static final Set<String> REFERENCING_FILE_SUFFIXES = new HashSet<String>() {
    {
      add(".h");
      add(".m");
      add(".pbxproj");
      add(".xib");
      add(".plist");
      add(".html");
      add(".strings");
    }
  };

  /** Suffixes for image files we'd like to detect. */
  private static final Set<String> IMAGE_FILE_SUFFIXES = new HashSet<String>() {
    {
      add(".png");
    }
  };

  /** Apple-defined standard application images. */
  private static final Set<String> STANDARD_APPLICATION_IMAGE_FILENAMES = new HashSet<String>() {
    {
      add("Default.png");
      add("Default@2x.png");
      add("Default-568h@2x.png");
      add("Default-Landscape.png");
      add("Default-Landscape@2x.png");
      add("Default-Portrait.png");
      add("Default-Portrait@2x.png");
      add("Icon.png");
      add("Icon@2x.png");
      add("Icon-72.png");
      add("Icon-72@2x.png");
      add("Icon-Small-50.png");
      add("Icon-Small-50@2x.png");
      add("Icon-Small.png");
      add("Icon-Small@2x.png");
      add("iTunesArtwork");
      add("iTunesArtwork@2x");
    }
  };

  /** Directories to skip over when detecting images */
  private static final Set<String> IGNORED_DIRECTORY_NAMES = emptySet();

  private static final Logger LOGGER = Logger.getLogger(ArtworkAnalyzer.class.getName());

  /**
   * Scans the given iOS project root directory and performs analysis on the contents of its artwork. This may take a
   * while for larger projects. All available CPU cores are utilized to perform parallel processing when possible.
   */
  public ApplicationArtwork extractApplicationArtwork(File projectRootDirectory) {
    if (projectRootDirectory == null)
      throw new NullPointerException("The 'projectRootDirectory' parameter cannot be null.");
    if (!projectRootDirectory.isDirectory())
      throw new IllegalArgumentException(format("'%s' is a regular file - it must be a directory.",
        projectRootDirectory));

    LOGGER.fine("Starting...");

    ApplicationArtwork applicationArtwork = new ApplicationArtwork();

    return applicationArtwork;
  }

  /** Fails fast if you pass in an already-retina image or if an image is invalid */
  public void generateRetinaImages(Iterable<File> nonretinaImages, File outputDirectory) {
    if (nonretinaImages == null)
      throw new NullPointerException("The 'nonretinaImages' parameter cannot be null.");
    if (outputDirectory == null)
      throw new NullPointerException("The 'outputDirectory' parameter cannot be null.");
    if (!outputDirectory.isDirectory())
      throw new IllegalArgumentException(format("'%s' is a regular file - it must be a directory.", outputDirectory));

    throw new UnsupportedOperationException();
  }

  /**
   * @return Suffixes for files which may reference images. For example: .m, .xib, .pbxproj
   */
  public Set<String> referencingFileSuffixes() {
    return REFERENCING_FILE_SUFFIXES;
  }

  /**
   * @return Suffixes for image files we'd like to detect. For example: .png
   */
  public Set<String> imageFileSuffixes() {
    return IMAGE_FILE_SUFFIXES;
  }

  /**
   * @return Apple-defined standard application images. For example: Icon-72.png
   */
  public Set<String> standardApplicationImageFilenames() {
    return STANDARD_APPLICATION_IMAGE_FILENAMES;
  }

  /**
   * @return Directories to skip over when detecting images. For example: FacebookSDK.framework. Default behavior is to
   *         not skip any directories.
   */
  public Set<String> ignoredDirectoryNames() {
    return IGNORED_DIRECTORY_NAMES;
  }
}