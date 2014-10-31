/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: FileSpecification.java,v 1.10.4.1 2003/12/22 15:24:37 ljnelson Exp $
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
import java.io.Serializable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import sfutils.Project; // for Javadoc only

/**
 * A collection of <a href="http://sourceforge.net/">SourceForge</a>-specific
 * attributes to be associated with a {@link File} in a {@link FileRelease}.
 *
 * <p><a name="fileReqs"></a><a href="http://sourceforge.net/">SourceForge</a>
 * imposes a number of requirements on the name and contents of a {@link File}.
 * Specifically, a {@link File} must <strong>not</strong>:
 *
 * <ul>
 *
 * <li>be <code>null</code></li>
 *
 * <li>have a {@linkplain File#getName() filename} that contains either a space
 *     (" "), tilde ("~"), left parenthesis ("(") or right parenthesis
 *     (")")</li>
 *
 * </ul>
 *
 * Additionally, a {@link FileSpecification}'s {@linkplain #getFile() associated
 * <code>File</code>} must {@linkplain File#exists() exist} and {@linkplain
 * File#canRead() be readable}.</p>
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.10.4.1 $ $Date: 2003/12/22 15:24:37 $
 * @since      June 19, 2003
 * @see        Project
 * @see        Package
 * @see        FileRelease
 * @see        FileSpecification 
 * @see        <a
 *               href="http://sourceforge.net/docman/display_doc.php?docid=6445&group_id=1">Guide
 *               to the SourceForge.net File Release System (FRS)</a> 
 */
public class FileSpecification implements Serializable {

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a <a
   * href="http://www.debian.org/doc/FAQ/ch-pkg_basics.en.html#s-package">Debian
   * package</a>.
   */
  public static final int DEBIAN_PACKAGE_FILE = 1000;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a binary <a href="http://www.rpm.org/">Red Hat package</a>.
   */
  public static final int BINARY_REDHAT_PACKAGE_FILE = 2000;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a <a
   * href="http://www.gnu.org/manual/gzip-1.2.4/html_mono/gzip.html"><code>zip</code>
   * archive</a> containing a binary distribution.
   */
  public static final int BINARY_ZIP_FILE = 3000;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a <a
   * href="http://sources.redhat.com/bzip2/"><code>bz2</code></a> archive
   * containing a binary distribution.
   */
  public static final int BINARY_BZIP2_FILE = 3001;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a <a
   * href="http://www.gnu.org/manual/gzip-1.2.4/html_mono/gzip.html"><code>gzip</code></a>
   * archive containing a binary distribution.
   */
  public static final int BINARY_GZIP_FILE = 3002;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a <a
   * href="http://www.gnu.org/manual/gzip-1.2.4/html_mono/gzip.html"><code>zip</code>
   * archive</a> containing a source distribution.
   */
  public static final int SOURCE_ZIP_FILE = 5000;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a <a
   * href="http://sources.redhat.com/bzip2/"><code>bz2</code></a> archive
   * containing a source distribution.
   */
  public static final int SOURCE_BZ2_FILE = 5001;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a <a
   * href="http://www.gnu.org/manual/gzip-1.2.4/html_mono/gzip.html"><code>gzip</code></a>
   * archive containing a source distribution.
   */
  public static final int SOURCE_GZIP_FILE = 5002;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a <a href="http://www.rpm.org">Red Hat package</a> containing a
   * source distribution.
   */
  public static final int SOURCE_REDHAT_PACKAGE_FILE = 5100;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is an otherwise unspecified source distribution or file.
   *
   * @see        #OTHER_FILE
   */
  public static final int OTHER_SOURCE_FILE = 5900;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a <a href="http://www.jpeg.org/jpeg_about.html">JPEG image</a>.
   */
  public static final int JPEG_IMAGE_FILE = 8000;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a text file.
   */
  public static final int TEXT_FILE = 8001;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is an <a href="http://www.w3.org/MarkUp/">HTML</a> page.
   */
  public static final int HTML_FILE = 8002;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a <a
   * href="http://www.adobe.com/products/acrobat/adobepdf.html">PDF</a> file.
   */
  public static final int PDF_FILE = 8003;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is an otherwise unspecified binary file.
   *
   * @see        #OTHER_SOURCE_FILE
   */
  public static final int OTHER_FILE = 9999;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a <a href="http://www.stuffit.com/">StuffIt</a> archive.
   */
  public static final int STUFFIT_FILE = 3003;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a <a
   * href="http://www.everything2.com/index.pl%3Fnode=nodeball">nodeball</a>.  
   */
  public static final int NODEBALL_FILE = 3004;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a DOS executable file.
   */
  public static final int DOS_EXE_FILE = 2500;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a 16-bit Windows executable file.
   */
  public static final int WINDOWS_16_BIT_EXE_FILE = 2501;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a 32-bit Windows executable file.
   */
  public static final int WINDOWS_32_BIT_EXE_FILE = 2502;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is an OS/2 executable file.
   */
  public static final int OS2_EXE_FILE = 2600;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a <a
   * href="http://developer.apple.com/documentation/UserExperience/Conceptual/AquaHIGuidelines/AHIGDirectories/chapter_15_section_2.html#//apple_ref/doc/uid/20000971/TPXREF103">Macintosh
   * Disk Image</a> file.
   */
  public static final int DMG_FILE = 3005;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a <a
   * href="http://java.sun.com/j2se/1.4.1/docs/guide/jar/jarGuide.html">jar
   * file</a>.
   */
  public static final int JAR_FILE = 2601;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a <a
   * href="http://www.gnu.org/manual/diffutils-2.8.1/html_mono/diff.html#Merging%20with%20patch">patch</a>
   * or <a
   * href="http://www.gnu.org/manual/diffutils-2.8.1/html_mono/diff.html#Hunks">diff
   * hunk</a>.
   */
  public static final int SOURCE_PATCH_OR_DIFF_FILE = 5901;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a <a
   * href="http://www.palmos.com/dev/support/docs/palmos/FilesAndDatabases.html#998668">Palm
   * Resource Database</a>.
   */
  public static final int PALM_RESOURCE_DATABASE_FILE = 2700;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is an <a href="http://www.filefound.com/answers.html#1">ISO 
   * file</a>.
   */
  public static final int ISO_FILE = 3006;
  
  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is an <a
   * href="http://www.gnu.org/manual/gzip-1.2.4/html_mono/gzip.html">archive
   * file generated by the <code>compress</code> program that can be read and
   * uncompressed with <code>gunzip</code></a>.  
   */
  public static final int SOURCE_Z_FILE = 5003;

