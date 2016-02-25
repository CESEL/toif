/*******************************************************************************
 * Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.ui.views;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;

import com.kdmanalytics.toif.ui.Activator;
import com.kdmanalytics.toif.ui.common.FindingEntry;

/**
 * 
 * @author Ken Duck
 *        
 */
class FindingStyledLabelProvider extends StyledCellLabelProvider {
  
  /** The green. */
  private static final Color GREEN = new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(220, 255, 220));
  
  private static final Color WHITE = new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(255, 255, 255));
  
  /** The red. */
  private static final Color RED = new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(255, 220, 220));
  
  /** The foreground green. */
  private static final Color FOREGROUND_GREEN = new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(0, 80, 0));
  
  private static final Color FOREGROUND_BLACK = new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(80, 80, 80));
  
  /** The foreground red. */
  private static final Color FOREGROUND_RED = new Color(PlatformUI.getWorkbench().getDisplay(), new RGB(100, 0, 0));
  
  /** The Constant TICK_KEY. */
  private static final String TICK_KEY = "tick";
  
  /** The Constant CROSS_KEY. */
  private static final String CROSS_KEY = "cross";
  
  /** The Constant PAGE_WHITE_STACK_KEY. */
  private static final String PAGE_WHITE_STACK_KEY = "page_stack";
  
  /** The Constant PAGE_KEY. */
  private static final String PAGE_KEY = "page";
  
  /** The Constant WRENCH_KEY. */
  private static final String WRENCH_KEY = "wrench";
  
  /**
   * 
   */
  public FindingStyledLabelProvider() {
    loadImagesIntoRegistry();
  }
  
  /**
   * Load images.
   */
  private void loadImagesIntoRegistry() {
    final ImageRegistry imgReg = Activator.getDefault().getImageRegistry();
    if (imgReg.get(TICK_KEY) == null) {
      final URL url = this.getClass().getResource("/icons/tick.png");
      imgReg.put(TICK_KEY, ImageDescriptor.createFromURL(url));
    }
    if (imgReg.get(CROSS_KEY) == null) {
      final URL url = this.getClass().getResource("/icons/cross.png");
      imgReg.put(CROSS_KEY, ImageDescriptor.createFromURL(url));
    }
    if (imgReg.get(PAGE_WHITE_STACK_KEY) == null) {
      final URL url = this.getClass().getResource("/icons/page_white_stack.png");
      imgReg.put(PAGE_WHITE_STACK_KEY, ImageDescriptor.createFromURL(url));
    }
    if (imgReg.get(PAGE_KEY) == null) {
      final URL url = this.getClass().getResource("/icons/page.png");
      imgReg.put(PAGE_KEY, ImageDescriptor.createFromURL(url));
    }
    if (imgReg.get(WRENCH_KEY) == null) {
      final URL url = this.getClass().getResource("/icons/wrench.png");
      imgReg.put(WRENCH_KEY, ImageDescriptor.createFromURL(url));
    }
  }
  
  /**
   * Get the entry text for the specified column
   * 
   * @param entry
   * @param colIndex
   * @return
   */
  private String getColumnText(FindingEntry entry, int colIndex) {
    switch (colIndex) {
      case 0: {
        return entry.getFileName();
      }
      case 1: {
        return entry.getLine();
      }
      case 2: {
        return entry.getTool();
      }
      case 3: {
        return entry.getSfp();
      }
      case 4: {
        return entry.getCwe();
      }
      case 5: {
        return Integer.toString(entry.getTrust());
      }
      case 6: {
        return entry.getDescription();
      }
    }
    return null;
  }
  
  /**
   * Gets the image.
   * 
   * @param element
   *          the element
   * @return the image
   */
  public Image getImage(FindingEntry entry, int colIndex) {
    final ImageRegistry imgReg = Activator.getDefault().getImageRegistry();
    
    switch (colIndex) {
      case 0: {
        return imgReg.get(PAGE_WHITE_STACK_KEY);
      }
      case 1: {
        return imgReg.get(PAGE_KEY);
      }
      case 2: {
        return imgReg.get(WRENCH_KEY);
      }
      case 3: {
        // fall through
      }
      case 4: {
        // fall through
      }
      case 5: {
        Boolean citing = entry.getCiting();
        if (citing != null) {
          return citing ? imgReg.get(CROSS_KEY) : imgReg.get(TICK_KEY);
        }
        return null;
      }
      case 6: {
        break;
      }
    }
    return null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.jface.viewers.CellLabelProvider#getToolTipText(java.lang. Object)
   */
  @Override
  public String getToolTipText(Object element) {
    if (element instanceof FindingEntry) {
      FindingEntry entry = ((FindingEntry) element);
      return entry.getPath();
    }
    return null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.jface.viewers.StyledCellLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
   */
  public void update(final ViewerCell cell) {
    final FindingEntry entry = (FindingEntry) cell.getElement();
    final StyledString styledString = new StyledString(getColumnText(entry, cell.getColumnIndex()));
    
    Boolean citing = entry.getCiting();
    if (citing != null) {
      if (citing) {
        cell.setForeground(FOREGROUND_RED);
        cell.setBackground(RED);
      } else {
        cell.setForeground(FOREGROUND_GREEN);
        cell.setBackground(GREEN);
      }
    } else {
      cell.setForeground(FOREGROUND_BLACK);
      cell.setBackground(WHITE);
    }
    
    cell.setText(styledString.toString());
    cell.setStyleRanges(styledString.getStyleRanges());
    cell.setImage(getImage(entry, cell.getColumnIndex()));
  }
  
}
