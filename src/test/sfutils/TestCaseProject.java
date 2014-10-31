/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: TestCaseProject.java,v 1.1 2003/07/12 16:13:24 ljnelson Exp $
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

import java.util.HashMap;
import java.util.Map;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 * A {@link TestCase} that exercises the functionality of the {@link
 * Project} class.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.1 $ $Date: 2003/07/12 16:13:24 $
 * @since      June 26, 2003
 * @see        Project
 * @see        TestCase
 */
public class TestCaseProject extends TestCase {

  /**
   * Creates a new {@link TestCaseProject}.
   *
   * @param      name
   *               the name of the test case to run; must not be
   *               <code>null</code>
   * @exception  AssertionFailedError
   *               if <code>name</code> is <code>null</code>
   */
  public TestCaseProject(final String name) {
    super(name);
  }

  /**
   * Tests the {@link Project#hashCode()} method indirectly by placing two
   * {@linkplain Project#equals(Object) equal} {@link Project} instances into a
   * {@link HashMap} under the same key, and verifying that the {@linkplain
   * Map#size() size} of the map remains equal to <code>1</code>.
   *
   * @exception  Exception
   *               if the test fails */
  public void testHashCodeIndirectly() throws Exception {
    final Map map = new HashMap();
    final Project projectA = new Project();
    projectA.setShortName("foo");
    projectA.setName("The Foo Project");
    projectA.setAdministrator(new Administrator("oog", "blah"));
    projectA.setID("A");
    map.put("A", projectA);
    final Project projectB = new Project();
    projectB.setShortName("foo");
    projectB.setName("The Foo Project");
    projectB.setAdministrator(new Administrator("oog", "blah"));
    projectB.setID("A");
    map.put("A", projectB);
    assertEquals(1, map.size());
  }

  /**
   * Tests the {@link Project#equals(Object)} method.
   *
   * @exception  Exception
   *               if the test fails
   */
  public void testCaseEquals() throws Exception {
    final Project projectA = new Project();
    final Project projectB = new Project();
    assertEquals(projectA, projectB);
    assertTrue(!projectA.equals(null));
    projectA.setAdministrator(new Administrator("a", "b"));
    assertTrue(!projectA.equals(projectB));
    projectB.setAdministrator(new Administrator("a", "b"));
    assertEquals(projectA, projectB);
    projectA.setName("A long name");
    assertTrue(!projectA.equals(projectB));
    projectB.setName("A long name");
    assertEquals(projectA, projectB);
    projectA.setShortName("shortname");
    assertTrue(!projectA.equals(projectB));
    projectB.setShortName("shortname");
    assertEquals(projectA, projectB);
    projectA.setID("id");
    assertTrue(!projectA.equals(projectB));
    projectB.setID("id");
    assertEquals(projectA, projectB);
    assertEquals(projectA.toString(), projectB.toString());
  }

}
