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
package fr.paris.lutece.plugins.mylutece.authentication;

import fr.paris.lutece.portal.service.security.LoginRedirectException;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.LuteceUser;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.mylutece.service.ILuteceUserAttributesProvidedDescription;
import fr.paris.lutece.plugins.mylutece.service.ILuteceUserRolesProvidedDescription;
import fr.paris.lutece.plugins.mylutece.service.MyLuteceUserService;
import java.util.Arrays;

public abstract class AbstractAuthentication implements LuteceAuthentication,ILuteceUserAttributesProvidedDescription,ILuteceUserRolesProvidedDescription
{
    /**
     * {@inheritDoc }
     */
    @Override
    public LuteceUser login( final String strUserName, final String strUserPassword, HttpServletRequest request ) throws LoginException, LoginRedirectException
    {
        LuteceUser user = processLogin( strUserName, strUserPassword, request );
        MyLuteceUserService.provideUserExternalInfos( user );

        return user;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public LuteceUser getHttpAuthenticatedUser( HttpServletRequest request )
    {
        LuteceUser user = processHttpAuthenticatedUser( request );
        MyLuteceUserService.provideUserExternalInfos( user );

        return user;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isUserInRole( LuteceUser user, HttpServletRequest request, String strRole )
    {
        return Arrays.asList( user.getRoles( ) ).contains( strRole );
    }

    /**
     * Process the login of the user
     * 
     * @param strUsername
     *            the username
     * @param strPassword
     *            the password
     * @param request
     *            the HttpServletRequest
     * @return the LuteceUser
     * @throws LoginException
     * @throws LoginRedirectException
     */
    protected LuteceUser processLogin( String strUsername, String strPassword, HttpServletRequest request ) throws LoginException, LoginRedirectException
    {
        // Default implementation doesn't do anything. This is used for backward compatibility
        // with authentication modules which called "login" method directly
        return null;
    }

    /**
     * Process the authentication of the user when authentication infos are on the request
     * 
     * @param request
     *            the HttpServletRequest
     * @return the LuteceUser
     */
    protected LuteceUser processHttpAuthenticatedUser( HttpServletRequest request )
    {
        // Default implementation doesn't do anything. This is used for backward compatibility
        // with authentication modules which called "getHttpAuthenticated" method directly
        return null;
    }

}