  /**
   * A constant for use by the {@link #setFileType(int)} method; indicates that
   * the file is a <a
   * href="http://www.lazerware.com/formats/macbinary.html">MacBinary</a> file.
   */
  public static final int MACBINARY_FILE = 2650;

  /*
   * Processor types
   */
  
  /**
   * A constant for use by the {@link #setProcessorType(int)} method; indicates
   * that the target hardware is a Pentium-class processor.
   */
  public static final int I386_PROCESSOR = 1000;

  /**
   * A constant for use by the {@link #setProcessorType(int)} method; indicates
   * that the target hardware is a Itanium-class processor.
   */
  public static final int IA64_PROCESSOR = 6000;

  /**
   * A constant for use by the {@link #setProcessorType(int)} method; indicates
   * that the target hardware is an Alpha processor.
   */
  public static final int ALPHA_PROCESSOR = 7000;

  /**
   * A constant for use by the {@link #setProcessorType(int)} method; indicates
   * that the target hardware is unimportant.
   *
   * @see        #PLATFORM_INDEPENDENT_PROCESSOR
   */
  public static final int ANY_PROCESSOR = 8000;

  /**
   * A constant for use by the {@link #setProcessorType(int)} method; indicates
   * that the target hardware is a PowerPC processor.
   */
  public static final int PPC_PROCESSOR = 2000;

  /**
   * A constant for use by the {@link #setProcessorType(int)} method; indicates
   * that the target hardware is a MIPS processor.
   */
  public static final int MIPS_PROCESSOR = 3000;

