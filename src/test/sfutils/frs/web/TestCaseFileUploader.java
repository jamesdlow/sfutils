/* -*- mode: JDE; c-basic-offset: 2; indent-tabs-mode: nil -*-
 *
 * $Id: TestCaseFileUploader.java,v 1.2 2003/06/30 20:53:40 ljnelson Exp $
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

import java.io.File;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;

import sfutils.frs.web.HttpUnitPublisher.FileUploader;

import junit.framework.TestCase;

public class TestCaseFileUploader extends TestCase {

  private File file1;
  private File file2;
  private File file3;

  public TestCaseFileUploader(final String name) {
    super(name);
  }

  public void setUp() throws Exception {
    this.file1 = File.createTempFile("TEST_", ".txt");
    this.file1.deleteOnExit();
    assertTrue(this.file1.canWrite());
    this.fillWithCrap(this.file1);

    this.file2 = File.createTempFile("TEST_", ".txt");
    this.file2.deleteOnExit();
    assertTrue(this.file2.canWrite());
    this.fillWithCrap(this.file2);

    this.file3 = File.createTempFile("TEST_", ".txt");
    this.file3.deleteOnExit();
    assertTrue(this.file3.canWrite());
    this.fillWithCrap(this.file3);
    
  }

  public void fillWithCrap(final File file) throws Exception {
    assertNotNull(file);
    final PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
    try {
      writer.println("Junk line 1");
      writer.println("Junk line 2");
      writer.println("Junk line 3");
    } finally {
      writer.close();
    }
  }

  public final void testUpload() throws Exception {
    final Collection errors = Collections.synchronizedList(new ArrayList());
    final HttpUnitPublisher.FileUploader uploader1 = 
      new HttpUnitPublisher.FileUploader(this.file1, errors);
    final HttpUnitPublisher.FileUploader uploader2 = 
      new HttpUnitPublisher.FileUploader(this.file2, errors);
    final HttpUnitPublisher.FileUploader uploader3 = 
      new HttpUnitPublisher.FileUploader(this.file3, errors);
    
    uploader1.start();
    uploader1.join();
    
    uploader2.start();
    uploader2.join();
    
    uploader3.start();
    uploader3.join();
    
    assertEquals(0, errors.size());
  }
  
}
