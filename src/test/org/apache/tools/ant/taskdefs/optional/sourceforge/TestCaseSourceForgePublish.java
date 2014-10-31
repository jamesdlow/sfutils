/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: TestCaseSourceForgePublish.java,v 1.4 2003/07/11 18:49:30 ljnelson Exp $
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
package org.apache.tools.ant.taskdefs.optional.sourceforge;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

/**
 * A {@link TestCase} that ensures that the {@link SourceForgePublish} class
 * functions properly when invoked from an <a
 * href="http://ant.apache.org/">Ant</a> build file.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.4 $ $Date: 2003/07/11 18:49:30 $
 * @since      July 11. 2003
 */
public class TestCaseSourceForgePublish extends TestCase {

  /**
   * The temporary build file that will be created.  This file will be
   * {@linkplain File#deleteOnExit() deleted when the virtual machine running
   * this <code>TestCase</code> exits}.  This field may be <code>null</code>.
   */
  private File buildFile;

  /**
   * Creates a new {@link TestCaseSourceForgePublish}.
   *
   * @param      name
   *               the name of the test case to run; provided by the <a
   *               href="http://www.junit.org/">JUnit</a> framework; will never
   *               be <code>null</code>
   */
  public TestCaseSourceForgePublish(final String name) {
    super(name);
  }

  /**
   * Fills the supplied {@link File} with a bunch of junk text.
   *
   * @param      file
   *               the {@link File} to fill; must not be <code>null</code> and
   *               must be {@linkplain File#canWrite() writable} 
   * @exception  IOException
   *               if an error occurs
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
   * Sets up this {@link TestCaseSourceForgePublish} for testing.  This method
   * is called by the <a href="http://www.junit.org/">JUnit</a> framework before
   * each test case is run.
   *
   * @exception  Exception
   *               if setup could not be completed
   */
  public void setUp() throws Exception {

    // Load up any user-specified properties.
    final Properties buildProperties = new Properties();
    final File buildPropertiesFile = 
      new File(System.getProperty("user.home"), "build.properties");
    if (buildPropertiesFile.canRead()) {
      buildProperties.load(new FileInputStream(buildPropertiesFile));
    }

    // Figure out which SourceForge project we're targeting.
    final String userName = 
      System.getProperty("sf.project.userName", 
                         buildProperties.getProperty("sf.project.userName"));
    final String password = 
      System.getProperty("sf.project.password",
                         buildProperties.getProperty("sf.project.password"));
    final String projectShortName = 
      System.getProperty("sf.project.shortName",
                         buildProperties.getProperty("sf.project.shortName"));
    assertNotNull("Please set the sf.project.userName system property.",
                  userName);
    assertNotNull("Please set the sf.project.password system property.",
                  password);
    assertNotNull("Please set the sf.project.shortName system property.",
                  projectShortName);

    // Create a temporary build file.
    final File buildFile = File.createTempFile("TEST-build", ".xml");
    assertNotNull(buildFile);
    assertTrue(buildFile.canRead());
    buildFile.deleteOnExit();

    // Create a release notes file.
    final File releaseNotes = File.createTempFile("TEST-ant-releaseNotes", ".txt");
    assertNotNull(releaseNotes);
    this.fillWithCrap(releaseNotes);
    assertTrue(releaseNotes.canRead());
    releaseNotes.deleteOnExit();

    // Create a file to upload to SourceForge to be part of the file release
    // being tested.
    final File file = File.createTempFile("TEST-ant", ".txt");
    assertNotNull(file);
    assertTrue(file.canWrite());
    file.deleteOnExit();
    this.fillWithCrap(file);

    // Save our current date and time, as we'll need it during the course of
    // writing out the build file.
    final Date now = new Date();
    final long nowTime = now.getTime();
    final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    // Write the build file, referring along the way to the junk file we
    // created.
    PrintWriter writer = null;
    try {
      writer = new PrintWriter(new FileWriter(buildFile));
      writer.println("<?xml version=\"1.0\" ?>");
      writer.println("<project default=\"test\">");
      writer.println("  <taskdef name=\"sfpublish\" classname=\"" +
                     SourceForgePublish.class.getName() + "\"/>");
      writer.println("  <target name=\"test\">");
      writer.println("    <sfpublish releasename=\"test-ant" + nowTime + "\"");
      writer.println("               packagename=\"test-ant" + nowTime + "\"");
      writer.println("               packagehidden=\"no\"");
      writer.println("               hidden=\"no\"");
      writer.println("               projectshortname=\"" +
                     projectShortName + "\"");
      writer.println("               username=\"" +
                     userName + "\"");
      writer.println("               password=\"" +
                     password + "\"");
      writer.println("               releasenotes=\"" +
                     releaseNotes.getAbsolutePath() + "\"");
      writer.println("               releasedate=\"" +
                     format.format(now) + "\">");
      writer.println("      <filespec file=\"" + 
                     file.getAbsolutePath() + "\"");
      writer.println("                filetype=\"text\"");
      writer.println("                processortype=\"platform_independent\"");
      writer.println("      />");
      writer.println("    </sfpublish>");
      writer.println("  </target>");
      writer.println("</project>");
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (final Exception ignore) {
          // ignore
        }
      }
    }

    // Save the build file away for the test cases to use.
    this.buildFile = buildFile;

  }

  /**
   * Tests the {@link SourceForgePublish#execute()} method indirectly by
   * constructing a simple but valid build file and executing it using <a
   * href="http://ant.apache.org/">Ant</a>.
   *
   * @exception  Exception
   *               if the test fails for any reason
   */
  public void testExecute() throws Exception {

    // Make sure the setUp() method worked.
    assertNotNull(this.buildFile);
    assertTrue(this.buildFile.canRead());

    // Get the full path to the build file so there are no ambiguities about
    // where it is currently living.
    final String buildFilePath = this.buildFile.getCanonicalPath();
    assertNotNull(buildFilePath);

    // Jump through the hoops to cause Ant to start up, read the build file, and
    // execute it.
    final Project project = new Project();
    project.init();
    project.setUserProperty("ant.file", buildFilePath);
    ProjectHelper.configureProject(project, this.buildFile);
    project.executeTarget("test");
  }

}