  /**
   * A constant for use by the {@link #setProcessorType(int)} method; indicates
   * that the target hardware is a SPARC processor.
   */
  public static final int SPARC_PROCESSOR = 4000;

  /**
   * A constant for use by the {@link #setProcessorType(int)} method; indicates
   * that the target hardware is an UltraSPARC processor.
   */
  public static final int ULTRASPARC_PROCESSOR = 5000;

  /**
   * A constant for use by the {@link #setProcessorType(int)} method; indicates
   * that the target hardware is unimportant.
   *
   * @see        #ANY_PROCESSOR
   */
  public static final int PLATFORM_INDEPENDENT_PROCESSOR = 8500;

  /**
   * A {@link Map} that indexes file type constants by filename suffix.  For
   * example, a "<code>.deb</code>" suffix is mapped to the {@link
   * #DEBIAN_PACKAGE_FILE} constant.
   */
  protected static final Map SUFFIX_TO_FILE_TYPE_MAP;

  /**
   * Static initializer; initializes the {@link #SUFFIX_TO_FILE_TYPE_MAP} field.
   */
  static {
    final Map map = new HashMap(31, 1F);
    map.put(".deb", new Integer(DEBIAN_PACKAGE_FILE));
    map.put(".rpm", new Integer(BINARY_REDHAT_PACKAGE_FILE));
    map.put(".zip", new Integer(BINARY_ZIP_FILE));
    map.put(".bz2", new Integer(BINARY_BZIP2_FILE));
    map.put(".gz", new Integer(BINARY_GZIP_FILE));
    map.put(".tgz", new Integer(BINARY_GZIP_FILE));
    map.put(".jpg", new Integer(JPEG_IMAGE_FILE));
    map.put(".jpeg", new Integer(JPEG_IMAGE_FILE));
    map.put(".txt", new Integer(TEXT_FILE));
    map.put(".text", new Integer(TEXT_FILE));
    map.put(".htm", new Integer(HTML_FILE));
    map.put(".html", new Integer(HTML_FILE));
    map.put(".pdf", new Integer(PDF_FILE));
    map.put(".sit", new Integer(STUFFIT_FILE));
    map.put(".nbz", new Integer(NODEBALL_FILE));
    map.put(".nbz", new Integer(NODEBALL_FILE));
    map.put(".exe", new Integer(WINDOWS_32_BIT_EXE_FILE));
    map.put(".dmg", new Integer(DMG_FILE));
    map.put(".jar", new Integer(JAR_FILE));
    map.put(".diff", new Integer(SOURCE_PATCH_OR_DIFF_FILE));
    map.put(".patch", new Integer(SOURCE_PATCH_OR_DIFF_FILE));
    map.put(".prc", new Integer(PALM_RESOURCE_DATABASE_FILE));
    map.put(".iso", new Integer(ISO_FILE));
    map.put(".z", new Integer(SOURCE_Z_FILE));
    map.put(".bin", new Integer(MACBINARY_FILE));
    SUFFIX_TO_FILE_TYPE_MAP = 
      Collections.synchronizedMap(Collections.unmodifiableMap(map));
  }

  /**
   * The type of processor this {@link FileSpecification}'s enclosing {@link
   * FileRelease} is destined for.  Processor type constants are defined
   * elsewhere in this class; see, for example, the {@link #ANY_PROCESSOR}
   * field.
   *
   * @see        #ANY_PROCESSOR 
   */
  private int processorType;

  /**
   * The {@link Date} on which this {@link FileSpecification} is to be
   * considered released.
   *
   * <p>I am not sure why <a href="http://sourceforge.net/">SourceForge</a>
   * makes this an attribute of a file specification as well as of its enclosing
   * file release, since a file specification cannot be released independent of
   * a file release.</p>
   *
   * <p>This field may be <code>null</code>, in which case the current {@link
   * Date} should be used instead.</p>
   */
  private Date releaseDate;

  /**
   * The {@link File} that this {@link FileSpecification} adds additional
   * attributes to.  This field may be <code>null</code>.
   */
  private File file;

