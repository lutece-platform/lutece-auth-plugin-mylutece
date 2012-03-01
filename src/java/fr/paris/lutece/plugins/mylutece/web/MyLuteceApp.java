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
package fr.paris.lutece.plugins.mylutece.web;

import fr.paris.lutece.plugins.mylutece.authentication.MultiLuteceAuthentication;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.security.LoginRedirectException;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.PortalJspBean;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;


/**
 * This class provides the XPageApp that manage personalization features for Lutece
 * : login, account management, ...
 */
public class MyLuteceApp implements XPageApplication
{
    //////////////////////////////////////////////////////////////////////////////////
    // Constants

    // Markers
    private static final String MARK_ERROR_MESSAGE = "error_message";
    private static final String MARK_ERROR_DETAIL = "error_detail";
    private static final String MARK_URL_DOLOGIN = "url_dologin";
    private static final String MARK_LIST_AUTHENTICATIONS = "list_authentications";
    private static final String MARK_AUTH_PROVIDER = "auth_provider";

    // Parameters
    private static final String PARAMETER_ACTION = "action";
    private static final String PARAMETER_USERNAME = "username";
    private static final String PARAMETER_PASSWORD = "password";
    private static final String PARAMETER_ERROR = "error";
    private static final String PARAMETER_ERROR_VALUE_INVALID = "invalid";
    private static final String PARAMETER_ERROR_MSG = "error_msg";
    private static final String PARAMETER_AUTH_PROVIDER = "auth_provider";

    // Actions
    private static final String ACTION_LOGIN = "login";
    private static final String ACTION_CREATE_ACCOUNT = "createAccount";
    private static final String ACTION_VIEW_ACCOUNT = "viewAccount";
    private static final String ACTION_LOST_PASSWORD = "lostPassword";

    // Properties
    private static final String PROPERTY_MYLUTECE_PAGETITLE_LOGIN = "mylutece.pageTitle.login";
    private static final String PROPERTY_MYLUTECE_PATHLABEL_LOGIN = "mylutece.pagePathLabel.login";
    private static final String PROPERTY_MYLUTECE_MESSAGE_INVALID_LOGIN = "mylutece.message.error.invalid.login";
    private static final String PROPERTY_MYLUTECE_LOGIN_PAGE_URL = "mylutece.url.login.page";
    private static final String PROPERTY_MYLUTECE_DOLOGIN_URL = "mylutece.url.doLogin";
    private static final String PROPERTY_MYLUTECE_DOLOGOUT_URL = "mylutece.url.doLogout";
    private static final String PROPERTY_MYLUTECE_CREATE_ACCOUNT_URL = "mylutece.url.createAccount.page";
    private static final String PROPERTY_MYLUTECE_VIEW_ACCOUNT_URL = "mylutece.url.viewAccount.page";
    private static final String PROPERTY_MYLUTECE_LOST_PASSWORD_URL = "mylutece.url.lostPassword.page";
    private static final String PROPERTY_MYLUTECE_DEFAULT_REDIRECT_URL = "mylutece.url.default.redirect";
    private static final String PROPERTY_MYLUTECE_TEMPLATE_ACCESS_DENIED = "mylutece.template.accessDenied";
    private static final String PROPERTY_MYLUTECE_TEMPLATE_ACCESS_CONTROLED = "mylutece.template.accessControled";

    // i18n Properties
    private static final String PROPERTY_CREATE_ACCOUNT_LABEL = "mylutece.xpage.createAccountLabel";
    private static final String PROPERTY_CREATE_ACCOUNT_TITLE = "mylutece.xpage.createAccountTitle";
    private static final String PROPERTY_VIEW_ACCOUNT_LABEL = "mylutece.xpage.viewAccountLabel";
    private static final String PROPERTY_VIEW_ACCOUNT_TITLE = "mylutece.xpage.viewAccountTitle";
    private static final String PROPERTY_LOST_PASSWORD_LABEL = "mylutece.xpage.lostPasswordLabel";
    private static final String PROPERTY_LOST_PASSWORD_TITLE = "mylutece.xpage.lostPasswordTitle";

    // Templates
    private static final String TEMPLATE_LOGIN_PAGE = "skin/plugins/mylutece/login_form.html";
    private static final String TEMPLATE_LOGIN_MULTI_PAGE = "skin/plugins/mylutece/login_form_multi.html";
    
    private static final String TEMPLATE_LOST_PASSWORD_PAGE = "skin/plugins/mylutece/lost_password.html";
    private static final String TEMPLATE_CREATE_ACCOUNT_PAGE = "skin/plugins/mylutece/create_account.html";
    private static final String TEMPLATE_VIEW_ACCOUNT_PAGE = "skin/plugins/mylutece/view_account.html";
    private Locale _locale;

