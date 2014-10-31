/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: FileRelease.java,v 1.12.4.1 2003/12/22 15:24:37 ljnelson Exp $
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

import java.io.File;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Date;

import sfutils.Project; // for Javadoc only

/**
 * A {@link HideableNamedObject} corresponding to a <a
 * href="http://sourceforge.net/">SourceForge</a> file release, the fundamental
 * unit of publication at <a href="http://sourceforge.net/">SourceForge</a>.
 *
 * <p>Briefly:
 *
 * <ul>
 *
 * <li>a <a href="http://sourceforge.net/">SourceForge</a> <b>project</b> has
 *     one or more <b>packages</b></li>
 *
 * <li>a <a href="http://sourceforge.net/">SourceForge</a> package has zero or
 *     more <b>file releases</b></li>
 *
 * <li>a <a href="http://sourceforge.net/">SourceForge</a> file release has one
 *     or more <b>file specifications</b></li>
 *
 * </ul>
 *
 * These are represented by the following classes, in order:
 *
 * <ul>
 *
 * <li>{@link Project}</li>
 *
 * <li>{@link Package}</li>
 *
 * <li>{@link FileRelease}</li>
 *
 * <li>{@link FileSpecification}</li>
 *
 * </ul></p>
 *
 * <p><a name="fileReqs"></a><a href="http://sourceforge.net/">SourceForge</a>
 * imposes a number of requirements on the name and contents of a {@link File}
 * used as a changelog or release notes file.  Specifically, such a {@link File}
 * must <strong>not</strong>:
 *
 * <ul>
 *
 * <li>be <code>null</code></li>
 *
 * <li>have a {@linkplain File#getName() filename} that contains either a space
 *     (" "), tilde ("~"), left parenthesis ("(") or right parenthesis
 *     (")")</li>
 *
 * <li>be fewer than 20 bytes in {@linkplain File#length() length}</li>
 *
 * <li>be greater than 256,000 bytes in {@linkplain File#length()}</li>
 *
 * </ul>
 *
 * Additionally, such a {@link File} must {@linkplain File#exists() exist} and
 * {@linkplain File#canRead() be readable}.</p>
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.12.4.1 $ $Date: 2003/12/22 15:24:37 $
 * @since      May 21, 2003 
 * @see        Project
 * @see        Package
 * @see        FileRelease
 * @see        FileSpecification 
 * @see        <a
 *               href="http://sourceforge.net/docman/display_doc.php?docid=6445&group_id=1">Guide
 *               to the SourceForge.net File Release System (FRS)</a>
 * @see        <a href="package-summary.html#package_description">The
 *               <code>sfutils.frs</code> package description</a>
 * @see        #validate(File)
 */
public class FileRelease extends HideableNamedObject {

  /**
   * The identifier of this {@link FileRelease}.  {@link FileRelease}
   * identifiers are assigned by <a 
   * href="http://sourceforge.net/">SourceForge</a>.  This field may be
   * <code>null</code>.
   */
  private String id;

  /**
   * The {@link Package} this {@link FileRelease} is a part of.  This field may
   * be <code>null</code>.
   */
  private Package projectPackage;
  
  /**
   * The {@link Date} on which this {@link FileRelease} is to be released.  This
   * field may be <code>null</code>.
   */
  private Date releaseDate;

  /**
   * Whether or not to preserve any formatting that is present in either the
   * {@link #releaseNotes} or {@link #changeLog} field.  This field is
   * initialized to <code>true</code>.
   */
  private boolean preserveFormattedText;

  /**
   * Whether or not to send email to those <a 
   * href="http://sourceforge.net/">SourceForge</a> users who are monitoring
   * this {@link FileRelease}'s {@link Package} describing the new release.
   * This field is initialized to <code>true</code>.  
   */
  private boolean notifyOthers;

  /**
   * A {@link String} containing the release notes for this {@link FileRelease}.
   * This field is ignored if the {@link #releaseNotesFile} field is
   * non-<code>null</code>.
   *
   * <p>This field may be <code>null</code>.</p>
   * 
   * @see        #releaseNotesFile
   */
  private String releaseNotes;

