/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: HttpUnitPublisher.java,v 1.13.2.1 2003/07/15 21:38:40 ljnelson Exp $
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

import java.io.Serializable;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import java.util.logging.Logger;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.TableCell;
import com.meterware.httpunit.UploadFileSpec;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

import org.xml.sax.SAXException;

import sfutils.Administrator;
import sfutils.Project;

import sfutils.frs.FileRelease;
import sfutils.frs.FileSpecification;
import sfutils.frs.HideableNamedObject;
import sfutils.frs.Package;
import sfutils.frs.Publisher;
import sfutils.frs.PublishingException;

//import ch.ethz.ssh2.*;
import com.sshtools.j2ssh.*;
import com.sshtools.j2ssh.authentication.*;

/**
 * A {@link Publisher} that uses the <a
 * href="http://httpunit.sourceforge.net/">HttpUnit</a> framework to interface
 * with the <a href="http://sourceforge.net/">SourceForge</a> File Release
 * System.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.13.2.1 $ $Date: 2003/07/15 21:38:40 $
 * @since      June 19, 2003
 * @see        Publisher
 * @see        FileRelease
 * @see        <a
 *               href="http://sourceforge.net/docman/display_doc.php?docid=6445&group_id=1">Guide
 *               to the SourceForge File Release System (FRS)</a>
 * @todo       Possibly reorganize to be less flow-driven and more stateless.
 *               Ideas include methods that extract identifiers for projects,
 *               packages and file releases, and methods that perform operations
 *               on projects, packages and file releases given their
 *               identifiers.
 * @todo       Introduce case insensitivity in appropriate places.
 */
public class HttpUnitPublisher implements Publisher, Serializable {

  /**
   * The {@link String} that represents the URL to use to log in to <a
   * href="http://sourceforge.net/">SourceForge</a>.
   */
  private static final String LOGIN_URL = 
    "http://sourceforge.net/account/login.php";

  /**
   * A {@link String} representing a package or file release's visibility
   * status.
   */
  private static final String VISIBLE = "1";

  /**
   * A {@link String} that indicates that a value of a form has been turned on.
   */
  private static final String ON = "1";

  /**
   * A {@link String} that corresponds to a file release's or package's hidden
   * status.
   */
  private static final String HIDDEN = "3";

  /**
   * The name of a form parameter in the <a
   * href="http://sourceforge.net/">SourceForge</a> file release system whose
   * value indicates what action to take next.
   */
  private static final String PACKAGE_ACTION_PARAMETER = "func";

  /**
   * One possible value for the form parameter whose name is equal to the value
   * of the {@link #PACKAGE_ACTION_PARAMETER} field.
   */
  private static final String UPDATE_PACKAGE = "update_package";

  /**
   * One possible value for the form parameter whose name is equal to the value
   * of the {@link #PACKAGE_ACTION_PARAMETER} field.
   */
  private static final String ADD_PACKAGE = "add_package";

  /**
   * A form parameter name whose corresponding value will be the name of a
   * package.
   */
  private static final String PACKAGE_NAME = "package_name";

  /**
   * A form parameter name whose corresponding value will be the identifier of a
   * package.
   */
  private static final String PACKAGE_ID = "package_id";

  /**
   * A form parameter name whose corresponding value will be the identifier of a
   * project.
   */
  private static final String GROUP_ID = "group_id";

  /**
   * A form parameter name whose corresponding value will be either {@link
   * #HIDDEN} or {@link #VISIBLE}.
   */
  private static final String STATUS = "status_id";

  /**
   * A form parameter name whose corresponding value will be the name of a file
   * release.
   */
  private static final String RELEASE_NAME = "release_name";

  /**
   * A form parameter name whose corresponding value will be the release date of
   * a file release.
   */
  private static final String RELEASE_DATE = "release_date";

  /**
   * A form parameter name whose corresponding value will be a {@link File} that
   * will be uploaded to the <a href="http://sourceforge.net/">SourceForge</a>
   * website to describe the changes in a given file release.
   */
  private static final String UPLOAD_CHANGE_LOG = "uploaded_changes";

  /**
   * A form parameter name whose corresponding value will be a {@link File} that
   * will be uploaded to the <a href="http://sourceforge.net/">SourceForge</a>
   * website to describe the changes in a given file release from one version to
   * the next.
   */
  private static final String UPLOAD_RELEASE_NOTES = "uploaded_notes";

  /**
   * A form parameter name whose value will be the contents of a changelog.
   */
  private static final String CHANGE_LOG = "release_changes";

  /**
   * A form parameter name whose value will be the contents of a release notes
   * file.
   */
  private static final String RELEASE_NOTES = "release_notes";

  /**
   * A form parameter name whose value will indicate whether preformatted text
   * is to be preserved.  Its value may be equal to the value of the {@link #ON}
   * field.
   */
  private static final String PRESERVE_FORMATTED_TEXT = "preformatted";

  /**
   * An <code>int</code> that corresponds to the step on the "edit release" page
   * that will actually edit the file release in question.
   */
  private static final int EDIT_RELEASE_STEP = 1;

  /**
   * An <code>int</code> that corresponds to the step on the "edit release" page
   * that will select files to be included as part of the file release in
   * question.
   */
  private static final int ADD_FILES_STEP = 2;

  /**
   * An <code>int</code> that corresponds to the step on the "edit release" page
   * that will cause other <a href="http://sourceforge.net/">SourceForge</a>
   * users monitoring the file release in question to be notified by email of
   * its release.  
   */
  private static final int NOTIFY_OTHERS_STEP = 4;

  /**
   * A relative URL {@link String} used as the value for a certain form's
   * <code>ACTION</code> attribute when the form is designed to return a page
   * that allows the user to edit the file releases that belong to a package.
   */
  private static final String EDIT_RELEASES_ACTION =
    "/project/admin/editreleases.php";

  /**
   * A relative URL {@link String} used as the value for a certain form's
   * <code>ACTION</code> attribute when the form is designed to return a page
   * that allows the user to edit the packages that belong to a project.
   */
  private static final String EDIT_PACKAGES_ACTION =
    "/project/admin/editpackages.php";

  /**
   * The {@link Logger} used by all instances of this class.  This field is
   * never <code>null</code>.
   */
  private static final Logger LOGGER;

  /**
   * The {@link DateFormat} used to format release dates.  This field is never
   * <code>null</code> and <i>must</i> be synchronized on before use.
   */
  private static final DateFormat DATE_FORMATTER;

  /**
   * Static initializer; initializes the {@link Logger} used by this class as
   * well as other private fields.
   */
  static {
    LOGGER = Logger.getLogger(HttpUnitPublisher.class.getName());
    DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    HttpUnitOptions.setExceptionsThrownOnScriptError(false);
  }

  /**
   * Creates a new {@link HttpUnitPublisher}.
   */
  public HttpUnitPublisher() {
    super();
  }

  /**
   * Publishes the supplied {@link FileRelease} to its associated {@link
   * Project} area on <a href="http://sourceforge.net/">SourceForge</a>.
   *
   * @param      release
   *               the {@link FileRelease} to publish; must not be
   *               <code>null</code>
   * @exception  PublishingException
   *               if the supplied {@link FileRelease} could not be published
   */
  public void publish(final FileRelease release)
    throws PublishingException {

    // Validate the FileRelease object graph.
    assertNotNull(release, "release");
    final FileSpecification[] files = release.getFileSpecifications();
    assertArrayFull(files, "files");
    final Package pkg = release.getPackage();
    assertNotNull(pkg, "pkg");
    final Project project = pkg.getProject();
    assertNotNull(project, "project");
    final String projectShortName = project.getShortName();
    assertNotNull(projectShortName, "projectShortName");
    final Administrator admin = project.getAdministrator();
    assertNotNull(admin, "admin");
    final String userName = admin.getName();
    assertNotNull(userName, "userName");
    final String password = admin.getPassword();

    // Begin our session.
    final WebConversation conversation = new WebConversation();

    try {
/*
      // First, log in.
      final WebResponse loginResponse = this.login(conversation, project);
      assertNotNull(loginResponse, "loginResponse");
      LOGGER.info("Retrieved " + loginResponse.getTitle() +
                  " (result of logging in)");
      LOGGER.info("Logged in as " + userName);

      // Next, request the project Summary page.
//James: conversation wasn't remaining logged in, find link on page
//      final WebResponse summaryPage =
//        this.getSummaryPage(conversation, projectShortName);
      final WebResponse summaryPage =
        this.getSummaryPage(loginResponse, projectShortName);
      assertNotNull(summaryPage, "summaryPage");
      LOGGER.info("Retrieved " + summaryPage.getTitle() + " (summary page)");

      // Next, go to the Admin page linked off it.
      final WebResponse devPage = this.getDevPage(summaryPage);
      assertNotNull(devPage, "devPage");
      LOGGER.info("Retrieved " + devPage.getTitle() + " (developer page)");


      // Next, go to the Admin page linked off it.
      /*final WebResponse adminPage = this.getAdminPage(devPage);
      assertNotNull(adminPage, "adminPage");
      LOGGER.info("Retrieved " + adminPage.getTitle() + " (admin page)");

      // Now go to the Packages page.
      final WebResponse packagesPage = this.getPackagesPage(adminPage);
      */
/*
      //Can get package page straight from devPage
      final WebResponse packagesPage = this.getPackagesPage(devPage);
      assertNotNull(packagesPage, "packagesPage");
      LOGGER.info("Retrieved " + packagesPage.getTitle() + " (packages page)");
*/
      // Work around a race condition in SourceForge that means that we have to
      // upload our files now.
//James: Upload now uses SFTP
//      this.uploadFiles(release);
      this.uploadSFTP(release,userName,password);
/*
      // Get the Edit Release page from it.  This is a bulky operation.
      WebResponse editReleasePage =
        this.getEditReleasePage(conversation,
                                packagesPage,
                                release);
      assertNotNull(editReleasePage, "editReleasePage");
      LOGGER.info("Retrieved " + editReleasePage.getTitle() +
                  " (packages page)");

      // Go, finally, edit the new or existing file release that corresponds to
      // the supplied FileRelease object.
      this.processFileRelease(editReleasePage, release);
*/
      LOGGER.info("Finished processing release.");
/*
    } catch (final SAXException wrapMe) {
      throw new PublishingException(wrapMe);
*/      
    } catch (final IOException e) {
     throw new PublishingException(e);
    }

  }

