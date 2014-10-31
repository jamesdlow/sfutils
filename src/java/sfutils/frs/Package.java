/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: Package.java,v 1.5 2003/07/12 16:13:24 ljnelson Exp $
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

import sfutils.Project;

/**
 * A {@link HideableNamedObject} that represents a <a
 * href="http://sourceforge.net/">SourceForge</a> package.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.5 $ $Date: 2003/07/12 16:13:24 $
 * @since      June 19, 2003
 */
public class Package extends HideableNamedObject {

  /**
   * The identifier for this {@link Package}.  {@link Package} identifiers are
   * assigned by <a href="http://sourceforge.net/">SourceForge</a>.  This field
   * may be <code>null</code>.
   */
  private String id;

  /**
   * The {@link Project} to which this {@link Package} belongs.  This field may
   * be <code>null</code>.
   */
  private Project project;

  /**
   * Creates a new {@link Package}.
   */
  public Package() {
    this(null, null);
  }

  /**
   * Creates a new {@link Package} with the supplied name.  This constructor
   * calls the {@link #Package(Project, String)} constructor with
   * <code>null</code> and the supplied name as its argument values.
   *
   * @param      name
   *               the name for the new {@link Package}; may be
   *               <code>null</code>
   */
  public Package(final String name) {
    this(null, name);
  }

  /**
   * Creates a new {@link Package} with the supplied {@link Project} as its
   * parent.  This constructor calls the {@link #Package(Project, String)}
   * constructor with the supplied {@link Project} and <code>null</code> as its
   * argument values.
   *
   * @param      parent
   *               the {@link Project} to which this new {@link Package} will
   *               belong; may be <code>null</code>
   */
  public Package(final Project parent) {
    this(parent, null);
  }

  /**
   * Creates a new {@link Package} with the supplied name and that will belong
   * to the supplied {@link Project}.  This constructor calls its {@linkplain
   * HideableNamedObject#HideableNamedObject(String) superclass' implementation}
   * with the supplied name, and then calls the {@link #setProject(Project)}
   * method with the supplied {@link Project}.
   *
   * @param      parent
   *               the {@link Project} to which this {@link Package} will
   *               belong; may be <code>null</code>
   * @param      name
   *               the name of this new {@link Package}; may be
   *               <code>null</code>
   */
  public Package(final Project parent,
                 final String name) {
    super(name);
    this.setProject(parent);
  }

  /**
   * Sets the identifier of this {@link Package}.  Identifiers are normally
   * assigned by <a href="http://sourceforge.net/">SourceForge</a>.
   *
   * @param      id
   *               the new identifier; may be <code>null</code>
   * @see        #getID()
   */
  public void setID(final String id) {
    this.id = id;
  }

  /**
   * Returns the identifier of this {@link Package}.  Identifiers are normally
   * assigned by <a href="http://sourceforge.net/">SourceForge</a>.  This method
   * may return <code>null</code>.
   *
   * @return     the identifier of this {@link Package}, or <code>null</code>
   * @see        #setID(String)
   */
  public String getID() {
    return this.id;
  }

  /**
   * Returns the {@link Project} to which this {@link Package} belongs.  This
   * method may return <code>null</code>.
   *
   * @return     the {@link Project} to which this {@link Package} belongs, or
   *               <code>null</code>
   * @see        #setProject(Project)
   */
  public Project getProject() {
    return this.project;
  }

  /**
   * Sets the {@link Project} to which this {@link Package} belongs.
   *
   * @param      project
   *               the {@link Project} to which this {@link Package} will
   *               belong; may be <code>null</code>
   * @see        #getProject()
   */
  public void setProject(final Project project) {
    this.project = project;
  }

  /**
   * Returns a hashcode for this {@link Package} based on all of its attributes.
   *
   * @return     a hashcode for this {@link Package}
   */
  public int hashCode() {
    final String id = this.getID();
    final Project project = this.getProject();
    int hashCode = super.hashCode();
    if (id != null) {
      hashCode += id.hashCode();
    }
    if (project != null) {
      hashCode += project.hashCode();
    }
    return hashCode;
  }

  /**
   * Tests the supplied {@link Object} to see if it is equal to this {@link
   * Package}.  An {@link Object} is equal to this {@link Package} if it is an
   * instance of the {@link Package} class and all of its {@link Package}
   * attributes are equal to this {@link Package}'s attributes.  {@link
   * Package}s are, in other words, value objects.
   *
   * @param      anObject
   *               the {@link Object} to test; may be <code>null</code>
   * @return     <code>true</code> if and only if the supplied {@link Object} is
   *               equal to this {@link Package}
   */
  public boolean equals(final Object anObject) {
    if (anObject == this) {
      return true;
    } else if (anObject instanceof Package &&
               super.equals(anObject)) {
      final Package other = (Package)anObject;

      // Compare IDs.
      final String id = this.getID();
      final String otherID = other.getID();
      if (id == null) {
        if (otherID != null) {
          return false;
        }
      } else if (!id.equals(otherID)) {
        return false;
      }

      // Compare projects.
      final Project project = this.getProject();
      final Project otherProject = other.getProject();
      if (project == null) {
        return otherProject == null;
      }
      return project.equals(otherProject);

    } else {
      return false;
    }
  }

}
