/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: SourceForgePublish.java,v 1.10 2003/07/12 16:13:24 ljnelson Exp $
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

import java.util.Date;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import sfutils.Administrator;
import sfutils.Project;

import sfutils.frs.FileRelease;
import sfutils.frs.FileSpecification;
import sfutils.frs.Package;

import sfutils.frs.web.HttpUnitPublisher;

/**
 * An <a href="http://ant.apache.org/">Ant</a> {@link Task} that makes a file
 * release available on <a href="http://sourceforge.net/">SourceForge</a>.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.10 $ $Date: 2003/07/12 16:13:24 $
 * @since      July 1, 2003
 */
public class SourceForgePublish extends Task {

  /**
   * The {@link FileRelease} this {@link SourceForgePublish} {@link Task} will
   * publish.  This field will never be <code>null</code>.
   */
  private final FileRelease release;

  /**
   * The {@link FileSpec}s contained by this {@link SourceForgePublish} {@link
   * Task}.  This field will never be <code>null</code>.
   */
  private final Vector fileSpecs;

  /**
   * Creates a new {@link SourceForgePublish} {@link Task}.
   */
  public SourceForgePublish() {
    super();
    this.fileSpecs = new Vector();
    this.release = createFileReleaseShell();
    assertNotNull(this.release);
  }

  /**
   * Creates a {@link FileRelease} with its associated {@link Package}, {@link
   * Project} and {@link Administrator} set.  The resulting {@link FileRelease}
   * is in an illegal state until it is fully configured.  This method never
   * returns <code>null</code>.
   *
   * @return     a {@link FileRelease} "shell"; never <code>null</code>
   * @see        FileRelease
   */
  private static final FileRelease createFileReleaseShell() {
    final Package pkg = new Package();
    final Project project = new Project();
    final Administrator administrator = new Administrator();
    final FileRelease fileRelease = new FileRelease();
    project.setAdministrator(administrator);
    pkg.setProject(project);
    fileRelease.setPackage(pkg);
    return fileRelease;
  }

  /**
   * Called when the <code>releasename</code> XML attribute is encountered.
   * Sets the {@link FileRelease}'s {@linkplain FileRelease#setName(String)
   * name}.
   *
   * @param      releaseName
   *               the name for the {@link FileRelease} that will be published;
   *               may be <code>null</code>
   */
  public void setReleaseName(final String releaseName) {
    assertNotNull(this.release);
    this.log("Setting releasename: " + releaseName);
    this.release.setName(releaseName);
  }

  /**
   * Called when the <code>hidden</code> XML attribute is encountered.  Sets
   * whether the {@link FileRelease} that will be published will be active or
   * hidden.
   *
   * @param      hidden
   *               if <code>true</code>, then the {@link FileRelease} that will
   *               be published will not be visible
   */
  public void setHidden(final boolean hidden) {
    assertNotNull(this.release);
    this.log("Setting hidden: " + hidden);
    this.release.setHidden(hidden);
  }

  /**
   * Called when the <code>packagename</code> XML attribute is encountered.
   * Sets the {@linkplain Package#setName(String) <code>Package</code> name}.
   *
   * @param      packageName
   *               the name for the {@link Package} to which the {@link
   *               FileRelease} that will be published belongs; may be
   *               <code>null</code>
   */
  public void setPackageName(final String packageName) {
    assertNotNull(this.release);
    final Package pkg = this.release.getPackage();
    assertNotNull(pkg);
    this.log("Setting packagename: " + packageName);
    pkg.setName(packageName);
  }

  /**
   * Called when the <code>packagehidden</code> XML attribute is encountered.
   * Sets the {@linkplain Package#setHidden(boolean) visibility of the
   * <code>Package</code>}.
   *
   * @param      hidden
   *               if <code>true</code>, then the {@link Package} to which the
   *               {@link FileRelease} that will be published belongs will be
   *               marked as invisible
   */
  public void setPackageHidden(final boolean hidden) {
    assertNotNull(this.release);
    final Package pkg = this.release.getPackage();
    assertNotNull(pkg);
    this.log("Setting packagehidden: " + hidden);
    pkg.setHidden(hidden);
  }

  /**
   * Called when the <code>projectshortname</code> XML attribute is encountered.
   * Sets the {@linkplain Project#setShortName(String) short name of the
   * <code>Project</code>} to which the {@link FileRelease} that will be
   * published belongs.
   *
   * @param      shortName
   *               the short name of the {@link Project} to which the {@link
   *               FileRelease} that will be published belongs; may be
   *               <code>null</code>
   */
  public void setProjectShortName(final String shortName) {
    assertNotNull(this.release);
    final Package pkg = this.release.getPackage();
    assertNotNull(pkg);
    final Project project = pkg.getProject();
    assertNotNull(project);
    this.log("Setting projectshortname: " + shortName);
    project.setShortName(shortName);
  }

