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

/**
 * Encapsulates image metadata.
 * <p>
 * Instances of this class are immutable.
 * 
 * @author <a href="http://revetkn.com">Mark Allen</a>
 */
public class ImageMetrics {
  private int width;
  private int height;
  private int size;
  private String contentType;

  /**
   * Constructs an immutable {@code ImageMetrics} instance.
   * 
   * @param width
   *          The image's width in pixels.
   * @param height
   *          The image's height in pixels.
   * @param size
   *          The image's size in bytes.
   * @param contentType
   *          The image's content type, for example {@code image/png}.
   */
  public ImageMetrics(int width, int height, int size, String contentType) {
    if (width <= 0)
      throw new IllegalArgumentException("Image width must be > 0");
    if (height <= 0)
      throw new IllegalArgumentException("Image height must be > 0");
    if (size <= 0)
      throw new IllegalArgumentException("Image size must be > 0");
    if (contentType == null || contentType.trim().length() == 0)
      throw new IllegalArgumentException("You must specify a non-empty content type, e.g. image/png.");

    this.width = width;
    this.height = height;
    this.size = size;
    this.contentType = contentType;
  }

  /**
   * @return The image's width in pixels.
   */
  public int getWidth() {
    return width;
  }

  /**
   * @return The image's height in pixels.
   */
  public int getHeight() {
    return height;
  }

  /**
   * @return The image's content type - {@code image/png}, for example.
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * @return The image's size in bytes.
   */
  public int getSize() {
    return size;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object other) {
    if (this == other)
      return true;
    if (!(other instanceof ImageMetrics))
      return false;

    ImageMetrics otherMetrics = (ImageMetrics) other;
    return otherMetrics.getContentType().equals(getContentType()) && otherMetrics.getHeight() == getHeight()
        && otherMetrics.getSize() == getSize() && otherMetrics.getWidth() == getWidth();
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    int hash = 1;
    hash = hash * 31 + getContentType().hashCode();
    hash = hash * 29 + getWidth();
    hash = hash * 17 + getHeight();
    hash = hash * 13 + getSize();
    return hash;
  }

  /**
   * Returns a description containing the {@code width}, {@code height}, {@code size}, and {@code contentType} of the
   * image.
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return format("%s{width=%d, height=%d, size=%d, contentType=%s}", getClass().getSimpleName(), getWidth(),
      getHeight(), getSize(), getContentType());
  }
}
