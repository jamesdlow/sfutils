/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: TestCaseBasicConstruction.java,v 1.2 2003/07/12 16:13:24 ljnelson Exp $
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import junit.framework.TestCase;

import sfutils.Administrator;
import sfutils.Project;

/**
 * A {@link TestCase} that ensures that constructing a {@link FileRelease} graph
 * works properly.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.2 $ $Date: 2003/07/12 16:13:24 $
 * @since      June 19, 2003
 */
public class TestCaseBasicConstruction extends TestCase {

  /**
   * Creates a new {@link TestCaseBasicConstruction}.
   *
   * @param      name
   *               the name of the test to run; supplied by the <a
   *               href="http://www.junit.org/">JUnit</a> framework; will not be
   *               <code>null</code>
   */
  public TestCaseBasicConstruction(final String name) {
    super(name);
  }

  /**
   * Creates a temporary {@link File} and ensures it has some content in it.
   * This newly created {@link File} will be deleted when the virtual machine
   * during whose lifespan it was created exits.  This method never returns
   * <code>null</code>.
   *
   * @return     the newly-created temporary {@link File}; never
   *               <code>null</code>
   * @exception  IOException
   *               if the temporary {@link File} could not be created for any
   *               reason
   */
  private File createTempFile() throws IOException {
    final File returnMe = File.createTempFile("TEST", ".txt");
    assertNotNull(returnMe);
    this.fillWithCrap(returnMe);
    returnMe.deleteOnExit();
    return returnMe;
  }

  /**
   * Puts meaningless content into the supplied {@link File}.
   *
   * @param      file
   *               the {@link File} to fill; must not be <code>null</code>
   * @exception  IOException
   *               if the supplied {@link File} could not be filled
   */
  private void fillWithCrap(final File file) throws IOException {
    assertNotNull(file);
    assertTrue(file.canWrite());
    final PrintWriter writer = 
      new PrintWriter(new BufferedWriter(new FileWriter(file)));
    try {
      writer.println("Junk line 1");
      writer.println("Junk line 2");
      writer.println("Junk line 3");
    } finally {
      writer.close();
    }
  }

  /**
   * Creates a simple {@link FileRelease} and ensures that its parts are all set
   * properly.
   *
   * @exception  IOException
   *               if the {@link File}s that are part of the new {@link
   *               FileRelease}'s {@link FileSpecification} objects could not be
   *               created or filled with content
   */
  public void testBasicConstruction() throws IOException {
    final Administrator admin = new Administrator("root", "password");
    final Project project = new Project();
    project.setName("Test");
    project.setShortName("test");
    assertEquals("Test", project.getName());
    assertEquals("test", project.getShortName());
    project.setAdministrator(admin);
    assertEquals(admin, project.getAdministrator());
    final Package projectPackage = new Package();
    projectPackage.setName("TestPackage");
    projectPackage.setProject(project);
    final FileRelease release = new FileRelease();
    release.setPackage(projectPackage);
    final FileSpecification fileOne = new FileSpecification();
    fileOne.setFile(this.createTempFile());
    final FileSpecification fileTwo = new FileSpecification();
    fileTwo.setFile(this.createTempFile());
    release.setFileSpecifications(new FileSpecification[] { fileOne, fileTwo });
  }

}