  /**
   * A {@link File} containing the release notes for this {@link FileRelease}.
   * The {@link #releaseNotes} field will be ignored if this field is
   * non-<code>null</code>.
   *
   * @see        #releaseNotes
   */
  private File releaseNotesFile;

  /**
   * A {@link String} containing the change log for this {@link FileRelease}.
   * This field is ignored if the {@link #changeLogFile} field is
   * non-<code>null</code>.
   *
   * <p>This field may be <code>null</code>.</p>
   * 
   * @see        #changeLogFile
   */
  private String changeLog;

  /**
   * A {@link File} containing the change log for this {@link FileRelease}.  The
   * {@link #changeLog} field will be ignored if this field is
   * non-<code>null</code>.
   *
   * @see        #releaseNotes 
   */
  private File changeLogFile;

  /**
   * A {@link Map} of {@link FileSpecification}s indexed by the {@linkplain
   * File#getName() unqualified name}s of their {@linkplain
   * FileSpecification#getFile() associated <code>File</code>}s.  This field
   * cannot be <code>null</code>.  
   */
  private final Map specs;

  /**
   * A {@link Publisher} that handles the uploading and distribution of this
   * {@link FileRelease} to <a href="http://sourceforge.net/">SourceForge</a>.
   * This field may be <code>null</code>.
   */
  private Publisher publisher;

  /**
   * Creates a new {@link FileRelease}.  This constructor simply calls the
   * {@link #FileRelease(Package, String)} constructor with <code>null</code>
   * arguments.
   *
   * <p>The resulting {@link FileRelease} is in an illegal state until, at a
   * minimum, its {@linkplain #setName(String) associated name} and {@linkplain
   * #setPackage(Package) associated <code>Package</code>} are set.</p>
   *
   * @see        #FileRelease(Package, String)
   * @see        #setName(String)
   * @see        #setPackage(Package)
   */
  public FileRelease() {
    this(null, null);
  }

  /**
   * Creates a new {@link FileRelease} and sets its {@linkplain #setName(String)
   * associated name} to the supplied name.  In addition, this constructor
   * creates a new {@link Package} and {@linkplain Package#setName(String) sets
   * its associated name} to the supplied name as well.
   *
   * <p>This constructor {@linkplain Package#Package(String) creates a new
   * <code>Package</code> with the supplied name}, and passes it and the
   * supplied name to the {@link #FileRelease(Package, String)} constructor.</p>
   *
   * @param      name
   *               the name of this {@link FileRelease} (and the name that will
   *               be assigned to the new {@link Package} it will belong to);
   *               may be <code>null</code>, rather uselessly
   * @see        #FileRelease(Package, String)
   * @see        Package#Package(String)
   */
  public FileRelease(final String name) {
    this(new Package(name), name);
  }

  /**
   * Creates a new {@link FileRelease} that belongs to the supplied {@link
   * Package}.  This constructor simply calls the {@link #FileRelease(Package,
   * String)} constructor, passing it the supplied {@link Package} and a
   * <code>null</code> name.
   *
   * @param      projectPackage
   *               the {@link Package} to which this {@link FileRelease}
   *               belongs; may be <code>null</code>, rather uselessly
   * @see        #FileRelease(Package, String)
   */
  public FileRelease(final Package projectPackage) {
    this(projectPackage, null);
  }

  /**
   * Creates a new {@link FileRelease} with the supplied name that belongs to
   * the supplied {@link Package}.  In addition, this constructor also
   * {@linkplain #setReleaseDate(Date) sets the release date} to today's date,
   * {@linkplain #setPreserveFormattedText(boolean) indicates that preformatted
   * change log or release notes text is to be preserved}, and {@linkplain
   * #setNotifyOthers(boolean) sets this <code>FileRelease</code> up to notify
   * those monitoring it when it is released}.
   *
   * @param      projectPackage
   *               the {@link Package} to which this {@link FileRelease}
   *               belongs; may be <code>null</code>, rather uselessly
   * @param      name
   *               the name of this new {@link FileRelease}; may be
   *               <code>null</code>, rather uselessly
   * @see        #setReleaseDate(Date)
   * @see        #setPreserveFormattedText(boolean)
   * @see        #setNotifyOthers(boolean) 
   */
  public FileRelease(final Package projectPackage,
                     final String name) {
    super(name);
    this.specs = new LinkedHashMap(7);
    this.setNotifyOthers(true);
    this.setPackage(projectPackage);
    this.setPreserveFormattedText(true);
    this.setReleaseDate(new Date());
  }

