/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.plugins.mylutece.web.MyLuteceApp;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.LuteceUser;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * ExternalAuthentication.
 */
public abstract class ExternalAuthentication implements LuteceAuthentication
{
    private static final String CONSTANT_PATH_ICON = "images/local/skin/plugins/mylutece/mylutece.png";

    /**
     * Indicates that the user should be already authenticated by an external authentication service (ex : Web Server authentication).
     * @return true if the authentication is external, false if the authentication is provided by the Lutece portal.
     */
    public boolean isExternalAuthentication(  )
    {
        return true;
    }

    /**
     *
     * Always false
     * @return false
     */
    public boolean isDelegatedAuthentication(  )
    {
        return false;
    }

    /**
     * Returns the Login page URL of the Authentication Service
     * @return The URL
     */
    public String getLoginPageUrl(  )
    {
        return null;
    }

    /**
     * Returns the DoLogin URL of the Authentication Service
     * @return The URL
     */
    public String getDoLoginUrl(  )
    {
        return null;
    }

    /**
     * Returns the DoLogout URL of the Authentication Service
     * @return The URL
     */
    public String getDoLogoutUrl(  )
    {
        return null;
    }

    /**
     * Returns the new account page URL of the Authentication Service
     * @return The URL
     */
    public String getNewAccountPageUrl(  )
    {
        return null;
    }

    /**
     * Returns the View account page URL of the Authentication Service
     * @return The URL
     */
    public String getViewAccountPageUrl(  )
    {
        return null;
    }

    /**
     * Returns the lost password URL of the Authentication Service
     * @return The URL
     */
    public String getLostPasswordPageUrl(  )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLostLoginPageUrl(  )
    {
        return null;
    }

    /**
     * Returns the access denied template
     * @return The template
     */
    public String getAccessDeniedTemplate(  )
    {
        return MyLuteceApp.getAccessDeniedTemplate(  );
    }

    /**
     * Returns the access controled template
     * @return The template
     */
    public String getAccessControledTemplate(  )
    {
        return MyLuteceApp.getAccessControledTemplate(  );
    }

    /**
     * Tells whether or not the authentication service can provide a list of all its users
     * @return true if the service can return a users list
     */
    public boolean isUsersListAvailable(  )
    {
        return false;
    }

    /**
     * Returns all users managed by the authentication service if this feature is available.
     * @return A collection of Lutece users or null if the service doesn't provide a users list
     */
    public Collection<LuteceUser> getUsers(  )
    {
        return null;
    }

    /**
     * Returns the user managed by the authentication service if this feature is available.
     * @param userLogin user login
     * @return A Lutece users or null if the service doesn't provide a user
     */
    public LuteceUser getUser( String userLogin )
    {
        return null;
    }

    /**
     * get all roles for this user : - user's roles - user's groups roles
     *
     * @param user The user
     * @return Array of roles
     */
    public String[] getRolesByUser( LuteceUser user )
    {
        return null;
    }

    /**
     * Returns false
     * @return false
     * @see LuteceAuthentication#isMultiAuthenticationSupported()
     *
     */
    public boolean isMultiAuthenticationSupported(  )
    {
        return false;
    }

    /**
     *
     *{@inheritDoc}
     */
    public String getIconUrl(  )
    {
        return CONSTANT_PATH_ICON;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public boolean findResetPassword( HttpServletRequest request, String strLogin )
    {
        return false;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    public String getResetPasswordPageUrl( HttpServletRequest request )
    {
        return null;
    }

    /**
     *
     *{@inheritDoc}
     */
    @Override
    public void updateDateLastLogin( LuteceUser user, HttpServletRequest request )
    {
    }
}
