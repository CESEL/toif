/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import com.kdmanalytics.toif.ui.common.FindingEntry;

/**
 * Provides column sorting.
 * 
 * Derived from ReportViewerComparator in design
 * 
 * @author Ken Duck
 *        
 */
public class FindingViewerComparator extends ViewerComparator {
  
  /** The column index. */
  private int columnIndex;
  
  /** The Constant DESCENDING. */
  private static final int DESCENDING = 1;
  
  private static final int ASCENDING = 0;
  
  /** The direction. */
  private int direction = DESCENDING;
  
  /**
   * Instantiates a new report viewer comparator.
   */
  public FindingViewerComparator() {
    this.columnIndex = 0;
    direction = ASCENDING;
  }
  
  /**
   * Gets the direction.
   * 
   * @return the direction
   */
  public int getDirection() {
    return direction == 1 ? SWT.DOWN : SWT.UP;
  }
  
  /**
   * Sets the column.
   * 
   * @param column
   *          the new column
   */
  public void setColumn(int column) {
    if (column == this.columnIndex) {
      // Same column as last sort; toggle the direction
      direction = 1 - direction;
    } else {
      // New column; do an ascending sort
      this.columnIndex = column;
      direction = DESCENDING;
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface. viewers.Viewer,
   * java.lang.Object, java.lang.Object)
   */
  @Override
  public int compare(Viewer viewer, Object e1, Object e2) {
    int result = 0;
    FindingEntry entry1 = (FindingEntry) e1;
    FindingEntry entry2 = (FindingEntry) e2;
    
    switch (columnIndex) {
      case 0: {
        IFile file1 = entry1.getFile();
        IFile file2 = entry2.getFile();
        result = file1.getName().split(" ")[0].compareTo(file2.getName().split(" ")[0]);
        break;
      }
      case 1: {
        int line1 = 0;
        try {
          line1 = Integer.parseInt(entry1.getLine());
        } catch (NumberFormatException e) {}
        int line2 = 0;
        try {
          line2 = Integer.parseInt(entry2.getLine());
        } catch (NumberFormatException e) {}
        
        if (line1 == line2) {
          // If the lines are the same, order by name as well
          IFile file1 = entry1.getFile();
          IFile file2 = entry2.getFile();
          result = file1.getName().split(" ")[0].compareTo(file2.getName().split(" ")[0]);
        } else {
          result = line1 - line2;
        }
        break;
      }
      case 2: {
        String tool1 = entry1.getTool();
        String tool2 = entry2.getTool();
        result = tool1.compareTo(tool2);
        break;
      }
      case 3: {
        String sfp1 = entry1.getSfp().replace("SFP-", "");
        String sfp2 = entry2.getSfp().replace("SFP-", "");
        int sfp1Int = 0;
        int sfp2Int = 0;
        
        try {
          sfp1Int = Integer.parseInt(sfp1);
        } catch (NumberFormatException nfe) {
          sfp1Int = -1;
        }
        
        try {
          sfp2Int = Integer.parseInt(sfp2);
        } catch (NumberFormatException nfe) {
          sfp2Int = -1;
        }
        
        result = sfp1Int - sfp2Int;
        break;
      }
      case 4: {
        String cwe1 = entry1.getCwe().replace("CWE-", "").trim();
        String cwe2 = entry2.getCwe().replace("CWE-", "").trim();
        
        int cwe1Int = 0;
        int cwe2Int = 0;
        try {
          cwe1Int = Integer.parseInt(cwe1);
        } catch (NumberFormatException nfe) {
          cwe1Int = -1;
        }
        
        try {
          cwe2Int = Integer.parseInt(cwe2);
        } catch (NumberFormatException nfe) {
          cwe2Int = -1;
        }
        result = cwe1Int - cwe2Int;
        
        break;
      }
      case 5: {
        int cwe1Int = entry1.getTrust();
        int cwe2Int = entry2.getTrust();
        result = cwe1Int - cwe2Int;
        break;
      }
      case 6: {
        String desc1 = entry1.getDescription();
        String desc2 = entry2.getDescription();
        result = desc1.compareTo(desc2);
        break;
      }
    }
    
    // If descending order, flip the direction
    if (direction == DESCENDING) {
      result = -result;
    }
    
    return result;
  }
}
