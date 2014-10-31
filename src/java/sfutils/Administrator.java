/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: Administrator.java,v 1.1 2003/07/12 16:13:24 ljnelson Exp $
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
 * An administrator of a <a href="http://sourceforge.net/">SourceForge</a>
 * project.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.1 $ $Date: 2003/07/12 16:13:24 $
 * @since      June 17, 2003
 */
public class Administrator extends NamedObject {

  /**
   * This {@link Administrator}'s password.  This field will never be
   * <code>null</code> or equal to the empty {@link String}.
   */
  private String password;

  /**
   * Creates a new {@link Administrator} without a username or password.  This
   * new {@link Administrator} will be in an illegal state until both the
   * username and password are set to non-<code>null</code> values that are not
   * equal to the empty {@link String}.
   *
   * @see        #setName(String)
   * @see        #setPassword(String)
   */
  public Administrator() {
    super();
  }

  /**
   * Creates a new {@link Administrator} with the supplied username and
   * password.  Neither the username nor the password may be <code>null</code>
   * or equal to the empty {@link String}.
   *
   * @param      name
   *               the username; must not be <code>null</code> or equal to the
   *               empty {@link String}
   * @param      password
   *               the password; must not be <code>null</code> or equal to the
   *               empty {@link String}
   * @exception  IllegalArgumentException
   *               if either <code>name</code> or <code>password</code> is
   *               <code>null</code> or equal to the empty {@link String}
   */
  public Administrator(final String name,
                       final String password)
    throws IllegalArgumentException {
    this();
    this.setName(name);
    this.setPassword(password);
  }

  /**
   * Returns the username of this {@link Administrator}.  If the username has
   * not been previously set via the {@link #setName(String)} method, then this
   * method will throw an {@link IllegalStateException}.  This method will never
   * return <code>null</code> or the empty {@link String}, and subclasses must
   * honor this contract.
   *
   * @return     the username of this {@link Administrator}; never
   *               <code>null</code> or the empty {@link String}
   * @exception  IllegalStateException
   *               if the {@link #setName(String)} method has not yet been
   *               called
   * @see        #setName(String)
   */
  public String getName() throws IllegalStateException {
    final String name = super.getName();
    if (name == null) {
      throw new IllegalStateException("Set name first");
    }
    return name;
  }

  /**
   * Sets the username of this {@link Administrator}.  The supplied username
   * must not be <code>null</code> or equal to the empty {@link String}, or an
   * {@link IllegalArgumentException} will be thrown.
   *
   * @param      username
   *               the username; must not be <code>null</code> or equal to the
   *               empty {@link String}
   * @exception  IllegalArgumentException
   *               if <code>username</code> is <code>null</code> or equal to the
   *               empty {@link String}
   */
  public void setName(final String username)
    throws IllegalArgumentException {
    if (username == null || username.length() <= 0) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    super.setName(username);
  }

  /**
   * Returns the password of this {@link Administrator}.  If the password has
   * not been previously set via the {@link #setPassword(String)} method, then
   * this method will throw an {@link IllegalStateException}.  This method will
   * never return <code>null</code> or the empty {@link String}, and subclasses
   * must honor this contract.
   *
   * @return     the password of this {@link Administrator}; never
   *               <code>null</code> or the empty {@link String}
   * @exception  IllegalStateException
   *               if the {@link #setPassword(String)} method has not yet been
   *               called
   * @see        #setPassword(String)
   */
  public String getPassword() throws IllegalStateException {
    if (this.password == null) {
      throw new IllegalStateException("Set password first");
    }
    return this.password;
  }

  /**
   * Sets the password of this {@link Administrator}.  The supplied password
   * must not be <code>null</code> or equal to the empty {@link String}, or an
   * {@link IllegalArgumentException} will be thrown.
   *
   * @param      password
   *               the password; must not be <code>null</code> or equal to the
   *               empty {@link String}
   * @exception  IllegalArgumentException
   *               if <code>password</code> is <code>null</code> or equal to the
   *               empty {@link String}
   */
  public void setPassword(final String password)
    throws IllegalArgumentException {
    if (password == null || password.length() <= 0) {
      throw new IllegalArgumentException("Password cannot be null or empty");
    }
    this.password = password;
  }
  
