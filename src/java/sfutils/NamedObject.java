/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: NamedObject.java,v 1.1 2003/07/12 16:13:24 ljnelson Exp $
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

import java.io.Serializable;

/**
 * An {@link Object} that has a canonical name of some kind.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.1 $ $Date: 2003/07/12 16:13:24 $
 * @since      May 21, 2003
 */
public abstract class NamedObject implements Serializable {

  /**
   * The name of this {@link NamedObject}.  This field may be <code>null</code>.
   */
  private String name;

  /**
   * Creates a new {@link NamedObject}.
   */
  public NamedObject() {
    super();
  }

  /**
   * Creates a new {@link NamedObject} with the supplied name.  This constructor
   * calls the {@link #setName(String)} method with the supplied {@link String}.
   *
   * @param      name
   *               the name for the new {@link NamedObject}; may be
   *               <code>null</code>
   */
  public NamedObject(final String name) {
    super();
    this.setName(name);
  }

  /**
   * Sets the name of this {@link NamedObject}.
   *
   * @param      name
   *               the new name; may be <code>null</code>
   */
  public void setName(final String name) {
    this.name = name;
  }

  /**
   * Returns the name of this {@link NamedObject}.  This method may return
   * <code>null</code>.
   *
   * @return     the name of this {@link NamedObject}, or <code>null</code>
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns a hashcode for this {@link NamedObject} based off its {@linkplain
   * #getName() name}.
   *
   * @return     a hashcode for this {@link NamedObject}
   */
  public int hashCode() {
    final String name = this.getName();
    if (name == null) {
      return 0;
    }
    return name.hashCode();
  }

  /**
   * Tests the supplied {@link Object} to see if it is equal to this {@link
   * NamedObject}.  An {@link Object} is equal to this {@link NamedObject} if it
   * is an instance of the {@link NamedObject} class and its {@linkplain
   * #getName() name} is equal to this {@link NamedObject}'s {@linkplain
   * #getName() name}.  {@link NamedObject}s are, in other words, value objects.
   *
   * @param      anObject
   *               the {@link Object} to test; may be <code>null</code>
   * @return     <code>true</code> if and only if the supplied {@link Object} is
   *               equal to this {@link NamedObject} 
   */
  public boolean equals(final Object anObject) {
    if (anObject == this) {
      return true;
    } else if (anObject instanceof NamedObject) {
      final NamedObject other = (NamedObject)anObject;
      final String name = this.getName();
      if (name == null) {
        return other.getName() == null;
      }
      return name.equals(other.getName());
    } else {
      return false;
    }
  }

  /**
   * Returns a {@link String} representation of this {@link NamedObject}.  This
   * method never returns <code>null</code>.
   *
   * @return     a {@link String} representation of this {@link NamedObject};
   *               never <code>null</code>
   */
  public String toString() {
    final String name = this.getName();
    if (name == null) {
      return "Unnamed";
    }
    return name;
  }

}
