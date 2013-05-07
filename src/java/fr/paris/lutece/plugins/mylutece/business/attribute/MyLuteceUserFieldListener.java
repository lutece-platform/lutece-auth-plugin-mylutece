/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.mylutece.business.attribute;

import fr.paris.lutece.portal.business.user.AdminUser;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * MyLuteceUserFieldListener
 *
 */
public interface MyLuteceUserFieldListener 
{
    /**
     * Create user fields
     * @param nIdUser The Id of the user
     * @param request HttpServletRequest
     * @param locale locale
     */
	void doCreateUserFields( int nIdUser, HttpServletRequest request, Locale locale );

    /**
     * Create user fields. This method may do nothing if user fields can not be
     * created just from a String value.
     * @param nIdUser The Id of the user
     * @param listUserFields The list of user fields to create
     * @param locale locale
     */
    void doCreateUserFields( int nIdUser, List<MyLuteceUserField> listUserFields, Locale locale );

    /**
     * Modify user fields
     * @param nIdUser The Id of the user
     * @param request HttpServletRequest
     * @param locale locale
     * @param currentUser current user. The current user may be null.
     */
	void doModifyUserFields( int nIdUser, HttpServletRequest request, Locale locale, AdminUser currentUser );

    /**
     * Modify user fields
     * @param nIdUser The Id of the user
     * @param listUserFields The list of user fields to update
     * @param locale locale
     * @param currentUser current user. The current user may be null.
     */
    void doModifyUserFields( int nIdUser, List<MyLuteceUserField> listUserFields, Locale locale, AdminUser currentUser );

    /**
     * Remove user fields
     * @param nIdUser The Id of the user
     * @param request HttpServletRequest
     * @param locale locale
     */
	void doRemoveUserFields( int nIdUser, HttpServletRequest request, Locale locale );

    /**
     * Remove user fields
     * @param nIdUser The Id of the user
     * @param locale locale
     */
    void doRemoveUserFields( int nIdUser, Locale locale );
}
