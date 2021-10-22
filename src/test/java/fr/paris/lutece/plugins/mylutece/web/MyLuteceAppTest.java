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
package fr.paris.lutece.plugins.mylutece.web;

import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.mock.web.MockHttpServletRequest;

import fr.paris.lutece.plugins.mylutece.authentication.logs.ConnectionLog;
import fr.paris.lutece.plugins.mylutece.authentication.logs.ConnectionLogHome;
import fr.paris.lutece.plugins.mylutece.service.MyLutecePlugin;
import fr.paris.lutece.plugins.mylutece.util.SecurityUtils;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.test.LuteceTestCase;

public class MyLuteceAppTest extends LuteceTestCase
{
    private static final String STR_IP_ADDRESS = "127.0.0.1";
    private Plugin _plugin;
    private ConnectionLog _connetionLog;

    @Override
    public void setUp( ) throws Exception
    {
        super.setUp( );
        _plugin = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );
        _connetionLog = new ConnectionLog( );
        _connetionLog.setDateLogin( new Timestamp( new Date( ).getTime( ) ) );
        _connetionLog.setIpAddress( STR_IP_ADDRESS );
        _connetionLog.setLoginStatus( ConnectionLog.LOGIN_DENIED );
        ConnectionLogHome.addUserLog( _connetionLog, _plugin );

        TimeUnit.SECONDS.sleep( 1 ); // wait till next second
        assertEquals( 1, ConnectionLogHome.getLoginErrors( _connetionLog, 1, _plugin ) );
    }

    @Override
    public void tearDown( ) throws Exception
    {
        ConnectionLogHome.resetConnectionLogs( STR_IP_ADDRESS, new Timestamp( new Date( ).getTime( ) ), 1, _plugin );
        super.tearDown( );
    }

    public void testDoResetConnectionLog( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setRemoteAddr( STR_IP_ADDRESS );
        String strUrl = SecurityUtils.buildResetConnectionLogUrl( 1, request );
        Pattern pattern = Pattern.compile( "[\\?&]([^=]+)=([^&]+)" );
        Matcher matcher = pattern.matcher( strUrl );
        request = new MockHttpServletRequest( );
        while ( matcher.find( ) )
        {
            if ( "date_login".equals( matcher.group( 1 ) ) )
            {
                request.setParameter( "date_login", matcher.group( 2 ) );
            }
            else
                if ( "key".equals( matcher.group( 1 ) ) )
                {
                    request.setParameter( "key", matcher.group( 2 ) );
                }
        }
        MyLuteceApp app = new MyLuteceApp( );
        request.setParameter( "ip", STR_IP_ADDRESS );
        request.setParameter( "interval", "1" );
        strUrl = app.doResetConnectionLog( request );

        assertEquals( "../../../../" + MyLuteceApp.getLoginPageUrl( ), strUrl );
        assertEquals( 0, ConnectionLogHome.getLoginErrors( _connetionLog, 1, _plugin ) );
    }

    public void testDoResetConnectionLogBadKey( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setRemoteAddr( STR_IP_ADDRESS );
        String strUrl = SecurityUtils.buildResetConnectionLogUrl( 1, request );
        Pattern pattern = Pattern.compile( "[\\?&]([^=]+)=([^&]+)" );
        Matcher matcher = pattern.matcher( strUrl );
        request = new MockHttpServletRequest( );
        while ( matcher.find( ) )
        {
            if ( "date_login".equals( matcher.group( 1 ) ) )
            {
                request.setParameter( "date_login", matcher.group( 2 ) );
            }
            else
                if ( "key".equals( matcher.group( 1 ) ) )
                {
                    String strKey = matcher.group( 2 );
                    if ( strKey.startsWith( "0" ) )
                    {
                        strKey = "1" + strKey.substring( 1 );
                    }
                    else
                    {
                        strKey = "0" + strKey.substring( 1 );
                    }
                    request.setParameter( "key", strKey );
                }
        }
        MyLuteceApp app = new MyLuteceApp( );
        request.setParameter( "ip", STR_IP_ADDRESS );
        request.setParameter( "interval", "1" );
        strUrl = app.doResetConnectionLog( request );

        assertEquals( "../../../../" + MyLuteceApp.getLoginPageUrl( ), strUrl );
        assertEquals( 1, ConnectionLogHome.getLoginErrors( _connetionLog, 1, _plugin ) );
    }
}