  /**
   * Ensures that all the properties and attributes specified in the supplied
   * {@link FileRelease} are saved persistently to <a
   * href="http://sourceforge.net/">SourceForge</a>.  This method is called by
   * the {@link #processFileRelease(WebResponse, FileRelease)} method.
   *
   * <p>This method never returns <code>null</code>.</p>
   *
   * @param      editReleasePage
   *               the {@link WebResponse} that corresponds to the edit release
   *               page in <a href="http://sourceforge.net/">SourceForge</a>'s
   *               file release system; must not be <code>null</code>
   * @param      release
   *               the {@link FileRelease} to save; must not be
   *               <code>null</code>
   * @return     what amounts to the same page as the supplied {@link
   *               WebResponse} that hopefully now reflects the attributes and
   *               properties of the supplied {@link FileRelease}; never
   *               <code>null</code>
   * @exception  PublishingException
   *               if an error occurs 
   */
  protected WebResponse saveFileReleaseAttributes(WebResponse editReleasePage,
                                                  final FileRelease release)
    throws PublishingException {
    assertNotNull(editReleasePage, "editReleasePage");
    assertNotNull(release, "release");
    final String formattedDate = this.formatReleaseDate(release);
    assertNotNull(formattedDate, "formattedDate");
    try {
      final WebForm step1Form = 
        this.getStepForm(editReleasePage, EDIT_RELEASE_STEP);
      assertNotNull(step1Form, "step1Form");
      step1Form.setParameter(RELEASE_DATE, formattedDate);
      if (release.isHidden()) {
        step1Form.setParameter(STATUS, HIDDEN);
      } else {
        step1Form.setParameter(STATUS, VISIBLE);
      }
      if (release.getPreserveFormattedText()) {
        step1Form.setParameter(PRESERVE_FORMATTED_TEXT, ON);
      } else {
        step1Form.removeParameter(PRESERVE_FORMATTED_TEXT);
      }
      final File changeLogFile = release.getChangeLogFile();
      if (changeLogFile != null && changeLogFile.canRead()) {
        step1Form.setParameter(UPLOAD_CHANGE_LOG,
                               new UploadFileSpec[] { 
                                 new UploadFileSpec(changeLogFile) 
                               });
      } else {
        final String changeLogText = release.getChangeLog();
        if (changeLogText != null) {
          step1Form.setParameter(CHANGE_LOG, changeLogText);
        }
      }
      final File releaseNotesFile = release.getReleaseNotesFile();
      if (releaseNotesFile != null && releaseNotesFile.canRead()) {
        step1Form.setParameter(UPLOAD_RELEASE_NOTES,
                               new UploadFileSpec[] { 
                                 new UploadFileSpec(releaseNotesFile)
                               });
      } else {
        final String releaseNotesText = release.getReleaseNotes();
        if (releaseNotesText != null) {
          step1Form.setParameter(RELEASE_NOTES, releaseNotesText);
        }
      }
      editReleasePage = step1Form.submit();
      assertNotNull(editReleasePage, "editReleasePage");
      return editReleasePage;
    } catch (final SAXException wrapMe) {
      throw new PublishingException(wrapMe);
    } catch (final IOException wrapMe) {
      throw new PublishingException(wrapMe);
    }
  }

  /**
   * Formats the {@linkplain FileRelease#getReleaseDate() release date
   * associated with the supplied <code>FileRelease</code>}.  This method may
   * return <code>null</code>.
   *
   * @param      release
   *               the {@link FileRelease} whose {@linkplain
   *               FileRelease#getReleaseDate() release date} will be formatted;
   *               if <code>null</code>, then <code>null</code> will be returned
   * @return     the formatted {@link Date}, or <code>null</code>
   */
  protected String formatReleaseDate(final FileRelease release) {
    if (release == null) {
      return null;
    }
    final Date releaseDate = release.getReleaseDate();
    if (releaseDate == null) {
      return null;
    }
    synchronized (DATE_FORMATTER) {
      return DATE_FORMATTER.format(releaseDate);
    }
  }

  /**
   * Works on the "edit release" page so that the supplied {@link FileRelease}
   * is effectively saved to <a href="http://sourceforge.net/">SourceForge</a>.
   * Returns the "edit release" page after all edits have been made.
   *
   * <p>This method is called by the {@link #publish(FileRelease)} method and
   * will never return <code>null</code>.</p>
   *
   * @param      editReleasePage
   *               a {@link WebResponse} that represents the "edit release"
   *               page; must not be <code>null</code>
   * @param      release
   *               the {@link FileRelease} to be released; must not be
   *               <code>null</code>
   * @return     a {@link WebResponse} representing the "edit release" page
   *               after all edits have been made; never <code>null</code>
   * @exception  PublishingException
   *               if an error occurs
   */
  protected WebResponse processFileRelease(WebResponse editReleasePage,
                                           final FileRelease release)
    throws PublishingException {
    editReleasePage = this.saveFileReleaseAttributes(editReleasePage, release);
    editReleasePage = this.addFiles(editReleasePage, release);
    editReleasePage = this.editFiles(editReleasePage, release);
    editReleasePage = this.notifyOthers(editReleasePage, release);
    return editReleasePage;
  }

  /**
   * Ensures that other <a href="http://sourceforge.net/">SourceForge</a> users
   * who have expressed interest are notified when the supplied {@link
   * FileRelease} is released.  Returns the "edit release" page after all edits
   * have been made.  This method is called by the {@link
   * #processFileRelease(WebResponse, FileRelease)} method and never returns
   * <code>null</code>.
   *
   * @param      editReleasePage
   *               a {@link WebResponse} that represents the "edit release"
   *               page; must not be <code>null</code>
   * @param      release
   *               the {@link FileRelease} to be released; must not be
   *               <code>null</code>
   * @return     a {@link WebResponse} representing the "edit release" page
   *               after all edits have been made; never <code>null</code>
   * @exception  PublishingException
   *               if an error occurs
   */
  protected WebResponse notifyOthers(WebResponse editReleasePage,
                                     final FileRelease release)
    throws PublishingException {
    assertNotNull(editReleasePage, "editReleasePage");
    assertNotNull(release, "release");
    if (release.getNotifyOthers()) {
      final WebForm form =
        this.getStepForm(editReleasePage, NOTIFY_OTHERS_STEP);
      if (form != null) {
        try {
          editReleasePage = form.submit();
        } catch (final SAXException kaboom) {
          throw new PublishingException(kaboom);
        } catch (final IOException kaboom) {
          throw new PublishingException(kaboom);
        }
      }
    }
    return editReleasePage;
  }

  /**
   * Adds the {@link File}s that were uploaded earlier in the publishing process
   * to the <a href="http://sourceforge.net/">SourceForge</a> representation of
   * the supplied {@link FileRelease}.  This method works on the "edit release"
   * page and makes the necessary edits there before returning a {@link
   * WebResponse} representing the "edit release" page with all the edits
   * complete.
   *
   * <p>This method is called by the {@link #processFileRelease(WebResponse,
   * FileRelease)} method and will never return <code>null</code>.</p>
   *
   * @param      editReleasePage
   *               a {@link WebResponse} that represents the "edit release"
   *               page; must not be <code>null</code>
   * @param      release
   *               the {@link FileRelease} to be released; must not be
   *               <code>null</code>
   * @return     a {@link WebResponse} representing the "edit release" page 
   *               after all edits have been made; never <code>null</code>
   * @exception  PublishingException
   *               if an error occurs
   */
  protected WebResponse addFiles(WebResponse editReleasePage,
                                 final FileRelease release)
    throws PublishingException {
    assertNotNull(editReleasePage, "editReleasePage");
    assertNotNull(release, "release");
    final FileSpecification[] specs = release.getFileSpecifications();
    assertArrayFull(specs, "specs");

    try {
      WebForm form = this.getStepForm(editReleasePage, ADD_FILES_STEP);
      assertNotNull(form, "form");

      // Next, we submit the form, which effectively causes it to refresh.
      // Lordy, this interface sucks.
      editReleasePage = form.submit();
      assertNotNull(editReleasePage, "editReleasePage");

      form = this.getStepForm(editReleasePage, ADD_FILES_STEP);
      assertNotNull(form, "form");

      // We take advantage of the fact that the value for a file checkbox is the
      // filename itself.
      final SortedSet shortFileNames = this.extractShortFileNames(release);
      assertNotNull(shortFileNames, "shortFileNames");
      LOGGER.info("Setting file_list[] to " + shortFileNames);
      form.setParameter("file_list[]",
                        (String[])shortFileNames.toArray(new String[shortFileNames.size()]));

      editReleasePage = form.submit();
      assertNotNull(editReleasePage, "editReleasePage");
      return editReleasePage;

    } catch (final IOException kaboom) {
      throw new PublishingException(kaboom);
    } catch (final SAXException kaboom) {
      throw new PublishingException(kaboom);
    }

  }

