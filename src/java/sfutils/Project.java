/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: Project.java,v 1.1 2003/07/12 16:13:24 ljnelson Exp $
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
 * A {@link NamedObject} that represents a <a
 * href="http://sourceforge.net/">SourceForge</a> project for the purposes of
 * interacting with the <a
 * href="http://sourceforge.net/docman/display_doc.php%3Fdocid=6445&group_id=1">SourceForge
 * File Release System</a>.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.1 $ $Date: 2003/07/12 16:13:24 $
 * @since      June 19, 2003
 */
public class Project extends NamedObject {

  /**
   * The {@link Administrator} that owns this {@link Project}.  This field may
   * be <code>null</code>.
   *
   * @see        #getAdministrator()
   * @see        #setAdministrator(Administrator)
   */
  private Administrator administrator;

  /**
   * The "short name" of this {@link Project}.  The short name of a <a
   * href="http://sourceforge.net/">SourceForge</a> project ends up being the
   * directory name its contents are listed under.  This field may be
   * <code>null</code>.
   *
   * @see        #getShortName()
   * @see        #setShortName(String)
   */
  private String shortName;

  /**
   * The identifier for this {@link Project}.  {@link Project} identifiers are
   * normally assigned by <a href="http://sourceforge.net/">SourceForge</a>.
   * This field may be <code>null</code>.
   *
   * @see        #getID()
   * @see        #setID(String)
   */
  private String id;

  /**
   * Creates a new {@link Project}.  This constructor calls the {@link
   * #Project(String, String, Administrator)} constructor with <code>null</code>
   * arguments.
   */
  public Project() {
    this(null, null, null);
  }

  /**
   * Creates a new {@link Project} with the supplied long and short names and
   * the supplied {@link Administrator} to oversee it.  This constructor calls
   * its {@linkplain NamedObject#NamedObject() superclass' implementation} and
   * then calls the {@link #setName(String)}, {@link #setShortName(String)} and
   * {@link #setAdministrator(Administrator)} methods with the supplied,
   * relevant arguments.
   *
   * @param      longName
   *               the long, human-readable name for the {@link Project}; may be
   *               <code>null</code>
   * @param      shortName
   *               the short, Unix-friendly name for the {@link Project}; may be
   *               <code>null</code>
   * @param      admin
   *               the {@link Administrator} for this {@link Project}; may be
   *               <code>null</code>
   */
  public Project(final String longName,
                 final String shortName,
                 final Administrator admin) {
    super();
    this.setName(longName);
    this.setShortName(shortName);
    this.setAdministrator(admin);
  }

  /**
   * Creates a new {@link Project} with the supplied long and short names and
   * the supplied {@link Administrator} username and password.  This constructor
   * calls the {@link #Project(String, String, Administrator)} constructor with
   * the supplied long and short names, and a new {@link Administrator} it
   * attempts to {@linkplain Administrator#Administrator(String, String) build
   * with the supplied username and password}.
   *
   * @param      longName
   *               the long, human-readable name for the {@link Project}; may be
   *               <code>null</code>
   * @param      shortName
   *               the short, Unix-friendly name for the {@link Project}; may be
   *               <code>null</code>
   * @param      userName
   *               the username; must not be <code>null</code> or equal to the
   *               empty {@link String}
   * @param      password
   *               the password; must not be <code>null</code> or equal to the
   *               empty {@link String}
   * @exception  IllegalArgumentException
   *               if either <code>userName</code> or <code>password</code> is
   *               <code>null</code> or equal to the empty {@link String}
   */
  public Project(final String longName, 
                 final String shortName, 
                 final String userName,
                 final String password)
    throws IllegalArgumentException { 
    this(longName, shortName, new Administrator(userName, password));
  }

  /**
   * Sets the identifier for this {@link Project}.  {@link Project} identifiers
   * are normally assigned by <a href="http://sourceforge.net/">SourceForge</a>.
   *
   * @param      id
   *               the identifier for this {@link Project}; may be
   *               <code>null</code>
   * @see        #getID()
   */
  public void setID(final String id) {
    this.id = id;
  }

  /**
   * Returns the identifier for this {@link Project}.  {@link Project}
   * identifiers are normally assigned by <a
   * href="http://sourceforge.net/">SourceForge</a>.  This method may return
   * <code>null</code>.
   *
   * @return     the identifier for this {@link Project}, or <code>null</code>
   * @see        #setID(String)
   */
  public String getID() {
    return this.id;
  }

