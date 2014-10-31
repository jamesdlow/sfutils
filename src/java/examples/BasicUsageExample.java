/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: BasicUsageExample.java,v 1.3 2003/07/12 16:13:23 ljnelson Exp $
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
package examples;

import java.io.File;

import java.util.Date;

import sfutils.Administrator;
import sfutils.Project;

import sfutils.frs.FileRelease;
import sfutils.frs.FileSpecification;
import sfutils.frs.Package;

import sfutils.frs.web.HttpUnitPublisher;

/**
 * An example of how to use the file release system classes.  This example will
 * compile, but will not work as all of the parameters (file names, project
 * names, passwords and the like) are fictitious.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.3 $ $Date: 2003/07/12 16:13:23 $
 * @since      July 7, 2003
 */
public class BasicUsageExample {

  /**
   * Creates a new {@link BasicUsageExample}.  Not used.
   */
  private BasicUsageExample() {
    super();
  }

  /**
   * If this were a fully-functional class, this method would create a {@link
   * FileRelease} and {@linkplain FileRelease#publish() publish it}.
   *
   * @param      args
   *               the command line arguments; ignored
   * @exception  Exception
   *               if an error occurs, which it will if this method is actually
   *               invoked
   */
  public static final void main(final String[] args) throws Exception {

    final Administrator admin = new Administrator();
    admin.setName("username");
    admin.setPassword("password");

    final Project project = new Project();
    project.setName("My Project"); // the "normal" project name
    project.setShortName("myproj"); // must be a valid project shortname
    project.setAdministrator(admin);

    final Package packaj = new Package();
    packaj.setName("myproj"); // name it whatever you like
    packaj.setHidden(false);
    packaj.setProject(project);

    final FileRelease release = new FileRelease();
    release.setName("myproj-1.0"); // name it whatever you like
    release.setPackage(packaj);
    release.setHidden(false);
    release.setReleaseDate(new Date()); // or whenever you like
    release.setNotifyOthers(true);

    final File changeLogFile = new File("/path/to/changelog.txt");
    release.setChangeLogFile(changeLogFile);

    release.setReleaseNotes("Behold the release notes");

    final FileSpecification spec1 = new FileSpecification();
    spec1.setFile(new File("/path/to/file1.txt"));
    spec1.setFileType(FileSpecification.TEXT_FILE);
    spec1.setProcessorType(FileSpecification.PLATFORM_INDEPENDENT_PROCESSOR);
    spec1.setReleaseDate(new Date());

    release.setFileSpecifications(new FileSpecification[] { spec1 });

    release.setPublisher(new HttpUnitPublisher());
    release.publish();

  }

}