  /**
   * Edits the attributes of the {@link File}s that were uploaded earlier in the
   * publishing process.  This method works on the "edit release"
   * page and makes the necessary edits there before returning a {@link
   * WebResponse} representing the "edit release" page with all the edits
   * complete.
   *
   * <p>This method is called by the {@link #processFileRelease(WebResponse,
   * FileRelease)} method and will never return <code>null</code>.</p>
   *
   * @param      editReleasePage
   *               a {@link WebResponse} that represents the "edit release"
   *               page; must not be <code>null</code>
   * @param      release
   *               the {@link FileRelease} to be released; must not be
   *               <code>null</code>
   * @return     a {@link WebResponse} representing the "edit release" page
   *               after all edits have been made; never <code>null</code>
   * @exception  PublishingException
   *               if an error occurs
   */
  protected WebResponse editFiles(WebResponse editReleasePage,
                                  final FileRelease release)
    throws PublishingException {
    assertNotNull(editReleasePage, "editReleasePage");
    assertNotNull(release, "release");
    try {
      WebForm[] forms =
        this.retainFormsWithAction(editReleasePage.getForms(),
                                   EDIT_RELEASES_ACTION);
      assertArrayFull(forms, "forms");
      if (forms.length < 2) {
        throw new PublishingException("Expected at least two forms");
      } else if (forms.length == 2) {
        // No step three form; no files were uploaded.  We're done.
        return editReleasePage;
      }

      // OK, some more weird assembly here.  Get the table that wraps the forms
      // for editing each individual file.  We have to get this in addition to
      // the forms themselves, because the file name to which a given form
      // applies is stored as plain text in a table cell.
      WebTable table = editReleasePage.getTableStartingWithPrefix("Filename");
      assertNotNull(table, "table");

      WebForm form;
      String title;
      FileSpecification spec;
      int formIndex = 2;
      int tableRowIndex = 1;
      while (formIndex < forms.length) {
        form = forms[formIndex];
        assertNotNull(form, "form");
        if (form.hasParameterNamed("im_sure")) {
          tableRowIndex += 3;
        } else if (form.hasParameterNamed("processor_id") &&
                   form.hasParameterNamed("type_id")) {
          title = table.getCellAsText(tableRowIndex, 0);
          assertNotNull(title, "title");
          spec = release.getFileSpecification(title);
          assertNotNull(spec, "spec");
          form.setParameter("processor_id", 
                            String.valueOf(spec.getProcessorType()));
          form.setParameter("type_id", String.valueOf(spec.getFileType()));
          editReleasePage = form.submit();
          assertNotNull(editReleasePage, "editReleasePage");
          forms =
            this.retainFormsWithAction(editReleasePage.getForms(),
                                       EDIT_RELEASES_ACTION);
          assertArrayFull(forms, "forms");
          table = editReleasePage.getTableStartingWithPrefix("Filename");
          assertNotNull(table, "table");
        } else {
          // ???
        }
        formIndex++;
      }

      return editReleasePage;
    } catch (final IOException kaboom) {
      throw new PublishingException(kaboom);
    } catch (final SAXException kaboom) {
      throw new PublishingException(kaboom);
    }
  }

  /**
   * A convenience method that returns a {@link WebForm} representing the form
   * on the "edit release" page with the supplied "step".  For example, the
   * {@link WebForm} corresponding to the form labeled "Step 1" will be returned
   * if the supplied step is equal to <code>1</code>.  This method is called by
   * the {@link #addFiles(WebResponse, FileRelease)}, {@link
   * #notifyOthers(WebResponse, FileRelease)} and {@link
   * #saveFileReleaseAttributes(WebResponse, FileRelease)} methods and may
   * return <code>null</code>.
   *
   * @param      editReleasePage
   *               a {@link WebResponse} that represents the "edit release"
   *               page; must not be <code>null</code>
   * @param      step
   *               the "step number" of the form on the "edit release" page that
   *               chooses the {@link WebForm} to be returned; must be greater
   *               than or equal to <code>1</code>
   * @return     an appropriate {@link WebForm}, or <code>null</code>
   * @exception  PublishingException
   *               if an error occurs
   */
  protected WebForm getStepForm(final WebResponse editReleasePage,
                                final int step)
    throws PublishingException {
    assertNotNull(editReleasePage, "editReleasePage");
    WebForm[] forms = null;
    try {
      forms = this.retainFormsWithAction(editReleasePage.getForms(),
                                         EDIT_RELEASES_ACTION);
    } catch (final SAXException kaboom) {
      throw new PublishingException(kaboom);
    }
    assertArrayFull(forms, "forms");
    WebForm form;
    for (int i = 0; i < forms.length; i++) {
      form = forms[i];
      if (form != null && form.hasParameterNamed("step" + step)) {
        return form;
      }
    }
    return null;
  }

  /**
   * Uploads all {@link File}s that are reachable from the supplied {@link
   * FileRelease} to <code>ftp://upload.sourceforge.net/incoming/</code>.  The
   * default implementation of this method creates and starts a new {@link
   * HttpUnitPublisher.FileUploader} for each {@link File}, so it is tacitly
   * assumed that the total number of {@link File}s to be uploaded will be
   * relatively small.  This method is called by the {@link
   * #publish(FileRelease)} method.
   *
   * @param      release
   *               the {@link FileRelease} containing {@link FileSpecification}s
   *               containing the {@link File}s to be uploaded; must not be
   *               <code>null</code>
   * @exception  PublishingException
   *               if an error occurs
   */
  public void uploadFiles(final FileRelease release)
    throws PublishingException {
    assertNotNull(release, "release");
    final File[] files = release.getFiles();
    assertArrayFull(files, "files");
    File file;
    String shortName;
    URL url;
    URLConnection connection;
    final Thread[] threads = new Thread[files.length];
    Thread thread;
    final Collection errors = Collections.synchronizedList(new ArrayList());
    try {
      for (int i = 0; i < files.length; i++) {
        file = files[i];
        if (file != null && file.canRead()) {
          thread = new FileUploader(file, errors);
          thread.start();
          threads[i] = thread;
        }
      }
      for (int i = 0; i < threads.length; i++) {
        threads[i].join();
      }
      if (!errors.isEmpty()) {
        final Exception[] exceptions =
          (Exception[])errors.toArray(new Exception[errors.size()]);
        throw new UploadException(exceptions);
      }
    } catch (final InterruptedException ignore) {
      throw new UploadException(new InterruptedException[] { ignore });
    }
  }

  public void uploadSFTP(final FileRelease release, final String username, final String password)
  		throws IOException {
	final String hostname = "frs.sourceforge.net";
	final String releasename = release.getName();
	final Package package2 = release.getPackage();
	final String packagename = package2.getName().toLowerCase();
	final Project project = package2.getProject();
	final String projectname = project.getName().toLowerCase();
	final String dir = "/home/frs/project/" + projectname.substring(0,1) + "/" + projectname.substring(0,2) + "/" + projectname + "/" + projectname + "/" + releasename;
	final int port = 22;
	final File[] files = release.getFiles();
	//String previousValue = System.setProperty("log4j.rootCategory", "WARN");
	SshClient ssh = new SshClient();
	ssh.connect(hostname);
	//Authenticate
	PasswordAuthenticationClient passwordAuthenticationClient = new PasswordAuthenticationClient();
	passwordAuthenticationClient.setUsername(username + "," + projectname);
	passwordAuthenticationClient.setPassword(password);
	int result = ssh.authenticate(passwordAuthenticationClient);
	if(result != AuthenticationProtocolState.COMPLETE){
		throw new IOException("Authentication failed.");
	}
	//Open the SFTP channel
	SftpClient client = ssh.openSftpClient();
	System.out.println("MAKING DIRS " + dir);
	client.mkdirs(dir);
	System.out.println("FINISHED");
    for (int i = 0; i < files.length; i++) {
        File file = files[i];
		//Send the file
		client.put(file.getAbsolutePath(),dir + "/" + file.getName());
		//client.put(file.getAbsolutePath());
	}
	//disconnect
	client.quit();
	ssh.disconnect();
	//System.setProperty("log4j.rootCategory", previousValue);
  }
/*
//for use with //import ch.ethz.ssh2.*; j2ssh was better (see above)
  public void uploadSFTP(final FileRelease release, final String username, final String password) 
		throws IOException {
	final String hostname = "frs.sourceforge.net";
	final String dir = "uploads";
	Connection conn = new Connection(hostname);
	conn.connect();
	boolean isAuthenticated = conn.authenticateWithPassword(username, password);
	if (isAuthenticated == false) {
		throw new IOException("Authentication failed.");
	}
	SFTPv3Client sftp = new SFTPv3Client(conn);
	final File[] files = release.getFiles();
    for (int i = 0; i < files.length; i++) {
        File file = files[i];
		SFTPv3FileHandle remotefile = sftp.createFile(dir + "/" + file.getName());
		InputStream in = new FileInputStream(file);
		int c;
		int total = 0;
		byte[] buffer = new byte[32768]; 
		while ((c = in.read(buffer,total,32768)) != -1) {
			sftp.write(remotefile, 0, buffer, total, c);
			total += c;
		}
	}
  }
*/

