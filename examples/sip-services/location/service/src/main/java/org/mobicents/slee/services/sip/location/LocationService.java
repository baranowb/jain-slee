/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.mobicents.slee.services.sip.location;

import java.util.Map;
import java.util.Set;

import org.mobicents.slee.services.sip.location.jmx.LocationServiceManagementMBean;

public interface LocationService extends LocationServiceManagementMBean {

	/**
	 * Adds new contact binding for particular user..
	 * 
	 * @param sipAddress -
	 *            user address of record sip:ala@ma.kota.w.domu.com
	 * @param contactAddress -
	 *            contact address - sip:+381243256
	 * @param comment -
	 *            possible comment note
	 * @param expires -
	 *            long - seconds for which this contact is to remain valid
	 * @param registrationDate -
	 *            long - date when the registration was created/updated
	 * @param qValue -
	 *            q parameter
	 * @param callId -
	 *            call id
	 * @param cSeq -
	 *            seq numbers
	 * @return - binding created in this operation
	 * @throws LocationServiceException
	 */
	public RegistrationBinding addBinding(String sipAddress,
			String contactAddress, String comment, long expires,
			long registrationDate, float qValue, String callId, long cSeq)
			throws LocationServiceException;

	/**
	 * Returns set of user that have registered - set contains adress of record
	 * for each user, something like sip:ala@kocia.domena.com
	 * 
	 * @return
	 * @throws LocationServiceException 
	 */
	public Set<String> getRegisteredUsers() throws LocationServiceException;

	/**
	 * Returns map which contains mapping contactAddress->registrationBinding
	 * for particular user - address of record sip:nie@ma.mnie.tu
	 * 
	 * @param sipAddress
	 * @return
	 * @throws LocationServiceException
	 */
	public Map<String, RegistrationBinding> getBindings(String sipAddress)
			throws LocationServiceException;


	/**
	 * Updates the specified registration binding.
	 * 
	 * @param registrationBinding	
	 * @throws LocationServiceException
	 */
	public void updateBinding(RegistrationBinding registrationBinding)
			throws LocationServiceException;

	/**
	 * Removes contact address from user bindings.
	 * 
	 * @param sipAddress -
	 *            sip:ala@kocia.domena.au
	 * @param contactAddress -
	 *            sip:+481234567890
	 * @throws LocationServiceException
	 */
	public void removeBinding(String sipAddress, String contactAddress)
			throws LocationServiceException;


	/**
	 * Starts the location service
	 */
	public void init();
	
	/**
	 * Shutdown the location service
	 */
	public void shutdown();
	
}
