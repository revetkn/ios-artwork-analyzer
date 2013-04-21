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

import java.io.File;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author <a href="http://revetkn.com">Mark Allen</a>
 */
public class ApplicationArtwork {
  private SortedSet<File> allImageFiles = new TreeSet<File>();
  private SortedMap<File, SortedSet<File>> allImageFilesAndReferencingFiles = new TreeMap<File, SortedSet<File>>();
  private SortedMap<File, ImageMetrics> allImageFilesWithMetrics = new TreeMap<File, ImageMetrics>();
  private SortedSet<File> unreferencedImageFiles = new TreeSet<File>();
  private SortedSet<File> onlyProjectFileReferencedImageFiles = new TreeSet<File>();
  private SortedSet<File> retinaImageFiles = new TreeSet<File>();
  private SortedSet<File> nonretinaImageFiles = new TreeSet<File>();
  private SortedSet<File> nonretinaImageFilesMissingRetinaImages = new TreeSet<File>();
  private SortedSet<File> retinaImageFilesMissingNonretinaImages = new TreeSet<File>();
  private SortedSet<File> missingApplicationImageFiles = new TreeSet<File>();

  /** Example: background~iphone.png */
  private SortedSet<File> imagesFilesWithIncorrectDeviceSuffix = new TreeSet<File>();

  private SortedSet<File> incorrectlySizedRetinaImageFiles = new TreeSet<File>();

  private long sizeOfAllImagesFilesInBytes;

  public SortedSet<File> getAllImageFiles() {
    return allImageFiles;
  }

  public void setAllImageFiles(SortedSet<File> allImageFiles) {
    this.allImageFiles = allImageFiles;
  }

  public SortedMap<File, SortedSet<File>> getAllImageFilesAndReferencingFiles() {
    return allImageFilesAndReferencingFiles;
  }

  public void setAllImageFilesAndReferencingFiles(SortedMap<File, SortedSet<File>> allImageFilesAndReferencingFiles) {
    this.allImageFilesAndReferencingFiles = allImageFilesAndReferencingFiles;
  }

  public SortedMap<File, ImageMetrics> getAllImageFilesWithMetrics() {
    return allImageFilesWithMetrics;
  }

  public void setAllImageFilesWithMetrics(SortedMap<File, ImageMetrics> allImageFilesWithMetrics) {
    this.allImageFilesWithMetrics = allImageFilesWithMetrics;
  }

  public SortedSet<File> getUnreferencedImageFiles() {
    return unreferencedImageFiles;
  }

  public void setUnreferencedImageFiles(SortedSet<File> unreferencedImageFiles) {
    this.unreferencedImageFiles = unreferencedImageFiles;
  }

  public SortedSet<File> getOnlyProjectFileReferencedImageFiles() {
    return onlyProjectFileReferencedImageFiles;
  }

  public void setOnlyProjectFileReferencedImageFiles(SortedSet<File> onlyProjectFileReferencedImageFiles) {
    this.onlyProjectFileReferencedImageFiles = onlyProjectFileReferencedImageFiles;
  }

  public SortedSet<File> getRetinaImageFiles() {
    return retinaImageFiles;
  }

  public void setRetinaImageFiles(SortedSet<File> retinaImageFiles) {
    this.retinaImageFiles = retinaImageFiles;
  }

  public SortedSet<File> getNonretinaImageFiles() {
    return nonretinaImageFiles;
  }

  public void setNonretinaImageFiles(SortedSet<File> nonretinaImageFiles) {
    this.nonretinaImageFiles = nonretinaImageFiles;
  }

  public SortedSet<File> getNonretinaImageFilesMissingRetinaImages() {
    return nonretinaImageFilesMissingRetinaImages;
  }

  public void setNonretinaImageFilesMissingRetinaImages(SortedSet<File> nonretinaImageFilesMissingRetinaImages) {
    this.nonretinaImageFilesMissingRetinaImages = nonretinaImageFilesMissingRetinaImages;
  }

  public SortedSet<File> getRetinaImageFilesMissingNonretinaImages() {
    return retinaImageFilesMissingNonretinaImages;
  }

  public void setRetinaImageFilesMissingNonretinaImages(SortedSet<File> retinaImageFilesMissingNonretinaImages) {
    this.retinaImageFilesMissingNonretinaImages = retinaImageFilesMissingNonretinaImages;
  }

  public SortedSet<File> getMissingApplicationImageFiles() {
    return missingApplicationImageFiles;
  }

  public void setMissingApplicationImageFiles(SortedSet<File> missingApplicationImageFiles) {
    this.missingApplicationImageFiles = missingApplicationImageFiles;
  }

  public SortedSet<File> getImagesFilesWithIncorrectDeviceSuffix() {
    return imagesFilesWithIncorrectDeviceSuffix;
  }

  public void setImagesFilesWithIncorrectDeviceSuffix(SortedSet<File> imagesFilesWithIncorrectDeviceSuffix) {
    this.imagesFilesWithIncorrectDeviceSuffix = imagesFilesWithIncorrectDeviceSuffix;
  }

  public SortedSet<File> getIncorrectlySizedRetinaImageFiles() {
    return incorrectlySizedRetinaImageFiles;
  }

  public void setIncorrectlySizedRetinaImageFiles(SortedSet<File> incorrectlySizedRetinaImageFiles) {
    this.incorrectlySizedRetinaImageFiles = incorrectlySizedRetinaImageFiles;
  }

  public long getSizeOfAllImagesFilesInBytes() {
    return sizeOfAllImagesFilesInBytes;
  }

  public void setSizeOfAllImagesFilesInBytes(long sizeOfAllImagesFilesInBytes) {
    this.sizeOfAllImagesFilesInBytes = sizeOfAllImagesFilesInBytes;
  }
}