  /**
   * Assembles a {@link SortedSet} of {@linkplain File#getName()
   * <code>File</code> "short name"s} from the supplied {@link FileRelease}.
   * This method is called by the {@link #addFiles(WebResponse, FileRelease)}
   * method and never returns <code>null</code>.
   *
   * @param      release
   *               the {@link FileRelease} to work on; must not be
   *               <code>null</code>
   * @return     a {@link SortedSet} of {@linkplain File#getName()
   *               <code>File</code> "short name"s} from the supplied {@link
   *               FileRelease}
   * @exception  PublishingException
   *               if an error occurs
   */
  protected SortedSet extractShortFileNames(final FileRelease release)
    throws PublishingException {
    assertNotNull(release, "release");
    final SortedSet names = new TreeSet();
    names.addAll(Arrays.asList(release.getShortFileNames()));
    return names;
  }

  /**
   * Returns a {@link WebResponse} that represents the "summary page" for the
   * supplied <a href="http://sourceforge.net/">SourceForge</a> project.  This
   * method will never return <code>null</code>.
   *
   * @param      conversation
   *               the {@link WebConversation} to which all interaction with <a
   *               href="http://sourceforge.net/">SourceForge</a> logically
   *               belongs; must not be <code>null</code>
   * @param      projectShortName
   *               the "short name" of the <a
   *               href="http://sourceforge.net/">SourceForge</a> project whose
   *               summary page should be returned; must not be
   *               <code>null</code>
   * @return     a {@link WebResponse} corresponding to the "summary page" for
   *               the supplied <a
   *               href="http://sourceforge.net/">SourceForge</a> project; never
   *               <code>null</code>
   * @exception  PublishingException
   *               if an error occurs 
   */
  protected WebResponse getSummaryPage(final WebConversation conversation,
                                       final String projectShortName)
    throws PublishingException {
    assertNotNull(conversation, "conversation");
    assertNotNull(projectShortName, "projectShortName");

    final WebRequest mainProjectPageRequest =
      new GetMethodWebRequest(projectURL(projectShortName));
    try {
      final WebResponse returnMe =
        conversation.getResponse(mainProjectPageRequest);
      assertNotNull(returnMe, "returnMe");
      return returnMe;
    } catch (final IOException kaboom) {
      throw new PublishingException(kaboom);
    } catch (final SAXException wrapMe) {
      throw new PublishingException(wrapMe);
    }
  }
  protected WebResponse getSummaryPage(final WebResponse homePage, final String projectShortName)
    throws PublishingException {
    assertNotNull(homePage, "homePage");

    try {
      final WebLink[] links = homePage.getLinks();
      assertArrayFull(links, "homePage.getLinks()");
      
      final WebLink projectLink = this.findLinkWithPartialUrl(links, projectRelative(projectShortName));
      assertNotNull(projectLink, "projectLink");

      final WebResponse returnMe = projectLink.click();
      assertNotNull(returnMe, "returnMe");

      return returnMe;

    } catch (final IOException wrapMe) {
      throw new PublishingException(wrapMe);
    } catch (final SAXException wrapMe) {
      throw new PublishingException(wrapMe);
    }
  }
  protected String projectURL(final String projectShortName) {
  	return "https://" + projectURL(projectShortName) + "/";
  }
  protected String projectFull(final String projectShortName) {
  	return "sourceforge.net/" + projectRelative(projectShortName);
  }
  protected String projectRelative(final String projectShortName) {
  	return "projects/" + projectShortName;
  }
  protected WebLink findLinkWithPartialUrl(final WebLink[] links,
                                          final String urlToMatch) {
    if (urlToMatch == null || links == null || links.length <= 0) {
      return null;
    }
    WebLink link = null;
    String linkUrl;
    boolean found = false;
    for (int i = 0; i < links.length; i++) {
      link = links[i];
      if (link != null) {
        linkUrl = link.getURLString();
        if (linkUrl != null) {
          if (linkUrl.lastIndexOf(urlToMatch) >= 0) {
            found = true;
            break;
          }
        }
      }
    }
    if (!found) {
      return null;
    }
    assert link != null;
    return link;
  }

  /**
   * Returns a {@link WebResponse} that represents the "admin page" for the <a
   * href="http://sourceforge.net/">SourceForge</a> project summarized in the
   * supplied {@link WebResponse} that must have been supplied by the {@link
   * #getSummaryPage(WebConversation, String)} method.  This method will never
   * return <code>null</code>.
   *
   * @param      summaryPage
   *               the {@link WebResponse} returned by the {@link
   *               #getSummaryPage(WebConversation, String)} method; must not be
   *               <code>null</code>
   * @return     a {@link WebResponse} that represents the "admin page" for the
   *               <a href="http://sourceforge.net/">SourceForge</a> project
   *               summarized in the supplied {@link WebResponse}; never
   *               <code>null</code>
   * @exception  PublishingException
   *               if an error occurs
   */
  protected WebResponse getAdminPage(final WebResponse summaryPage)
    throws PublishingException {
    assertNotNull(summaryPage, "summaryPage");

    try {
      final WebLink[] links = summaryPage.getLinks();
      assertArrayFull(links, "summaryPage.getLinks()");
/*      final WebLink loginLink = this.findLinkWithExactText(links, "Log in");      
      if (loginLink == null) {
      	System.out.println("logged in !!!!!!");
      } else {
		  System.out.println(loginLink.asText() + " " + loginLink.getURLString());
	  }
//	for (int i = 0; i < links.length; i++) {
//		System.out.println(links[i].asText() + " " + links[i].getURLString());
//	} */
      final WebLink adminLink = this.findLinkWithExactText(links, "Project Admin");
      assertNotNull(adminLink, "adminLink");

      final WebResponse returnMe = adminLink.click();
      assertNotNull(returnMe, "returnMe");

      return returnMe;

    } catch (final IOException wrapMe) {
      throw new PublishingException(wrapMe);
    } catch (final SAXException wrapMe) {
      throw new PublishingException(wrapMe);
    }
  }
  protected WebResponse getDevPage(final WebResponse devPage)
    throws PublishingException {
    assertNotNull(devPage, "devPage");

    try {
      final WebLink[] links = devPage.getLinks();
      assertArrayFull(links, "devPage.getLinks()");
      final WebLink devLink = this.findLinkWithExactText(links, "Develop", 2);
      assertNotNull(devLink, "devLink");

      final WebResponse returnMe = devLink.click();
      assertNotNull(returnMe, "returnMe");

      return returnMe;

    } catch (final IOException wrapMe) {
      throw new PublishingException(wrapMe);
    } catch (final SAXException wrapMe) {
      throw new PublishingException(wrapMe);
    }
  }

  /**
   * Returns a {@link WebResponse} that represents the "packages page" for the
   * <a href="http://sourceforge.net/">SourceForge</a> project summarized in the
   * supplied {@link WebResponse} that must have been supplied by the {@link
   * #getAdminPage(WebResponse)} method.  This method will never return
   * <code>null</code>.
   *
   * @param      adminPage
   *               the {@link WebResponse} returned by the {@link
   *               #getAdminPage(WebResponse)} method; must not be
   *               <code>null</code>
   * @return     a {@link WebResponse} that represents the "packages page" for
   *               the <a href="http://sourceforge.net/">SourceForge</a> project
   *               summarized in the supplied {@link WebResponse}; never
   *               <code>null</code>
   * @exception  PublishingException
   *               if an error occurs
   */
  protected WebResponse getPackagesPage(final WebResponse adminPage)
    throws PublishingException {
    assertNotNull(adminPage, "adminPage");

    try {
      final WebLink fileReleasesLink =
//        adminPage.getLinkWith("File Releases");
        adminPage.getLinkWith("File Manager");
      assertNotNull(fileReleasesLink, "fileReleasesLink");

      final WebResponse packagesPage = fileReleasesLink.click();
      assertNotNull(packagesPage, "packagesPage");

      return packagesPage;

    } catch (final IOException wrapMe) {
      throw new PublishingException(wrapMe);
    } catch (final SAXException wrapMe) {
      throw new PublishingException(wrapMe);
    }
  }

