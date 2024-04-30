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
package fr.paris.lutece.plugins.mylutece.authentication.logs;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.sql.Timestamp;

/**
 * This class provides Data Access methods for AppUser objects
 */
@ApplicationScoped
@Named( "mylutece.connectionLogDAO" )
public final class ConnectionLogDAO implements IConnectionLogDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT_LOGIN_ERRORS = " SELECT COUNT(*) FROM mylutece_connections_log  WHERE ip_address = ? AND login_status = ? "
            + " AND date_login > ? AND date_login < ? ";
    private static final String SQL_QUERY_INSERT_LOGS = " INSERT INTO mylutece_connections_log ( ip_address, date_login, login_status ) "
            + " VALUES ( ?, ?, ? )";
    private static final String SQL_UPDATE_CLEAR_LOGS = " UPDATE mylutece_connections_log SET login_status = ? WHERE ip_address = ? AND date_login > ? AND date_login < ? ";

    /**
     * {@inheritDoc}
     */
    @Override
    public int selectLoginErrors( ConnectionLog connectionLog, int nIntervalMinutes, Plugin plugin )
    {
        int nCount = 0;
        java.sql.Timestamp dateEnd = new java.sql.Timestamp( new java.util.Date( ).getTime( ) );
        java.sql.Timestamp dateBegin = new java.sql.Timestamp( dateEnd.getTime( ) - ( nIntervalMinutes * 1000 * 60 ) );

        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_LOGIN_ERRORS, plugin ) )
        {
            daoUtil.setString( 1, connectionLog.getIpAddress( ) );
            daoUtil.setInt( 2, ConnectionLog.LOGIN_DENIED );
            daoUtil.setTimestamp( 3, dateBegin );
            daoUtil.setTimestamp( 4, dateEnd );
    
            daoUtil.executeQuery( );
    
            if ( daoUtil.next( ) )
            {
                nCount = daoUtil.getInt( 1 );
            }
        }

        return nCount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertLog( ConnectionLog connectionLog, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT_LOGS, plugin ) )
        {
            daoUtil.setString( 1, connectionLog.getIpAddress( ) );
            daoUtil.setTimestamp( 2, connectionLog.getDateLogin( ) );
            daoUtil.setInt( 3, connectionLog.getLoginStatus( ) );
    
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetConnectionLogs( String strIp, Timestamp dateLogin, int nIntervalMinutes, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_UPDATE_CLEAR_LOGS, plugin ) )
        {    
            Timestamp dateMin = new Timestamp( dateLogin.getTime( ) - ( nIntervalMinutes * 1000 * 60 ) );
            Timestamp dateMax = new Timestamp( dateLogin.getTime( ) + ( nIntervalMinutes * 1000 * 60 ) );
    
            daoUtil.setInt( 1, ConnectionLog.LOGIN_DENIED_CANCELED );
            daoUtil.setString( 2, strIp );
            daoUtil.setTimestamp( 3, dateMin );
            daoUtil.setTimestamp( 4, dateMax );
    
            daoUtil.executeUpdate( );
        }
    }
}
