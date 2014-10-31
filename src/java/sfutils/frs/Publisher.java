/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: Publisher.java,v 1.3 2003/07/10 19:48:14 ljnelson Exp $
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

/**
 * An interface indicating that implementors are capable of publishing a {@link
 * FileRelease} to its project's "Files" section on <a
 * href="http://sourceforge.net/">SourceForge</a>.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.3 $ $Date: 2003/07/10 19:48:14 $
 * @since      July 3, 2003
 * @see        FileRelease#getPublisher()
 * @see        FileRelease#setPublisher(Publisher)
 */
public interface Publisher {

  /**
   * Publishes the supplied {@link FileRelease} to its proper place on <a
   * href="http://sourceforge.net/">SourceForge</a>.
   *
   * @param      release
   *               the {@link FileRelease} to be published; may technically be
   *               <code>null</code> although most implementations will reject a
   *               <code>null</code> value
   * @exception  PublishingException
   *               if the supplied {@link FileRelease} could not be published
   *               for any reason 
   */
  public void publish(final FileRelease release)
    throws PublishingException;

}
