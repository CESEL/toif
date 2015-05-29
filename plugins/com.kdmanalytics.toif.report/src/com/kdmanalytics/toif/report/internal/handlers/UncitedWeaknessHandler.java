/*******************************************************************************
 * Copyright (c) 2015 KDM Analytics, Inc. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Open
 * Source Initiative OSI - Open Software License v3.0 which accompanies this
 * distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 ******************************************************************************/

package com.kdmanalytics.toif.report.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import com.kdmanalytics.toif.report.internal.items.FindingEntry.Citing;
import com.kdmanalytics.toif.report.internal.views.ReportView;
import com.kdmanalytics.toif.report.items.IFindingEntry;
import com.kdmanalytics.toif.report.items.IToifReportEntry;

/**
 * removes the isweakness in the repository
 * 
 * @author Adam Nunn <adam@kdmanalytics.com>
 * 
 */
public class UncitedWeaknessHandler extends AbstractHandler
{
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands
     * .ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        final IStructuredSelection s = (IStructuredSelection) HandlerUtil.getCurrentSelection(event);
        final ReportView view = (ReportView) HandlerUtil.getActivePart(event);
        
        Repository rep = view.getReportInput().getRepository();
        for (Object obj : s.toArray())
        {
            if (obj instanceof IToifReportEntry)
            {
                IToifReportEntry toifReportEntry = (IToifReportEntry) obj;
                IFindingEntry entry = toifReportEntry.getFindingEntry();
                entry.setIsOk(Citing.UNKNOWN);
                removeIsOkInRepository(rep, entry);
                
            }
        }
        view.refresh();
        return null;
    }
    
    /**
     * Sets the is ok in repository.
     * 
     * @param rep
     *            the rep
     * @param finding
     *            the finding
     */
    private void removeIsOkInRepository(Repository rep, IFindingEntry finding)
    {
        try
        {
            ValueFactory factory = rep.getValueFactory();
            RepositoryConnection con = rep.getConnection();
            
            URI isWeaknessURI = factory.createURI("http://toif/isWeakness");
            URI findingURI = factory.createURI(finding.getFindingId());
            
            con.remove(findingURI, isWeaknessURI, null);
            
        }
        catch (RepositoryException e)
        {
            System.err.println("Could not add or remove the trust statements in the repository: " + e);
        }
        
    }
}