  /**
   * The type of file this {@link FileSpecification} represents.  File type
   * constants are defined elsewhere in this class; see, for example, the {@link
   * #BINARY_BZIP2_FILE} field.
   *
   * @see        #BINARY_BZIP2_FILE
   */
  private int fileType;

  /**
   * Creates a new {@link FileSpecification}.  The {@linkplain #getFileType()
   * file type} is initialized to {@link #OTHER_FILE}, the {@linkplain
   * #getProcessorType() processor type} is initialized to {@link
   * #ANY_PROCESSOR}, and the {@linkplain #getReleaseDate() release date} is
   * initialized to a new {@link Date}.
   *
   * <p>The resulting {@link FileSpecification} object will be in an illegal
   * state until its {@link #setFile(File)} method is called with a valid
   * argument.</p>
   *
   * @see        #setFile(File) 
   */
  public FileSpecification() {
    super();
    this.setFileType(OTHER_FILE);
    this.setProcessorType(ANY_PROCESSOR);
    this.setReleaseDate(new Date());
  }

  /**
   * Creates a new {@link FileSpecification}.  This constructor calls the {@link
   * #FileSpecification()} constructor and then calls the {@link #setFile(File)}
   * method with the supplied {@link File}.
   *
   * @param      file
   *               the {@link File} this {@link FileSpecification} represents;
   *               must not be <code>null</code> and must meet the other
   *               <a href="#fileReqs">SourceForge filename requirements</a>
   * @exception  IllegalArgumentException
   *               if the supplied {@link File} does not meet the <a
   *               href="#fileReqs">SourceForge filename requirements</a>
   */
  public FileSpecification(final File file) 
    throws IllegalArgumentException {
    this();
    this.setFile(file);
  }

  /**
   * Returns the type of processor this {@link FileSpecification} targets.  The
   * return value will be one of the processor type constants defined elsewhere
   * in this class.  See, for example, the {@link #ANY_PROCESSOR} field.
   *
   * @return     the type of processor this {@link FileSpecification} targets
   * @see        #ANY_PROCESSOR
   */
  public int getProcessorType() {
    return this.processorType;
  }

  /**
   * Sets the type of the processor this {@link FileSpecification}'s enclosing
   * {@link FileRelease} is destined for.  The supplied value must be one of
   * {@link #I386_PROCESSOR}, {@link #IA64_PROCESSOR}, {@link #ALPHA_PROCESSOR},
   * {@link #ANY_PROCESSOR}, {@link #PPC_PROCESSOR}, {@link #MIPS_PROCESSOR},
   * {@link #SPARC_PROCESSOR}, {@link #ULTRASPARC_PROCESSOR} or {@link
   * #PLATFORM_INDEPENDENT_PROCESSOR}.
   *
   * @param      processorType
   *               the type of processor this {@link FileSpecification}'s
   *               enclosing {@link FileRelease} is destined for; must be one of
   *               {@link #I386_PROCESSOR}, {@link #IA64_PROCESSOR}, {@link
   *               #ALPHA_PROCESSOR}, {@link #ANY_PROCESSOR}, {@link
   *               #PPC_PROCESSOR}, {@link #MIPS_PROCESSOR}, {@link
   *               #SPARC_PROCESSOR}, {@link #ULTRASPARC_PROCESSOR} or {@link
   *               #PLATFORM_INDEPENDENT_PROCESSOR} 
   */
  public void setProcessorType(int processorType) {
    switch (processorType) {
    case ALPHA_PROCESSOR:
    case ANY_PROCESSOR:
    case I386_PROCESSOR:
    case IA64_PROCESSOR:
    case MIPS_PROCESSOR:
    case PLATFORM_INDEPENDENT_PROCESSOR:
    case PPC_PROCESSOR:
    case SPARC_PROCESSOR:
    case ULTRASPARC_PROCESSOR:
      break;
    default:
      processorType = ANY_PROCESSOR;
    }
    this.processorType = processorType;
  }

