/*******************************************************************************
 * /////////////////////////////////////////////////////////////////////////////
 * ///// // Copyright (c) 2016 KDM Analytics, Inc. All rights reserved. This
 * program and the // accompanying materials are made available under the terms
 * of the Open Source // Initiative OSI - Open Software License v3.0 which
 * accompanies this // distribution, and is available at
 * http://www.opensource.org/licenses/osl-3.0.php/
 * /////////////////////////////////////////////////////////////////////////////
 * /////
 ******************************************************************************/

package com.kdmanalytics.toif.framework.xmlElements.entities;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Entity is an extension of the element class.
 * 
 * @author adam
 *         
 */
@XmlRootElement(name = "entity")
public class Entity extends Element
{
    
    /**
     * required empty constructor.
     */
    public Entity()
    {
        super();
    }
    
}
