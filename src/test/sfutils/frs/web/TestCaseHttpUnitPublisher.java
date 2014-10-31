/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: TestCaseHttpUnitPublisher.java,v 1.5.4.1 2004/03/12 15:06:50 ljnelson Exp $
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
package sfutils.frs.web;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;

import com.meterware.httpunit.WebConversation;

import junit.framework.TestCase;

import sfutils.Administrator;
import sfutils.Project;

import sfutils.frs.Package;
import sfutils.frs.FileRelease;
import sfutils.frs.FileSpecification;

public class TestCaseHttpUnitPublisher extends TestCase {

  private final Random random;

  private FileRelease release;

  private FileRelease newReleaseOldPackage;

  private FileRelease newReleaseNewPackage;

  private Date now;

  public TestCaseHttpUnitPublisher(final String name) {
    super(name);    
    this.random = new Random();
  }

  public void setUp() throws Exception {
    final Properties userBuildProperties = new Properties();
    final File userBuildPropertiesFile = 
      new File(System.getProperty("user.home"), "build.properties");
    if (userBuildPropertiesFile.canRead()) {
      userBuildProperties.load(new FileInputStream(userBuildPropertiesFile));
    }
    final String userName = 
      System.getProperty("sf.project.userName", 
                         userBuildProperties.getProperty("sf.project.userName"));
    final String password = 
      System.getProperty("sf.project.password",
                         userBuildProperties.getProperty("sf.project.password"));
    final String projectShortName = 
      System.getProperty("sf.project.shortName",
                         userBuildProperties.getProperty("sf.project.shortName"));
    assertNotNull("Please set the sf.project.userName system property.",
                  userName);
    assertNotNull("Please set the sf.project.password system property.",
                  password);
    assertNotNull("Please set the sf.project.shortName system property.",
                  projectShortName);
    final Administrator admin = new Administrator(userName, password);
    final Project project = new Project();
    project.setShortName(projectShortName);
    project.setAdministrator(admin);
    assertEquals(projectShortName, project.getShortName());
    assertEquals(admin, project.getAdministrator());
    this.now = new Date();
    final Package projectPackage = new Package();
    projectPackage.setName("testPackage_" + this.now.getTime());
    projectPackage.setHidden(true);
    projectPackage.setProject(project);
    final FileRelease release = new FileRelease();
    release.setName("testRelease_" + this.now.getTime());
    release.setPackage(projectPackage);
    release.setHidden(true);

    final File changeLog = File.createTempFile("TEST_ChangeLog_", ".txt");
    changeLog.deleteOnExit();
    assertTrue(changeLog.canRead());
    this.fillChangeLogWithCrap(changeLog);

    final String releaseNotes = "This is only a test.";

    final File file1 = File.createTempFile("TEST_", ".txt");
    file1.deleteOnExit();
    assertTrue(file1.canWrite());
    this.fillWithCrap(file1);
    final FileSpecification fileOne = new FileSpecification();
    fileOne.setFile(file1);
    assertEquals(FileSpecification.TEXT_FILE, fileOne.getFileType());
    fileOne.setProcessorType(FileSpecification.PLATFORM_INDEPENDENT_PROCESSOR);

    final File file2 = File.createTempFile("TEST_", ".txt");
    file2.deleteOnExit();
    assertTrue(file2.canWrite());
    this.fillWithCrap(file2);
    final FileSpecification fileTwo = new FileSpecification();
    fileTwo.setFile(file2);
    assertEquals(FileSpecification.TEXT_FILE, fileTwo.getFileType());
    fileTwo.setProcessorType(FileSpecification.PLATFORM_INDEPENDENT_PROCESSOR);

    release.setFileSpecifications(new FileSpecification[] { fileOne, fileTwo });
    release.setReleaseDate(this.now);
    release.setChangeLogFile(changeLog);
    release.setReleaseNotes(releaseNotes);
    release.setPreserveFormattedText(true);

    this.newReleaseNewPackage = release;
  }

  public void fillChangeLogWithCrap(final File file) throws IOException {
    assertNotNull(file);
    final PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)));
    try {
      printWriter.println("This is a bogus ChangeLog for testing only.");
    } finally {
      printWriter.flush();
      printWriter.close();
    }
  }

  public void fillWithCrap(final File file) throws IOException {
    assertNotNull(file);
    final byte[] randomBytes = new byte[260000];
    this.random.nextBytes(randomBytes);
    final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
    try {
      stream.write(randomBytes, 0, randomBytes.length);
    } finally {
      try {
        stream.close();
      } catch (final IOException kaboom) {
        // ignore
      }
    }
    assertEquals(260000L, file.length());
  }

  public void testPublish() throws Exception {
    final HttpUnitPublisher httpUnitPublisher = new HttpUnitPublisher();
    httpUnitPublisher.publish(this.newReleaseNewPackage);
  }

}
