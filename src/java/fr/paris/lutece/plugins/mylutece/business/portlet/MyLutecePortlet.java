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
package fr.paris.lutece.plugins.mylutece.business.portlet;

import fr.paris.lutece.plugins.mylutece.authentication.MultiLuteceAuthentication;
import fr.paris.lutece.portal.business.portlet.PortletHtmlContent;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

/**
 * MyLutecePortlet
 */
public class MyLutecePortlet extends PortletHtmlContent
{
    private static final String TEMPLATE_PORTLET_MYLUTECE = "skin/plugins/mylutece/portlet/portlet_mylutece.html";
    private static final String TOKEN_ACTION_LOGIN = "dologin";
    private static final String MARK_PORTLET_NAME = "portlet_name";
    private static final String MARK_PORTLET_ID = "portlet_id";
    private static final String MARK_USER = "user";
    private static final String MARK_LIST_AUTHENTICATIONS = "list_authentications";
    private static final String MARK_DO_LOGIN = "url_dologin";
    private static final String MARK_DO_LOGOUT = "url_dologout";
    private static final String MARK_URL_ACCOUNT = "url_account";
    private static final String MARK_URL_NEWACCOUNT = "url_new_account";
    private static final String MARK_URL_LOSTPASSWORD = "url_lost_password";

    /**
     * Constructor
     */
    public MyLutecePortlet( )
    {
        setPortletTypeId( MyLutecePortletHome.getInstance( ).getPortletTypeId( ) );
    }

    /**
     * Returns the HTML content of the MyLutece portlet
     *
     * @param request
     *            The HTTP Servlet request
     * @return the HTML content of the MyLutece portlet
     */
    @Override
    public String getHtmlContent( HttpServletRequest request )
    {
        if ( !SecurityService.isAuthenticationEnable( ) || ( request == null ) )
        {
            return StringUtils.EMPTY;
        }

        Map<String, Object> model = new HashMap<>( );

        if ( this.getDisplayPortletTitle( ) == 0 )
        {
            model.put( MARK_PORTLET_NAME, this.getName( ) );
        }

        model.put( MARK_PORTLET_ID, this.getId( ) );

        LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
        model.put( MARK_USER, user );
        model.put( MARK_DO_LOGIN, SecurityService.getInstance( ).getDoLoginUrl( ) );
        model.put( MARK_DO_LOGOUT, SecurityService.getInstance( ).getDoLogoutUrl( ) );
        model.put( MARK_URL_ACCOUNT, SecurityService.getInstance( ).getViewAccountPageUrl( ) );
        model.put( MARK_URL_NEWACCOUNT, SecurityService.getInstance( ).getNewAccountPageUrl( ) );
        model.put( MARK_URL_LOSTPASSWORD, SecurityService.getInstance( ).getLostPasswordPageUrl( ) );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, TOKEN_ACTION_LOGIN ) );

        LuteceAuthentication luteceAuthentication = SecurityService.getInstance( ).getAuthenticationService( );

        if ( SecurityService.getInstance( ).isMultiAuthenticationSupported( ) && luteceAuthentication instanceof MultiLuteceAuthentication )
        {
            model.put( MARK_LIST_AUTHENTICATIONS, ( (MultiLuteceAuthentication) luteceAuthentication ).getListLuteceAuthentication( ) );
        }
        else
        {
            model.put( MARK_LIST_AUTHENTICATIONS, Collections.singletonList( luteceAuthentication ) );
        }

        Locale locale = request.getLocale( );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PORTLET_MYLUTECE, locale, model );

        return template.getHtml( );
    }

    /**
     * Update portlet's data
     */
    public void update( )
    {
        MyLutecePortletHome.getInstance( ).update( this );
    }

    /**
     * Remove of this portlet
     */
    @Override
    public void remove( )
    {
        MyLutecePortletHome.getInstance( ).remove( this );
    }
}
