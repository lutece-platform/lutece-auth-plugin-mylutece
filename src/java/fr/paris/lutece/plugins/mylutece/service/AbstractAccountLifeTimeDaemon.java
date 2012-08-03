/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.plugins.mylutece.util.SecurityUtils;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * Daemon to anonymize users
 */
public abstract class AbstractAccountLifeTimeDaemon extends Daemon
{

    private static final String PARAMETER_TIME_BEFORE_ALERT_ACCOUNT = "time_before_alert_account";
    private static final String PARAMETER_NB_ALERT_ACCOUNT = "nb_alert_account";
    private static final String PARAMETER_TIME_BETWEEN_ALERTS_ACCOUNT = "time_between_alerts_account";

    private static final String PARAMETER_EXPIRED_ALERT_MAIL_SENDER = "expired_alert_mail_sender";
    private static final String PARAMETER_EXPIRED_ALERT_MAIL_SUBJECT = "expired_alert_mail_subject";
    private static final String PARAMETER_FIRST_ALERT_MAIL_SENDER = "first_alert_mail_sender";
    private static final String PARAMETER_FIRST_ALERT_MAIL_SUBJECT = "first_alert_mail_subject";
    private static final String PARAMETER_OTHER_ALERT_MAIL_SENDER = "other_alert_mail_sender";
    private static final String PARAMETER_OTHER_ALERT_MAIL_SUBJECT = "other_alert_mail_subject";

    /**
     * Get the account life time service implementation to use
     * @return The account life time service
     */
    public abstract IAccountLifeTimeService getAccountLifeTimeService( );