    /**
     * Constructor
     */
    public MyLuteceApp(  )
    {
    }

    /**
     * This method builds a XPage object corresponding to the request
     * @param request The HTTP request
     * @param nMode The mode
     * @param plugin The plugin object which belongs the App
     * @return The XPage object containing the page content
     */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
    {
        XPage page = new XPage(  );

        String strAction = request.getParameter( PARAMETER_ACTION );
        _locale = request.getLocale(  );

        if ( strAction.equals( ACTION_LOGIN ) )
        {
            return getLoginPage( page, request );
        }
        else if ( strAction.equals( ACTION_CREATE_ACCOUNT ) )
        {
            return getCreateAccountPage( page, request );
        }
        else if ( strAction.equals( ACTION_VIEW_ACCOUNT ) )
        {
            return getViewAccountPage( page );
        }
        else if ( strAction.equals( ACTION_LOST_PASSWORD ) )
        {
            return getLostPasswordPage( page );
        }

        return page;
    }

    /**
     * Build the Login page
     * @param page The XPage object to fill
     * @param request The HTTP request
     * @return The XPage object containing the page content
     */
    private XPage getLoginPage( XPage page, HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        String strError = request.getParameter( PARAMETER_ERROR );
        String strErrorMessage = "";
        String strErrorDetail = "";

        if ( ( strError != null ) && ( strError.equals( PARAMETER_ERROR_VALUE_INVALID ) ) )
        {
            strErrorMessage = AppPropertiesService.getProperty( PROPERTY_MYLUTECE_MESSAGE_INVALID_LOGIN );

            if ( request.getParameter( PARAMETER_ERROR_MSG ) != null )
            {
                strErrorDetail = request.getParameter( PARAMETER_ERROR_MSG );
            }
        }

        model.put( MARK_ERROR_MESSAGE, strErrorMessage );
        model.put( MARK_ERROR_DETAIL, strErrorDetail );
        model.put( MARK_URL_DOLOGIN, getDoLoginUrl(  ) );
        model.put( MARK_AUTH_PROVIDER, request.getParameter( PARAMETER_AUTH_PROVIDER ) );

        HtmlTemplate template;
        
        if ( SecurityService.getInstance().isMultiAuthenticationSupported(  ) )
        {
        	LuteceAuthentication luteceAuthentication = SecurityService.getInstance().getAuthenticationService(  );
    		if ( luteceAuthentication instanceof MultiLuteceAuthentication )
    		{
    			model.put(  MARK_LIST_AUTHENTICATIONS, ( ( MultiLuteceAuthentication ) luteceAuthentication ).getListLuteceAuthentication(  ) );
    		}
        	template = AppTemplateService.getTemplate( TEMPLATE_LOGIN_MULTI_PAGE, _locale, model );
        }
        else
        {
        	template = AppTemplateService.getTemplate( TEMPLATE_LOGIN_PAGE, _locale, model );
        }

        page.setContent( template.getHtml(  ) );
        page.setPathLabel( AppPropertiesService.getProperty( PROPERTY_MYLUTECE_PATHLABEL_LOGIN ) );
        page.setTitle( AppPropertiesService.getProperty( PROPERTY_MYLUTECE_PAGETITLE_LOGIN ) );

        return page;
    }

    /**
     * This method is call by the JSP named DoMyLuteceLogin.jsp
     * @param request The HTTP request
     * @return The URL to forward depending of the result of the login.
     */
    public String doLogin( HttpServletRequest request )
    {
        String strUsername = request.getParameter( PARAMETER_USERNAME );
        String strPassword = request.getParameter( PARAMETER_PASSWORD );
        String strAuthProvider = request.getParameter( PARAMETER_AUTH_PROVIDER );

        try
        {
            SecurityService.getInstance(  ).loginUser( request, strUsername, strPassword );
        }
        catch ( LoginRedirectException ex )
        {
            return ex.getRedirectUrl(  );
        }
        catch ( FailedLoginException ex )
        {
            String strReturn = "../../" + getLoginPageUrl(  ) + "&" + PARAMETER_ERROR + "=" +
                PARAMETER_ERROR_VALUE_INVALID;
            
            if ( StringUtils.isNotBlank( strAuthProvider ) )
        	{
        		strReturn += "&" + PARAMETER_AUTH_PROVIDER + "=" + strAuthProvider;
        	}

            if ( ex.getMessage(  ) != null )
            {
                String strMessage = "&" + PARAMETER_ERROR_MSG + "=" + ex.getMessage(  );
                strReturn += strMessage;
            }

            return strReturn;
        }
        catch ( LoginException ex )
        {
            String strReturn = "../../" + getLoginPageUrl(  ) + "&" + PARAMETER_ERROR + "=" +
                PARAMETER_ERROR_VALUE_INVALID;
            
            if ( StringUtils.isNotBlank( strAuthProvider ) )
        	{
        		strReturn += "&" + PARAMETER_AUTH_PROVIDER + "=" +
        		strAuthProvider;
        	}

            if ( ex.getMessage(  ) != null )
            {
                String strMessage = "&" + PARAMETER_ERROR_MSG + "=" + ex.getMessage(  );
                strReturn += strMessage;
            }

            return strReturn;
        }

        String strNextUrl = PortalJspBean.getLoginNextUrl( request );

        if ( strNextUrl != null )
        {
            return strNextUrl;
        }

        return getDefaultRedirectUrl(  );
    }

