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
package fr.paris.lutece.plugins.mylutece.web.security;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.mylutece.service.MyLuteceResourceIdService;
import fr.paris.lutece.plugins.mylutece.service.security.AuthenticationFilterService;
import fr.paris.lutece.portal.business.rbac.RBAC;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.admin.AdminFeaturesPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;


/**
 *
 * PublicUrlJspBean used for managing Public Url
 *
 */
public class AuthenticationFilterJspBean extends AdminFeaturesPageJspBean
{
    /**
    *
    */
    public static final String RIGHT_MANAGE_AUTHENTICATION_FILTER = "MYLUTECE_MANAGE_AUTHENTICATION_FILTER";
    private static final long serialVersionUID = -669562727518395523L;
   
    // Parameters
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_PUBLIC_URL_CODE = "public_url_code";
    private static final String PARAMETER_PUBLIC_URL_VALUE = "public_url_value";

    // Jsp url
    private static final String JSP_MANAGE_AUTHENTICATION_FILTER = "ManageAuthenticationFilter.jsp";
    private static final String JSP_DO_REMOVE_PUBLIC_URL = "jsp/admin/plugins/mylutece/security/DoRemovePublicUrl.jsp";
    private static final String JSP_DO_CHANGE_USER_AUTHENTICATION_REQUIRED = "jsp/admin/plugins/mylutece/security/DoChangeUseAuthenticationRequired.jsp";
    

    // Properties
    private static final String PROPERTY_MANAGE_AUTHENTICATION_FILTER = "mylutece.manage_authentication_filter.pageTitle";

    // Template
    private static final String TEMPLATE_MANAGE_AUTHENTICATION_FILTER = "admin/plugins/mylutece/security/manage_authentication_filter.html";

    //Message
    private static final String MESSAGE_PUBLIC_URL_CODE_ALREADY_EXIST = "mylutece.messagePublicUrlCodeAlreadyExist";
    private static final String MESSAGE_PUBLIC_URL_CONFIRM_REMOVE = "mylutece.messagePublicUrlConfirmRemove";
    private static final String MESSAGE_CONFIRM_ENABLE_AUTHENTICATION_REQUIRED = "mylutece.messageConfirmEnableAuthenticationRequired";
    private static final String MESSAGE_CONFIRM_DISABLE_AUTHENTICATION_REQUIRED = "mylutece.messageConfirmDisableAuthenticationRequired";
    
    private static final String CONSTANTE_PORTAL_AUTHENTICATION_REQUIRED = "mylutece.portal.authentication.required";
    

    /**
     * Builds the advanced parameters management page
     * @param request the HTTP request
     * @return the built page
     */
    public String getManageAdvancedParameters( HttpServletRequest request )throws AccessDeniedException
    {
    	if (!RBACService.isAuthorized( MyLuteceResourceIdService.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                MyLuteceResourceIdService.PERMISSION_MANAGE_AUTHENTICATION_FILTER, getUser(  ) ))
        {
            throw new AccessDeniedException( "User " + getUser(  ) + " is not authorized to permission " +
            		MyLuteceResourceIdService.PERMISSION_MANAGE_AUTHENTICATION_FILTER );
        }

        setPageTitleProperty( PROPERTY_MANAGE_AUTHENTICATION_FILTER );

        Map<String, Object> model = AuthenticationFilterService.getInstance(  ).getManageAdvancedParameters( getUser(  ), request );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_AUTHENTICATION_FILTER, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Create public Url
     * @param request the HTTP request
     * @return the jsp URL of the process result
     * @throws AccessDeniedException if permission to create Public Url
     * on security service has not been granted to the user
     */
    public String doCreatePublicUrl( HttpServletRequest request )
        throws AccessDeniedException
    {
        if (!RBACService.isAuthorized( MyLuteceResourceIdService.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                MyLuteceResourceIdService.PERMISSION_MANAGE_AUTHENTICATION_FILTER, getUser(  ) ))
        {
            throw new AccessDeniedException( "User " + getUser(  ) + " is not authorized to permission " +
            		MyLuteceResourceIdService.PERMISSION_MANAGE_AUTHENTICATION_FILTER );
        }

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            ReferenceItem publicUrlData = getPublicUrlData( request );
            normalizedPublicUrlCode( publicUrlData );

            String strError = StringUtils.EMPTY;

            if ( StringUtils.isBlank( publicUrlData.getCode(  ) ) || StringUtils.isBlank( publicUrlData.getName(  ) ) )
            {
                strError = AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
            }
            else if ( DatastoreService.getDataValue( publicUrlData.getCode(  ), null ) != null )
            {
                strError = AdminMessageService.getMessageUrl( request, MESSAGE_PUBLIC_URL_CODE_ALREADY_EXIST,
                        AdminMessage.TYPE_STOP );
            }

            if ( !StringUtils.isBlank( strError ) )
            {
                return strError;
            }

            //create public url
            DatastoreService.setDataValue( publicUrlData.getCode(  ), publicUrlData.getName(  ) );
        }

        return JSP_MANAGE_AUTHENTICATION_FILTER;
    }

