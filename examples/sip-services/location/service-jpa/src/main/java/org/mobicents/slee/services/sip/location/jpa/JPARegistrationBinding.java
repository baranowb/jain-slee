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

package org.mobicents.slee.services.sip.location.jpa;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.mobicents.slee.services.sip.location.RegistrationBinding;

@Entity
@Table(name = "SLEE_SIPSERVICES_REGISTRAR_BINDINGS")
@NamedQueries({
	@NamedQuery(name="selectBindingsForSipAddress",query="SELECT x FROM JPARegistrationBinding x WHERE x.key.sipAddress = :sipAddress"),
	@NamedQuery(name="selectAllBindings",query="SELECT x FROM JPARegistrationBinding x"),
	@NamedQuery(name="updateBindingByKey",query="UPDATE JPARegistrationBinding x SET x.callId=:callId,x.comment=:comment,x.cSeq=:cSeq,x.expires=:expires,x.registrationDate=:registrationDate,x.qValue=:qValue WHERE x.key.sipAddress = :sipAddress AND x.key.contactAddress = :contactAddress"),
	@NamedQuery(name="deleteBindingsByKey",query="DELETE FROM JPARegistrationBinding x WHERE x.key.sipAddress = :sipAddress AND x.key.contactAddress = :contactAddress")
})
public class JPARegistrationBinding extends RegistrationBinding {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1996317730252868349L;
	
	@EmbeddedId
	private JPARegistrationBindingKey key;
	// This date stores the absolute time when this entry will expire
	@Column(name = "EXPIRES", nullable = false)
	private long expires;
	@Column(name = "REGISTRATION_DATE", nullable = false)
	private long registrationDate;
	@Column(name = "QVALUE", nullable = true)
    private float qValue;
	@Column(name = "CALLID", nullable = false)
    private String callId;
	@Column(name = "CSEQ", nullable = false)
    private long cSeq;
	@Column(name = "COMMENT", nullable = true)
    private String comment;
	
    public JPARegistrationBinding() {}
    
    public JPARegistrationBinding(String sipAddress, String contactAddress, String comment, long expires, long registrationDate, float qValue, String callId, long cSeq) {
    	this.key = new JPARegistrationBindingKey(contactAddress,sipAddress);
    	this.comment = comment;
        this.expires = expires;
        this.registrationDate = registrationDate;
        this.qValue = qValue;
        this.callId = callId;
        this.cSeq = cSeq;
    }

	public String getCallId() { 
		return callId;
	}
    
    public void setCallId(String id) {
        this.callId = id;
    }

    public String getComment() {
    	return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
       
    public long getCSeq() {
    	return cSeq;
    }
    
    public void setCSeq(long seq) {
        this.cSeq = seq;
    }
    
	public long getExpires() {
		return expires;
	}

	public void setExpires(long expires) {
		this.expires = expires;
	}
    
	public float getQValue() { 
		return qValue;
	}
    
    public void setQValue(float q) {
        this.qValue = q;
    }

    public long getRegistrationDate() {
		return registrationDate;
	}
    
    public void setRegistrationDate(long registrationDate) {
		this.registrationDate = registrationDate;
	}

	public JPARegistrationBindingKey getKey() {
		return key;
	}

	public void setKey(JPARegistrationBindingKey key) {
		this.key = key;
	}

	public String getContactAddress() {
		return key.getContactAddress();
	}
	
	public String getSipAddress() {
		return key.getSipAddress();
	}
	
}
