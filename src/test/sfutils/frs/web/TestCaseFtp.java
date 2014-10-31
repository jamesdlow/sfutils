/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: TestCaseFtp.java,v 1.1 2003/06/30 20:07:58 ljnelson Exp $
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

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.net.URL;
import java.net.URLConnection;

import junit.framework.TestCase;

public class TestCaseFtp extends TestCase {

  public TestCaseFtp(final String name) {
    super(name);
  }

  public void testFtpToSourceforge() throws Exception {
    OutputStream stream = null;
    PrintWriter writer = null;
    try {
      final URL url = 
        new URL("ftp://upload.sourceforge.net/incoming/sfutils-test-feel-free-to-delete-me-" +
                String.valueOf(System.currentTimeMillis()) +
                ".txt");
      final URLConnection connection = url.openConnection();
      assert connection != null;
      connection.setDoOutput(true);
      connection.connect();
      stream = connection.getOutputStream();
      assert stream != null;
      writer = 
        new PrintWriter(new BufferedWriter(new OutputStreamWriter(stream)));
      writer.println("This is a test.");
      writer.flush();
    } finally {
      if (writer != null) {
        writer.close();
      }
      try {
        if (stream != null) {
          stream.close();
        }
      } catch (final IOException ignore) {
        // ignore
      }
    }
  }
  
}