  /**
   * Logs the supplied {@link Project}'s {@link Project#getAdministrator()
   * Administrator} into the supplied {@link Project} on <a
   * href="http://sourceforge.net/">SourceForge</a>.  The login lasts for as
   * long as the supplied {@link WebConversation} is in memory.
   *
   * <p>This method never returns <code>null</code>.</p>
   *
   * @param      conversation
   *               the {@link WebConversation} in whose scope the login will
   *               take place; must not be <code>null</code>
   * @param      project
   *               the {@link Project} to log into; also the source of the
   *               {@link Administrator} who will be logged in; must not be
   *               <code>null</code> and must provide a non-<code>null</code>
   *               {@link Project#getAdministrator() Administrator}
   * @return     the {@link WebResponse} corresponding to the page that is shown
   *               once a login has completed successfully; never
   *               <code>null</code>
   * @exception  InvalidCredentialsException
   *               if the associated {@link Administrator} could not log in
   * @exception  PublishingException
   *               if any other error occurs
   */
  protected WebResponse login(final WebConversation conversation,
                              final Project project)
    throws InvalidCredentialsException, PublishingException {
    assertNotNull(conversation, "conversation");
    assertNotNull(project, "project");
    final Administrator administrator = project.getAdministrator();
    assertNotNull(administrator, "administrator");
    final String userName = administrator.getName();
    assertNotNull(userName, "userName");
    final String password = administrator.getPassword();

    try {
      final WebResponse getLoginPageResponse =
        conversation.getResponse(new GetMethodWebRequest(LOGIN_URL));
      assertNotNull(getLoginPageResponse, "getLoginPageResponse");
      LOGGER.info("Retrieved " + getLoginPageResponse.getTitle());

      final WebForm[] forms = getLoginPageResponse.getForms();
      assertArrayFull(forms, "forms");

      final WebForm loginForm =
        this.findFormWithAction(forms,
                                "https://sourceforge.net/account/login.php");
      assertNotNull(loginForm, "loginForm");

      loginForm.setParameter("form_loginname", userName);
      loginForm.setParameter("form_pw", password);
      //loginForm.removeParameter("form_securemode");
      //loginForm.removeParameter("form_rememberme");

      final WebRequest loginSubmissionRequest = loginForm.getRequest("login");
      assertNotNull(loginSubmissionRequest, "loginSubmissionRequest");

      final WebResponse loginResponse =
        conversation.getResponse(loginSubmissionRequest);
      assertNotNull(loginResponse, "loginResponse");
      LOGGER.info("Retrieved " + loginResponse.getTitle());

      final String text = loginResponse.getText();
      assertNotNull(text, "text");

      if (text.indexOf("Invalid Password or User Name") >= 0) {
        throw new InvalidCredentialsException(userName, password);
      }

      return loginResponse;

    } catch (final IOException wrapMe) {
      throw new PublishingException(wrapMe);
    } catch (final SAXException wrapMe) {
      throw new PublishingException(wrapMe);
    }
  }

  /**
   * A convenience method that extracts a {@link WebLink} from the supplied
   * array of {@link WebLink}s provided that its {@linkplain WebLink#asText()
   * text} is equal to the supplied {@link String}.  This method may return
   * <code>null</code>.
   *
   * @param      links
   *               the array of {@link WebLink}s from which an appropriate
   *               {@link WebLink} is to be extracted; may be <code>null</code>
   *               in which case <code>null</code> will be returned
   * @param      textToMatch
   *               the {@link String} to which a {@link WebLink}'s {@linkplain
   *               WebLink#asText() text} must be equal in order to be selected;
   *               if <code>null</code>, then <code>null</code> will be returned
   * @return     a {@link WebLink} from the supplied array of {@link WebLink}s
   *               provided that its {@linkplain WebLink#asText() text} is equal
   *               to the supplied {@link String}, or <code>null</code>
   */
  protected WebLink findLinkWithExactText(final WebLink[] links,
                                          final String textToMatch) {
	return findLinkWithExactText(links, textToMatch, 1);
  }
  protected WebLink findLinkWithExactText(final WebLink[] links,
                                          final String textToMatch,
                                          final int index) {
    if (textToMatch == null || links == null || links.length <= 0) {
      return null;
    }
    WebLink link = null;
    String linkText;
    boolean found = false;
    int count = 1;
    for (int i = 0; i < links.length; i++) {
      link = links[i];
      if (link != null) {
        linkText = link.asText();
        if (textToMatch == "Develop") {
	        System.out.println(linkText);
	    }
        if (linkText != null) {
          if (textToMatch.equals(linkText.trim())) {
          	System.out.println(linkText); 
            if (count >= index) {
              found = true;
              break;
            } else {
              count++;
            }
          }
        }
      }
    }
    if (!found) {
      return null;
    }
    assert link != null;
    return link;
  }

  /**
   * Extracts a {@link WebForm} from the supplied array of {@link WebForm}s,
   * provided that its {@linkplain WebForm#getAction() action} is equal to the
   * supplied {@link String}.  This method may return <code>null</code>.
   *
   * @param      forms
   *               the array of {@link WebForm}s to consider; if
   *               <code>null</code> then <code>null</code> will be returned
   * @param      action
   *               the action {@link String} to which a given {@link WebForm}'s
   *               {@linkplain WebForm#getAction() action} must be equal in
   *               order for that {@link WebForm} to be returned; if
   *               <code>null</code> then <code>null</code> will be returned
   * @return     a {@link WebForm} from the supplied array of {@link WebForm}s,
   *               provided that its {@linkplain WebForm#getAction() action} is
   *               equal to the supplied {@link String}, or <code>null</code>
   */
  protected WebForm findFormWithAction(final WebForm[] forms,
                                       final String action) {
    if (forms == null || forms.length <= 0) {
      return null;
    }
    WebForm form;
    String formAction;
    for (int i = 0; i < forms.length; i++) {
      form = forms[i];
      if (form != null) {
        formAction = form.getAction();
        if (formAction == null) {
          if (action == null) {
            return null;
          }
        } else {
          if (formAction.equals(action)) {
            return form;
          }
        }
      }
    }
    return null;
  }

  /**
   * Returns an array of {@link WebForm}s whose elements are those {@link
   * WebForm}s extracted from the supplied {@link WebForm} array with
   * {@linkplain WebForm#getAction() actions} equal to the supplied {@link
   * String}.  This method never returns <code>null</code>.
   *
   * @param      forms
   *               the {@link WebForm} array in which to find candidates; may 
   *               be <code>null</code>
   * @param      action
   *               the {@linkplain WebForm#getAction() action} {@link String} to
   *               match; may be <code>null</code>
   * @return     an array of {@link WebForm}s whose elements are those {@link
   *               WebForm}s extracted from the supplied {@link WebForm} array
   *               with {@linkplain WebForm#getAction() actions} equal to the
   *               supplied {@link String}; never <code>null</code>
   */
  protected WebForm[] retainFormsWithAction(final WebForm[] forms,
                                            final String action) {
    if (forms == null || forms.length <= 0) {
      return new WebForm[0];
    }
    final Collection returnForms = new ArrayList(forms.length);
    WebForm form;
    String formAction;
    for (int i = 0; i < forms.length; i++) {
      form = forms[i];
      if (form != null) {
        formAction = form.getAction();
        if (formAction == null) {
          if (action == null) {
            returnForms.add(form);
          }
        } else {
          if (formAction.equals(action)) {
            returnForms.add(form);
          }
        }
      }
    }
    return (WebForm[])returnForms.toArray(new WebForm[returnForms.size()]);
  }

  /**
   * Creates a new package on the <a
   * href="http://sourceforge.net/">SourceForge</a> website and returns the
   * resulting "packages page", which should now include the newly-created
   * package among its list of existing packages.  This method may return
   * <code>null</code>, or at least cannot be guaranteed to return a
   * non-<code>null</code> result.
   *
   * <p>This method is called from the {@link
   * #getEditReleasePage(WebConversation, WebResponse, FileRelease)} method.</p>
   *
   * @param      createPackageForm
   *               a {@link WebForm} that represents the "create package" form
   *               on the "packages page"; must not be <code>null</code>
   * @param      packageToCreate
   *               the {@link Package} to be created on <a
   *               href="http://sourceforge.net/">SourceForge</a>; must not be
   *               <code>null</code>
   * @return     a {@link WebResponse} representing the "packages page", or
   *               <code>null</code>
   * @exception  PublishingException
   *               if an error occurs
   */
  protected WebResponse createPackage(final WebForm createPackageForm,
                                      final Package packageToCreate)
    throws PublishingException {
    assertNotNull(createPackageForm, "createPackageForm");
    assertNotNull(packageToCreate, "packageToCreate");
    String packageName = packageToCreate.getName();
    assertNotNull(packageName, "packageName");

    try {
      if (!EDIT_PACKAGES_ACTION.equals(createPackageForm.getAction()) ||
          !ADD_PACKAGE.equals(createPackageForm.getParameterValue(PACKAGE_ACTION_PARAMETER))) {
        throw new PublishingException("Wrong form");
      }
      createPackageForm.setParameter(PACKAGE_NAME, packageName);
      return createPackageForm.submit();
    } catch (final IOException kaboom) {
      throw new PublishingException(kaboom);
    } catch (final SAXException kaboom) {
      throw new PublishingException(kaboom);
    }
  }