  /**
   * Converts the supplied {@link String} into a suitable processor type
   * constant.  The supplied {@link String} must be one of &quot;{@link
   * #I386_PROCESSOR}&quot;, &quot;{@link #IA64_PROCESSOR}&quot;, &quot;{@link
   * #ALPHA_PROCESSOR}&quot;, &quot;{@link #ANY_PROCESSOR}&quot;, &quot;{@link
   * #PPC_PROCESSOR}&quot;, &quot;{@link #MIPS_PROCESSOR}&quot;, &quot;{@link
   * #SPARC_PROCESSOR}&quot;, &quot;{@link #ULTRASPARC_PROCESSOR}&quot; or
   * &quot;{@link #PLATFORM_INDEPENDENT_PROCESSOR}&quot;.
   *
   * @param      type
   *               a {@link String} representation of the {@linkplain
   *               #setProcessorType(int) processor type to set}; must be one of
   *               &quot;{@link #I386_PROCESSOR}&quot;, &quot;{@link
   *               #IA64_PROCESSOR}&quot;, &quot;{@link #ALPHA_PROCESSOR}&quot;,
   *               &quot;{@link #ANY_PROCESSOR}&quot;, &quot;{@link
   *               #PPC_PROCESSOR}&quot;, &quot;{@link #MIPS_PROCESSOR}&quot;,
   *               &quot;{@link #SPARC_PROCESSOR}&quot;, &quot;{@link
   *               #ULTRASPARC_PROCESSOR}&quot; or &quot;{@link
   *               #PLATFORM_INDEPENDENT_PROCESSOR}&quot;
   * @exception  IllegalArgumentException
   *               if the supplied {@link String} is not one of &quot;{@link
   *               #I386_PROCESSOR}&quot;, &quot;{@link #IA64_PROCESSOR}&quot;,
   *               &quot;{@link #ALPHA_PROCESSOR}&quot;, &quot;{@link
   *               #ANY_PROCESSOR}&quot;, &quot;{@link #PPC_PROCESSOR}&quot;,
   *               &quot;{@link #MIPS_PROCESSOR}&quot;, &quot;{@link
   *               #SPARC_PROCESSOR}&quot;, &quot;{@link
   *               #ULTRASPARC_PROCESSOR}&quot; or &quot;{@link
   *               #PLATFORM_INDEPENDENT_PROCESSOR}&quot; 
   */
  public void setProcessorTypeString(String type) 
    throws IllegalArgumentException {
    this.setTypeString(type, false);
  }

  /**
   * Returns the {@link File} that this {@link FileSpecification} represents.
   * This method never returns <code>null</code> and will throw an {@link
   * IllegalStateException} if the {@link #setFile(File)} method has not yet
   * been called with a non-<code>null</code> argument.
   *
   * @return     the {@link File} that this {@link FileSpecification} 
   *               represents; never <code>null</code>
   * @exception  IllegalStateException
   *               if the {@link File} associated with this {@link
   *               FileSpecification} is <code>null</code> for any reason
   */
  public File getFile()
    throws IllegalStateException {
    if (this.file == null) {
      throw new IllegalStateException("this.file == null");
    }
    return this.file;
  }

  /**
   * Sets the {@link File} that this {@link FileSpecification} will represent.
   * The supplied {@link File} must conform to the <a
   * href="#fileReqs">SourceForge-imposed filename restrictions</a>.
   *
   * <p>If possible, the {@linkplain #setFileType(int) file type} will be set
   * appropriately from the supplied {@link File}'s suffix.</p>
   *
   * @param      file
   *               the {@link File} that this {@link FileSpecification} will
   *               represent; must be non-<code>null</code>, must {@linkplain
   *               File#canRead() exist and be readable}, must be between 20 and
   *               256,000 bytes in {@linkplain File#length() length}, and must
   *               not contain a space, tilde or parenthesis in its {@linkplain
   *               File#getName() name}
   * @exception  IllegalArgumentException
   *               if the supplied {@link File} does not satisfy the <a
   *               href="#fileReqs">requirements above</a> 
   */
  public void setFile(final File file)
    throws IllegalArgumentException {
    validate(file);
    assert file != null;
    final int fileType = computeFileType(file);
    if (isValidFileType(fileType)) {
      this.setFileType(fileType);
    } else {
      this.setFileType(OTHER_FILE);
    }
    this.file = file;
  }

