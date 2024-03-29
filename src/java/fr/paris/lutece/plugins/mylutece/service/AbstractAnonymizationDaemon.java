/*
 * Copyright (c) 2002-2021, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.mylutece.service;

import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.util.AppLogService;

import java.util.List;
import java.util.Locale;

/**
 * Daemon to anonymize users
 */
public abstract class AbstractAnonymizationDaemon extends Daemon
{
    private static final String CONSTANT_NO_EXPIRED_USER = "There is no expired user to anonymize";
    private static final String CONSTANT_FOUND_EXPIRED_USER_ANONYMIZED_START = "MyLuteceAnonymizationService - Expired users have been found. Begining anonymization...";

    /**
     * Get the anonymization service implementation to use
     * 
     * @return The anonymization service
     */
    public abstract IAnonymizationService getAnonymizationService( );

    /**
     * Get the name of the daemon
     * 
     * @return The name of the daemon
     */
    public abstract String getDaemonName( );

    /**
     * {@inheritDoc}
     */
    @Override
    public void run( )
    {
        IAnonymizationService anonymizationService = getAnonymizationService( );
        Locale locale = Locale.getDefault( );
        StringBuilder sbLogs = new StringBuilder( );
        StringBuilder sbResult = new StringBuilder( );
        List<Integer> expiredUserIdList = anonymizationService.getExpiredUserIdList( );

        if ( ( expiredUserIdList != null ) && ( expiredUserIdList.size( ) > 0 ) )
        {
            int nbUserFound = expiredUserIdList.size( );
            AppLogService.info( CONSTANT_FOUND_EXPIRED_USER_ANONYMIZED_START );

            for ( Integer nIdUser : expiredUserIdList )
            {
                anonymizationService.anonymizeUser( nIdUser, locale );
                AppLogService.info( getDaemonName( ) + " - User with id " + Integer.toString( nIdUser ) + " has been anonymized" );
            }

            sbLogs.append( getDaemonName( ) );
            sbLogs.append( " - " );
            sbLogs.append( nbUserFound );
            sbLogs.append( " user(s) have been anonymized" );
            AppLogService.info( sbLogs.toString( ) );
            sbResult.append( sbLogs.toString( ) );
        }
        else
        {
            sbLogs.append( getDaemonName( ) );
            sbLogs.append( " - " );
            sbLogs.append( CONSTANT_NO_EXPIRED_USER );
            AppLogService.info( sbLogs.toString( ) );
            sbResult.append( sbLogs.toString( ) );
        }

        this.setLastRunLogs( sbResult.toString( ) );
    }
}