  /**
   * Returns a {@link WebResponse} representing the "edit release" page.
   * Producing this page may involve creating a new file release or a new
   * package along the way to accurately reflect the contents of the supplied
   * {@link FileRelease}.  This method never returns <code>null</code>.
   *
   * @param      conversation
   *               the {@link WebConversation} currently in effect; must not be
   *               <code>null</code>
   * @param      packagesPage
   *               a {@link WebResponse} representing the "packages page"; must
   *               not be <code>null</code>
   * @param      fileRelease
   *               the {@link FileRelease} whose <a
   *               href="http://sourceforge.net/">SourceForge</a> analog is to
   *               be edited; must not be <code>null</code>
   * @return     a {@link WebResponse} representing the "edit release" page;
   *               never <code>null</code>
   * @exception  PublishingException
   *               if an error occurs 
   */
  protected WebResponse getEditReleasePage(final WebConversation conversation,
                                           WebResponse packagesPage,
                                           final FileRelease fileRelease)
    throws PublishingException {
    assertNotNull(conversation, "conversation");
    assertNotNull(packagesPage, "packagesPage");
    assertNotNull(fileRelease, "fileRelease");
    final Package pkg = fileRelease.getPackage();
    assertNotNull(pkg, "pkg");
    final Project project = pkg.getProject();
    assertNotNull(project, "project");
    final String fileReleaseName = fileRelease.getName();

    try {

      // Get all the forms on the packages page that can affect packages.  These
      // will include:
      //
      // (Legend: {x} = link; _x__ = textbox; [[x]] = dropdown; (x) = button)
      //
      //
      //           Releases                Package Name     Status    Update
      // {Add Releases}{Edit Releases} _foo______________ [[Hidden]] (Update)
      // {Add Releases}{Edit Releases} _bar______________ [[Active]] (Update)
      //
      // New Package Name:
      // _____________
      //
      // (Create This Package)
      //
      //
      // The (Update) and (Create This Package) buttons will submit to the
      // associated action, which is, in all cases,
      // /project/admin/editpackages.php, even though (Update) causes an update
      // to happen to an EXISTING package, and (Create This Package) causes a
      // NEW package to be created.  Note that the New Package form will ALWAYS
      // exist.
      final WebForm[] packageForms =
        this.retainFormsWithAction(packagesPage.getForms(),
                                   EDIT_PACKAGES_ACTION);
      assertArrayFull(packageForms, "packageForms");

      if (packageForms.length == 1) {
        // If there is only one form on the page (with the relevant action
        // attribute; see above), then it is guaranteed to be the form for
        // adding a new package.  That means that there are no existing
        // packages.  So we need to create one.  We'll call this method
        // recursively and return its result, since the creation of a new
        // package will cause that package to show up as an editable package
        // when the packages page is displayed again.
        return
          this.getEditReleasePage(conversation,
                                  this.createPackage(packageForms[0],
                                                     pkg),
                                  fileRelease);
      }

      // By the time we get here, we know that there is more than one package
      // form, which means that we have at least one existing package.  Spin
      // through these package forms and see if they contain the package we've
      // been handed.
      WebForm createPackageForm = null;
      WebForm updatePackageForm = null;
      WebForm currentPackageForm;
      for (int i = 0; i < packageForms.length; i++) {
        currentPackageForm = packageForms[i];
        if (this.isUpdatePackageFormFor(currentPackageForm, pkg)) {
          updatePackageForm = currentPackageForm;
          break;
        } else if (this.isAddPackageForm(currentPackageForm)) {
          createPackageForm = currentPackageForm;
          // (don't do a break here; loop around again, maximizing our chances
          // of finding an update package form)
        }
      }
      if (updatePackageForm == null) {
        assertNotNull(createPackageForm, "createPackageForm");

        // Create a new package.  We'll call this method recursively and return
        // its result, since the creation of a new package will cause that
        // package to show up as an editable package when the packages page is
        // displayed again.
        return
          this.getEditReleasePage(conversation,
                                  this.createPackage(createPackageForm,
                                                     pkg),
                                  fileRelease);
      }

      // We found the package form we want to work with.  Make sure the package
      // status is sync'ed up.  This may involve a form submission that will
      // return us to this page.
      packagesPage = this.synchronizeStatus(updatePackageForm, pkg);

      // We now need to save several things: the package ID, and its "group" ID
      // (which is really the ID of the project that got us here).  We need
      // these because in order to actually edit or add a file release later, we
      // need to call a script that is used SourceForge-wide, and depends on
      // these identifiers to function.

      // Get the package identifier...
      final String packageID = updatePackageForm.getParameterValue(PACKAGE_ID);
      assertNotNull(packageID, "packageID");

      pkg.setID(packageID);

      // ...and then grab its "group ID", which I think is really the
      // project identifier.
      final String groupID = updatePackageForm.getParameterValue(GROUP_ID);
      assertNotNull(groupID, "groupID");

      project.setID(groupID);

      final WebResponse releasesPage =
        this.getReleasesPage(conversation, packageID, groupID);

      if (releasesPage != null) {
        // 1. packagesPage --(edit releases)--> releasesPage --(edit this release)--> editReleasePage
        //                                      ^^^^^^^^^^^^
        LOGGER.info("Found releases page for " + pkg.getName());
        final WebLink editThisReleaseLink =
          this.getEditReleaseLink(releasesPage, fileRelease);
        if (editThisReleaseLink != null) {
          return editThisReleaseLink.click();
        }
      }

      // 2. packagesPage --(edit releases)--> noReleasesPage --(back)----> packagesPage --(add release)--> createReleasePage --(create)--> editReleasePage
      //                                                                   ^^^^^^^^^^^^
      // or
      // 3. packagesPage --(edit releases)--> releasesPage --(no match)--> packagesPage --(add release)--> createReleasePage --(create)--> editReleasePage
      //                                                                   ^^^^^^^^^^^^
      final String newReleaseURL = 
        this.buildNewReleaseHref(conversation, packageID, groupID);
      assertNotNull(newReleaseURL, "newReleaseURL");

      final WebResponse createReleasePage = 
        conversation.getResponse(newReleaseURL);
      assertNotNull(createReleasePage, "createReleasePage");
      LOGGER.info("Retrieved " + createReleasePage.getTitle());

      final WebForm newReleaseForm =
        this.findFormWithAction(createReleasePage.getForms(),
                                "/project/admin/newrelease.php");
      assertNotNull(newReleaseForm, "newReleaseForm");

      newReleaseForm.setParameter(RELEASE_NAME, fileRelease.getName());
      newReleaseForm.setParameter(PACKAGE_ID, packageID);
      newReleaseForm.setParameter(GROUP_ID, groupID);

      return newReleaseForm.submit();

    } catch (final IOException kaboom) {
      throw new PublishingException(kaboom);
    } catch (final SAXException kaboom) {
      throw new PublishingException(kaboom);
    }
  }

  /**
   * Changes the {@linkplain HideableNamedObject#isHidden() hidden status} of
   * the <a href="http://sourceforge.net/'>SourceForge</a> analog of the
   * supplied {@link HideableNamedObject} to match that of the supplied {@link
   * HideableNamedObject}.  This method is called from the {@link
   * #getEditReleasePage(WebConversation, WebResponse, FileRelease)} method and
   * never returns <code>null</code>.
   *
   * @param      updateForm
   *               a {@link WebForm} capable of updating the hidden status of
   *               either a file release or a package (or another type of object
   *               that can be hidden); must not be <code>null</code>
   * @param      hideable
   *               a {@link HideableNamedObject} whose {@linkplain
   *               HideableNamedObject#isHidden() hidden status} will be
   *               examined; must not be <code>null</code>
   * @return     the result of {@linkplain WebForm#submit() submitting} the
   *               the supplied {@link WebForm}; never <code>null</code>
   * @exception  PublishingException
   *               if an error occurs
   */
  protected WebResponse synchronizeStatus(final WebForm updateForm,
                                          final HideableNamedObject hideable)
    throws PublishingException {
    assertNotNull(updateForm, "updateForm");
    assertNotNull(hideable, "hideable");
    final String name = hideable.getName();
    assertNotNull(name, "name");

    try {

      // Make sure its status is correct.  If it's visible, and our
      // Hideable object says that the status is supposed to be hidden,
      // change it accordingly.
      final String status = updateForm.getParameterValue(STATUS);
      assertNotNull(status, "status");

      WebResponse currentPage = null;
      if (VISIBLE.equals(status) && hideable.isHidden()) {
        updateForm.setParameter(STATUS, HIDDEN);
        LOGGER.info("Changing status for " + name + " to hidden");
        currentPage = updateForm.submit();
      } else if (HIDDEN.equals(status) && !hideable.isHidden()) {
        updateForm.setParameter(STATUS, VISIBLE);
        LOGGER.info("Changing status for " + name + " to visible");
        currentPage = updateForm.submit();
      }

      return currentPage;

    } catch (final IOException kaboom) {
      throw new PublishingException(kaboom);
    } catch (final SAXException kaboom) {
      throw new PublishingException(kaboom);
    }
  }

