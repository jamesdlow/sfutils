/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: UploadException.java,v 1.3 2003/07/03 14:41:12 ljnelson Exp $
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
package sfutils.frs.web;

import sfutils.frs.PublishingException;

/**
 * A {@link PublishingException} that is thrown when a multithreaded file upload
 * operation fails.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.3 $ $Date: 2003/07/03 14:41:12 $
 * @since      June 19, 2003
 */
public class UploadException extends PublishingException {

  /**
   * The {@link Exception}s that caused this {@link UploadException} to be
   * created and thrown.  This field may be <code>null</code>.
   */
  private final Exception[] causes;

  /**
   * Creates a new {@link UploadException}.
   *
   * @param      causes
   *               the {@link Exception}s that caused this {@link
   *               UploadException} to be created and thrown; may be
   *               <code>null</code>
   */
  public UploadException(final Exception[] causes) {
    super(causes == null ? null : causes[0]);
    this.causes = causes;
  }

  /**
   * Returns the {@link Exception}s that caused this {@link UploadException} to
   * be thrown.  This method may return <code>null</code>.
   *
   * @return     the {@link Exception}s that caused this {@link UploadException}
   *               to be thrown, or <code>null</code>
   */
  public Exception[] getCauses() {
    return this.causes;
  }

}