    /**
     * Get the Parameter service to use
     * @return The parameter service to use
     */
    public abstract IUserParameterService getParameterService( );

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "deprecation" )
    @Override
    public void run( )
    {
        StringBuilder sbLogs = null;
        
        IAccountLifeTimeService accountLifeTimeService = getAccountLifeTimeService( );
        IUserParameterService parameterService = getParameterService( );
        Plugin plugin = accountLifeTimeService.getPlugin( );

        Timestamp currentTimestamp = new Timestamp( new java.util.Date( ).getTime( ) );
        List<Integer> accountsToSetAsExpired = accountLifeTimeService
                .getIdUsersWithExpiredLifeTimeList( currentTimestamp );

        // We first set as expirated user that have reached their life time limit
        if ( accountsToSetAsExpired != null && accountsToSetAsExpired.size( ) > 0 )
        {
            int nbAccountToExpire = accountsToSetAsExpired.size( );
            String strBody = accountLifeTimeService.getExpirationtMailBody( );

            ReferenceItem referenceItem = parameterService.findByKey( PARAMETER_EXPIRED_ALERT_MAIL_SENDER, plugin );
            String strSender = referenceItem == null ? StringUtils.EMPTY : referenceItem.getName( );

            referenceItem = parameterService.findByKey( PARAMETER_EXPIRED_ALERT_MAIL_SUBJECT, plugin );
            String strSubject = referenceItem == null ? StringUtils.EMPTY : referenceItem.getName( );

            for ( Integer nIdUser : accountsToSetAsExpired )
            {
                try
                {
                    String strUserMail = accountLifeTimeService.getUserMainEmail( nIdUser );
                    if ( strUserMail != null && StringUtils.isNotBlank( strUserMail ) )
                    {
                        Map<String, String> model = new HashMap<String, String>( );
                        accountLifeTimeService.addParametersToModel( model, nIdUser );
                        HtmlTemplate template = AppTemplateService.getTemplateFromStringFtl( strBody,
                                Locale.getDefault( ), model );
                        MailService.sendMailHtml( strUserMail, strSender, strSender, strSubject, template.getHtml( ) );
                    }
                }
                catch ( Exception e )
                {
                    AppLogService.error( "MyluteceAccountLifeTimeDaemon - Error sending expiration alert to user : "
                                    + e.getMessage( ), e );
                }
            }
            accountLifeTimeService.setUserStatusExpired( accountsToSetAsExpired );

            sbLogs = new StringBuilder( );
            sbLogs.append( "MyluteceAccountLifeTimeDaemon - " );
            sbLogs.append( Integer.toString( nbAccountToExpire ) );
            sbLogs.append( " account(s) have expired" );
            AppLogService.info( sbLogs.toString( ) );
        }
        else
        {
            AppLogService.info( "MyluteceAccountLifeTimeDaemon - No expired user found" );
        }
        
        // We send first alert to users
        long nbDaysBeforeFirstAlert = SecurityUtils.getIntegerSecurityParameter( parameterService, plugin,
                PARAMETER_TIME_BEFORE_ALERT_ACCOUNT );

        Timestamp firstAlertMaxDate = new Timestamp( currentTimestamp.getTime( )
                + DateUtil.convertDaysInMiliseconds( nbDaysBeforeFirstAlert ) );
        if ( nbDaysBeforeFirstAlert <= 0 )
        {
            AppLogService.info( "MyluteceAccountLifeTimeDaemon - First alert deactivated, skipping" );
        }
        else
        {
            List<Integer> userIdListToSendFirstAlert = accountLifeTimeService
                    .getIdUsersToSendFirstAlert( firstAlertMaxDate );
            if ( userIdListToSendFirstAlert != null && userIdListToSendFirstAlert.size( ) > 0 )
            {
                int nbFirstAlertSent = userIdListToSendFirstAlert.size( );
                String strBody = accountLifeTimeService.getFirstAlertMailBody( );

                ReferenceItem referenceItem = parameterService.findByKey( PARAMETER_FIRST_ALERT_MAIL_SENDER, plugin );
                String strSender = referenceItem == null ? StringUtils.EMPTY : referenceItem.getName( );

                referenceItem = parameterService.findByKey( PARAMETER_FIRST_ALERT_MAIL_SUBJECT, plugin );
                String strSubject = referenceItem == null ? StringUtils.EMPTY : referenceItem.getName( );

                for ( Integer nIdUser : userIdListToSendFirstAlert )
                {
                    try
                    {
                        String strUserMail = accountLifeTimeService.getUserMainEmail( nIdUser );
                        if ( strUserMail != null && StringUtils.isNotBlank( strUserMail ) )
                        {
                            Map<String, String> model = new HashMap<String, String>( );
                            accountLifeTimeService.addParametersToModel( model, nIdUser );
                            HtmlTemplate template = AppTemplateService.getTemplateFromStringFtl( strBody,
                                    Locale.getDefault( ), model );
                            MailService
                                    .sendMailHtml( strUserMail, strSender, strSender, strSubject, template.getHtml( ) );
                        }
                    }
                    catch ( Exception e )
                    {
                        AppLogService
                                .error( "MyluteceAccountLifeTimeDaemon - Error sending first alert to user : "
                                        + e.getMessage( ), e );
                    }
                }

                accountLifeTimeService.updateNbAlert( userIdListToSendFirstAlert );

                sbLogs = new StringBuilder( );
                sbLogs.append( "MyluteceAccountLifeTimeDaemon - " );
                sbLogs.append( Integer.toString( nbFirstAlertSent ) );
                sbLogs.append( " first alert(s) have been sent" );
                AppLogService.info( sbLogs.toString( ) );
            }
            else
            {
                AppLogService.info( "MyluteceAccountLifeTimeDaemon - No first alert to send" );
            }
        }
        
        // We send other alert to users
        int maxNumberOfAlerts = SecurityUtils.getIntegerSecurityParameter( parameterService, plugin,
                PARAMETER_NB_ALERT_ACCOUNT );
        int nbDaysBetweenAlerts = SecurityUtils.getIntegerSecurityParameter( parameterService, plugin,
                PARAMETER_TIME_BETWEEN_ALERTS_ACCOUNT );
        Timestamp timeBetweenAlerts = new Timestamp( DateUtil.convertDaysInMiliseconds( nbDaysBetweenAlerts ) );

        if ( maxNumberOfAlerts <= 0 || nbDaysBetweenAlerts <= 0 )
        {
            AppLogService.info( "MyluteceAccountLifeTimeDaemon - Other alerts deactivated, skipping" );
        }
        else
        {
            List<Integer> userIdListToSendNextAlert = accountLifeTimeService.getIdUsersToSendOtherAlert(
                    firstAlertMaxDate, timeBetweenAlerts, maxNumberOfAlerts );
            if ( userIdListToSendNextAlert != null && userIdListToSendNextAlert.size( ) > 0 )
            {
                int nbOtherAlertSent = userIdListToSendNextAlert.size( );
                String strBody = accountLifeTimeService.getOtherAlertMailBody( );

                ReferenceItem referenceItem = parameterService.findByKey( PARAMETER_OTHER_ALERT_MAIL_SENDER, plugin );
                String strSender = referenceItem == null ? StringUtils.EMPTY : referenceItem.getName( );

                referenceItem = parameterService.findByKey( PARAMETER_OTHER_ALERT_MAIL_SUBJECT, plugin );
                String strSubject = referenceItem == null ? StringUtils.EMPTY : referenceItem.getName( );

                for ( Integer nIdUser : userIdListToSendNextAlert )
                {
                    try
                    {
                        String strUserMail = accountLifeTimeService.getUserMainEmail( nIdUser );
                        if ( strUserMail != null && StringUtils.isNotBlank( strUserMail ) )
                        {
                            Map<String, String> model = new HashMap<String, String>( );
                            accountLifeTimeService.addParametersToModel( model, nIdUser );
                            HtmlTemplate template = AppTemplateService.getTemplateFromStringFtl( strBody,
                                    Locale.getDefault( ), model );
                            MailService
                                    .sendMailHtml( strUserMail, strSender, strSender, strSubject, template.getHtml( ) );
                        }
                    }
                    catch ( Exception e )
                    {
                        AppLogService
                                .error( "MyluteceAccountLifeTimeDaemon - Error sending next alert to user : "
                                        + e.getMessage( ), e );
                    }
                }

                accountLifeTimeService.updateNbAlert( userIdListToSendNextAlert );

                sbLogs = new StringBuilder( );
                sbLogs.append( "MyluteceAccountLifeTimeDaemon - " );
                sbLogs.append( Integer.toString( nbOtherAlertSent ) );
                sbLogs.append( " next alert(s) have been sent" );
                AppLogService.info( sbLogs.toString( ) );
            }
            else
            {
                AppLogService.info( "MyluteceAccountLifeTimeDaemon - No next alert to send" );
            }
        }
    }
}
