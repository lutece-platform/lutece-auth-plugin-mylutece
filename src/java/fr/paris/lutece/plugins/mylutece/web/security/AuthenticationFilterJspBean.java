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
package fr.paris.lutece.plugins.mylutece.web.security;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.mylutece.service.MyLuteceResourceIdService;
import fr.paris.lutece.plugins.mylutece.service.security.AuthenticationFilterService;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.url.UrlItem;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Management of the authentication filter of the front office: the urls that stay public, and whether the whole site requires an
 * authentication. Every mutation is an action posted with the security token of the view that shows its form.
 */
@RequestScoped
@Named
@Controller( controllerJsp = AuthenticationFilterJspBean.CONTROLLER_JSP, controllerPath = AuthenticationFilterJspBean.CONTROLLER_PATH, right = AuthenticationFilterJspBean.RIGHT_MANAGE_AUTHENTICATION_FILTER, securityTokenEnabled = true )
public class AuthenticationFilterJspBean extends MVCAdminJspBean
{
    /**
     * Right to manage the authentication filter
     */
    public static final String RIGHT_MANAGE_AUTHENTICATION_FILTER = "MYLUTECE_MANAGE_AUTHENTICATION_FILTER";

    /**
     * Token of the management page, shared by the creation and the modification of the public urls posted from it. The admin dashboard,
     * which shows the same forms, issues a token of this name too
     */
    public static final String TOKEN_PUBLIC_URLS = "publicUrls";
    static final String CONTROLLER_JSP = "ManageAuthenticationFilter.jsp";
    static final String CONTROLLER_PATH = "jsp/admin/plugins/mylutece/security/";
    private static final long serialVersionUID = -669562727518395523L;

    // Views
    private static final String VIEW_MANAGE_AUTHENTICATION_FILTER = "manageAuthenticationFilter";
    private static final String VIEW_CONFIRM_REMOVE_PUBLIC_URL = "confirmRemovePublicUrl";
    private static final String VIEW_CONFIRM_CHANGE_USE_AUTHENTICATION_REQUIRED = "confirmChangeUseAuthenticationRequired";

    // Actions
    private static final String ACTION_CREATE_PUBLIC_URL = "createPublicUrl";
    private static final String ACTION_MODIFY_PUBLIC_URL = "modifyPublicUrl";
    private static final String ACTION_REMOVE_PUBLIC_URL = "removePublicUrl";
    private static final String ACTION_CHANGE_USE_AUTHENTICATION_REQUIRED = "changeUseAuthenticationRequired";

    // Parameters
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_PUBLIC_URL_CODE = "public_url_code";
    private static final String PARAMETER_PUBLIC_URL_VALUE = "public_url_value";

    // Properties
    private static final String PROPERTY_MANAGE_AUTHENTICATION_FILTER = "mylutece.manage_authentication_filter.pageTitle";

    // Template
    private static final String TEMPLATE_MANAGE_AUTHENTICATION_FILTER = "admin/plugins/mylutece/security/manage_authentication_filter.html";

    // Messages
    private static final String MESSAGE_PUBLIC_URL_CODE_ALREADY_EXIST = "mylutece.messagePublicUrlCodeAlreadyExist";
    private static final String MESSAGE_PUBLIC_URL_CONFIRM_REMOVE = "mylutece.messagePublicUrlConfirmRemove";
    private static final String MESSAGE_CONFIRM_ENABLE_AUTHENTICATION_REQUIRED = "mylutece.messageConfirmEnableAuthenticationRequired";
    private static final String MESSAGE_CONFIRM_DISABLE_AUTHENTICATION_REQUIRED = "mylutece.messageConfirmDisableAuthenticationRequired";
    private static final String CONSTANTE_PORTAL_AUTHENTICATION_REQUIRED = "mylutece.portal.authentication.required";

    /**
     * Get the management page of the authentication filter
     *
     * @param request
     *            the HTTP request
     * @param model
     *            the model of the page
     * @return the html of the page
     * @throws AccessDeniedException
     *             if the user may not manage the authentication filter
     */
    @View( value = VIEW_MANAGE_AUTHENTICATION_FILTER, defaultView = true, securityTokenAction = TOKEN_PUBLIC_URLS )
    public String getManageAuthenticationFilter( HttpServletRequest request, Models model ) throws AccessDeniedException
    {
        checkPermission( );

        for ( Map.Entry<String, Object> entry : AuthenticationFilterService.getInstance( ).getManageAdvancedParameters( getUser( ), request ).entrySet( ) )
        {
            model.put( entry.getKey( ), entry.getValue( ) );
        }

        return getPage( PROPERTY_MANAGE_AUTHENTICATION_FILTER, TEMPLATE_MANAGE_AUTHENTICATION_FILTER );
    }

