package net.java.slee.resources.smpp.pdu;

import java.io.Serializable;
import java.util.Map;

/**
 * 
 * @author amit bhayani
 * 
 */
public interface PDU extends Serializable {

	public long getCommandId();

	public long getCommandStatus();

	public long getSequenceNum();

	public void setSequenceNum(long sequenceNum);

	// TLV operations
	public void addTLV(Tag tag, Object value) throws TLVNotPermittedException;

	public Object getValue(Tag tag);

	public Object removeTLV(Tag tag);

	public boolean hasTLV(Tag tag);

	public boolean isTLVPermitted(Tag tag);

	public Map<Tag, Object> getAllTLVs();

}