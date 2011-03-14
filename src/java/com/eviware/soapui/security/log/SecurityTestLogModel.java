/*
 *  soapUI, copyright (C) 2004-2011 eviware.com 
 *
 *  soapUI is free software; you can redistribute it and/or modify it under the 
 *  terms of version 2.1 of the GNU Lesser General Public License as published by 
 *  the Free Software Foundation.
 *
 *  soapUI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without 
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU Lesser General Public License for more details at gnu.org.
 */

package com.eviware.soapui.security.log;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

import org.apache.commons.collections.list.TreeList;

import com.eviware.soapui.model.security.SecurityCheck;
import com.eviware.soapui.security.result.SecurityCheckRequestResult;
import com.eviware.soapui.security.result.SecurityCheckResult;
import com.eviware.soapui.security.result.SecurityResult;
import com.eviware.soapui.security.result.SecurityTestStepResult;

/**
 * SecurityTestLog
 * 
 * @author soapUI team
 */
public class SecurityTestLogModel extends AbstractListModel
{
	private List<Object> items = Collections.synchronizedList( new TreeList() );
	private List<SoftReference<SecurityResult>> results = Collections.synchronizedList( new TreeList() );
	private int maxSize = 100;
	private int stepCount;
	private int checkCount;

	public synchronized Object getElementAt( int arg0 )
	{
		try
		{
			return items.get( arg0 );
		}
		catch( Throwable e )
		{
			return null;
		}
	}

	@Override
	public int getSize()
	{
		return items.size();
	}

	public synchronized void addText( String msg )
	{
		items.add( msg );
		results.add( null );
		fireIntervalAdded( this, items.size() - 1, items.size() - 1 );

		enforceMaxSize();
	}

	public synchronized SecurityResult getTestStepResultAt( int index )
	{
		if( index >= results.size() )
			return null;

		SoftReference<SecurityResult> result = results.get( index );
		return result == null ? null : result.get();
	}

	public synchronized void addSecurityTestStepResult( SecurityTestStepResult result )
	{
		stepCount++ ;
		checkCount = 0;

		int size = items.size();

		SoftReference<SecurityResult> stepResultRef = new SoftReference<SecurityResult>( result );

		items.add( "Step " + stepCount + " [" + result.getOriginalTestStepResult().getTestStep().getName() + "] "
				+ result.getOriginalTestStepResult().getStatus() + ": took "
				+ result.getOriginalTestStepResult().getTimeTaken() + " ms" );
		results.add( stepResultRef );
		// checkResults.add( null );
		for( String msg : result.getOriginalTestStepResult().getMessages() )
		{
			items.add( " -> " + msg );
			results.add( stepResultRef );
			// checkResults.add( null );
		}
		// if( AbstractSecurityCheck.isSecurable(
		// result.getOriginalTestStepResult().getTestStep() )
		// && !result.getSecurityCheckResultList().isEmpty() )
		// {
		// for( int i = 0; i < result.getSecurityCheckResultList().size(); i++ )
		// {
		// SecurityCheckResult securityCheckResult =
		// result.getSecurityCheckResultList().get( i );
		// addSecurityCheckResult( securityCheckResult );
		// }
		// }

		fireIntervalAdded( this, size, items.size() - 1 );
		enforceMaxSize();
	}

	public synchronized void addSecurityCheckResult( SecurityCheckResult securityCheckResult )
	{
		checkCount++ ;

		int size = items.size();

		SoftReference<SecurityResult> checkResultRef = new SoftReference<SecurityResult>( securityCheckResult );

		items.add( "Check " + checkCount + " [" + securityCheckResult.getSecurityCheck().getName() + "] "
				+ securityCheckResult.getStatus() + ": took " + securityCheckResult.getTimeTaken() + " ms" );
		results.add( checkResultRef );
		for( SecurityCheckRequestResult requestResult : securityCheckResult.getSecurityRequestResultList() )
		{
			addSecurityCheckRequestResult( requestResult );
		}

		fireIntervalAdded( this, size, items.size() - 1 );
		enforceMaxSize();
	}

	public synchronized void addSecurityCheckStarted( SecurityCheck securityCheck)
	{
		int size = items.size();

		SecurityCheckResult securityCheckResult = new SecurityCheckResult( securityCheck );
		SoftReference<SecurityResult> checkResultRef = new SoftReference<SecurityResult>( securityCheckResult );

		items.add( "SecurityCheck [" + securityCheck.getName() + "] started at "
				+ securityCheckResult.getTimeStamp() );
		results.add( checkResultRef );

		fireIntervalAdded( this, size, items.size() - 1 );
		enforceMaxSize();
	}

	public synchronized void addSecurityCheckEnded( SecurityCheckResult securityCheckResult )
	{
		int size = items.size();

		SoftReference<SecurityResult> checkResultRef = new SoftReference<SecurityResult>( securityCheckResult );

		items.add( "SecurityCheck [" + securityCheckResult.getSecurityCheck().getName() + "] finished with status [ "
				+ securityCheckResult.getStatus() + "], time taken = " + securityCheckResult.getTimeTaken() );
		results.add( checkResultRef );

		fireIntervalAdded( this, size, items.size() - 1 );
		enforceMaxSize();
	}

	public synchronized void addSecurityCheckRequestResult( SecurityCheckRequestResult securityCheckRequestResult )
	{
		int size = items.size();

		for( String msg : securityCheckRequestResult.getMessages() )
		{
			SoftReference<SecurityResult> checkReqResultRef = new SoftReference<SecurityResult>(
					securityCheckRequestResult );
			items.add( " -> " + msg );
			results.add( checkReqResultRef );
		}

		fireIntervalAdded( this, size, items.size() - 1 );
		enforceMaxSize();
	}

	public synchronized void clear()
	{
		int sz = items.size();
		items.clear();
		results.clear();
		stepCount = 0;
		fireIntervalRemoved( this, 0, sz );
	}

	public int getMaxSize()
	{
		return maxSize;
	}

	public void setMaxSize( int maxSize )
	{
		this.maxSize = maxSize;
		enforceMaxSize();
	}

	private synchronized void enforceMaxSize()
	{
		while( items.size() > maxSize )
		{
			items.remove( 0 );
			results.remove( 0 );
			fireIntervalRemoved( this, 0, 0 );
		}
	}

}
