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

/**
 * 
 * @author <a href="http://revetkn.com">Mark Allen</a>
 */
public class ApplicationArtwork {
  private SortedSet<File> allImages;
  private SortedMap<File, SortedSet<File>> allImagesAndReferencingFiles;
  private SortedSet<File> unreferencedImages;
  private SortedSet<File> onlyProjectFileReferencedImages;
  private SortedSet<File> retinaImages;
  private SortedSet<File> nonretinaImages;
  private SortedSet<File> nonretinaImagesMissingRetinaImages;
  private SortedSet<File> retinaImagesMissingNonretinaImages;
  private SortedSet<File> missingApplicationImages;

  /** Example: background~iphone.png */
  private SortedSet<File> imagesWithIncorrectDeviceSuffix;

  private SortedSet<File> incorrectlySizedRetinaImages;

  private long sizeOfAllImagesInBytes;

  public SortedSet<File> getAllImages() {
    return allImages;
  }

  public void setAllImages(SortedSet<File> allImages) {
    this.allImages = allImages;
  }

  public SortedMap<File, SortedSet<File>> getAllImagesAndReferencingFiles() {
    return allImagesAndReferencingFiles;
  }

  public void setAllImagesAndReferencingFiles(SortedMap<File, SortedSet<File>> allImagesAndReferencingFiles) {
    this.allImagesAndReferencingFiles = allImagesAndReferencingFiles;
  }

  public SortedSet<File> getUnreferencedImages() {
    return unreferencedImages;
  }

  public void setUnreferencedImages(SortedSet<File> unreferencedImages) {
    this.unreferencedImages = unreferencedImages;
  }

  public SortedSet<File> getOnlyProjectFileReferencedImages() {
    return onlyProjectFileReferencedImages;
  }

  public void setOnlyProjectFileReferencedImages(SortedSet<File> onlyProjectFileReferencedImages) {
    this.onlyProjectFileReferencedImages = onlyProjectFileReferencedImages;
  }

  public SortedSet<File> getRetinaImages() {
    return retinaImages;
  }

  public void setRetinaImages(SortedSet<File> retinaImages) {
    this.retinaImages = retinaImages;
  }

  public SortedSet<File> getNonretinaImages() {
    return nonretinaImages;
  }

  public void setNonretinaImages(SortedSet<File> nonretinaImages) {
    this.nonretinaImages = nonretinaImages;
  }

  public SortedSet<File> getNonretinaImagesMissingRetinaImages() {
    return nonretinaImagesMissingRetinaImages;
  }

  public void setNonretinaImagesMissingRetinaImages(SortedSet<File> nonretinaImagesMissingRetinaImages) {
    this.nonretinaImagesMissingRetinaImages = nonretinaImagesMissingRetinaImages;
  }

  public SortedSet<File> getRetinaImagesMissingNonretinaImages() {
    return retinaImagesMissingNonretinaImages;
  }

  public void setRetinaImagesMissingNonretinaImages(SortedSet<File> retinaImagesMissingNonretinaImages) {
    this.retinaImagesMissingNonretinaImages = retinaImagesMissingNonretinaImages;
  }

  public SortedSet<File> getMissingApplicationImages() {
    return missingApplicationImages;
  }

  public void setMissingApplicationImages(SortedSet<File> missingApplicationImages) {
    this.missingApplicationImages = missingApplicationImages;
  }

  public SortedSet<File> getImagesWithIncorrectDeviceSuffix() {
    return imagesWithIncorrectDeviceSuffix;
  }

  public void setImagesWithIncorrectDeviceSuffix(SortedSet<File> imagesWithIncorrectDeviceSuffix) {
    this.imagesWithIncorrectDeviceSuffix = imagesWithIncorrectDeviceSuffix;
  }

  public SortedSet<File> getIncorrectlySizedRetinaImages() {
    return incorrectlySizedRetinaImages;
  }

  public void setIncorrectlySizedRetinaImages(SortedSet<File> incorrectlySizedRetinaImages) {
    this.incorrectlySizedRetinaImages = incorrectlySizedRetinaImages;
  }

  public long getSizeOfAllImagesInBytes() {
    return sizeOfAllImagesInBytes;
  }

  public void setSizeOfAllImagesInBytes(long sizeOfAllImagesInBytes) {
    this.sizeOfAllImagesInBytes = sizeOfAllImagesInBytes;
  }
}