  /**
   * Sets the identifier of this {@link FileRelease}.  Identifiers are assigned
   * by <a href="http://sourceforge.net/">SourceForge</a>.
   *
   * @param      id
   *               the identifier to set; should be a valid <a
   *               href="http://sourceforge.net/">SourceForge</a>-assigned file
   *               release identifier, but may be <code>null</code> (rather
   *               uselessly)
   */
  public void setID(final String id) {
    this.id = id;
  }

  /**
   * Returns the identifier of this {@link FileRelease}.  This method may return
   * <code>null</code>.
   *
   * @return     the identifier of this {@link FileRelease}, or
   *               <code>null</code>
   */
  public String getID() {
    return this.id;
  }

  /**
   * Returns whether or not those monitoring this {@link FileRelease} will be
   * notified when it is published.
   *
   * @return     <code>true</code> if those monitoring this {@link FileRelease}
   *               will be notified when it is published; <code>false</code>
   *               otherwise
   */
  public boolean getNotifyOthers() {
    return this.notifyOthers;
  }
  
  /**
   * Sets whether or not those monitoring this {@link FileRelease} will be
   * notified when it is published.
   *
   * @param      notify
   *               if <code>true</code>, those monitoring this {@link 
   *               FileRelease} will be notified when it is published
   */
  public void setNotifyOthers(final boolean notify) {
    this.notifyOthers = notify;
  }

  /**
   * Returns whether or not text present in the {@linkplain #getReleaseNotes()
   * release notes} or {@linkplain #getChangeLog() change log} will have its
   * formatting preserved.
   *
   * @return     <code>true</code> if text present in the {@linkplain 
   *               #getReleaseNotes() release notes} or {@linkplain
   *               #getChangeLog() change log} will have its formatting
   *               preserved; <code>false</code> otherwise 
   */
  public boolean getPreserveFormattedText() {
    return this.preserveFormattedText;
  }

  /**
   * Sets whether or not text present in the {@linkplain #getReleaseNotes()
   * release notes} or {@linkplain #getChangeLog() change log} will have its
   * formatting preserved.
   *
   * @param      preserve
   *               if <code>true</code>, text present in the {@linkplain
   *               #getReleaseNotes() release notes} or {@linkplain
   *               #getChangeLog() change log} will have its formatting
   *               preserved 
   */
  public void setPreserveFormattedText(final boolean preserve) {
    this.preserveFormattedText = preserve;
  }

  /**
   * Returns the {@link Package} to which this {@link FileRelease} belongs.
   * This method may return <code>null</code>.
   *
   * @return     the {@link Package} to which this {@link FileRelease} belongs,
   *               or <code>null</code>
   */
  public Package getPackage() {
    return this.projectPackage;
  }
  
  /**
   * Sets the {@link Package} to which this {@link FileRelease} belongs.
   *
   * @param      projectPackage
   *               the {@link Package} to which this {@link FileRelease}
   *               belongs; may be <code>null</code> (rather uselessly)
   */
  public void setPackage(final Package projectPackage) {
    this.projectPackage = projectPackage;
  }

  /**
   * Returns the {@link Date} on which this {@link FileRelease} is to be
   * considered released.  The returned {@link Date} may be <code>null</code>,
   * and may not correspond to the actual date on which this {@link FileRelease}
   * is released.
   *
   * @return     the {@link Date} on which this {@link FileRelease} is to be
   *               considered released, or <code>null</code>
   */
  public Date getReleaseDate() {
    return this.releaseDate;
  }

  /**
   * Sets the {@link Date} on which this {@link FileRelease} is to be considered
   * released.
   *
   * @param      date
   *               the {@link Date} on which this {@link FileRelease} is to be
   *               considered released; may be <code>null</code>
   */
  public void setReleaseDate(final Date date) {
    this.releaseDate = date;
  }