  /**
   * Ensures that the supplied {@link File} meets the <a
   * href="#fileReqs">SourceForge-imposed filename requirements</a>.
   *
   * @param      file
   *               the {@link File} to be validated; may be <code>null</code>,
   *               although this will cause an {@link IllegalArgumentException}
   *               to be thrown
   * @exception  IllegalArgumentException
   *               if the supplied {@link File} does not meet all of the <a
   *               href="#fileReqs">SourceForge-imposed filename
   *               requirements</a> 
   */
  public static void validate(final File file)
    throws IllegalArgumentException {
    if (file == null) {
      throw new IllegalArgumentException("file is null");
    }
    if (!file.canRead()) {
      throw new IllegalArgumentException("file must exist and must be " +
                                         "readable");
    }
    final String name = file.getName();
    assert name != null;
    if (name.length() <= 0) {
      throw new IllegalArgumentException("file name must be at least one " +
                                         "character long");
    }
    if (name.indexOf(" ") >= 0 ||
        name.indexOf(")") >= 0 ||
        name.indexOf("(") >= 0 ||
        name.indexOf("~") >= 0) {
      throw new IllegalArgumentException("file name cannot contain a space, " +
                                         "tilde or parenthesis");
    }
  }

  /**
   * Returns a {@link File} type for the supplied {@link File} based on its
   * suffix.  If a suitable {@link File} type cannot be determined, then {@link
   * #OTHER_FILE} is returned.
   *
   * @param      file
   *               the {@link File} for which a {@link File} type should be
   *               returned; must not be <code>null</code> and must meet the <a
   *               href="#fileReqs">SourceForge-imposed filename
   *               requirements</a>
   * @return     a suitable {@link File} type for the supplied {@link File}
   * @exception  IllegalArgumentException
   *               if the supplied {@link File} does not meet all of the <a
   *               href="#fileReqs">SourceForge-imposed filename
   *               requirements</a> 
   */
  public static int computeFileType(final File file)
    throws IllegalArgumentException {
    validate(file);
    assert file != null;
    final String name = file.getName();
    assert name != null;
    assert name.length() > 0;
    final int lastPeriodIndex = name.lastIndexOf('.');
    if (lastPeriodIndex >= 0 && lastPeriodIndex != name.length() - 1) {
      String suffix = name.substring(lastPeriodIndex);
      assert suffix != null;
      suffix = suffix.toLowerCase();
      assert suffix != null;
      final Integer type = (Integer)SUFFIX_TO_FILE_TYPE_MAP.get(suffix);
      if (type == null) {
        return OTHER_FILE;
      }
      final int fileType = type.intValue();
      if (isValidFileType(fileType)) {
        return fileType;
      }
    }
    return OTHER_FILE;
  }

  /**
   * Returns <code>true</code> if and only if the supplied <code>int</code> is
   * one of the {@link File} type constants defined elsewhere in this class.
   *
   * @param      type
   *               the type to be tested
   * @return     <code>true</code> if and only if the supplied <code>int</code> 
   *               is one of the {@link File} type constants defined elsewhere
   *               in this class 
   */
  protected static boolean isValidFileType(final int type) {
    switch (type) {
    case BINARY_BZIP2_FILE:
    case BINARY_GZIP_FILE:
    case BINARY_REDHAT_PACKAGE_FILE:
    case BINARY_ZIP_FILE:
    case DEBIAN_PACKAGE_FILE:
    case DMG_FILE:
    case DOS_EXE_FILE:
    case HTML_FILE:
    case ISO_FILE:
    case JAR_FILE:
    case JPEG_IMAGE_FILE:
    case MACBINARY_FILE:
    case NODEBALL_FILE:
    case OS2_EXE_FILE:
    case OTHER_FILE:
    case OTHER_SOURCE_FILE:
    case PALM_RESOURCE_DATABASE_FILE:
    case PDF_FILE:
    case SOURCE_BZ2_FILE:
    case SOURCE_GZIP_FILE:
    case SOURCE_PATCH_OR_DIFF_FILE:
    case SOURCE_REDHAT_PACKAGE_FILE:
    case SOURCE_ZIP_FILE:
    case SOURCE_Z_FILE:
    case STUFFIT_FILE:
    case TEXT_FILE:
    case WINDOWS_16_BIT_EXE_FILE:
    case WINDOWS_32_BIT_EXE_FILE:
      return true;
    default:
      return false;
    }
  }

