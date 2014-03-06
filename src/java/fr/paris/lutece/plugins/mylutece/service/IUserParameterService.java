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
package fr.paris.lutece.plugins.mylutece.service;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

import java.sql.Timestamp;

import java.util.List;


/**
 * User parameter service interface
 *
 */
public interface IUserParameterService
{
    /**
     * Get the parameter from a given key
     * @param strParameterKey the key
     * @param plugin the plugin
     * @return the parameter
     */
    ReferenceItem findByKey( String strParameterKey, Plugin plugin );

    /**
     * Find all parameters
     * @param plugin the plugin
     * @return a ReferenceList
     */
    ReferenceList findAll( Plugin plugin );

    /**
     * Update a parameter
     * @param userParam the parameter
     * @param plugin the plugin
     */
    void update( ReferenceItem userParam, Plugin plugin );

    /**
     * Check if the passwords must be encrypted or not
     * @param plugin the plugin
     * @return true if they are encrypted, false otherwise
     */
    boolean isPasswordEncrypted( Plugin plugin );

    /**
     * Get the encryption algorithm
     * @param plugin the plugin
     * @return the encryption algorithm
     */
    String getEncryptionAlgorithm( Plugin plugin );

    /**
     * Get the number of password change done by a user since the given date.
     * @param minDate Minimum date to consider.
     * @param nUserId Id of the user
     * @param plugin the plugin
     * @return The number of password change done by the user since the given
     *         date.
     */
    int countUserPasswordHistoryFromDate( Timestamp minDate, int nUserId, Plugin plugin );

    /**
     * Gets the history of password of the given user
     * @param nUserId Id of the user
     * @param plugin the plugin
     * @return The collection of recent passwords used by the user.
     */
    List<String> selectUserPasswordHistory( int nUserId, Plugin plugin );
}