  /**
   * Returns the release notes for this {@link FileRelease}.  If this method
   * returns <code>null</code>, make sure to check the return value of the
   * {@link #getReleaseNotesFile()} method as well.
   *
   * @return     the release notes for this {@link FileRelease}, or
   *               <code>null</code>
   * @see        #getReleaseNotesFile()
   */
  public String getReleaseNotes() {
    return this.releaseNotes;
  }

  /**
   * Sets the release notes for this {@link FileRelease}.  Note that the
   * contents of a {@linkplain #getReleaseNotesFile() release notes
   * <code>File</code>} will be used in place of {@linkplain #getReleaseNotes()
   * ad-hoc release notes} wherever possible.
   *
   * @param      releaseNotes
   *               the release notes for this {@link FileRelease} ; may be
   *               <code>null</code> and will be ignored if the return value
   *               of the {@link #getReleaseNotesFile()} method is
   *               non-<code>null</code>
   * @see        #getReleaseNotesFile()
   * @see        #setReleaseNotesFile(File)
   */
  public void setReleaseNotes(final String releaseNotes) {
    this.releaseNotes = releaseNotes;
  }

  /**
   * Returns the {@link File} that contains the release notes for this {@link
   * FileRelease}.  This method may return <code>null</code>.  If this method
   * does <i>not</i> return <code>null</code>, then the returned {@link File}
   * is guaranteed to be {@linkplain File#canRead() readable}.
   *
   * @return     a {@linkplain File#canRead() readable} {@link File} that
   *               contains the release notes for this {@link FileRelease}, or
   *               <code>null</code> 
   */
  public File getReleaseNotesFile() {
    return this.releaseNotesFile;
  }

  /**
   * Sets the {@link File} that contains release notes for this {@link
   * FileRelease}.  If the supplied {@link File} is non-<code>null</code>, then
   * it must {@linkplain File#canRead() exist and be readable}.
   *
   * @param      releaseNotesFile
   *               the {@link File} that contains release notes; may be
   *               <code>null</code>, but if <i>non</i>-<code>null</code>
   *               {@linkplain File#canRead() must exist and be readable}
   * @exception  IllegalArgumentException
   *               if the supplied {@link File} is not {@linkplain
   *               File#canRead() readable}
   */
  public void setReleaseNotesFile(final File releaseNotesFile) 
    throws IllegalArgumentException {
    if (releaseNotesFile != null) {
      validate(releaseNotesFile);
    }
    this.releaseNotesFile = releaseNotesFile;
  }

  /**
   * Returns the change log for this {@link FileRelease}.  If this method
   * returns <code>null</code>, make sure to check the return value of the
   * {@link #getChangeLogFile()} method as well.
   *
   * @return     the change log for this {@link FileRelease}, or 
   *               <code>null</code>
   * @see        #getChangeLogFile()
   */
  public String getChangeLog() {
    return this.changeLog;
  }

  /**
   * Sets the change log for this {@link FileRelease}.  Note that the contents
   * of a {@linkplain #getChangeLogFile() change log <code>File</code>} will be
   * used in place of {@linkplain #getChangeLog() ad-hoc change log} wherever
   * possible.
   *
   * @param      changeLog
   *               the change log for this {@link FileRelease} ; may be
   *               <code>null</code> and will be ignored if the return value of
   *               the {@link #getChangeLogFile()} method is
   *               non-<code>null</code>
   * @see        #getChangeLogFile()
   * @see        #setChangeLogFile(File) 
   */
  public void setChangeLog(final String changeLog) {
    this.changeLog = changeLog;
  }

  /**
   * Returns the {@link File} that contains the change log for this {@link
   * FileRelease}.  This method may return <code>null</code>.  If this method
   * does <i>not</i> return <code>null</code>, then the returned {@link File}
   * is guaranteed to be {@linkplain File#canRead() readable}.
   *
   * @return     a {@linkplain File#canRead() readable} {@link File} that
   *               contains the change log for this {@link FileRelease}, or
   *               <code>null</code> 
   */
  public File getChangeLogFile() {
    return this.changeLogFile;
  }

