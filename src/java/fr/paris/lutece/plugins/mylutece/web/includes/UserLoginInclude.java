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
package fr.paris.lutece.plugins.mylutece.web.includes;

import fr.paris.lutece.plugins.mydashboard.modules.favorites.business.Favorite;
import fr.paris.lutece.plugins.mydashboard.modules.favorites.service.FavoriteService;
import fr.paris.lutece.plugins.mylutece.authentication.MultiLuteceAuthentication;
import fr.paris.lutece.plugins.mylutece.web.MyLuteceApp;
import fr.paris.lutece.plugins.subscribe.business.Subscription;
import fr.paris.lutece.plugins.subscribe.business.SubscriptionFilter;
import fr.paris.lutece.plugins.subscribe.service.SubscriptionService;
import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.content.XPageAppService;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

/**
 * This class provide user login form in a UserLoginInclude
 *
 */
public class UserLoginInclude implements PageInclude
{
    private static final String TOKEN_ACTION_LOGIN = "dologin";
    private static final String MARK_USERLOGIN = "pageinclude_userlogin";
    private static final String MARK_USERLOGIN_TITLE = "pageinclude_userlogin_title";
    private static final String MARK_LIST_AUTHENTICATIONS = "list_authentications";
    private static final String MARK_URL_NEWACCOUNT = "url_new_account";
    private static final String TEMPLATE_USER_LOGIN_TITLE = "/skin/plugins/mylutece/includes/user_login_title.html";
    private static final String TEMPLATE_USER_LOGIN_TITLE_LOGGED = "/skin/plugins/mylutece/includes/user_login_title_logged.html";
    private static final String TEMPLATE_USER_LOGIN_FORM = "/skin/plugins/mylutece/includes/user_login_include.html";
    private static final String TEMPLATE_USER_LOGIN_MULTI_FORM = "/skin/plugins/mylutece/includes/user_login_multi_include.html";
    private static final String MARK_USER = "user";
    private static final String MARK_DO_LOGIN = "url_dologin";
    private static final String MARK_DO_LOGOUT = "url_dologout";
    private static final String MARK_URL_ACCOUNT = "url_account";
    private static final String MARK_FAVORITES = "favorites";
    
    private static final String PARAMETER_XPAGE_MYLUTECE = "mylutece";
    
    private static final String FAVORITES_PROVIDER_NAME = "FAVORITES_PROVIDER";
    
    private static final String PROPERTY_NUMBER_OF_FAVORITES = "mylutece.menu.numberOfFavorites.show";


    /**
     * Substitue specific Freemarker markers in the page template.
     * 
     * @param rootModel
     *            the HashMap containing markers to substitute
     * @param data
     *            A PageData object containing applications data
     * @param nMode
     *            The current mode
     * @param request
     *            The HTTP request
     */
    public void fillTemplate( Map<String, Object> rootModel, PageData data, int nMode, HttpServletRequest request )
    {
        String strUserLoginTitle = "<!-- no authenticated user -->";
        String strUserLoginForm = "<!-- no authenticated user -->";

        Map<String, Object> model = new HashMap<String, Object>( );

        if ( SecurityService.isAuthenticationEnable( ) )
        {
            if ( request != null )
            {
                LuteceUser user = SecurityService.getInstance( ).getRegisteredUser( request );
                model.put( MARK_DO_LOGIN, SecurityService.getInstance( ).getDoLoginUrl( ) );
                model.put( MARK_DO_LOGOUT, SecurityService.getInstance( ).getDoLogoutUrl( ) );
                model.put( MARK_URL_ACCOUNT, SecurityService.getInstance( ).getViewAccountPageUrl( ) );
                // Add Token
                model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, TOKEN_ACTION_LOGIN ) );

                if ( user != null )
                {
                    model.put( MARK_USER, user );
                    
                    addFavoritesToModel( model, user );                    
                    
                    HtmlTemplate tTitle = AppTemplateService.getTemplate( TEMPLATE_USER_LOGIN_TITLE_LOGGED, request.getLocale( ), model );
                    strUserLoginTitle = tTitle.getHtml( );
                }
                else
                {
                    String strPage = request.getParameter( XPageAppService.PARAM_XPAGE_APP );

                    if ( ( strPage == null ) || !strPage.equals( PARAMETER_XPAGE_MYLUTECE ) )
                    {
                        MyLuteceApp.setCurrentUrl( request );
                    }

                    HtmlTemplate tTitle = AppTemplateService.getTemplate( TEMPLATE_USER_LOGIN_TITLE, request.getLocale( ), model );
                    strUserLoginTitle = tTitle.getHtml( );
                }

                HtmlTemplate t;
                model.put( MARK_URL_NEWACCOUNT, SecurityService.getInstance( ).getAuthenticationService( ).getNewAccountPageUrl( ) );

                if ( SecurityService.getInstance( ).isMultiAuthenticationSupported( ) )
                {
                    LuteceAuthentication luteceAuthentication = SecurityService.getInstance( ).getAuthenticationService( );

                    if ( luteceAuthentication instanceof MultiLuteceAuthentication )
                    {
                        model.put( MARK_LIST_AUTHENTICATIONS, ( (MultiLuteceAuthentication) luteceAuthentication ).getListLuteceAuthentication( ) );
                    }

                    t = AppTemplateService.getTemplate( TEMPLATE_USER_LOGIN_MULTI_FORM, request.getLocale( ), model );
                }
                else
                {
                    t = AppTemplateService.getTemplate( TEMPLATE_USER_LOGIN_FORM, request.getLocale( ), model );
                }

                strUserLoginForm = t.getHtml( );
                rootModel.put( "LUTECE_USER", user );
            }
        }

        rootModel.put( MARK_USERLOGIN_TITLE, strUserLoginTitle );
        rootModel.put( MARK_USERLOGIN, strUserLoginForm );

    }

    private void addFavoritesToModel( Map<String, Object> model, LuteceUser user )
    {
        int nNbFavoritesShow = AppPropertiesService.getPropertyInt( PROPERTY_NUMBER_OF_FAVORITES, 4 );

        SubscriptionFilter sFilter = new SubscriptionFilter( );
        sFilter.setIdSubscriber( user.getName( ) );
        sFilter.setSubscriptionProvider( FAVORITES_PROVIDER_NAME );
                
        List<Subscription> listFavorites = SubscriptionService.getInstance( ).findByFilter( sFilter );
        if( !listFavorites.isEmpty( ) )
        {
            listFavorites = listFavorites.stream( ).sorted( Comparator.comparing( Subscription::getOrder ) ).limit( nNbFavoritesShow ).collect( Collectors.toList() );
            
            List<Favorite> listFavoritesSuscribed = new ArrayList<>( );
            for ( Subscription sub : listFavorites )
            {
                Favorite favorite = FavoriteService.getInstance( ).findByPrimaryKey( Integer.parseInt( sub.getIdSubscribedResource( ) ) );
                favorite.setOrder( sub.getOrder( ) );
                listFavoritesSuscribed.add( favorite );
            }    
            model.put( MARK_FAVORITES, listFavoritesSuscribed);
        }
    }
}
