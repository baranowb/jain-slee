package org.mobicents.slee.runtime.transaction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.slee.transaction.CommitListener;
import javax.slee.transaction.RollbackListener;
import javax.slee.transaction.SleeTransaction;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.xa.XAResource;

import com.arjuna.ats.internal.jta.transaction.arjunacore.TransactionImple;

public class SleeTransactionImpl implements SleeTransaction {

	/**
	 * thread pool for async commit/rollback operations
	 */
	private static final ExecutorService executorService = Executors.newCachedThreadPool();
	
	/**
	 * the wrapped JBossTS transaction
	 */
	private final TransactionImple transaction;
	
	/**
	 * caching the wrapped transaction id
	 */
	private final String transactionId;
	
	public SleeTransactionImpl(TransactionImple transaction) {			
		this.transaction = transaction;
		this.transactionId = transaction.get_uid().toString();
	}

	/**
	 * Verifies if the wrapped transaction is active
	 * @throws IllegalStateException
	 * @throws SecurityException
	 */
	private void mandateActiveTransaction() throws IllegalStateException, SecurityException {
		try {			
			int status = transaction.getStatus();
			if (status != Status.STATUS_ACTIVE && status != Status.STATUS_MARKED_ROLLBACK) {
				throw new IllegalStateException(
						"There is no active tx, tx is in state: "
								+ status);
			}
		} catch (SystemException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public void asyncCommit(CommitListener commitListener) throws IllegalStateException,
			SecurityException {
		mandateActiveTransaction();
		executorService.submit(new AsyncTransactionCommitRunnable(commitListener,transaction));					
	}

	public void asyncRollback(RollbackListener rollbackListener)
			throws IllegalStateException, SecurityException {
		mandateActiveTransaction();
		executorService.submit(new AsyncTransactionRollbackRunnable(rollbackListener,transaction));			
	}

	public boolean delistResource(XAResource arg0, int arg1)
			throws IllegalStateException, SystemException {
		return transaction.delistResource(arg0, arg1);		
	}

	public boolean enlistResource(XAResource arg0)
			throws IllegalStateException, RollbackException {
		try {
			return transaction.enlistResource(arg0);
		} catch (SystemException e) {
			// this should be a bug in slee 1.1 api, the exceptions thrown
			// should match jta transaction interface
			throw new RuntimeException(e);
		}
	}

	public void commit() throws RollbackException, HeuristicMixedException,
			HeuristicRollbackException, SecurityException,
			IllegalStateException, SystemException {
		transaction.commit();
	}

	public int getStatus() throws SystemException {
		return transaction.getStatus();
	}

	public void registerSynchronization(Synchronization sync)
			throws RollbackException, IllegalStateException, SystemException {
		transaction.registerSynchronization(sync);		
	}

	public void rollback() throws IllegalStateException, SystemException {
		transaction.rollback();
	}

	public void setRollbackOnly() throws IllegalStateException, SystemException {
		transaction.setRollbackOnly();		
	}

	@Override
	public String toString() {
		return transactionId;
	}
	
	@Override
	public int hashCode() {		
		return transactionId.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj.getClass() == this.getClass()) {
			return ((SleeTransactionImpl)obj).transactionId.equals(this.transactionId);
		}
		else {
			return false;
		}
	}
	
}
