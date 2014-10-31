/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: TestCaseAdministrator.java,v 1.1 2003/07/12 16:13:24 ljnelson Exp $
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

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 * A {@link TestCase} that exercises the functionality of the {@link
 * Administrator} class.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.1 $ $Date: 2003/07/12 16:13:24 $
 * @since      June 26, 2003
 * @see        Administrator
 * @see        TestCase
 */
public class TestCaseAdministrator extends TestCase {

  /**
   * Creates a new {@link TestCaseAdministrator}.
   *
   * @param      name
   *               the name of the test case to run; must not be
   *               <code>null</code>
   * @exception  AssertionFailedError
   *               if <code>name</code> is <code>null</code>
   */
  public TestCaseAdministrator(final String name) {
    super(name);
  }

  /**
   * Tests the {@link Administrator#getName()} method.
   *
   * @exception  Exception
   *               if the test fails
   */
  public void testGetName() throws Exception {
    final Administrator admin = new Administrator();
    try {
      admin.getName();
      fail("Expected an IllegalStateException");
    } catch (final IllegalStateException expected) {
      assertNotNull(expected);
    }
    admin.setName("abc");
    assertEquals("abc", admin.getName());
  }

  /**
   * Tests the {@link Administrator#getPassword()} method.
   *
   * @exception  Exception
   *               if the test fails
   */
  public void testGetPassword() throws Exception {
    final Administrator admin = new Administrator();
    try {
      admin.getPassword();
      fail("Expected an IllegalStateException");
    } catch (final IllegalStateException expected) {
      assertNotNull(expected);
    }
    admin.setPassword("abc");
    assertEquals("abc", admin.getPassword());
  }

  /**
   * Tests the {@link Administrator#equals(Object)} method.
   *
   * @exception  Exception
   *               if the test fails
   */
  public void testEquals() throws Exception {
    final Administrator admin1 = new Administrator("abc", "def");
    final Administrator admin2 = new Administrator();
    assertTrue(!admin1.equals(null));
    assertTrue(!admin1.equals(admin2));
    admin2.setName("abc");
    assertTrue(!admin1.equals(admin2));
    admin2.setPassword("def");
    assertEquals(admin1, admin2);
  }

  /**
   * Tests the {@link Administrator#toString()} method.
   *
   * @exception  Exception
   *               if the test fails
   */
  public void testToString() throws Exception {
    final Administrator admin1 = new Administrator();
    assertEquals("Unspecified Administrator (password not set)", admin1.toString());
    admin1.setName("abc");
    assertEquals("abc (password not set)", admin1.toString());
    admin1.setPassword("def");
    assertEquals("abc (password set)", admin1.toString());
  }

}