  /**
   * Returns the type of the {@link File} that this {@link FileSpecification}
   * represents.
   *
   * @return     one of the {@link File} type constants defined elsewhere in
   *             this class; e.g. {@link #OTHER_FILE}
   * @see        #OTHER_FILE
   */
  public int getFileType() {
    return this.fileType;
  }

  /**
   * Sets the type of {@link File} that this {@link FileSpecification}
   * represents.  The supplied {@link File} type must be one of the {@link File}
   * types declared elsewhere in this class.  For example, see the {@link
   * #OTHER_FILE} field.
   *
   * @param      type
   *               the new {@link File} type; must be one of the {@link File}
   *               types declared elsewhere in this class; e.g. {@link
   *               #OTHER_FILE}
   * @see        #OTHER_FILE 
   */
  public void setFileType(final int type) {
    if (isValidFileType(type)) {
      this.fileType = type;
    } else {
      this.fileType = OTHER_FILE;
    }
  }

  /**
   * Sets the type of {@link File} that this {@link FileSpecification}
   * represents.  The supplied {@link String} must be equal to the name of a
   * {@link File} type constant defined elsewhere in this class.  See, for
   * example, the {@link #OTHER_FILE} constant.
   *
   * @param      type
   *               the new {@link File} type; must not be <code>null</code> and
   *               must be equal to the name of a {@link File} type constant
   *               defined elsewhere in this class 
   * @exception  IllegalArgumentException
   *               if the supplied {@link File} type {@link String} was not
   *               valid
   */
  public void setFileTypeString(final String type) 
    throws IllegalArgumentException {
    this.setTypeString(type, true);
  }
  
  /**
   * A convenience method called by the {@link #setFileTypeString(String)} and
   * {@link #setProcessorTypeString(String)} methods that translates its {@link
   * String} argument into the proper constant value.  For example, an argument
   * of &quot;<code>OTHER_FILE</code>&quot; would be translated into the value
   * of the {@link #OTHER_FILE} field, and the {@link #setFileType(int)} method
   * would be called with that value as an argument.
   *
   * @param      type
   *               the {@link String} value that hopefully corresponds to a
   *               valid "type" constant; must not be <code>null</code>
   * @param      fileType
   *               if <code>true</code>, then it is understood that the supplied
   *               {@link String} will be treated as a {@link File} type;
   *               otherwise it will be treated as a processor type
   * @exception  IllegalArgumentException
   *               if <code>type</code> is <code>null</code> or is an unknown
   *               type
   */
  private void setTypeString(final String type, final boolean fileType) 
    throws IllegalArgumentException {

    final String kind;
    final String fieldNameSuffix;
    if (fileType) {
      kind = "File";
      fieldNameSuffix = "_FILE";
    } else {
      kind = "Processor";
      fieldNameSuffix = "_PROCESSOR";
    }

    String workingType = type;
    if (workingType == null) {
      throw new IllegalArgumentException(kind + " type cannot be null");
    }
    workingType = workingType.trim();
    if (workingType == null) {
      throw new InternalError("String.trim() returned null");
    }
    workingType = workingType.replace(' ', '_');
    if (workingType == null) {
      throw new InternalError("String.replace() returned null");
    }
    if (!workingType.endsWith(fieldNameSuffix)) {
      workingType = workingType + fieldNameSuffix;
    }
    workingType = workingType.toUpperCase();
    
    // Since there are so many types, let's just use reflection to see if
    // there is a constant that is named the same thing.
    final Class myClass = this.getClass();
    if (myClass == null) {
      throw new InternalError("Object.getClass() returned null");
    }
    Field f = null;
    try {
      f = myClass.getField(workingType);
    } catch (final NoSuchFieldException kaboom) {
      throw new IllegalArgumentException("Unknown " + kind + " type: " + type);
    }
    if (f == null) {
      throw new IllegalArgumentException("Unknown " + kind + " type: " + type);
    }
    final int modifiers = f.getModifiers();
    if (!Modifier.isStatic(modifiers) ||
        !Modifier.isFinal(modifiers) ||
        !Integer.TYPE.equals(f.getType())) {
      throw new IllegalArgumentException("Unknown " + kind + " type: " + type);
    }
    try {
      if (fileType) {
        this.setFileType(f.getInt(this));
      } else {
        this.setProcessorType(f.getInt(this));
      }
    } catch (final IllegalAccessException wontHappen) {
      throw new IllegalArgumentException("Unknown " + kind + " type: " + type);
    }
  }