    /**
     * Returns the Login page URL of the Authentication Service
     * @return The URL
     */
    public static String getLoginPageUrl(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_LOGIN_PAGE_URL );
    }

    /**
     * Returns the DoLogin URL of the Authentication Service
     * @return The URL
     */
    public static String getDoLoginUrl(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_DOLOGIN_URL );
    }

    /**
     * Returns the DoLogout URL of the Authentication Service
     * @return The URL
     */
    public static String getDoLogoutUrl(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_DOLOGOUT_URL );
    }

    /**
     * Returns the NewAccount URL of the Authentication Service
     * @return The URL
     */
    public static String getNewAccountUrl(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_CREATE_ACCOUNT_URL );
    }

    /**
     * Returns the ViewAccount URL of the Authentication Service
     * @return The URL
     */
    public static String getViewAccountUrl(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_VIEW_ACCOUNT_URL );
    }

    /**
     * Returns the Lost Password URL of the Authentication Service
     * @return The URL
     */
    public static String getLostPasswordUrl(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_LOST_PASSWORD_URL );
    }

    /**
     * Returns the Default redirect URL of the Authentication Service
     * @return The URL
     */
    public static String getDefaultRedirectUrl(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_DEFAULT_REDIRECT_URL );
    }

    /**
     * This method is call by the JSP named DoMyLuteceLogout.jsp
     * @param request The HTTP request
     * @return The URL to forward depending of the result of the login.
     */
    public String doLogout( HttpServletRequest request )
    {
        SecurityService.getInstance(  ).logoutUser( request );

        return getDefaultRedirectUrl(  );
    }

    /**
     * Build the CreateAccount page
     * @param page The XPage object to fill
     * @param request the request
     * @return The XPage object containing the page content
     */
    private XPage getCreateAccountPage( XPage page, HttpServletRequest request )
    {
    	Map<String, Object> model = new HashMap<String, Object>(  );
    	if ( SecurityService.getInstance().isMultiAuthenticationSupported(  ) ) 
    	{
    		model.put( MARK_LIST_AUTHENTICATIONS , 
    				( (MultiLuteceAuthentication) SecurityService.getInstance().getAuthenticationService(  ) ).getListLuteceAuthentication(  ) );
    	}
    	
        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_CREATE_ACCOUNT_PAGE, _locale, model );
        page.setContent( t.getHtml(  ) );
        //	page.setPathLabel( "Create Account" );
        //		page.setTitle( "Create Account" );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_CREATE_ACCOUNT_LABEL, _locale ) );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_CREATE_ACCOUNT_TITLE, _locale ) );

        return page;
    }

    /**
     * Build the ViewAccount page
     * @param page The XPage object to fill
     * @return The XPage object containing the page content
     */
    private XPage getViewAccountPage( XPage page )
    {
        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_VIEW_ACCOUNT_PAGE, _locale );
        page.setContent( t.getHtml(  ) );
        //	page.setPathLabel( "View Account" );
        //page.setTitle( "View Account" );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_VIEW_ACCOUNT_LABEL, _locale ) );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_VIEW_ACCOUNT_TITLE, _locale ) );

        return page;
    }

    /**
     * Build the default Lost password page
     * @param page The XPage object to fill
     * @return The XPage object containing the page content
     */
    private XPage getLostPasswordPage( XPage page )
    {
        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_LOST_PASSWORD_PAGE, _locale );
        page.setContent( t.getHtml(  ) );
        //	page.setPathLabel( "Lost password" );
        //	page.setTitle( "Lost password" );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_LOST_PASSWORD_LABEL, _locale ) );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_LOST_PASSWORD_TITLE, _locale ) );

        return page;
    }

    /**
     * Returns the template for access denied
     * @return The template path
     */
    public static String getAccessDeniedTemplate(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_TEMPLATE_ACCESS_DENIED );
    }

    /**
     * Returns the template for access controled
     * @return The template path
     */
    public static String getAccessControledTemplate(  )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_TEMPLATE_ACCESS_CONTROLED );
    }
}