    /**
     * Do Modify Public Url
     * @param request the HTTP request
     * @return the jsp URL of the process result
     * @throws AccessDeniedException if permission to Manage Public Url
     * on security service has not been granted to the user
     */
    public String doModifyPublicUrl( HttpServletRequest request )
        throws AccessDeniedException
    {
    	 if (!RBACService.isAuthorized( MyLuteceResourceIdService.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                 MyLuteceResourceIdService.PERMISSION_MANAGE_AUTHENTICATION_FILTER, getUser(  ) ))
        {
            throw new AccessDeniedException( "User " + getUser(  ) + " is not authorized to permission " +
            		MyLuteceResourceIdService.PERMISSION_MANAGE_AUTHENTICATION_FILTER );
        }

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            ReferenceItem publicUrlData = getPublicUrlData( request );
            normalizedPublicUrlCode( publicUrlData );

            String strError = StringUtils.EMPTY;

            if ( StringUtils.isBlank( publicUrlData.getCode(  ) ) || StringUtils.isBlank( publicUrlData.getName(  ) ) ||
                    ( DatastoreService.getDataValue( publicUrlData.getCode(  ), null ) == null ) )
            {
                strError = AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
            }

            if ( !StringUtils.isBlank( strError ) )
            {
                return strError;
            }

            //updateParameter
            DatastoreService.setDataValue( publicUrlData.getCode(  ), publicUrlData.getName(  ) );
        }