    /**
     * Create a public url
     *
     * @param request
     *            the HTTP request
     * @return the url of the next page
     * @throws AccessDeniedException
     *             if the user may not manage the authentication filter
     */
    @Action( value = ACTION_CREATE_PUBLIC_URL, securityTokenAction = TOKEN_PUBLIC_URLS )
    public String doCreatePublicUrl( HttpServletRequest request ) throws AccessDeniedException
    {
        checkPermission( );

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            ReferenceItem publicUrlData = getPublicUrlData( request );
            String strError = null;

            if ( StringUtils.isBlank( publicUrlData.getCode( ) ) || StringUtils.isBlank( publicUrlData.getName( ) ) )
            {
                strError = AdminMessageService.getMessageUrlReturningTo( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP, getManageUrl( request ) );
            }
            else
                if ( DatastoreService.getDataValue( publicUrlData.getCode( ), null ) != null )
                {
                    strError = AdminMessageService.getMessageUrlReturningTo( request, MESSAGE_PUBLIC_URL_CODE_ALREADY_EXIST, AdminMessage.TYPE_STOP,
                            getManageUrl( request ) );
                }

            if ( strError != null )
            {
                return redirect( request, strError );
            }

            DatastoreService.setDataValue( publicUrlData.getCode( ), publicUrlData.getName( ) );
        }