  /**
   * Sets the Unix-friendly "short name" for this {@link Project}.
   * Pragmatically speaking, arguments supplied to this method should be
   * non-<code>null</code>, all-lowercase {@link String}s containing no spaces.
   * Technically speaking, they may be anything.
   *
   * @param      shortName
   *               the Unix-friendly "short name" for this {@link Project}; may
   *               be <code>null</code>
   * @see        #getShortName()
   */
  public void setShortName(final String shortName) {
    this.shortName = shortName;
  }

  /**
   * Returns the Unix-friendly "short name" for this {@link Project}.  This
   * method may return <code>null</code>.
   *
   * @return     the Unix-friendly "short name" for this {@link Project}, or
   *               <code>null</code>
   * @see        #setShortName(String)
   */
  public String getShortName() {
    return this.shortName;
  }

  /**
   * Returns the {@link Administrator} that oversees this {@link Project}.  This
   * method may return <code>null</code>.
   *
   * @return     the {@link Administrator} that oversees this {@link Project},
   *               or <code>null</code>
   * @see        #setAdministrator(Administrator)
   */
  public Administrator getAdministrator() {
    return this.administrator;
  }

  /**
   * Sets the {@link Administrator} that oversees this {@link Project}.
   *
   * @param      administrator
   *               the new {@link Administrator}; may be <code>null</code>
   * @see        #getAdministrator()
   */
  public void setAdministrator(final Administrator administrator) {
    this.administrator = administrator;
  }

  /**
   * Returns a hashcode for this {@link Project} based on all of its attributes.
   *
   * @return     a hashcode for this {@link Project}
   */
  public int hashCode() {
    final String id = this.getID();
    final String shortName = this.getShortName();
    final String longName = this.getName();
    final Administrator administrator = this.getAdministrator();
    int hashCode = 0;
    if (id != null) {
      hashCode += id.hashCode();
    }
    if (shortName != null) {
      hashCode += shortName.hashCode();
    }
    if (longName != null) {
      hashCode += longName.hashCode();
    }
    if (administrator != null) {
      hashCode += administrator.hashCode();
    }
    return hashCode;
  }

  /**
   * Tests the supplied {@link Object} to see if it is equal to this {@link
   * Project}.  An {@link Object} is equal to this {@link Project} if it is an
   * instance of the {@link Project} class and all of its {@link Project}
   * attributes are equal to this {@link Project}'s attributes.  {@link
   * Project}s are, in other words, value objects.
   *
   * @param      anObject
   *               the {@link Object} to test; may be <code>null</code>
   * @return     <code>true</code> if and only if the supplied {@link Object} is
   *               equal to this {@link Project}
   */
  public boolean equals(final Object anObject) {
    if (anObject == this) {
      return true;
    } else if (anObject instanceof Project) {
      final Project other = (Project)anObject;

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

      // Compare short names.
      final String shortName = this.getShortName();
      final String otherShortName = other.getShortName();
      if (shortName == null) {
        if (otherShortName != null) {
          return false;
        }
      } else if (!shortName.equals(otherShortName)) {
        return false;
      }

      // Compare long names.
      final String longName = this.getName();
      final String otherLongName = other.getName();
      if (longName == null) {
        if (otherLongName != null) {
          return false;
        }
      } else if (!longName.equals(otherLongName)) {
        return false;
      }

      // Compare administrators.
      final Administrator administrator = this.getAdministrator();
      final Administrator otherAdministrator = other.getAdministrator();
      if (administrator == null) {
        return otherAdministrator == null;
      }
      return administrator.equals(otherAdministrator);

    } else {
      return false;
    }
  }

  /**
   * Returns a {@link String} representation for this {@link Project}.  This
   * method never returns <code>null</code>.
   *
   * @return     a {@link String} representation for this {@link Project}; never
   *               <code>null</code>
   */
  public String toString() {
    final String id = this.getID();
    final String shortName = this.getShortName();
    final String longName = this.getName();
    final Administrator administrator = this.getAdministrator();
    final StringBuffer returnMe = new StringBuffer();
    if (longName == null) {
      returnMe.append("Unnamed project");
    } else {
      returnMe.append(longName);
    }
    if (shortName != null) {
      returnMe.append(" (");
      returnMe.append(shortName);
      returnMe.append(")");
    }
    if (id != null) {
      returnMe.append(" ID=");
      returnMe.append(id);
    }
    if (administrator != null) {
      returnMe.append(" administered by ");
      returnMe.append(administrator.toString());
    }
    return returnMe.toString();
  }

}
