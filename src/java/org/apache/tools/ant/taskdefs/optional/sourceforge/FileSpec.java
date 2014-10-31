/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: FileSpec.java,v 1.2 2003/07/10 19:48:13 ljnelson Exp $
 *
 * Copyright (c) 2003 Laird Jarrett Nelson.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * The original copy of this license is available at
 * http://www.opensource.org/license/mit-license.html.
 */
package org.apache.tools.ant.taskdefs.optional.sourceforge;

import java.io.File;
import java.io.Serializable;

import java.util.Date;

import org.apache.tools.ant.BuildException;

import sfutils.frs.FileSpecification;

/**
 * A simple adapter class that allows <a href="http://ant.apache.org/">Ant</a>
 * to create and set the attributes of a {@link FileSpecification}.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.2 $ $Date: 2003/07/10 19:48:13 $
 * @since      July 2, 2003
 */
public final class FileSpec implements Serializable {

  /**
   * The {@link FileSpecification} being adapted.  This field is never
   * <code>null</code>.
   */
  private final FileSpecification spec;

  /**
   * Creates a new {@link FileSpec}.
   */
  public FileSpec() {
    super();
    this.spec = new FileSpecification();
  }
  
  /**
   * Sets the {@link File} for the underlying {@link FileSpecification}.
   *
   * @param      file
   *               the {@link File} to install; must not be <code>null</code>
   * @exception  BuildException
   *               if an error occurs
   */
  public final void setFile(final File file) 
    throws BuildException {
    try {
      this.spec.setFile(file);
    } catch (final Exception anything) {
      throw new BuildException(anything);
    }
  }

  /**
   * Sets the {@linkplain FileSpecification#setReleaseDate(Date) release date}
   * for the underlying {@link FileSpecification}.
   *
   * @param      date
   *               the release {@link Date}; must not be <code>null</code>
   * @exception  BuildException
   *               if an error occurs
   */
  public final void setReleaseDate(final Date date) 
    throws BuildException {
    try {
      this.spec.setReleaseDate(date);
    } catch (final Exception anything) {
      throw new BuildException(anything);
    }
  }

  /**
   * Sets the {@linkplain FileSpecification#setProcessorType(int) processor
   * type} on the underlying {@link FileSpecification}.
   *
   * @param      type
   *               the processor type; must not be <code>null</code>
   * @exception  BuildException
   *               if an error occurs
   */
  public final void setProcessorType(final String type) 
    throws BuildException {
    try {
      this.spec.setProcessorTypeString(type);
    } catch (final Exception anything) {
      throw new BuildException(anything);
    }
  }

  /**
   * Sets the {@linkplain FileSpecification#setFileType(int) file type} on the
   * underlying {@link FileSpecification}.
   *
   * @param      type
   *               the file type; must not be <code>null</code>
   * @exception  BuildException
   *               if an error occurs
   */
  public final void setFileType(final String type) 
    throws BuildException {
    try {
      this.spec.setFileTypeString(type);
    } catch (final Exception anything) {
      throw new BuildException(anything);
    }
  }

  /**
   * Returns the underlying {@link FileSpecification}.  This method never
   * returns <code>null</code>.
   *
   * @return     the underlying {@link FileSpecification}; never
   *               <code>null</code>
   */
  public final FileSpecification getFileSpecification() {
    return this.spec;
  }

}
