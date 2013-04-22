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

import static com.revetkn.ios.analyzer.ImageType.IMAGE_TYPE_PNG;
import static com.revetkn.ios.analyzer.ImageUtilities.scaleImageUpToFit;
import static java.io.File.separator;
import static java.lang.Runtime.getRuntime;
import static java.lang.String.format;
import static java.util.Collections.emptySet;
import static java.util.Collections.synchronizedSortedMap;
import static java.util.Collections.synchronizedSortedSet;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.io.FileUtils.listFiles;
import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 * Performs artwork analysis and retina image generation for an iOS project.
 * <p>
 * <ul>
 * <li>Use {@link #extractApplicationArtwork(File)} to analyze and return data for all artwork in the supplied iOS
 * project directory.</li>
 * <li>Use {@link #generateRetinaImages(Iterable, File)} to create retina images given a set of nonretina images.</li>
 * </ul>
 * <p>
 * This class is threadsafe and immutable.
 * 
 * @author <a href="http://revetkn.com">Mark Allen</a>
 */
public class ArtworkAnalyzer {
  /** Backing thread pool used for concurrent execution of image processing tasks. */
  private ExecutorService executorService;

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

  /**
   * Creates a new artwork analyzer.
   */
  public ArtworkAnalyzer() {
    this.executorService = createExecutorService();
  }

  public void shutdown() {
    getExecutorService().shutdown();
  }

  /**
   * Scans the given iOS project root directory and performs analysis on the contents of its artwork. This may take a
   * while for larger projects. All available CPU cores are utilized to perform parallel processing when possible.
   * 
   * @throws ArtworkProcessingException
   *           If an error occurs during extraction/analysis.
   */
  public ApplicationArtwork extractApplicationArtwork(File projectRootDirectory) {
    return extractApplicationArtwork(projectRootDirectory, new ArtworkExtractionProgressCallback() {
      @Override
      public void onProcessedImageReferences(File imageFile, SortedSet<File> filesWhereImageIsReferenced,
          int currentImageFileNumber, int totalImageFiles) {}
    });
  }

  /**
   * Scans the given iOS project root directory and performs analysis on the contents of its artwork. This may take a
   * while for larger projects. All available CPU cores are utilized to perform parallel processing when possible.
   * 
   * @throws ArtworkProcessingException
   *           If an error occurs during extraction/analysis.
   */
  public ApplicationArtwork extractApplicationArtwork(File projectRootDirectory,
      ArtworkExtractionProgressCallback progressCallback) {
    if (projectRootDirectory == null)
      throw new NullPointerException("The 'projectRootDirectory' parameter cannot be null.");
    if (!projectRootDirectory.exists())
      throw new IllegalArgumentException(format("Directory '%s' does not exist.", projectRootDirectory));
    if (!projectRootDirectory.isDirectory())
      throw new IllegalArgumentException(format("'%s' is a regular file - it must be a directory.",
        projectRootDirectory));
    if (progressCallback == null)
      throw new NullPointerException("The 'progressCallback' parameter cannot be null.");

    try {
      ApplicationArtwork applicationArtwork = new ApplicationArtwork();
      applicationArtwork.setAllImageFiles(extractAllImageFiles(projectRootDirectory));

      detectImageMetrics(applicationArtwork);
      detectImageReferences(projectRootDirectory, applicationArtwork, progressCallback);
      detectRetinaAndNonretinaImages(applicationArtwork);
      detectStandardApplicationImages(applicationArtwork);

      applicationArtwork
        .setIncorrectlySizedRetinaImageFiles(extractIncorrectlySizedRetinaImageFiles(applicationArtwork));
      applicationArtwork
        .setImageFilesWithIncorrectDeviceSuffix(extractImageFilesWithIncorrectDeviceSuffix(applicationArtwork
          .getAllImageFiles()));

      return applicationArtwork;
    } catch (Throwable throwable) {
      throw new ArtworkProcessingException(throwable);
    }
  }

  public void generateRetinaImages(File projectRootDirectory, File outputDirectory, Set<File> nonretinaImageFiles) {
    generateRetinaImages(projectRootDirectory, outputDirectory, nonretinaImageFiles,
      new RetinaImageGenerationProgressCallback() {
        @Override
        public void generatedRetinaImage(File nonretinaSourceImageFile, File generatedRetinaImageFile,
            int retinaImageFilesGenerated, int totalRetinaImageFilesToGenerate) {}
      });
  }

  public void generateRetinaImages(final File projectRootDirectory, final File outputDirectory,
      final Set<File> nonretinaImageFiles, final RetinaImageGenerationProgressCallback progressCallback) {
    if (projectRootDirectory == null)
      throw new NullPointerException("The 'projectRootDirectory' parameter cannot be null.");
    if (!projectRootDirectory.exists())
      throw new IllegalArgumentException(format("Directory '%s' does not exist.", projectRootDirectory));
    if (!projectRootDirectory.isDirectory())
      throw new IllegalArgumentException(format("'%s' is a regular file - it must be a directory.",
        projectRootDirectory));
    if (outputDirectory == null)
      throw new NullPointerException("The 'outputDirectory' parameter cannot be null.");
    if (outputDirectory.exists() && !outputDirectory.isDirectory())
      throw new IllegalArgumentException(format("'%s' is a regular file - it must be a directory.", outputDirectory));
    if (nonretinaImageFiles == null)
      throw new NullPointerException("The 'nonretinaImageFiles' parameter cannot be null.");
    if (progressCallback == null)
      throw new NullPointerException("The 'progressCallback' parameter cannot be null.");

    Set<Callable<Object>> retinaScalingTasks = new HashSet<Callable<Object>>();
    final AtomicInteger imageFilesProcessed = new AtomicInteger(0);

    for (final File nonretinaImageFile : nonretinaImageFiles) {
      retinaScalingTasks.add(new Callable<Object>() {
        @Override
        public Object call() throws Exception {
          byte[] imageFileData = readFileToByteArray(nonretinaImageFile);
          ImageMetrics imageMetrics = ImageUtilities.extractImageMetrics(imageFileData);
          int retinaWidth = imageMetrics.getWidth() * 2;
          int retinaHeight = imageMetrics.getHeight() * 2;

          byte[] retinaImageData = scaleImageUpToFit(imageFileData, retinaWidth, retinaHeight, IMAGE_TYPE_PNG);

          String absoluteRetinaImageFilename = retinaImageFilename(nonretinaImageFile.getAbsolutePath());

          int rootDirectoryPathLength = projectRootDirectory.getAbsolutePath().length();
          String relativeRetinaImageFilename = absoluteRetinaImageFilename.substring(rootDirectoryPathLength + 1);

          String retinaImageFilename = outputDirectory.getAbsolutePath() + separator + relativeRetinaImageFilename;

          File retinaImageFile = new File(retinaImageFilename);
          writeByteArrayToFile(retinaImageFile, retinaImageData);

          progressCallback.generatedRetinaImage(nonretinaImageFile, retinaImageFile,
            imageFilesProcessed.incrementAndGet(), nonretinaImageFiles.size());

          return null;
        }
      });
    }

    try {
      for (Future<Object> future : getExecutorService().invokeAll(retinaScalingTasks))
        future.get();
    } catch (Throwable throwable) {
      throw new ArtworkProcessingException(throwable);
    }
  }

  protected void detectRetinaAndNonretinaImages(ApplicationArtwork applicationArtwork) {
    SortedSet<String> allImageFilenames = extractFilenames(applicationArtwork.getAllImageFiles());
    SortedSet<File> retinaImageFiles = new TreeSet<File>();
    SortedSet<File> nonretinaImageFiles = new TreeSet<File>();
    SortedSet<File> nonretinaImageFilesMissingRetinaImages = new TreeSet<File>();
    SortedSet<File> retinaImageFilesMissingNonretinaImages = new TreeSet<File>();

    for (String imageFilename : allImageFilenames) {
      int lastIndexOf2x = imageFilename.lastIndexOf("@2x");

      // Nonretina; search for a retina version
      if (lastIndexOf2x == -1) {
        nonretinaImageFiles.add(new File(imageFilename));

        if (!allImageFilenames.contains(retinaImageFilename(imageFilename)))
          nonretinaImageFilesMissingRetinaImages.add(new File(imageFilename));
      } else {
        retinaImageFiles.add(new File(imageFilename));

        String nonretinaImageFilename = imageFilename.replace("@2x", "");

        if (!allImageFilenames.contains(nonretinaImageFilename))
          retinaImageFilesMissingNonretinaImages.add(new File(imageFilename));
      }
    }

    applicationArtwork.setRetinaImageFiles(retinaImageFiles);
    applicationArtwork.setNonretinaImageFiles(nonretinaImageFiles);
    applicationArtwork.setNonretinaImageFilesMissingRetinaImages(nonretinaImageFilesMissingRetinaImages);
    applicationArtwork.setRetinaImageFilesMissingNonretinaImages(retinaImageFilesMissingNonretinaImages);
  }

  /** Modifies the passed-in {@code applicationArtwork} instance to include image reference data. */
  protected void detectImageReferences(File projectRootDirectory, final ApplicationArtwork applicationArtwork,
      final ArtworkExtractionProgressCallback progressCallback) throws Exception {
    final Map<File, String> contentsOfReferencingFiles = extractContentsOfReferencingFiles(projectRootDirectory);
    final SortedSet<File> unreferencedImageFiles = synchronizedSortedSet(new TreeSet<File>());
    final SortedSet<File> onlyProjectFileReferencedImageFiles = synchronizedSortedSet(new TreeSet<File>());
    final SortedMap<File, SortedSet<File>> allImageFilesAndReferencingFiles =
        synchronizedSortedMap(new TreeMap<File, SortedSet<File>>());

    final AtomicInteger imageFilesProcessed = new AtomicInteger(0);

    Set<Callable<Object>> imageReferenceProcessingTasks = new HashSet<Callable<Object>>();

    for (final File imageFile : applicationArtwork.getAllImageFiles()) {
      imageReferenceProcessingTasks.add(new Callable<Object>() {
        @Override
        public Object call() throws Exception {
          String imageFilename = imageFile.getName();
          SortedSet<File> filesWhereImageIsReferenced = new TreeSet<File>();
          Set<String> imageFilenameVariants = imageFilenameVariants(imageFilename);

          for (Entry<File, String> entry : contentsOfReferencingFiles.entrySet()) {
            String fileContents = entry.getValue();

            for (String imageFilenameVariant : imageFilenameVariants) {
              // Quoted references, e.g. "aboutBackground"
              if (fileContents.indexOf(format("\"%s\"", imageFilenameVariant)) != -1)
                filesWhereImageIsReferenced.add(entry.getKey());

              // Nib files, e.g. <string key="NSResourceName">aboutBackground~ipad.png</string>
              else if (fileContents.indexOf(format(">%s<", imageFilenameVariant)) != -1)
                filesWhereImageIsReferenced.add(entry.getKey());
            }
          }

          if (filesWhereImageIsReferenced.size() == 1
              && "project.pbxproj".equals(filesWhereImageIsReferenced.first().getName().toLowerCase()))
            onlyProjectFileReferencedImageFiles.add(imageFile);

          if (filesWhereImageIsReferenced.size() == 0) {
            unreferencedImageFiles.add(imageFile);
          } else {
            allImageFilesAndReferencingFiles.put(imageFile, filesWhereImageIsReferenced);
          }

          progressCallback.onProcessedImageReferences(imageFile, filesWhereImageIsReferenced,
            imageFilesProcessed.incrementAndGet(), applicationArtwork.getAllImageFiles().size());

          return null;
        }
      });
    }

    for (Future<Object> future : getExecutorService().invokeAll(imageReferenceProcessingTasks))
      future.get();

    applicationArtwork.setAllImageFilesAndReferencingFiles(allImageFilesAndReferencingFiles);
    applicationArtwork.setUnreferencedImageFiles(unreferencedImageFiles);
    applicationArtwork.setOnlyProjectFileReferencedImageFiles(onlyProjectFileReferencedImageFiles);
  }

  protected void detectStandardApplicationImages(ApplicationArtwork applicationArtwork) {
    SortedSet<File> standardApplicationImageFiles = new TreeSet<File>();
    SortedSet<String> missingStandardApplicationImageFilenames = new TreeSet<String>();

    for (String standardImageFilename : STANDARD_APPLICATION_IMAGE_FILENAMES) {
      boolean foundStandardImage = false;

      for (File imageFile : applicationArtwork.getAllImageFiles()) {
        if (imageFile.getName().equals(standardImageFilename)) {
          standardApplicationImageFiles.add(imageFile);
          foundStandardImage = true;
        }
      }

      if (!foundStandardImage)
        missingStandardApplicationImageFilenames.add(standardImageFilename);
    }

    applicationArtwork.setStandardApplicationImageFiles(standardApplicationImageFiles);
    applicationArtwork.setMissingStandardApplicationImageFilenames(missingStandardApplicationImageFilenames);
  }

  /** @return All image files in the project. */
  protected SortedSet<File> extractAllImageFiles(File projectRootDirectory) {
    SortedSet<File> allImageFiles = new TreeSet<File>();

    for (File pngFile : listFiles(projectRootDirectory,
      new SuffixFileFilter(new ArrayList<String>(imageFileSuffixes())), new NotFileFilter(new NameFileFilter(
        new ArrayList<String>(ignoredDirectoryNames())))))
      allImageFiles.add(pngFile);

    return allImageFiles;
  }

  protected SortedSet<String> extractFilenames(Iterable<File> files) {
    SortedSet<String> filenames = new TreeSet<String>();
    for (File file : files)
      filenames.add(file.getAbsolutePath());
    return filenames;
  }

  protected void detectImageMetrics(ApplicationArtwork applicationArtwork) throws IOException {
    SortedMap<File, ImageMetrics> allImageFilesWithMetrics = new TreeMap<File, ImageMetrics>();
    double sizeOfAllImagesFilesInBytes = 0;

    for (File imageFile : applicationArtwork.getAllImageFiles()) {
      byte[] imageData = readFileToByteArray(imageFile);
      sizeOfAllImagesFilesInBytes += imageData.length;
      allImageFilesWithMetrics.put(imageFile, ImageUtilities.extractImageMetrics(imageData));
    }

    applicationArtwork.setAllImageFilesWithMetrics(allImageFilesWithMetrics);
    applicationArtwork.setSizeOfAllImagesFilesInBytes(sizeOfAllImagesFilesInBytes);
  }

  protected SortedSet<File> extractIncorrectlySizedRetinaImageFiles(ApplicationArtwork applicationArtwork) {
    SortedSet<File> incorrectlySizedRetinaImageFiles = new TreeSet<File>();

    for (File retinaImageFile : applicationArtwork.getRetinaImageFiles()) {
      ImageMetrics imageMetrics = applicationArtwork.getAllImageFilesWithMetrics().get(retinaImageFile);
      if (imageMetrics.getWidth() % 2 != 0 || imageMetrics.getHeight() % 2 != 0)
        incorrectlySizedRetinaImageFiles.add(retinaImageFile);
    }

    return incorrectlySizedRetinaImageFiles;
  }

  protected SortedSet<File> extractImageFilesWithIncorrectDeviceSuffix(Iterable<File> imageFiles) {
    SortedSet<File> imageFilesWithIncorrectDeviceSuffix = new TreeSet<File>();

    for (File imageFile : imageFiles)
      if (imageFile.getName().contains("~iphone"))
        imageFilesWithIncorrectDeviceSuffix.add(imageFile);

    return imageFilesWithIncorrectDeviceSuffix;
  }

  /** @return Mapping of files that could potentially include image references -> their textual contents. */
  protected Map<File, String> extractContentsOfReferencingFiles(File projectRootDirectory) throws IOException {
    Map<File, String> referencingFilesToContents = new HashMap<File, String>();

    for (File textFile : listFiles(projectRootDirectory, new SuffixFileFilter(new ArrayList<String>(
      referencingFileSuffixes())), TrueFileFilter.INSTANCE)) {
      String contents = readFileToString(textFile);

      if (contents.length() > 0)
        referencingFilesToContents.put(textFile, contents);
    }

    return referencingFilesToContents;
  }

  /**
   * @return All possible variants of the given image file. For example, an input of {@code background.png} would return
   *         values like {@code background@2x.png}, {@code background~ipad.png}, etc.
   */
  SortedSet<String> imageFilenameVariants(String imageFilename) {
    SortedSet<String> filenameVariants = new TreeSet<String>();

    filenameVariants.add(imageFilename);

    // Remove .png
    imageFilename = imageFilename.substring(0, imageFilename.lastIndexOf("."));
    filenameVariants.add(imageFilename);

    // Remove @2x
    int lastIndexOf2x = imageFilename.lastIndexOf("@2x");
    if (lastIndexOf2x >= 0) {
      imageFilename = imageFilename.substring(0, lastIndexOf2x);
      filenameVariants.add(imageFilename);
    }

    // Remove ~ipad
    int lastIndexOfIpad = imageFilename.lastIndexOf("~ipad");
    if (lastIndexOfIpad >= 0) {
      imageFilename = imageFilename.substring(0, lastIndexOfIpad);
      filenameVariants.add(imageFilename);
    }

    // Remove ~iphone
    int lastIndexOfIphone = imageFilename.lastIndexOf("~iphone");
    if (lastIndexOfIphone >= 0) {
      imageFilename = imageFilename.substring(0, lastIndexOfIphone);
      filenameVariants.add(imageFilename);
    }

    // We must be in our most basic form now, e.g. "ma" from original ma@2x~ipad.png.
    // Let's work back up and add all possible variants in case there were any wacky references to the image, like
    // "ma.png"
    filenameVariants.add(format("%s.png", imageFilename));
    filenameVariants.add(format("%s~ipad.png", imageFilename));
    filenameVariants.add(format("%s~iphone.png", imageFilename));
    filenameVariants.add(format("%s@2x.png", imageFilename));
    filenameVariants.add(format("%s@2x~ipad.png", imageFilename));
    filenameVariants.add(format("%s@2x~iphone.png", imageFilename));

    return filenameVariants;
  }

  protected String retinaImageFilename(String imageFilename) {
    // If we're already a retina image, nothing to do
    if (imageFilename.indexOf("@2x") != -1)
      return imageFilename;

    int lastIndexOfIpad = imageFilename.lastIndexOf("~ipad");
    int lastIndexOfIphone = imageFilename.lastIndexOf("~iphone");
    int replacementIndex = -1;

    if (lastIndexOfIpad != -1)
      replacementIndex = lastIndexOfIpad;
    else if (lastIndexOfIphone != -1)
      replacementIndex = lastIndexOfIphone;
    else
      replacementIndex = imageFilename.lastIndexOf(".png");

    return format("%s@2x%s", imageFilename.substring(0, replacementIndex), imageFilename.substring(replacementIndex));
  }

  /**
   * @return Creates a backing thread pool used for concurrent execution of image processing tasks.
   */
  protected ExecutorService createExecutorService() {
    int coreThreadCount = getRuntime().availableProcessors();
    int maximumThreadCount = coreThreadCount;
    int unusedThreadTerminationTimeoutInSeconds = 5;

    ThreadPoolExecutor threadPoolExecutor =
        new ThreadPoolExecutor(coreThreadCount, maximumThreadCount, unusedThreadTerminationTimeoutInSeconds, SECONDS,
          new LinkedBlockingQueue<Runnable>());

    threadPoolExecutor.allowCoreThreadTimeOut(true);

    return threadPoolExecutor;
  }

  /** @return The backing thread pool used for concurrent execution of image processing tasks. */
  protected ExecutorService getExecutorService() {
    return executorService;
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