  /**
   * Returns a hashcode for this {@link Administrator} based off its {@linkplain
   * #getName() username} and {@linkplain #getPassword() password}.
   *
   * @return     a hashcode for this {@link Administrator}
   */
  public int hashCode() {
    String userName = null;
    try {
      userName = this.getName();
    } catch (final IllegalStateException ignore) {
      userName = null;
    }
    String password = null;
    try {
      password = this.getPassword();
    } catch (final IllegalStateException oops) {
      password = null;
    }
    if (userName == null) {
      if (password == null) {
        return 0;
      } else {
        return password.hashCode();
      }
    } else if (password == null) {
      return userName.hashCode();
    } else {
      final StringBuffer buffer = new StringBuffer(userName);
      buffer.append(password);
      return buffer.hashCode();
    }
  }

  /**
   * Tests the supplied {@link Object} to see if it is equal to this {@link
   * Administrator}.  An {@link Object} is equal to this {@link Administrator}
   * if it is an instance of the {@link Administrator} class, its {@linkplain
   * #getName() username} is equal to this {@link Administrator}'s {@linkplain
   * #getName() username} and its {@linkplain #getPassword() password} is equal
   * to this {@link Administrator}'s {@linkplain #getPassword() password}.
   * {@link Administrator}s are, in other words, value objects.
   *
   * @param      anObject
   *               the {@link Object} to test; may be <code>null</code>
   * @return     <code>true</code> if and only if the supplied {@link Object} is
   *               equal to this {@link Administrator} 
   */
  public boolean equals(final Object anObject) {
    if (anObject == this) {
      return true;
    } else if (anObject instanceof Administrator) {
      final Administrator other = (Administrator)anObject;
      
      // Compare usernames.  We do not invoke NamedObject.equals() here because
      // getName() can throw an IllegalStateException.
      String userName = null;
      try {
        userName = this.getName();
      } catch (final IllegalStateException ignore) {
        userName = null;
      }
      String otherUserName = null;
      try {
        otherUserName = other.getName();
      } catch (final IllegalStateException ignore) {
        otherUserName = null;
      }
      if (userName == null) {
        if (otherUserName != null) {
          return false;
        }
      } else if (!userName.equals(otherUserName)) {
        return false;
      }

      // Compare passwords.
      String password = null;
      try {
        password = this.getPassword();
      } catch (final IllegalStateException oops) {
        password = null;
      }
      String otherPassword = null;
      try {
        otherPassword = other.getPassword();
      } catch (final IllegalStateException oosp) {
        otherPassword = null;
      }
      if (password == null) {
        return otherPassword == null;
      } else {
        return password.equals(otherPassword);
      }
    
    } else {
      return false;
    }
  }

  /**
   * Returns a {@link String} representation of this {@link Administrator}.  The
   * {@linkplain #getPassword() password} is not displayed.  This method never
   * returns <code>null</code>
   *
   * @return     a {@link String} representation of this {@link Administrator};
   *               never <code>null</code>
   */
  public String toString() {
    String userName = null;
    try {
      userName = this.getName();
    } catch (final IllegalStateException ignore) {
      userName = null;
    }
    final StringBuffer returnMe = new StringBuffer();
    if (userName == null) {
      returnMe.append("Unspecified Administrator");
    } else {
      returnMe.append(userName);
    }
    returnMe.append(" (password ");
    String password = null;
    try {
      password = this.getPassword();
    } catch (final IllegalStateException ignore) {
      password = null;
    }
    if (password == null) {
      returnMe.append("not ");
    }
    returnMe.append("set)");
    return returnMe.toString();
  }

}
