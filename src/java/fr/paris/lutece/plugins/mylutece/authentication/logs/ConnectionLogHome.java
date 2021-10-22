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
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.sql.Timestamp;

/**
 * This class provides instances management methods (create, find, ...) for UserLog objects
 */
public final class ConnectionLogHome
{
    // Static variable pointed at the DAO instance
    private static IConnectionLogDAO _dao = SpringContextService.getBean( "mylutece.connectionLogDAO" );

    /**
     * Creates a new UserLogHome object.
     */
    private ConnectionLogHome( )
    {
    }

    /////////////////////////////////////////////////////////////////////////////
    // Connections logs

    /**
     * Insert a new record in the table of connections
     * 
     * @param connetionLog
     *            the ConnectionLog object
     * @param plugin
     *            The plugin
     */
    public static void addUserLog( ConnectionLog connetionLog, Plugin plugin )
    {
        _dao.insertLog( connetionLog, plugin );
    }

    /**
     * Calculate the number of connections with a given ip_address by a determinate time
     * 
     * @param connetionLog
     *            the connetionLog object
     * @param nIntervalMinutes
     *            The number of minutes of properties file
     * @param plugin
     *            The plugin
     * @return int the count of errors of login
     */
    public static int getLoginErrors( ConnectionLog connetionLog, int nIntervalMinutes, Plugin plugin )
    {
        return _dao.selectLoginErrors( connetionLog, nIntervalMinutes, plugin );
    }

    /**
     * Update connection logs of an IP to allow the user to login.
     * 
     * @param strIp
     *            Ip to clean
     * @param dateLogin
     *            Date of the last login. Anly logs between this date plus or minus the minute interval will be cleared.
     * @param nIntervalMinutes
     *            Minutes interval
     * @param plugin
     *            The plugin
     */
    public static void resetConnectionLogs( String strIp, Timestamp dateLogin, int nIntervalMinutes, Plugin plugin )
    {
        _dao.resetConnectionLogs( strIp, dateLogin, nIntervalMinutes, plugin );
    }
}
