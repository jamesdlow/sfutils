/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: TestCaseSourceForge.java,v 1.1 2003/07/13 17:49:34 ljnelson Exp $
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

import junit.framework.TestCase;

/**
 * A {@link TestCase} that exercises the functionality of the {@link
 * SourceForge} class.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.1 $ $Date: 2003/07/13 17:49:34 $
 * @since      July 13, 2003
 */
public class TestCaseSourceForge extends TestCase {
  
  /**
   * Creates a new {@link TestCaseSourceForge}.
   *
   * @param      name
   *               the name of the test case to run; passed by the <a
   *               href="http://www.junit.org/">JUnit</a> framework; will never
   *               be <code>null</code>
   */
  public TestCaseSourceForge(final String name) {
    super(name);
  }

  /**
   * Tests the {@link SourceForge#getProjectID(String)} method.
   *
   * @exception  Exception
   *               if the test fails
   * @see        SourceForge#getProjectID(String)
   */
  public void testGetProjectID() throws Exception {
    assertEquals("1", SourceForge.getProjectID("alexandria"));
  }

}