        return redirectView( request, VIEW_MANAGE_AUTHENTICATION_FILTER );
    }

    /**
     * Modify a public url
     *
     * @param request
     *            the HTTP request
     * @return the url of the next page
     * @throws AccessDeniedException
     *             if the user may not manage the authentication filter
     */
    @Action( value = ACTION_MODIFY_PUBLIC_URL, securityTokenAction = TOKEN_PUBLIC_URLS )
    public String doModifyPublicUrl( HttpServletRequest request ) throws AccessDeniedException
    {
        checkPermission( );

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            ReferenceItem publicUrlData = getPublicUrlData( request );

            if ( StringUtils.isBlank( publicUrlData.getCode( ) ) || StringUtils.isBlank( publicUrlData.getName( ) )
                    || ( DatastoreService.getDataValue( publicUrlData.getCode( ), null ) == null ) )
            {
                return redirect( request,
                        AdminMessageService.getMessageUrlReturningTo( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP, getManageUrl( request ) ) );
            }

            DatastoreService.setDataValue( publicUrlData.getCode( ), publicUrlData.getName( ) );
        }

        return redirectView( request, VIEW_MANAGE_AUTHENTICATION_FILTER );
    }

    /**
     * Ask the confirmation of the removal of a public url
     *
     * @param request
     *            the HTTP request
     * @return the url of the confirmation message
     * @throws AccessDeniedException
     *             if the user may not manage the authentication filter
     */
    @View( value = VIEW_CONFIRM_REMOVE_PUBLIC_URL, securityTokenAction = ACTION_REMOVE_PUBLIC_URL )
    public String getConfirmRemovePublicUrl( HttpServletRequest request ) throws AccessDeniedException
    {
        return redirect( request, getConfirmRemovePublicUrlMessage( request ) );
    }

    /**
     * Get the confirmation message of the removal of a public url
     *
     * @param request
     *            the HTTP request
     * @return the url of the confirmation message, whose button posts the removal of the url of the exact code given
     * @throws AccessDeniedException
     *             if the user may not manage the authentication filter
     */
    String getConfirmRemovePublicUrlMessage( HttpServletRequest request ) throws AccessDeniedException
    {
        checkPermission( );

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_PUBLIC_URL ) );
        url.addParameter( PARAMETER_PUBLIC_URL_CODE,
                URLEncoder.encode( StringUtils.defaultString( request.getParameter( PARAMETER_PUBLIC_URL_CODE ) ), StandardCharsets.UTF_8 ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_PUBLIC_URL_CONFIRM_REMOVE, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Remove a public url
     *
     * @param request
     *            the HTTP request
     * @return the url of the management page
     * @throws AccessDeniedException
     *             if the user may not manage the authentication filter
     */
    @Action( ACTION_REMOVE_PUBLIC_URL )
    public String doRemovePublicUrl( HttpServletRequest request ) throws AccessDeniedException
    {
        checkPermission( );

        ReferenceItem publicUrlData = getPublicUrlData( request );

        if ( StringUtils.isNotBlank( publicUrlData.getCode( ) ) )
        {
            DatastoreService.removeData( publicUrlData.getCode( ) );
        }

        return redirectView( request, VIEW_MANAGE_AUTHENTICATION_FILTER );
    }

    /**
     * Ask the confirmation of switching the authentication required on the whole site, on or off
     *
     * @param request
     *            the HTTP request
     * @return the url of the confirmation message
     * @throws AccessDeniedException
     *             if the user may not manage the authentication filter
     */
    @View( value = VIEW_CONFIRM_CHANGE_USE_AUTHENTICATION_REQUIRED, securityTokenAction = ACTION_CHANGE_USE_AUTHENTICATION_REQUIRED )
    public String getConfirmChangeUseAuthenticationRequired( HttpServletRequest request ) throws AccessDeniedException
    {
        checkPermission( );

        String strMessage = SecurityService.getInstance( ).isPortalAuthenticationRequired( ) ? MESSAGE_CONFIRM_DISABLE_AUTHENTICATION_REQUIRED
                : MESSAGE_CONFIRM_ENABLE_AUTHENTICATION_REQUIRED;

        return redirect( request,
                AdminMessageService.getMessageUrl( request, strMessage, getActionUrl( ACTION_CHANGE_USE_AUTHENTICATION_REQUIRED ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Switch the authentication required on the whole site, on or off
     *
     * @param request
     *            the HTTP request
     * @return the url of the management page
     * @throws AccessDeniedException
     *             if the user may not manage the authentication filter
     */
    @Action( ACTION_CHANGE_USE_AUTHENTICATION_REQUIRED )
    public String doChangeUseAuthenticationRequired( HttpServletRequest request ) throws AccessDeniedException
    {
        checkPermission( );

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            DatastoreService.setDataValue( CONSTANTE_PORTAL_AUTHENTICATION_REQUIRED,
                    Boolean.toString( !SecurityService.getInstance( ).isPortalAuthenticationRequired( ) ) );
        }

        return redirectView( request, VIEW_MANAGE_AUTHENTICATION_FILTER );
    }

    /**
     * Check that the user may manage the authentication filter
     *
     * @throws AccessDeniedException
     *             if permission to manage the authentication filter has not been granted to the user
     */
    private void checkPermission( ) throws AccessDeniedException
    {
        if ( !RBACService.isAuthorized( MyLuteceResourceIdService.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                MyLuteceResourceIdService.PERMISSION_MANAGE_AUTHENTICATION_FILTER, getUser( ) ) )
        {
            throw new AccessDeniedException(
                    "User " + getUser( ) + " is not authorized to permission " + MyLuteceResourceIdService.PERMISSION_MANAGE_AUTHENTICATION_FILTER );
        }
    }

    /**
     * Get the absolute url of the management page
     *
     * @param request
     *            the HTTP request
     * @return the url
     */
    private String getManageUrl( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + getViewFullUrl( VIEW_MANAGE_AUTHENTICATION_FILTER );
    }

    /**
     * Get the public url described by the request, its code normalized and prefixed as it is stored
     *
     * @param request
     *            the HTTP request
     * @return the code (stored key) and the name (url) of the public url
     */
    private ReferenceItem getPublicUrlData( HttpServletRequest request )
    {
        ReferenceItem publicUrlData = new ReferenceItem( );
        String strCode = StringUtils.trimToNull( request.getParameter( PARAMETER_PUBLIC_URL_CODE ) );
        publicUrlData.setCode( ( strCode != null ) ? AuthenticationFilterService.PUBLIC_URL_PREFIX + strCode.replace( " ", "_" ) : null );
        publicUrlData.setName( StringUtils.trimToNull( request.getParameter( PARAMETER_PUBLIC_URL_VALUE ) ) );

        return publicUrlData;
    }
}
