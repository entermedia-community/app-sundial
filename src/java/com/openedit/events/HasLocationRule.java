/*
 * $Id: HasLocationRule.java,v 1.1 2007/04/12 04:48:18 cburkey Exp $
 *
 * Created on 5/02/2006
 *
 * Copyright (c) 2006, Ben Fortuna
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  o Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 *  o Neither the name of Ben Fortuna nor the names of any other contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.openedit.events;

import java.util.Iterator;
import java.util.List;

import net.fortuna.ical4j.filter.ComponentRule;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.property.Location;

/**
 * A rule that matches any component containing the specified property. Note that
 * this rule ignores any parameters matching only on the value of the property.
 * @author Ben Fortuna
 */
public class HasLocationRule extends ComponentRule {

    private List property;
    

    /**
     * Constructs a new instance with the specified property. Ignores any
     * parameters matching only on the value of the property.
     * @param property
     */
    public HasLocationRule(final List property) {
        this.property = property;
    }
    
    
    /* (non-Javadoc)
     * @see net.fortuna.ical4j.filter.ComponentRule#match(net.fortuna.ical4j.model.Component)
     */
    public final boolean match(final Component component) {
        PropertyList properties = component.getProperties("LOCATION"); //Should be only one?
        for (Iterator i = properties.iterator(); i.hasNext();) {
            Property p = (Property) i.next();
            if( p instanceof Location)
            {
            	//loop over our list and see if this is included
            	for (Iterator iter2 = property.iterator(); iter2.hasNext();)
				{
					Location loc = (Location) iter2.next();
					if( loc.getValue().equalsIgnoreCase(p.getValue()))
					{
						return true;
					}
				}
            }
        }
        return false;
    }
}
