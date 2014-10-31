/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: PublishingException.java,v 1.3 2003/07/13 02:47:02 ljnelson Exp $
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
package sfutils.frs;

import sfutils.SourceForgeException;

/**
 * A {@link SourceForgeException} that indicates that something went wrong with
 * the {@linkplain FileRelease#publish() <code>FileRelease</code> publishing
 * process}.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.3 $ $Date: 2003/07/13 02:47:02 $
 * @since      July 3, 2003
 */
public class PublishingException extends SourceForgeException {

  /**
   * Creates a new {@link PublishingException} that was thrown because another
   * {@link Exception} was encountered.
   *
   * @param      cause
   *               the {@link Exception} that caused this {@link
   *               PublishingException} to be created; may be <code>null</code>
   */
  public PublishingException(final Exception cause) {
    super(cause);
  }

  /**
   * Creates a new {@link PublishingException}.
   *
   * @param      message
   *               a message describing the new {@link PublishingException}; may
   *               be <code>null</code>
   */
  public PublishingException(final String message) {
    super(message);
  }

}