  /**
   * Sets the {@link File} that contains the change log for this {@link
   * FileRelease}.  If the supplied {@link File} is non-<code>null</code>, then
   * it must {@linkplain File#canRead() exist and be readable}.
   *
   * @param      changeLogFile
   *               the {@link File} that contains the change log; may be
   *               <code>null</code>, but if <i>non</i>-<code>null</code>
   *               {@linkplain File#canRead() must exist and be readable}
   * @exception  IllegalArgumentException
   *               if the supplied {@link File} is not {@linkplain
   *               File#canRead() readable} 
   */
  public void setChangeLogFile(final File changeLogFile) 
    throws IllegalArgumentException {
    if (changeLogFile != null) {
      validate(changeLogFile);
    }
    this.changeLogFile = changeLogFile;
  }

  /**
   * A convenience method that returns all {@link File}s contained by the {@link
   * #getFileSpecifications() FileSpecification}s associated with this {@link
   * FileRelease}.  This method's implementation iterates through this {@link
   * FileRelease}'s {@linkplain #getFileSpecifications() associated
   * <code>FileSpecification</code>s} and extracts their associated {@link
   * File}s.  This method may return <code>null</code>.  Additionally, it cannot
   * be guaranteed that there are not <code>null</code> elements inside the
   * returned {@link File} array.
   *
   * @return     all {@link File}s indirectly associated with this {@link
   *               FileRelease}, or <code>null</code>
   */
  public final File[] getFiles() {
    final FileSpecification[] specs = this.getFileSpecifications();
    if (specs == null) {
      return null;
    }
    final File[] files = new File[specs.length];
    FileSpecification spec;
    for (int i = 0; i < specs.length; i++) {
      spec = specs[i];
      if (spec == null) {
        files[i] = null;
      } else {
        files[i] = spec.getFile();
      }
    }
    return files;
  }

  /**
   * A convenience method that returns all {@linkplain File#getName() filenames}
   * indirectly associated with this {@link FileRelease}.  This method's
   * implementation iterates through this {@link FileRelease}'s {@linkplain
   * #getFiles() associated <code>File</code>s} and extract their {@linkplain
   * File#getName() names}.  This method may return <code>null</code>.
   * Additionally, it cannot be guaranteed that there are not <code>null</code>
   * elements inside the returned {@link File} array.
   *
   * @return     all {@linkplain File#getName() filenames} indirectly associated
   *               with this {@link FileRelease}, or <code>null</code>
   */
  public final String[] getShortFileNames() {
    final File[] files = this.getFiles();
    if (files == null) {
      return null;
    }
    final String[] names = new String[files.length];
    File file;
    for (int i = 0; i < files.length; i++) {
      file = files[i];
      if (file == null) {
        names[i] = null;
      } else {
        names[i] = file.getName();
      }
    }
    return names;
  }

  /**
   * Returns all the {@link FileSpecification}s associated with this {@link
   * FileRelease}.  This method may return <code>null</code>.
   *
   * @return     all the {@link FileSpecification}s associated with this {@link
   *               FileRelease}, or <code>null</code>
   */
  public FileSpecification[] getFileSpecifications() {
    final Collection values = this.specs.values();
    if (values == null) {
      return null;
    }
    return (FileSpecification[])values.toArray(new FileSpecification[values.size()]);
  }

  /**
   * Returns the {@link FileSpecification} that contains the {@link File} with
   * the supplied {@linkplain File#getName() name}, or <code>null</code> if no
   * such {@link FileSpecification} exists.
   *
   * @param      fileBaseName
   *               the {@linkplain File#getName() short name} of the {@link
   *               File} in question; may be <code>null</code>
   * @return     the {@link FileSpecification} that contains the {@link File}
   *               with the supplied {@linkplain File#getName() name}, or
   *               <code>null</code> 
   */
  public FileSpecification getFileSpecification(final String fileBaseName) {
    return (FileSpecification)this.specs.get(fileBaseName);
  }

