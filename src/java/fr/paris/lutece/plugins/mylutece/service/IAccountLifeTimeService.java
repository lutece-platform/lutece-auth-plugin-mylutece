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

import java.sql.Timestamp;

import java.util.List;
import java.util.Map;


/**
 * Account life time service interface
 *
 */
public interface IAccountLifeTimeService
{
    /**
     * Get the list of id of users that have an expired time life but not the
     * expired status
     * @param currentTimestamp Timestamp describing the current time.
     * @return the list of id of users with expired time life
     */
    List<Integer> getIdUsersWithExpiredLifeTimeList( Timestamp currentTimestamp );

    /**
     * Get the list of id of users that need to receive their first alert
     * @param alertMaxDate The maximum date to send alerts.
     * @return the list of id of users that need to receive their first alert
     */
    List<Integer> getIdUsersToSendFirstAlert( Timestamp alertMaxDate );

    /**
     * Get the list of id of users that need to receive their first alert
     * @param alertMaxDate The maximum date to send alerts.
     * @param timeBetweenAlerts Timestamp describing the time between two
     *            alerts.
     * @param maxNumberAlerts Maximum number of alerts to send to a user
     * @return the list of id of users that need to receive their first alert
     */
    List<Integer> getIdUsersToSendOtherAlert( Timestamp alertMaxDate, Timestamp timeBetweenAlerts, int maxNumberAlerts );

    /**
     * Get the list of id of users that have an expired password but not the change password flag
     * @param currentTimestamp Timestamp describing the current time.
     * @return the list of id of users with expired passwords
     */
    List<Integer> getIdUsersWithExpiredPasswordsList( Timestamp currentTimestamp );

    /**
     * Increment the number of alert send to users by 1
     * @param listIdUser The list of users to update
     */
    void updateNbAlert( List<Integer> listIdUser );

    /**
     * Set the "change password" flag of users to true
     * @param listIdUser List of user's id to update
     */
    void updateChangePassword( List<Integer> listIdUser );

    /**
     * Set a user account status as expired. Expired user will be anonymized by an anonymization daemon
     * @param listIdUser User accounts list to set as expired
     */
    void setUserStatusExpired( List<Integer> listIdUser );

    /**
     * Get the body of the mail to send when a user account expire
     * @return The body of the mail to send
     */
    String getExpirationtMailBody(  );

    /**
     * Get the body of the mail to send for a first notification of a user
     * before his account expire
     * @return The body of the mail to send
     */
    String getFirstAlertMailBody(  );

    /**
     * Get the body of the mail to send for a new notification of a user
     * before his account expire
     * @return The body of the mail to send
     */
    String getOtherAlertMailBody(  );

    /**
     * Get the body of the mail to send when a password expired
     * @return The body of the mail to send
     */
    String getPasswordExpiredMailBody(  );

    /**
     * Add specifiques parameters to a given model
     * @param model The model
     * @param nIdUser The id of the user to add the parameters
     */
    void addParametersToModel( Map<String, String> model, Integer nIdUser );

    /**
     * Get the main email adresse of a user
     * @param nUserId Id of the user
     * @return The main email adresse of a user
     */
    String getUserMainEmail( int nUserId );

    /**
     * Get the current plugin
     * @return The plugin
     */
    Plugin getPlugin(  );
}
