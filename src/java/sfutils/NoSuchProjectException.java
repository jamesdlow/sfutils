/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: NoSuchProjectException.java,v 1.1 2003/07/13 02:47:02 ljnelson Exp $
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
package sfutils;

/**
 * A {@link SourceForgeException} that indicates that a project does not exist.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.1 $ $Date: 2003/07/13 02:47:02 $
 * @since      July 12, 2003
 */
public class NoSuchProjectException extends SourceForgeException {

  /**
   * Creates a {@link NoSuchProjectException}.
   *
   * @param      projectShortName
   *               the short name of the project that does not exist; may be
   *               <code>null</code>
   */
  public NoSuchProjectException(final String projectShortName) {
    super(projectShortName);
  }

}