  /**
   * Sets the {@link FileSpecification}s that are to be associated with this
   * {@link FileRelease}.
   *
   * <p>If the supplied {@link FileSpecification} array is
   * non-<code>null</code>, then no element of it may be <code>null</code> or an
   * {@link IllegalArgumentException} will be thrown.  In addition, any
   * non-<code>null</code> {@link FileSpecification#getFile() File} associated
   * with any given {@link FileSpecification} in the array must {@linkplain
   * File#canRead() exist and be readable}.</p>
   *
   * @param      specs
   *               the {@link FileSpecification}s; may be <code>null</code>
   * @exception  IllegalArgumentException
   *               if any of the {@link FileSpecification}s in the array is not
   *               set up properly; see this method's description for details 
   */
  public void setFileSpecifications(final FileSpecification[] specs) 
    throws IllegalArgumentException {
    if (specs != null) {
      FileSpecification spec;
      File file;
      String name;
      for (int i = 0; i < specs.length; i++) {
        spec = specs[i];
        if (spec == null) {
          throw new IllegalArgumentException("specs contains null elements");
        }
        file = spec.getFile();
        FileSpecification.validate(file);
        assert file != null;
        name = file.getName();
        assert name != null;
        this.specs.put(name, spec);
      }
    }
  }

  /**
   * Returns the {@link Publisher} used to upload this {@link FileRelease} to <a
   * href="http://sourceforge.net/">SourceForge</a>.  This method may return
   * <code>null</code>.
   *
   * @return     the {@link Publisher} used to upload this {@link FileRelease}
   *               to <a href="http://sourceforge.net/">SourceForge</a>, or
   *               <code>null</code> 
   */
  public Publisher getPublisher() {
    return this.publisher;
  }

  /**
   * Installs the supplied {@link Publisher} as the {@link Publisher} that will
   * be used to upload this {@link FileRelease} to <a
   * href="http://sourceforge.net/">SourceForge</a>.
   *
   * @param      publisher
   *               the new {@link Publisher}; may be <code>null</code>
   */
  public void setPublisher(final Publisher publisher) {
    this.publisher = publisher;
  }

  /**
   * Publishes this {@link FileRelease} to <a
   * href="http://sourceforge.net/">SourceForge</a>.  A non-<code>null</code>
   * {@link Publisher} must {@linkplain #setPublisher(Publisher) have been
   * previously installed}.
   *
   * @exception  PublishingException
   *               if an error occurs
   * @see        Publisher#publish(FileRelease)
   */
  public void publish() throws PublishingException {
    final Publisher publisher = this.getPublisher();
    if (publisher == null) {
      throw new PublishingException("Call setPublisher() first");
    }
    publisher.publish(this);
  }

  /**
   * Ensures that the supplied {@link File} meets the <a
   * href="#fileReqs">SourceForge-imposed filename requirements</a> for release
   * notes and changelog files.
   *
   * @param      file
   *               the {@link File} to be validated; may be <code>null</code>,
   *               although this will cause an {@link IllegalArgumentException}
   *               to be thrown
   * @exception  IllegalArgumentException
   *               if the supplied {@link File} does not meet all of the <a
   *               href="#fileReqs">SourceForge-imposed filename
   *               requirements</a> for release notes and changelog files
   */
  public static void validate(final File file)
    throws IllegalArgumentException {
    FileSpecification.validate(file);
    assert file != null;
    final long length = file.length();
    if (length < 20L || length > 256000L) {
      throw new IllegalArgumentException("file length must be between 20 and " +
                                         "256,000 bytes");
    }
  }

  /**
   * Returns a {@link String} representation of this {@link FileRelease}.  This
   * method never returns <code>null</code>.
   *
   * @return     a {@link String} representation of this {@link FileRelease};
   *               never <code>null</code>
   */
  public String toString() {
    final StringBuffer returnMe = new StringBuffer();
    final String superRep = super.toString();
    if (superRep != null) {
      returnMe.append(superRep);
    } else {
      final String name = this.getName();
      if (name != null) {
        returnMe.append(name);
      }
    }
    if (returnMe.length() > 0) {
      returnMe.append(" ");
    }
    final FileSpecification[] specs = this.getFileSpecifications();
    if (specs != null) {
      returnMe.append(String.valueOf(Arrays.asList(specs)));
    }
    return returnMe.toString();
  }

}
