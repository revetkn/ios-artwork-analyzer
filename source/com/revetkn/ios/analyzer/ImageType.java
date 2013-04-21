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

/**
 * Types of images the JDK knows about.
 * 
 * @author <a href="http://revetkn.com">Mark Allen</a>
 */
enum ImageType {
  /** A JPEG-formatted image. */
  IMAGE_TYPE_JPEG("jpg", "image/jpeg"),

  /** A PNG-formatted image. */
  IMAGE_TYPE_PNG("png", "image/png"),

  /** A GIF-formatted image. */
  IMAGE_TYPE_GIF("gif", "image/gif");

  private String jdkImageFormatName;
  private String contentType;

  private ImageType(String jdkImageFormatName, String contentType) {
    this.jdkImageFormatName = jdkImageFormatName;
    this.contentType = contentType;
  }

  /**
   * Gets a JDK-approved string representation of this image type.
   * 
   * @return A string representation of this image type.
   */
  public String getJdkImageFormatName() {
    return jdkImageFormatName;
  }

  /**
   * The HTTP Content-Type value of this image type, for example {@code image/png}.
   * 
   * @return The HTTP Content-Type value of this image type.
   */
  public String getContentType() {
    return contentType;
  }
}
