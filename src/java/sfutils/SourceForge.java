/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: SourceForge.java,v 1.3 2003/07/13 19:24:55 ljnelson Exp $
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

import java.io.IOException;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebResponse;

import org.xml.sax.SAXException;

/**
 * A utility class that provides access to small, core bits of information from
 * the <a href="http://sourceforge.net/">SourceForge</a> website.
 *
 * @author     <a href="mailto:ljnelson94@alumni.amherst.edu">Laird Nelson</a>
 * @version    $Revision: 1.3 $ $Date: 2003/07/13 19:24:55 $
 * @since      July 12, 2003
 */
public final class SourceForge {

  /**
   * The leading portion of the URL required to access a <a
   * href="http://sourceforge.net/">SourceForge</a> project.
   */
  private static final String PROJECT_PREFIX = 
    "http://sourceforge.net/projects/";

  /**
   * A {@link Map} of project IDs indexed by project short names.  This field is
   * thread-safe.
   */
  private static final Map PROJECT_ID_MAP = 
    Collections.synchronizedMap(new WeakHashMap());

  /**
   * Static initializer; ensures that the {@link
   * HttpUnitOptions#setExceptionsThrownOnScriptError(boolean)} method is called
   * with <code>false</code> as its parameter to work around the fact that <a
   * href="http://sourceforge.net/">SourceForge</a> pages have JavaScript errors
   * in them.
   */
  static {
    HttpUnitOptions.setExceptionsThrownOnScriptError(false);
  }

  /**
   * Throws an {@link UnsupportedOperationException} when invoked.
   *
   * @exception  UnsupportedOperationException
   *               when invoked
   */
  private SourceForge() throws UnsupportedOperationException {
    super();
    throw new UnsupportedOperationException();
  }

  /**
   * Returns the <a href="http://sourceforge.net/">SourceForge</a> project
   * identifier given a project short name.  This method may return
   * <code>null</code>.
   *
   * @param      projectShortName
   *               the name of the project whose identifier should be returned;
   *               may be <code>null</code> in which case <code>null</code> will
   *               be returned
   * @return     the identifier corresponding to the <a
   *               href="http://sourceforge.net/">SourceForge</a> project with
   *               the supplied short name, or <code>null</code>
   * @exception  SourceForgeException
   *               if an error occurs
   */
  public static final String getProjectID(final String projectShortName) 
    throws SourceForgeException {
    if (projectShortName == null) {
      return null;
    }
    String projectID = (String)PROJECT_ID_MAP.get(projectShortName);
    if (projectID != null) {
      return projectID;
    }
    try {
      final WebConversation webConversation = new WebConversation();
      final WebResponse summaryPage = 
        webConversation.getResponse(new GetMethodWebRequest(PROJECT_PREFIX +
                                                            projectShortName));
      assert summaryPage != null;
      final WebLink viewMembersLink = summaryPage.getLinkWith("[View Members]");
      if (viewMembersLink == null) {
        // Maybe it was an invalid project?
        final String text = summaryPage.getText();
        assert text != null;
        if (text.indexOf("Invalid Project") >= 0) {
          throw new NoSuchProjectException(projectShortName);
        }
        // OK, uh, UI change?
        throw new SourceForgeUIChangeException();
      }
      final String linkText = viewMembersLink.getURLString();
      assert linkText != null;
      final int groupIDIndex = linkText.indexOf("?group_id=");
      if (groupIDIndex < 0) {
        throw new SourceForgeUIChangeException();
      }
      projectID = linkText.substring(groupIDIndex + "?group_id=".length());
      PROJECT_ID_MAP.put(projectShortName, projectID);
      return projectID;
    } catch (final IOException kaboom) {
      throw new SourceForgeException(kaboom);
    } catch (final SAXException kaboom) {
      throw new SourceForgeException(kaboom);
    }
  }

}
