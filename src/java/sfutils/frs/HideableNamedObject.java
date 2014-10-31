/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: HideableNamedObject.java,v 1.5 2003/07/12 16:13:24 ljnelson Exp $
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

import sfutils.NamedObject;

/**
 * A {@link NamedObject} that {@linkplain #setHidden(boolean) may be hidden from
 * view}.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.5 $ $Date: 2003/07/12 16:13:24 $
 * @since      May 21, 2003
 */
public abstract class HideableNamedObject extends NamedObject {

  /**
   * Whether or not this {@link HideableNamedObject} is currently hidden.
   *
   * @see        #isHidden()
   */
  private boolean hidden;

  /**
   * Creates a new {@link HideableNamedObject}.
   */
  public HideableNamedObject() {
    super();
  }

  /**
   * Creates a new {@link HideableNamedObject} with the supplied name.  This
   * constructor calls its {@link NamedObject#NamedObject(String) superclass'
   * implementation} with the supplied name.
   *
   * @param      name
   *               the name for the new {@link HideableNamedObject}; may be
   *               <code>null</code>
   */
  public HideableNamedObject(final String name) {
    super(name);
  }

  /**
   * Returns <code>true</code> if and only if this {@link HideableNamedObject}
   * is hidden from view.
   *
   * @return     <code>true</code> if and only if this {@link
   *                HideableNamedObject} is hidden from view
   * @see        #setHidden(boolean)
   */
  public boolean isHidden() {
    return this.hidden;
  }

  /**
   * Sets whether this {@link HideableNamedObject} is hidden from view.
   *
   * @param      hidden
   *               if <code>true</code>, this {@link HideableNamedObject} will
   *               be hidden from view
   * @see        #isHidden()
   */
  public void setHidden(final boolean hidden) {
    this.hidden = hidden;
  }

  /**
   * Returns a hashcode for this {@link HideableNamedObject} based off its
   * {@linkplain #getName() name} and its {@linkplain #isHidden() hidden
   * status}.
   *
   * @return     a hashcode for this {@link HideableNamedObject}
   */
  public int hashCode() {
    int hashCode = super.hashCode();
    if (this.isHidden()) {
      ++hashCode;
    }
    return hashCode;
  }
  
  /**
   * Tests the supplied {@link Object} to see if it is equal to this {@link
   * HideableNamedObject}.  An {@link Object} is equal to this {@link
   * HideableNamedObject} if it is an instance of the {@link
   * HideableNamedObject} class and its {@linkplain #getName() name} is equal to
   * this {@link HideableNamedObject}'s {@linkplain #getName() name}.  {@link
   * HideableNamedObject}s are, in other words, value objects.
   *
   * @param      anObject
   *               the {@link Object} to test; may be <code>null</code>
   * @return     <code>true</code> if and only if the supplied {@link Object} is
   *               equal to this {@link HideableNamedObject} 
   */
  public boolean equals(final Object anObject) {
    if (anObject == this) {
      return true;
    } else if (anObject instanceof HideableNamedObject &&
               super.equals(anObject)) {

      // Compare hidden statuses.
      return this.isHidden() == ((HideableNamedObject)anObject).isHidden();

    } else {
      return false;
    }
  }

  /**
   * Returns a {@link String} representation of this {@link
   * HideableNamedObject}.  This method never returns <code>null</code>.
   *
   * @return     a {@link String} representation of this {@link
   *               HideableNamedObject}; never <code>null</code> 
   */
  public String toString() {
    final StringBuffer returnMe = new StringBuffer();
    final String parentRep = super.toString();
    if (parentRep == null) {
      returnMe.append("Unnamed");
    } else {
      returnMe.append(parentRep);
    }
    if (this.isHidden()) {
      returnMe.append(" (hidden)");
    }
    return returnMe.toString();
  }

}