  /**
   * Returns <code>true</code> if the supplied {@link WebForm} is one that can
   * add a package to <a href="http://sourceforge.net/">SourceForge</a>.
   *
   * @param      packageForm
   *               the {@link WebForm} to test; if <code>null</code> then
   *               <code>false</code> will be returned
   * @return     <code>true</code> if the supplied {@link WebForm} can
   *               add a package to <a
   *               href="http://sourceforge.net/">SourceForge</a>
   */
  protected boolean isAddPackageForm(final WebForm packageForm) {
    return this.isPackageFormWithFunction(packageForm, ADD_PACKAGE);
  }

  /**
   * Returns <code>true</code> if the supplied {@link WebForm} is capable of
   * updating the <a href="http://sourceforge.net/">SourceForge</a> analog of
   * the supplied {@link Package}.
   *
   * @param      packageForm
   *               the {@link WebForm} to test; if <code>null</code> then
   *               <code>false</code> will be returned
   * @param      pkg
   *               the {@link Package} in question; if <code>null</code> then
   *               <code>false</code> will be returned
   * @return     <code>true</code> if the supplied {@link WebForm} is capable of
   *               editing the <a href="http://sourceforge.net/">SourceForge</a>
   *               analog of the supplied {@link Package} 
   */
  protected boolean isUpdatePackageFormFor(final WebForm packageForm,
                                           final Package pkg) {
    return
      packageForm != null &&
      pkg != null &&
      this.isPackageFormFor(packageForm, pkg) &&
      this.isPackageFormWithFunction(packageForm, UPDATE_PACKAGE);
  }

  /**
   * Returns <code>true</code> if the supplied {@link WebForm} will affect the
   * <a href="http://sourceforge.net/">SourceForge</a> analog of the supplied
   * {@link Package} in some way.
   *
   * <p>This method is called only by the {@link
   * #isUpdatePackageFormFor(WebForm, Package)} method.</p>
   *
   * @param      packageForm
   *               the {@link WebForm} to test; if <code>null</code> then
   *               <code>false</code> will be returned
   * @param      pkg
   *               the {@link Package} in question; if <code>null</code> then
   *               <code>false</code> will be returned
   * @return     <code>true</code> if the supplied {@link WebForm} will affect
   *               the <a href="http://sourceforge.net/">SourceForge</a>
   *               analog of the supplied {@link Package} in some way
   */
  protected boolean isPackageFormFor(final WebForm packageForm,
                                     final Package pkg) {
    if (packageForm == null || pkg == null) {
      return false;
    }
    final String pkgName = pkg.getName();
    final String packageName =
      packageForm.getParameterValue(PACKAGE_NAME);
    if (pkgName == null) {
      return packageName == null;
    }
    return pkgName.equals(packageName);
  }

  /**
   * Returns <code>true</code> if the supplied {@link WebForm} represents a form
   * to edit a package that has the supplied {@link String} as the value of its
   * "<code>func</code>" input parameter.  This method is essentially a
   * refactored helper method that is called by the {@link
   * #isUpdatePackageFormFor(WebForm, Package)} method.
   *
   * @param      packageForm
   *               the {@link WebForm} to test; may be <code>null</code> in
   *               which case <code>false</code> is returned
   * @param      soughtValue
   *               the "<code>func</code>" parameter value to look for; may be
   *               <code>null</code> in which case this method will almost
   *               certainly return <code>false</code> given that it's extremely
   *               unlikely that the SourceForge website will have
   *               "<code>null</code>" or "" as the value of this parameter
   * @return     <code>true</code> if the supplied {@link WebForm} represents a
   *               form to edit a package that has the supplied {@link String}
   *               as the value of its "<code>func</code>" input parameter;
   *               <code>false</code> in absolutely all other cases 
   */
  protected boolean isPackageFormWithFunction(final WebForm packageForm,
                                              final String soughtValue) {
    if (packageForm == null) {
      return false;
    }
    final String packageAction =
      packageForm.getParameterValue(PACKAGE_ACTION_PARAMETER);
    if (soughtValue == null) {
      return packageAction == null;
    }
    return soughtValue.equals(packageAction);
  }

  /**
   * Returns <code>true</code> if the supplied {@link WebResponse} represents
   * the "edit releases" page.
   *
   * @param      page
   *               the {@link WebResponse} to test; must not be
   *               <code>null</code>
   * @param      checkForReleases
   *               whether or not to dig a little deeper and see if there are
   *               file releases on the page represented by the supplied {@link
   *               WebResponse}
   * @return     <code>true</code> if the supplied {@link WebResponse}
   *               represents the "edit releases" page
   * @exception  PublishingException
   *               if an error occurs 
   */
  protected boolean isReleasesPage(final WebResponse page,
                                   final boolean checkForReleases)
    throws PublishingException {
    assertNotNull(page, "page");

    try {
      final String title = page.getTitle();
      if (title == null || title.indexOf("FRS: Releases") < 0) {
        return false;
      }
      if (checkForReleases) {
        final String text = page.getText();
        return
          text != null &&
          text.indexOf("You Have No Releases Of This Package Defined") < 0;
      }
      return true;
    } catch (final IOException kaboom) {
      throw new PublishingException(kaboom);
    } catch (final SAXException kaboom) {
      throw new PublishingException(kaboom);
    }
  }

  /**
   * Returns a {@link String} that, when treated as a URL, will edit the file
   * releases that belong to the package and project with the corresponding
   * supplied identifiers.  This method never returns <code>null</code>.
   *
   * @param      conversation
   *               the {@link WebConversation} currently in effect; must not be
   *               <code>null</code>
   * @param      packageID
   *               the identifier of the package whose file releases will
   *               ultimately be edited; must not be <code>null</code>
   * @param      groupID
   *               the identifier of the project whose file releases will
   *               ultimately be edited; must not be <code>null</code> and must
   *               have a package with the supplied package identifier
   * @return     a {@link String} that, when treated as a URL, will edit the 
   *               file releases that belong to the package and project with the
   *               corresponding supplied identifiers; never <code>null</code>
   * @exception  PublishingException
   *               if an error occurs 
   */
  protected String buildEditReleasesHref(final WebConversation conversation,
                                         final String packageID,
                                         final String groupID)
    throws PublishingException {
    assertNotNull(conversation, "conversation");
    assertNotNull(packageID, "packageID");
    assertNotNull(groupID, "groupID");

    final WebResponse currentPage = conversation.getCurrentPage();
    assertNotNull(currentPage, "currentPage");

    final URL currentPageURL = currentPage.getURL();
    assertNotNull(currentPageURL, "currentPageURL");

    final StringBuffer returnMe =
      new StringBuffer("editreleases.php?package_id=");
    returnMe.append(packageID);
    returnMe.append("&group_id=");
    returnMe.append(groupID);
    try {
      return new URL(currentPageURL, returnMe.toString()).toString();
    } catch (final MalformedURLException kaboom) {
      throw new PublishingException(kaboom);
    }
  }

  /**
   * Returns a {@link String} that, when treated as a URL, will create a new
   * file release that will belong to the package and project with the
   * corresponding supplied identifiers.  This method never returns
   * <code>null</code>.
   *
   * @param      conversation
   *               the {@link WebConversation} currently in effect; must not be
   *               <code>null</code>
   * @param      packageID
   *               the identifier of the package to which a new file release
   *               will belong; must not be <code>null</code>
   * @param      groupID
   *               the identifier of the project to which a new file release
   *               will belong; must not be <code>null</code> and must have a
   *               package with the supplied package identifier
   * @return     a {@link String} that, when treated as a URL, will create a new
   *               file release that will belong to the package and project with
   *               the corresponding supplied identifiers; never
   *               <code>null</code>
   * @exception  PublishingException
   *               if an error occurs 
   */
  protected String buildNewReleaseHref(final WebConversation conversation,
                                       final String packageID,
                                       final String groupID)
    throws PublishingException {
    assertNotNull(conversation, "conversation");
    assertNotNull(packageID, "packageID");
    assertNotNull(groupID, "groupID");

    final WebResponse currentPage = conversation.getCurrentPage();
    assertNotNull(currentPage, "currentPage");

    final URL currentPageURL = currentPage.getURL();
    assertNotNull(currentPageURL, "currentPageURL");

    final StringBuffer returnMe =
      new StringBuffer("newrelease.php?package_id=");
    returnMe.append(packageID);
    returnMe.append("&group_id=");
    returnMe.append(groupID);
    try {
      return new URL(currentPageURL, returnMe.toString()).toString();
    } catch (final MalformedURLException kaboom) {
      throw new PublishingException(kaboom);
    }
  }