  /**
   * Called if a <code>projectlongname</code> XML attribute is encountered.
   * Calls the {@link #setProjectName(String)} method.
   *
   * @param      name
   *               the human-readable name for the {@link Project} to which the
   *               {@link FileRelease} that will be published belongs; may be
   *               <code>null</code>
   */
  public final void setProjectLongName(final String name) {
    this.setProjectName(name);
  }

  /**
   * Called when the <code>projectname</code> XML attribute is encountered.
   * Sets the human-readable {@linkplain Project#setName(String) name of the
   * <code>Project</code>} to which the {@link FileRelease} that will be
   * published belongs.
   *
   * @param      name
   *               the human-readable {@linkplain Project#setName(String) name
   *               of the <code>Project</code>} to which the {@link FileRelease}
   *               that will be published belongs; may be <code>null</code>
   */
  public void setProjectName(final String name) {
    assertNotNull(this.release);
    final Package pkg = this.release.getPackage();
    assertNotNull(pkg);
    final Project project = pkg.getProject();
    assertNotNull(project);
    this.log("Setting projectname: " + name);
    project.setName(name);
  }

  /**
   * Called when the <code>username</code> XML attribute is encountered.  Sets
   * the {@linkplain Administrator#setName(String) username of the
   * <code>Administrator</code>} that governs the {@link Project} to which the
   * {@link FileRelease} to be published belongs.
   *
   * @param      userName
   *               the name for the associated {@link Administrator}; must not
   *               be <code>null</code>
   * @exception  BuildException
   *               if the supplied user name is <code>null</code>, equal to the
   *               empty {@link String}, or consists solely of whitespace
   */
  public void setUserName(final String userName)
    throws BuildException {
    assertNotNull(this.release);
    final Package pkg = this.release.getPackage();
    assertNotNull(pkg);
    final Project project = pkg.getProject();
    assertNotNull(project);
    final Administrator admin = project.getAdministrator();
    assertNotNull(admin);
    this.log("Setting username: " + userName);
    try {
      admin.setName(userName);
    } catch (final IllegalArgumentException kaboom) {
      throw new BuildException(kaboom);
    }
  }

  /**
   * Called when the <code>password</code> XML attribute is encountered.  Sets
   * the {@linkplain Administrator#setPassword(String) password of the
   * <code>Administrator</code>} that governs the {@link Project} to which the
   * {@link FileRelease} to be published belongs.
   *
   * @param      password
   *               the name for the associated {@link Administrator}; may be
   *               <code>null</code>
   */
  public void setPassword(final String password) {
    assertNotNull(this.release);
    final Package pkg = this.release.getPackage();
    assertNotNull(pkg);
    final Project project = pkg.getProject();
    assertNotNull(project);
    final Administrator admin = project.getAdministrator();
    assertNotNull(admin);
    this.log("Setting password: " + password);
    admin.setPassword(password);
  }

  /**
   * Called when the <code>releasedate</code> XML attribute is encountered.
   * Sets the {@linkplain FileRelease#setReleaseDate(Date) release date for the
   * <code>FileRelease</code>} that will be published.
   *
   * @param      date
   *               the new release {@link Date}; may be <code>null</code> in
   *               which case the current {@link Date} will be used instead
   */
  public void setReleaseDate(final Date date) {
    assertNotNull(this.release);
    final Date actualDate;
    if (date == null) {
      actualDate = new Date();
    } else {
      actualDate = date;
    }
    this.log("Setting releasedate: " + actualDate);
    this.release.setReleaseDate(actualDate);
  }

  /**
   * Called when the <code>notify</code> XML attribute is encountered.  Sets
   * whether <a href="http://sourceforge.net/">SourceForge</a> users are
   * notified when the {@link FileRelease} to be published actually is
   * published.
   *
   * @param      notify
   *               if <code>true</code>, then <a
   *               href="http://sourceforge.net/">SourceForge</a> users will be
   *               notified when the {@link FileRelease} to be published
   *               actually is published 
   */
  public void setNotify(final boolean notify) {
    assertNotNull(this.release);
    this.log("Setting notify: " + notify);
    this.release.setNotifyOthers(notify);
  }