  /**
   * Returns the {@link Date} on which the {@linkplain #getFile()
   * <code>File</code> associated with this <code>FileSpecification</code>} is
   * to be released.  This method may return <code>null</code>, in which case
   * the current {@link Date} should be used instead.
   *
   * @return     the {@link Date} on which the {@linkplain #getFile()
   *               <code>File</code> associated with this
   *               <code>FileSpecification</code>} is to be released, or
   *               <code>null</code> if the current {@link Date} should be used
   *               instead
   */
  public Date getReleaseDate() {
    return this.releaseDate;
  }

  /**
   * Sets the {@link Date} on which the {@linkplain #getFile()
   * <code>File</code> associated with this <code>FileSpecification</code>} is
   * to be released.
   *
   * @param      date
   *               the new {@link Date} on which the {@linkplain #getFile()
   *               <code>File</code> associated with this
   *               <code>FileSpecification</code>} is to be released; may be
   *               <code>null</code>, in which case the current {@link Date}
   *               will be used instead
   */
  public void setReleaseDate(final Date date) {
    if (date == null) {
      this.releaseDate = new Date();
    } else {
      this.releaseDate = date;
    }
  }

  /**
   * Returns a hashcode for this {@link FileSpecification} based on its
   * {@linkplain #getFile() associated <code>File</code>}.
   *
   * @return     a hashcode for this {@link FileSpecification}
   */
  public int hashCode() {
    File file = null;
    try {
      file = this.getFile();
    } catch (final IllegalStateException ignore) {
      file = null;
    }
    if (file == null) {
      return 0;
    }
    return file.hashCode();
  }

  /**
   * Tests the supplied {@link Object} to see if it is equal to this {@link
   * FileSpecification}.  An {@link Object} is equal to this {@link
   * FileSpecification} if it is an instance of the {@link FileSpecification}
   * class and its {@linkplain #getFile() associated <code>File</code>} is equal
   * to this {@link FileSpecification}'s {@linkplain #getFile() associated
   * <code>File</code>}.
   *
   * @param      anObject
   *               the {@link Object} to test; may be <code>null</code>
   * @return     <code>true</code> if and only if the supplied {@link Object} is
   *               equal to this {@link FileSpecification}
   */
  public boolean equals(final Object anObject) {
    if (anObject == this) {
      return true;
    } else if (anObject instanceof FileSpecification) {
      final FileSpecification other = (FileSpecification)anObject;
      File file = null;
      try {
        file = this.getFile();
      } catch (final IllegalStateException ignore) {
        file = null;
      }
      File otherFile = null;
      try {
        otherFile = this.getFile();
      } catch (final IllegalStateException ignore) {
        otherFile = null;
      }
      if (file == null) {
        return otherFile == null;
      }
      return file.equals(otherFile);
    } else {
      return false;
    }
  }

  /**
   * Returns a {@link String} representation of this {@link FileSpecification}.
   * This method never returns <code>null</code>.
   * 
   * @return     a {@link String} representation of this {@link
   *               FileSpecification}; never <code>null</code>
   */
  public String toString() {
    File file = null;
    try {
      file = this.getFile();
    } catch (final IllegalStateException ignore) {
      file = null;
    }
    if (file == null) {
      return "Uninitialized FileSpecification";
    }
    return file.getName();
  }

}