  /**
   * Returns a {@link WebResponse} that represents the "releases page"
   * appropriate for the given project and package identifiers.  This method may
   * return <code>null</code>.
   *
   * @param      conversation
   *               the {@link WebConversation} currently in effect; must not be
   *               <code>null</code>
   * @param      packageID
   *               the identifier of the package whose releases will be shown;
   *               must not be <code>null</code>
   * @param      groupID
   *               the identifier of the project to which the package with the
   *               supplied package identifier must belong; must not be
   *               <code>null</code>
   * @return     a {@link WebResponse} that represents the "releases page", or
   *               <code>null</code>
   * @exception  PublishingException
   *               if an error occurs
   */
  protected WebResponse getReleasesPage(final WebConversation conversation,
                                        final String packageID,
                                        final String groupID)
    throws PublishingException {
    assertNotNull(conversation, "conversation");
    assertNotNull(packageID, "packageID");
    assertNotNull(groupID, "groupID");

    try {
      final String url =
        this.buildEditReleasesHref(conversation, packageID, groupID);
      assertNotNull(url, "url");

      final WebResponse editReleasesPage = conversation.getResponse(url);
      assertNotNull(editReleasesPage, "editReleasesPage");
      LOGGER.info("Retrieved " + editReleasesPage.getTitle());

      if (this.isReleasesPage(editReleasesPage, true)) {
        return editReleasesPage;
      }
      return null;
    } catch (final IOException kaboom) {
      throw new PublishingException(kaboom);
    } catch (final SAXException kaboom) {
      throw new PublishingException(kaboom);
    }
  }

  /**
   * A very special-purpose helper method that extracts the "<code>[Edit
   * Release]</code>" link from the "releases page" that is appropriate for the
   * supplied {@link FileRelease}.  This method may return <code>null</code>.
   *
   * @param      releasesPage
   *               a {@link WebResponse} representing the "releases page"; must
   *               not be <code>null</code>
   * @param      release
   *               the {@link FileRelease} in question; must not be
   *               <code>null</code>
   * @return     a {@link WebLink} corresponding to the appropriate "<code>[Edit
   *               Release]</code>" link, or <code>null</code>
   * @exception  PublishingException
   *               if an error occurs
   */
  protected WebLink getEditReleaseLink(final WebResponse releasesPage,
                                       final FileRelease release)
    throws PublishingException {
    assertNotNull(releasesPage, "releasesPage");
    assertNotNull(release, "release");
    try {
      // The releases page has a table of file releases.  The number of rows
      // in the table, minus one for its title row, will be equal to the
      // number of existing file releases.  So let's look to see if our file
      // release is in here.  If it's not, we'll have to back up and add a
      // release instead.
      final WebTable table = releasesPage.getTableStartingWith("Release Name");
      assertNotNull(table, "table");

      final int rowCount = table.getRowCount();
      TableCell cell;
      String cellText;
      String releaseName;
      int leftBracketIndex;
      WebLink link;
      for (int i = 1; i < rowCount; i++) {
        cell = table.getTableCell(i, 0);
        if (cell != null) {
          cellText = cell.asText();
          if (cellText != null) {
            leftBracketIndex = cellText.indexOf("[Edit This Release]");
            if (leftBracketIndex > 0) {
              releaseName = cellText.substring(0, leftBracketIndex);
              if (releaseName != null) {
                releaseName = releaseName.trim();
                if (releaseName != null &&
                    releaseName.equals(release.getName())) {
                  link = cell.getLinkWith("[Edit This Release]");
                  if (link != null) {
                    return link;
                  }
                }
              }
            }
          }
        }
      }
      return null;
    } catch (final SAXException kaboom) {
      throw new PublishingException(kaboom);
    }
  }

  /**
   * A convenience method that throws a {@link NullObjectException} if the
   * supplied {@link Object} is <code>null</code>.  This method is primarily
   * used to check method arguments and in other cases where a checked {@link
   * Exception} is preferred to the usual {@link IllegalArgumentException}
   * alternative.
   *
   * @param      object
   *               the {@link Object} to check; if <code>null</code> a {@link
   *               NullObjectException} will be thrown
   * @param      message
   *               a message describing the problem if the supplied {@link
   *               Object} is <code>null</code>; may be <code>null</code>
   * @exception  NullObjectException
   *               if the supplied {@link Object} is <code>null</code>
   */
  protected static final void assertNotNull(final Object object,
                                            final String message)
    throws NullObjectException {
    if (object == null) {
      throw new NullObjectException(message);
    }
  }

  /**
   * A convenience metohd that throws either a {@link NullObjectException} or an
   * {@link EmptyArrayException} if the supplied {@link Object} array is
   * <code>null</code> or has a length less than or equal to <code>0</code>
   * respectively.
   *
   * @param      object
   *               the {@link Object} array to check; if <code>null</code> a
   *               {@link NullObjectException} will be thrown and if empty an
   *               {@link EmptyArrayException} will be thrown
   * @param      message
   *               a message describing the problem if the supplied {@link
   *               Object} array is <code>null</code> or empty; may be
   *               <code>null</code>
   * @exception  NullObjectException
   *               if the supplied {@link Object} array is <code>null</code>
   * @exception  EmptyArrayException
   *               if the supplied {@link Object} array has a length less than 
   *               or equal to <code>0</code>
   */
  protected static final void assertArrayFull(final Object[] object,
                                              final String message)
    throws NullObjectException, EmptyArrayException {
    assertNotNull(object, message);
    if (object.length <= 0) {
      throw new EmptyArrayException(message);
    }
  }

  /**
   * A {@link Thread} that uploads a {@link File} to an FTP site.
   *
   * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
   * @version    $Revision: 1.13.2.1 $ $Date: 2003/07/15 21:38:40 $
   * @since June 19, 2003 */
  static final class FileUploader extends Thread {

    /**
     * The FTP URL to which this {@link HttpUnitPublisher.FileUploader}'s
     * {@linkplain #getFile() associated <code>File</code>} will be uploaded.
     */
    private static final String UPLOAD_DIRECTORY =
      "ftp://upload.sourceforge.net/incoming";

    /**
     * The {@link File} to upload.  This field may be <code>null</code>.
     *
     * @see        #getFile()
     */
    private final File file;

    /**
     * The {@link Collection} to which any {@link Exception}s encountered during
     * the execution of the {@link #upload()} method will be added.  This field
     * must never be <code>null</code>.
     *
     * @see        #getErrors()
     */
    private final Collection errors;

    /**
     * Creates a new {@link HttpUnitPublisher.FileUploader}.
     *
     * @param      file
     *               the {@link File} to upload; may be <code>null</code> in
     *               which case the {@link #upload()} method will do nothing
     * @param      errors
     *               the {@link Collection} to which any {@link Exception}s
     *               encountered during the execution of the {@link #upload()}
     *               method will be added; may be <code>null</code> in which
     *               case a new {@link ArrayList} will be used instead
     */
    FileUploader(final File file, final Collection errors) {
      super();
      this.file = file;
      if (errors == null) {
        this.errors = new ArrayList(5);
      } else {
        this.errors = errors;
      }
    }

    /**
     * Returns the {@link File} to upload.  This method may return
     * <code>null</code>.
     *
     * @return     the {@link File} to upload, or <code>null</code>
     */
    public File getFile() {
      return this.file;
    }

    /**
     * Returns a {@link Collection} of any errors encountered during the
     * execution of either the {@link #run()} or the {@link #upload()} method.
     * This method may return <code>null</code>.
     *
     * @return     a {@link Collection} of errors, or <code>null</code>
     */
    public final Collection getErrors() {
      return this.errors;
    }

    /**
     * Calls the {@link #upload()} method.
     */
    public final void run() {
      this.upload();
    }

    /**
     * Uploads this {@link HttpUnitPublisher.FileUploader}'s associated {@link
     * #getFile() File} via FTP to the
     * <code>ftp://upload.sourceforge.net/incoming/</code> directory.  Any
     * errors encountered are placed in the {@linkplain #getErrors() associated
     * errors <code>Collection</code>}.
     */
    public final void upload() {
      InputStream inputStream = null;
      OutputStream outputStream = null;
      final File file = this.getFile();
      if (file == null) {
        return;
      }
      final Collection errors = this.getErrors();
      try {

        // Get an input stream from the file.
        inputStream = new FileInputStream(file);

        // Get an output stream to the FTP location.
        final String shortName = file.getName();
        assert shortName != null;
        final URLConnection connection =
          new URL(UPLOAD_DIRECTORY + "/" + shortName).openConnection();
        assert connection != null;

        connection.setDoOutput(true);
        connection.connect();

        outputStream = connection.getOutputStream();
        assert outputStream != null;

        // Copy the file.
        this.copyStream(inputStream, outputStream);

      } catch (final Exception kaboom) {
        if (errors != null) {
          errors.add(kaboom);
        }
      } finally {
        try {
          if (outputStream != null) {
            outputStream.close();
          }
        } catch (final IOException ignore) {
          // ignore
        }
        try {
          if (inputStream != null) {
            inputStream.close();
          }
        } catch (final IOException ignore) {
          // ignore
        }
      }
    }

    /**
     * Copies the supplied {@link InputStream} to the supplied {@link
     * OutputStream} in chunks of 1,024 bytes.
     *
     * @param      inputStream
     *               the {@link InputStream} to copy; must not be
     *               <code>null</code>
     * @param      outputStream
     *               the {@link OutputStream} to copy the supplied {@link
     *               InputStream} to; must not be <code>null</code>
     * @exception  IOException
     *               if an input/output error occurs
     */
    private final void copyStream(final InputStream inputStream,
                                  final OutputStream outputStream)
      throws IOException {
      assert inputStream != null;
      assert outputStream != null;
      final byte[] buffer = new byte[1024];
      int numberOfBytesRead = 0;
      while ((numberOfBytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, numberOfBytesRead);
      }
    }

  }

}
