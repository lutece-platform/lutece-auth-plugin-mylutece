/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.mylutece.service.search;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import fr.paris.lutece.util.ReferenceList;

/**
 * This is the business class for the object MyLuteceSearchUser
 * DTO used to represent user search results from IUserSearchProvider
 */
public class MyLuteceSearchUser implements Serializable
{
    private static final long serialVersionUID = 1L;

    // Variables declarations
    private int _nId;

    @NotEmpty( message = "#i18n{mylutece.validation.mylutecesearchuser.Login.notEmpty}" )
    @Size( max = 255, message = "#i18n{mylutece.validation.mylutecesearchuser.Login.size}" )
    private String _strLogin;

    @NotEmpty( message = "#i18n{mylutece.validation.mylutecesearchuser.GivenName.notEmpty}" )
    @Size( max = 255, message = "#i18n{mylutece.validation.mylutecesearchuser.GivenName.size}" )
    private String _strGivenName;

    @NotEmpty( message = "#i18n{mylutece.validation.mylutecesearchuser.LastName.notEmpty}" )
    @Size( max = 255, message = "#i18n{mylutece.validation.mylutecesearchuser.LastName.size}" )
    private String _strLastName;

    @Email( message = "#i18n{portal.validation.message.email}" )
    @Size( max = 255, message = "#i18n{mylutece.validation.mylutecesearchuser.Email.size}" )
    private String _strEmail;
    private String _strproviderUserId;
    private ReferenceList _strAttributes;

    /**
     * Returns the Id
     *
     * @return The Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     *
     * @param nId
     *            The Id
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * Returns the provider user ID
     *
     * @return The provider user ID
     */
    public String getProviderUserId( )
    {
        return _strproviderUserId;
    }

    /**
     * Sets the provider user ID
     *
     * @param strproviderUserId
     *            The provider user ID
     */
    public void setProviderUserId( String strproviderUserId )
    {
        _strproviderUserId = strproviderUserId;
    }

    /**
     * Returns the Login
     *
     * @return The Login
     */
    public String getLogin( )
    {
        return _strLogin;
    }

    /**
     * Sets the Login
     *
     * @param strLogin
     *            The Login
     */
    public void setLogin( String strLogin )
    {
        _strLogin = strLogin;
    }

    /**
     * Returns the GivenName
     *
     * @return The GivenName
     */
    public String getGivenName( )
    {
        return _strGivenName;
    }

    /**
     * Sets the GivenName
     *
     * @param strGivenName
     *            The GivenName
     */
    public void setGivenName( String strGivenName )
    {
        _strGivenName = strGivenName;
    }

    /**
     * Returns the LastName
     *
     * @return The LastName
     */
    public String getLastName( )
    {
        return _strLastName;
    }

    /**
     * Sets the LastName
     *
     * @param strLastName
     *            The LastName
     */
    public void setLastName( String strLastName )
    {
        _strLastName = strLastName;
    }

    /**
     * Returns the Email
     *
     * @return The Email
     */
    public String getEmail( )
    {
        return _strEmail;
    }

    /**
     * Sets the Email
     *
     * @param strEmail
     *            The Email
     */
    public void setEmail( String strEmail )
    {
        _strEmail = strEmail;
    }

    /**
     * Returns the Attributes
     *
     * @return The Attributes
     */
    public ReferenceList getAttributes( )
    {
        return _strAttributes;
    }

    /**
     * Sets the Attributes
     *
     * @param strAttributes
     *            The Attributes
     */
    public void setAttributes( ReferenceList strAttributes )
    {
        _strAttributes = strAttributes;
    }
}
