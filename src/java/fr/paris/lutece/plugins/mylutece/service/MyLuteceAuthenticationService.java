/*
 * Copyright (c) 2002-2018, Mairie de Paris
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

import fr.paris.lutece.plugins.mylutece.authentication.logs.ConnectionLog;
import fr.paris.lutece.plugins.mylutece.authentication.logs.ConnectionLogHome;
import static fr.paris.lutece.plugins.mylutece.web.MyLuteceApp.getCurrentUrl;
import static fr.paris.lutece.plugins.mylutece.web.MyLuteceApp.getDefaultRedirectUrl;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.FailedLoginCaptchaException;
import fr.paris.lutece.portal.service.security.LoginRedirectException;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.PortalJspBean;
import fr.paris.lutece.util.http.SecurityUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;

public class MyLuteceAuthenticationService
{
    // Parameters
    private static final String PARAMETER_USERNAME = "username";
    private static final String PARAMETER_PASSWORD = "password";
    
    public static final String PARAMETER_ERROR = "error";
    public static final String PARAMETER_ERROR_VALUE_INVALID = "invalid";
    public static final String PARAMETER_ERROR_MSG = "error_msg";
    public static final String PARAMETER_AUTH_PROVIDER = "auth_provider";
    public static final String PARAMETER_IS_ACTIVE_CAPTCHA = "mylutece_is_active_captcha";
    public static final String PARAMETER_ERROR_CAPTCHA = "error_captcha";
    public static final String PARAMETER_REDIRECT_URL = "redirect_url";
    public static final String PARAMETER_LOGIN_ATTEMPT = "login_attempt";
    public static final String PARAMETER_LOGIN_ATTEMPT_SUCCESS = "success";    
    
    private static final String PROPERTY_MYLUTECE_DEFAULT_REDIRECT_URL = "mylutece.url.default.redirect";
    
    /**
     * This method is call by the JSP named DoMyLuteceLogin.jsp or REST call
     * 
     * @param request The HTTP request
     * @return The URL to forward depending of the result of the login.
     * @throws UnsupportedEncodingException
     */
    public static Map<String,String> doLogin( HttpServletRequest request ) throws UnsupportedEncodingException
    {
        String strUsername = request.getParameter( PARAMETER_USERNAME );
        String strPassword = request.getParameter( PARAMETER_PASSWORD );
        String strAuthProvider = request.getParameter( PARAMETER_AUTH_PROVIDER );

        HashMap<String,String> mapReturn = new HashMap<>();

        Boolean bIsCaptchaEnabled = (Boolean) request.getSession( true ).getAttribute( PARAMETER_IS_ACTIVE_CAPTCHA );

        if ( ( bIsCaptchaEnabled != null ) && bIsCaptchaEnabled )
        {
            CaptchaSecurityService captchaService = new CaptchaSecurityService( );

            if ( !captchaService.validate( request ) )
            {
                mapReturn.put( PARAMETER_ERROR, PARAMETER_ERROR_CAPTCHA );
            }
        }

        Plugin plugin = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );

        try
        {
            SecurityService.getInstance( ).loginUser(request, strUsername, strPassword );
        }
        catch ( LoginRedirectException ex )
        {
            HttpSession session = request.getSession( false );

            if ( session != null )
            {
                session.removeAttribute( PARAMETER_IS_ACTIVE_CAPTCHA );
            }
            
            mapReturn.put( PARAMETER_REDIRECT_URL , ex.getRedirectUrl( ) );
            return mapReturn ;
        }
        catch ( FailedLoginException ex )
        {
            // Creating a record of connections log
            ConnectionLog connectionLog = new ConnectionLog( );
            connectionLog.setIpAddress( SecurityUtil.getRealIp( request ) );
            connectionLog.setDateLogin( new java.sql.Timestamp( new java.util.Date( ).getTime( ) ) );
            connectionLog.setLoginStatus( ConnectionLog.LOGIN_DENIED ); // will be inserted only if access denied
            ConnectionLogHome.addUserLog( connectionLog, plugin );

            mapReturn.put( PARAMETER_ERROR , PARAMETER_ERROR_VALUE_INVALID );

            if ( StringUtils.isNotBlank( strAuthProvider ) )
            {
                mapReturn.put( PARAMETER_AUTH_PROVIDER , strAuthProvider );
            }

            if ( ex.getMessage( ) != null )
            {
                mapReturn.put( PARAMETER_ERROR_MSG , URLEncoder.encode( ex.getMessage( ), "UTF-8" ) );
            }

            if ( ex instanceof FailedLoginCaptchaException )
            {
                Boolean bEnableCaptcha = ( (FailedLoginCaptchaException) ex ).isCaptchaEnabled( );
                request.getSession( true ).setAttribute( PARAMETER_IS_ACTIVE_CAPTCHA, bEnableCaptcha );
            }

            return mapReturn;
        }
        catch ( LoginException ex )
        {
            mapReturn.put( PARAMETER_ERROR, PARAMETER_ERROR_VALUE_INVALID );

            if ( StringUtils.isNotBlank( strAuthProvider ) )
            {
                mapReturn.put( PARAMETER_AUTH_PROVIDER, strAuthProvider );
            }

            if ( ex.getMessage( ) != null )
            {
                mapReturn.put( PARAMETER_ERROR_MSG , ex.getMessage( ) );
            }

            return mapReturn;
        }

        // login successfull
        mapReturn.put( PARAMETER_LOGIN_ATTEMPT, PARAMETER_LOGIN_ATTEMPT_SUCCESS );
        
        HttpSession session = request.getSession( false );
        if ( session != null )
        {
            session.removeAttribute( PARAMETER_IS_ACTIVE_CAPTCHA );
        }

        String strNextUrl = PortalJspBean.getLoginNextUrl( request );
        String strCurrentUrl = getCurrentUrl( request );

        if ( strNextUrl != null )
        {
            mapReturn.put( PARAMETER_REDIRECT_URL , strNextUrl );
        }
        else if ( strCurrentUrl != null )
        {
            mapReturn.put( PARAMETER_REDIRECT_URL , strCurrentUrl );
        }
        else
        {
            mapReturn.put( PARAMETER_REDIRECT_URL ,  AppPropertiesService.getProperty( PROPERTY_MYLUTECE_DEFAULT_REDIRECT_URL ) );
        }
        
        return mapReturn ;
    }

    
    /**
     * This method is call by the JSP named DoMyLuteceLogout.jsp or REST call
     * 
     * @param request The HTTP request
     * @return The URL to forward depending of the result of the login.
     * @throws UnsupportedEncodingException
     */
    public static Map<String,String> doLogout( HttpServletRequest request ) 
    {    
        SecurityService.getInstance( ).logoutUser( request );
        
        HashMap<String,String> mapReturn = new HashMap<>();
        mapReturn.put( PARAMETER_REDIRECT_URL ,  AppPropertiesService.getProperty( PROPERTY_MYLUTECE_DEFAULT_REDIRECT_URL ) );
        
        return mapReturn;
    }
        
}