        return JSP_MANAGE_AUTHENTICATION_FILTER;
    }
    
    
    
    /**
     * Do change use authentication required
     * @param request the HTTP request
     * @return the jsp URL of the process result
     * @throws AccessDeniedException if permission to Manage Public Url
     * on security service has not been granted to the user
     */
    public String getConfirmChangeUseAuthenticationRequired( HttpServletRequest request )
        throws AccessDeniedException
    {
    	 if (!RBACService.isAuthorized( MyLuteceResourceIdService.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                 MyLuteceResourceIdService.PERMISSION_MANAGE_AUTHENTICATION_FILTER, getUser(  ) ))
        {
            throw new AccessDeniedException( "User " + getUser(  ) + " is not authorized to permission " +
            		MyLuteceResourceIdService.PERMISSION_MANAGE_AUTHENTICATION_FILTER );
        }
    	  	String strMessage=SecurityService.getInstance().isPortalAuthenticationRequired()?MESSAGE_CONFIRM_DISABLE_AUTHENTICATION_REQUIRED:MESSAGE_CONFIRM_ENABLE_AUTHENTICATION_REQUIRED;
    	  	UrlItem url = new UrlItem( JSP_DO_CHANGE_USER_AUTHENTICATION_REQUIRED );
            url.addParameter( PARAMETER_PUBLIC_URL_CODE, request.getParameter( PARAMETER_PUBLIC_URL_CODE ) );

            return AdminMessageService.getMessageUrl( request, strMessage, url.getUrl(  ),
                AdminMessage.TYPE_CONFIRMATION );
    }
    
       
    
    /**
     * Do change use authentication required
     * @param request the HTTP request
     * @return the jsp URL of the process result
     * @throws AccessDeniedException if permission to Manage Public Url
     * on security service has not been granted to the user
     */
    public String doChangeUseAuthenticationRequired( HttpServletRequest request )
        throws AccessDeniedException
    {
    	 if (!RBACService.isAuthorized( MyLuteceResourceIdService.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                 MyLuteceResourceIdService.PERMISSION_MANAGE_AUTHENTICATION_FILTER, getUser(  ) ))
        {
            throw new AccessDeniedException( "User " + getUser(  ) + " is not authorized to permission " +
            		MyLuteceResourceIdService.PERMISSION_MANAGE_AUTHENTICATION_FILTER );
        }

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
        	

            //updateParameter
        	
        	DatastoreService.setDataValue( CONSTANTE_PORTAL_AUTHENTICATION_REQUIRED, new Boolean(!SecurityService.getInstance().isPortalAuthenticationRequired()).toString());
        }

        return JSP_MANAGE_AUTHENTICATION_FILTER;
    }

    /**
     * Remove Public Url
     * @param request the HTTP request
     * @return the jsp URL of the process result
     * @throws AccessDeniedException if permission manage Public Url
     * on security service has not been granted to the user
     */
    public String doRemovePublicUrl( HttpServletRequest request )
        throws AccessDeniedException
    {
    	 if (!RBACService.isAuthorized( MyLuteceResourceIdService.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                 MyLuteceResourceIdService.PERMISSION_MANAGE_AUTHENTICATION_FILTER, getUser(  ) ))
        {
            throw new AccessDeniedException( "User " + getUser(  ) + " is not authorized to permission " +
            		MyLuteceResourceIdService.PERMISSION_MANAGE_AUTHENTICATION_FILTER);
        }

        ReferenceItem publicUrlData = getPublicUrlData( request );

        if ( publicUrlData != null )
        {
            normalizedPublicUrlCode( publicUrlData );
            DatastoreService.removeData( publicUrlData.getCode(  ) );
        }

        return JSP_MANAGE_AUTHENTICATION_FILTER;
    }

    /**
     * Get the Public Url Data
     * @param request The HTTP request
     * @return ReferenceItem
     */
    private ReferenceItem getPublicUrlData( HttpServletRequest request )
    {
        ReferenceItem publicUrlData = new ReferenceItem(  );
        String strPublicUrlCode = ( request.getParameter( PARAMETER_PUBLIC_URL_CODE ) != null )
            ? request.getParameter( PARAMETER_PUBLIC_URL_CODE ).trim(  ) : null;
        String strPublicUrlValue = ( request.getParameter( PARAMETER_PUBLIC_URL_VALUE ) != null )
            ? request.getParameter( PARAMETER_PUBLIC_URL_VALUE ).trim(  ) : null;
        publicUrlData.setCode( strPublicUrlCode );
        publicUrlData.setName( strPublicUrlValue );

        return publicUrlData;
    }

    /**
     * Gets the confirmation page of delete Public Url
     * @param request The HTTP request
     * @throws AccessDeniedException the {@link AccessDeniedException}
     * @return the confirmation page of Remove Public Url
     */
    public String getConfirmRemovePublicUrl( HttpServletRequest request )
        throws AccessDeniedException
    {
    	 if (!RBACService.isAuthorized( MyLuteceResourceIdService.RESOURCE_TYPE, RBAC.WILDCARD_RESOURCES_ID,
                 MyLuteceResourceIdService.PERMISSION_MANAGE_AUTHENTICATION_FILTER, getUser(  ) ))
        {
            throw new AccessDeniedException( "User " + getUser(  ) + " is not authorized to permission " +
            		MyLuteceResourceIdService.PERMISSION_MANAGE_AUTHENTICATION_FILTER );
        }

        UrlItem url = new UrlItem( JSP_DO_REMOVE_PUBLIC_URL );
        url.addParameter( PARAMETER_PUBLIC_URL_CODE, request.getParameter( PARAMETER_PUBLIC_URL_CODE ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_PUBLIC_URL_CONFIRM_REMOVE, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * normalized public url code
     * @param publicUrl publicUrlCode
     */
    private void normalizedPublicUrlCode( ReferenceItem publicUrl )
    {
        if ( !StringUtils.isBlank( publicUrl.getCode(  ) ) )
        {
            String strCode = publicUrl.getCode(  );
            strCode = strCode.replaceAll( " ", "_" );
            publicUrl.setCode( AuthenticationFilterService.PUBLIC_URL_PREFIX + strCode );
        }
    }
}