  /**
   * Called when the <code>changelog</code> XML attribute is encountered.  Sets
   * the changelog {@link File} associated with the {@link FileRelease} to be
   * published.
   *
   * @param      changeLogFile
   *               the changelog {@link File} to be associated with the {@link
   *               FileRelease} to be published; may be <code>null</code>
   */
  public void setChangeLog(final File changeLogFile) {
    assertNotNull(this.release);
    this.log("Setting changelog: " + changeLogFile);
    this.release.setChangeLogFile(changeLogFile);
  }

  /**
   * Called when the <code>releasenotes</code> XML attribute is encountered.
   * Sets the release notes {@link File} associated with the {@link FileRelease}
   * to be published.
   *
   * @param      releaseNotesFile
   *               the release notes {@link File} to be associated with the
   *               {@link FileRelease} to be published; may be <code>null</code>
   */
  public void setReleaseNotes(final File releaseNotesFile) {
    assertNotNull(this.release);
    this.log("Setting releasenotes: " + releaseNotesFile);
    this.release.setReleaseNotesFile(releaseNotesFile);
  }

  /**
   * Called when a nested <code>filespec</code> XML element is encountered.
   * Creates a new, unconfigured {@link FileSpec} object.  This method never
   * returns <code>null</code>.
   *
   * @return     a new {@link FileSpec} object; never <code>null</code>
   */
  public FileSpec createFilespec() {
    final FileSpec spec = new FileSpec();
    this.fileSpecs.addElement(spec);
    return spec;
  }

  /**
   * Ensures that the supplied {@link Administrator} is not <code>null</code>.
   *
   * @param      admin
   *               the {@link Administrator} to test; may be <code>null</code>
   *               in which case a {@link BuildException} will be thrown
   * @exception  BuildException
   *               if the supplied {@link Administrator} is <code>null</code>
   */
  private static final void assertNotNull(final Administrator admin)
    throws BuildException {
    if (admin == null) {
      throw new BuildException("admin == null");
    }
  }

  /**
   * Ensures that the supplied {@link Project} is not <code>null</code>.
   *
   * @param      project
   *               the {@link Project} to test; may be <code>null</code> in 
   *               which case a {@link BuildException} will be thrown
   * @exception  BuildException
   *               if the supplied {@link Project} is <code>null</code>
   */
  private static final void assertNotNull(final Project project)
    throws BuildException {
    if (project == null) {
      throw new BuildException("project == null");
    }
  }

  /**
   * Ensures that the supplied {@link Package} is not <code>null</code>.
   *
   * @param      pkg
   *               the {@link Package} to test; may be <code>null</code> in
   *               which case a {@link BuildException} will be thrown
   * @exception  BuildException
   *               if the supplied {@link Package} is <code>null</code>
   */
  private static final void assertNotNull(final Package pkg)
    throws BuildException {
    if (pkg == null) {
      throw new BuildException("pkg == null");
    }
  }

  /**
   * Ensures that the supplied {@link FileRelease} is not <code>null</code>.
   *
   * @param      release
   *               the {@link FileRelease} to test; may be <code>null</code> in
   *               which case a {@link BuildException} will be thrown
   * @exception  BuildException
   *               if the supplied {@link FileRelease} is <code>null</code>
   */
  private static final void assertNotNull(final FileRelease release)
    throws BuildException {
    if (release == null) {
      throw new BuildException("release == null");
    }
  }

  /**
   * Called by the <a href="http://ant.apache.org/">Ant</a> framework to execute
   * this {@link SourceForgePublish} {@link Task}.  {@linkplain
   * FileRelease#publish() Publishes} the {@link FileRelease} that has been
   * configured behind the scenes by this task.
   *
   * @exception  BuildException
   *               if an error occurs
   */
  public void execute() throws BuildException {
    // We use Vectors and Enumerations here instead of ArrayLists and Iterators
    // because Ant 1.5 and earlier require only JDK 1.1, which didn't have the
    // collections classes.
    final Vector specs = new Vector();
    final Enumeration e = this.fileSpecs.elements();
    if (e != null) {
      FileSpec fileSpec;
      FileSpecification spec;
      while (e.hasMoreElements()) {
        try {
          fileSpec = (FileSpec)e.nextElement();
          if (fileSpec == null) {
            continue;
          }
        } catch (final NoSuchElementException ignore) {
          continue;
        } catch (final ClassCastException wontHappen) {
          continue;
        }
        spec = fileSpec.getFileSpecification();
        if (spec != null) {
          specs.addElement(spec);
        }
      }
      final FileSpecification[] specsArray = 
        new FileSpecification[specs.size()];
      specs.copyInto(specsArray);
      this.release.setFileSpecifications(specsArray);
      this.release.setPublisher(new HttpUnitPublisher());
      try {
        this.release.publish();
      } catch (final Exception everything) {
        throw new BuildException(everything);
      }
    }
  }
}
