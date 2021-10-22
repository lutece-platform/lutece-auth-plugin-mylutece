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
package fr.paris.lutece.plugins.mylutece.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.mylutece.authentication.MultiLuteceAuthentication;
import fr.paris.lutece.plugins.mylutece.authentication.logs.ConnectionLog;
import fr.paris.lutece.plugins.mylutece.authentication.logs.ConnectionLogHome;
import fr.paris.lutece.plugins.mylutece.service.MyLutecePlugin;
import fr.paris.lutece.portal.service.captcha.CaptchaSecurityService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.FailedLoginCaptchaException;
import fr.paris.lutece.portal.service.security.LoginRedirectException;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.util.CryptoService;
import fr.paris.lutece.portal.web.PortalJspBean;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.http.SecurityUtil;
import fr.paris.lutece.util.json.AbstractJsonResponse;
import fr.paris.lutece.util.json.JsonResponse;
import fr.paris.lutece.util.json.JsonUtil;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the XPageApp that manage personalization features for Lutece : login, account management, ...
 */
public class MyLuteceApp implements XPageApplication
{
    //////////////////////////////////////////////////////////////////////////////////
    // Constants
    private static final String ATTRIBUTE_CURRENT_URL = "luteceCurrentUrl";
    private static final String TOKEN_ACTION_LOGIN = "dologin";
    // Markers
    private static final String MARK_ERROR_MESSAGE = "error_message";
    private static final String MARK_ERROR_DETAIL = "error_detail";
    private static final String MARK_URL_DOLOGIN = "url_dologin";
    private static final String MARK_URL_NEWACCOUNT = "url_new_account";
    private static final String MARK_LIST_AUTHENTICATIONS = "list_authentications";
    private static final String MARK_AUTH_PROVIDER = "auth_provider";
    private static final String MARK_IS_ACTIVE_CAPTCHA = "is_active_captcha";
    private static final String MARK_CAPTCHA = "captcha";

    // Parameters
    private static final String PARAMETER_ACTION = "action";
    private static final String PARAMETER_USERNAME = "username";
    private static final String PARAMETER_PASSWORD = "password";
    private static final String PARAMETER_ERROR = "error";
    private static final String PARAMETER_ERROR_VALUE_INVALID = "invalid";
    private static final String PARAMETER_ERROR_MSG = "error_msg";
    private static final String PARAMETER_AUTH_PROVIDER = "auth_provider";
    private static final String PARAMETER_IS_ACTIVE_CAPTCHA = "mylutece_is_active_captcha";
    private static final String PARAMETER_ERROR_CAPTCHA = "error_captcha";
    private static final String PARAMETER_DATE_LOGIN = "date_login";
    private static final String PARAMETER_INTERVAL = "interval";
    private static final String PARAMETER_IP = "ip";
    private static final String PARAMETER_KEY = "key";

    // Actions
    private static final String ACTION_CREATE_ACCOUNT = "createAccount";
    private static final String ACTION_VIEW_ACCOUNT = "viewAccount";
    private static final String ACTION_LOST_PASSWORD = "lostPassword";
    private static final String ACTION_LOST_LOGIN = "lostLogin";

    // Properties
    private static final String PROPERTY_MYLUTECE_PAGETITLE_LOGIN = "mylutece.pageTitle.login";
    private static final String PROPERTY_MYLUTECE_PATHLABEL_LOGIN = "mylutece.pagePathLabel.login";
    private static final String PROPERTY_MYLUTECE_MESSAGE_INVALID_LOGIN = "mylutece.message.error.invalid.login";
    private static final String PROPERTY_MYLUTECE_MESSAGE_INVALID_CAPTCHA = "mylutece.message.error.invalid.captcha";
    private static final String PROPERTY_MYLUTECE_LOGIN_PAGE_URL = "mylutece.url.login.page";
    private static final String PROPERTY_MYLUTECE_DOLOGIN_URL = "mylutece.url.doLogin";
    private static final String PROPERTY_MYLUTECE_DOLOGOUT_URL = "mylutece.url.doLogout";
    private static final String PROPERTY_MYLUTECE_CREATE_ACCOUNT_URL = "mylutece.url.createAccount.page";
    private static final String PROPERTY_MYLUTECE_VIEW_ACCOUNT_URL = "mylutece.url.viewAccount.page";
    private static final String PROPERTY_MYLUTECE_LOST_PASSWORD_URL = "mylutece.url.lostPassword.page";
    private static final String PROPERTY_MYLUTECE_LOST_LOGIN_URL = "mylutece.url.lostLogin.page";
    private static final String PROPERTY_MYLUTECE_RESET_PASSWORD_URL = "mylutece.url.resetPassword.page";
    private static final String PROPERTY_MYLUTECE_DEFAULT_REDIRECT_URL = "mylutece.url.default.redirect";
    private static final String PROPERTY_MYLUTECE_TEMPLATE_ACCESS_DENIED = "mylutece.template.accessDenied";
    private static final String PROPERTY_MYLUTECE_TEMPLATE_ACCESS_CONTROLED = "mylutece.template.accessControled";
    private static final String PROPERTY_DEFAULT_ENCRYPTION_ALGORITHM = "security.defaultValues.algorithm";

    // i18n Properties
    private static final String PROPERTY_CREATE_ACCOUNT_LABEL = "mylutece.xpage.createAccountLabel";
    private static final String PROPERTY_CREATE_ACCOUNT_TITLE = "mylutece.xpage.createAccountTitle";
    private static final String PROPERTY_VIEW_ACCOUNT_LABEL = "mylutece.xpage.viewAccountLabel";
    private static final String PROPERTY_VIEW_ACCOUNT_TITLE = "mylutece.xpage.viewAccountTitle";
    private static final String PROPERTY_LOST_PASSWORD_LABEL = "mylutece.xpage.lostPasswordLabel";
    private static final String PROPERTY_LOST_PASSWORD_TITLE = "mylutece.xpage.lostPasswordTitle";
    private static final String PROPERTY_LOST_LOGIN_LABEL = "mylutece.xpage.lostLoginLabel";
    private static final String PROPERTY_LOST_LOGIN_TITLE = "mylutece.xpage.lostLoginTitle";

    // Templates
    private static final String TEMPLATE_LOGIN_PAGE = "skin/plugins/mylutece/login_form.html";
    private static final String TEMPLATE_LOGIN_MULTI_PAGE = "skin/plugins/mylutece/login_form_multi.html";
    private static final String TEMPLATE_LOST_PASSWORD_PAGE = "skin/plugins/mylutece/lost_password.html";
    private static final String TEMPLATE_LOST_LOGIN_PAGE = "skin/plugins/mylutece/lost_login.html";
    private static final String TEMPLATE_CREATE_ACCOUNT_PAGE = "skin/plugins/mylutece/create_account.html";
    private static final String TEMPLATE_VIEW_ACCOUNT_PAGE = "skin/plugins/mylutece/view_account.html";
    private static final String CONSTANT_DEFAULT_ENCRYPTION_ALGORITHM = "SHA-256";
    private Locale _locale;

    /**
     * Constructor
     */
    public MyLuteceApp( )
    {
    }

    /**
     * This method builds a XPage object corresponding to the request
     * 
     * @param request
     *            The HTTP request
     * @param nMode
     *            The mode
     * @param plugin
     *            The plugin object which belongs the App
     * @return The XPage object containing the page content
     */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin )
    {
        XPage page = new XPage( );

        String strAction = request.getParameter( PARAMETER_ACTION );
        _locale = request.getLocale( );

        if ( StringUtils.equals( strAction, ACTION_CREATE_ACCOUNT ) )
        {
            return getCreateAccountPage( page, request );
        }
        else
            if ( StringUtils.equals( strAction, ACTION_VIEW_ACCOUNT ) )
            {
                return getViewAccountPage( page );
            }
            else
                if ( StringUtils.equals( strAction, ACTION_LOST_PASSWORD ) )
                {
                    return getLostPasswordPage( page );
                }
                else
                    if ( StringUtils.equals( strAction, ACTION_LOST_LOGIN ) )
                    {
                        return getLostLoginPage( page );
                    }

        return getLoginPage( page, request );
    }

    /**
     * Build the Login page
     * 
     * @param page
     *            The XPage object to fill
     * @param request
     *            The HTTP request
     * @return The XPage object containing the page content
     */
    private XPage getLoginPage( XPage page, HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>( );

        String strError = request.getParameter( PARAMETER_ERROR );
        String strErrorMessage = "";
        String strErrorDetail = "";

        if ( strError != null )
        {
            if ( strError.equals( PARAMETER_ERROR_VALUE_INVALID ) )
            {
                strErrorMessage = AppPropertiesService.getProperty( PROPERTY_MYLUTECE_MESSAGE_INVALID_LOGIN );

                if ( request.getParameter( PARAMETER_ERROR_MSG ) != null )
                {
                    strErrorDetail = request.getParameter( PARAMETER_ERROR_MSG );
                }
            }
            else
                if ( strError.equals( PARAMETER_ERROR_CAPTCHA ) )
                {
                    strErrorMessage = I18nService.getLocalizedString( PROPERTY_MYLUTECE_MESSAGE_INVALID_CAPTCHA, request.getLocale( ) );
                }
        }

        HttpSession session = request.getSession( false );
        Boolean bEnableCaptcha = Boolean.FALSE;

        if ( session != null )
        {
            bEnableCaptcha = (Boolean) session.getAttribute( PARAMETER_IS_ACTIVE_CAPTCHA );

            if ( bEnableCaptcha == null )
            {
                bEnableCaptcha = Boolean.FALSE;
            }
        }

        model.put( MARK_ERROR_MESSAGE, strErrorMessage );
        model.put( MARK_ERROR_DETAIL, strErrorDetail );
        model.put( MARK_URL_DOLOGIN, getDoLoginUrl( ) );
        model.put( MARK_AUTH_PROVIDER, request.getParameter( PARAMETER_AUTH_PROVIDER ) );
        model.put( MARK_IS_ACTIVE_CAPTCHA, bEnableCaptcha );

        if ( bEnableCaptcha )
        {
            CaptchaSecurityService captchaService = new CaptchaSecurityService( );
            model.put( MARK_CAPTCHA, captchaService.getHtmlCode( ) );
        }

        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, TOKEN_ACTION_LOGIN ) );

        HtmlTemplate template;
        model.put( MARK_URL_NEWACCOUNT, SecurityService.getInstance( ).getAuthenticationService( ).getNewAccountPageUrl( ) );

        if ( SecurityService.getInstance( ).isMultiAuthenticationSupported( ) )
        {
            LuteceAuthentication luteceAuthentication = SecurityService.getInstance( ).getAuthenticationService( );

            if ( luteceAuthentication instanceof MultiLuteceAuthentication )
            {
                model.put( MARK_LIST_AUTHENTICATIONS, ( (MultiLuteceAuthentication) luteceAuthentication ).getListLuteceAuthentication( ) );
            }

            template = AppTemplateService.getTemplate( TEMPLATE_LOGIN_MULTI_PAGE, _locale, model );
        }
        else
        {
            template = AppTemplateService.getTemplate( TEMPLATE_LOGIN_PAGE, _locale, model );
        }

        page.setContent( template.getHtml( ) );
        page.setPathLabel( AppPropertiesService.getProperty( PROPERTY_MYLUTECE_PATHLABEL_LOGIN ) );
        page.setTitle( AppPropertiesService.getProperty( PROPERTY_MYLUTECE_PAGETITLE_LOGIN ) );

        return page;
    }

    /**
     * This method is call by the JSP named DoMyLuteceLogin.jsp
     * 
     * @param request
     *            The HTTP request
     * @return The URL to forward depending of the result of the login.
     * @throws UnsupportedEncodingException
     */
    public String doLogin( HttpServletRequest request ) throws UnsupportedEncodingException
    {
        String strUsername = request.getParameter( PARAMETER_USERNAME );
        String strPassword = request.getParameter( PARAMETER_PASSWORD );
        String strAuthProvider = request.getParameter( PARAMETER_AUTH_PROVIDER );

        String strReturn = "../../../../" + getLoginPageUrl( );

        Boolean bIsCaptchaEnabled = (Boolean) request.getSession( true ).getAttribute( PARAMETER_IS_ACTIVE_CAPTCHA );

        if ( ( bIsCaptchaEnabled != null ) && bIsCaptchaEnabled )
        {
            CaptchaSecurityService captchaService = new CaptchaSecurityService( );

            if ( !captchaService.validate( request ) )
            {
                strReturn += ( "&" + PARAMETER_ERROR + "=" + PARAMETER_ERROR_CAPTCHA );
            }
        }

        Plugin plugin = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );

        try
        {
            SecurityService.getInstance( ).loginUser( request, strUsername, strPassword );
        }
        catch( LoginRedirectException ex )
        {
            HttpSession session = request.getSession( false );

            if ( session != null )
            {
                session.removeAttribute( PARAMETER_IS_ACTIVE_CAPTCHA );
            }

            return ex.getRedirectUrl( );
        }
        catch( FailedLoginException ex )
        {
            // Creating a record of connections log
            ConnectionLog connectionLog = new ConnectionLog( );
            connectionLog.setIpAddress( SecurityUtil.getRealIp( request ) );
            connectionLog.setDateLogin( new java.sql.Timestamp( new java.util.Date( ).getTime( ) ) );
            connectionLog.setLoginStatus( ConnectionLog.LOGIN_DENIED ); // will be inserted only if access denied
            ConnectionLogHome.addUserLog( connectionLog, plugin );

            strReturn += ( "&" + PARAMETER_ERROR + "=" + PARAMETER_ERROR_VALUE_INVALID );

            if ( StringUtils.isNotBlank( strAuthProvider ) )
            {
                strReturn += ( "&" + PARAMETER_AUTH_PROVIDER + "=" + strAuthProvider );
            }

            if ( ex.getMessage( ) != null )
            {
                String strMessage = "&" + PARAMETER_ERROR_MSG + "=" + URLEncoder.encode( ex.getMessage( ), "UTF-8" );
                strReturn += strMessage;
            }

            if ( ex instanceof FailedLoginCaptchaException )
            {
                Boolean bEnableCaptcha = ( (FailedLoginCaptchaException) ex ).isCaptchaEnabled( );
                request.getSession( true ).setAttribute( PARAMETER_IS_ACTIVE_CAPTCHA, bEnableCaptcha );
            }

            return strReturn;
        }
        catch( LoginException ex )
        {
            strReturn += ( "&" + PARAMETER_ERROR + "=" + PARAMETER_ERROR_VALUE_INVALID );

            if ( StringUtils.isNotBlank( strAuthProvider ) )
            {
                strReturn += ( "&" + PARAMETER_AUTH_PROVIDER + "=" + strAuthProvider );
            }

            if ( ex.getMessage( ) != null )
            {
                String strMessage = "&" + PARAMETER_ERROR_MSG + "=" + ex.getMessage( );
                strReturn += strMessage;
            }

            return strReturn;
        }

        HttpSession session = request.getSession( false );

        if ( session != null )
        {
            session.removeAttribute( PARAMETER_IS_ACTIVE_CAPTCHA );
        }

        String strNextUrl = PortalJspBean.getLoginNextUrl( request );
        String strCurrentUrl = getCurrentUrl( request );

        if ( strNextUrl != null )
        {
            return strNextUrl;
        }
        else
            if ( strCurrentUrl != null )
            {
                return strCurrentUrl;
            }

        return getDefaultRedirectUrl( );
    }

    /**
     * Check if the current user is authenticated
     * 
     * @param request
     *            The request
     * @return A JSON string containing true in the field result if the user is authenticated
     */
    public String isUserAuthenticated( HttpServletRequest request )
    {
        AbstractJsonResponse jsonResponse = null;

        LuteceUser user = null;

        user = SecurityService.getInstance( ).getRegisteredUser( request );

        if ( user != null )
        {
            jsonResponse = new JsonResponse( Boolean.TRUE );
        }
        else
        {
            jsonResponse = new JsonResponse( Boolean.FALSE );
        }

        return JsonUtil.buildJsonResponse( jsonResponse );
    }

    /**
     * Returns the Login page URL of the Authentication Service
     * 
     * @return The URL
     */
    public static String getLoginPageUrl( )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_LOGIN_PAGE_URL );
    }

    /**
     * Returns the DoLogin URL of the Authentication Service
     * 
     * @return The URL
     */
    public static String getDoLoginUrl( )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_DOLOGIN_URL );
    }

    /**
     * Returns the DoLogout URL of the Authentication Service
     * 
     * @return The URL
     */
    public static String getDoLogoutUrl( )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_DOLOGOUT_URL );
    }

    /**
     * Returns the NewAccount URL of the Authentication Service
     * 
     * @return The URL
     */
    public static String getNewAccountUrl( )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_CREATE_ACCOUNT_URL );
    }

    /**
     * Returns the ViewAccount URL of the Authentication Service
     * 
     * @return The URL
     */
    public static String getViewAccountUrl( )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_VIEW_ACCOUNT_URL );
    }

    /**
     * Returns the Lost Password URL of the Authentication Service
     * 
     * @return The URL
     */
    public static String getLostPasswordUrl( )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_LOST_PASSWORD_URL );
    }

    /**
     * Returns the Lost login URL of the Authentication Service
     * 
     * @return The URL
     */
    public static String getLostLoginUrl( )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_LOST_LOGIN_URL );
    }

    /**
     * Returns the Reset Password URL of the Authentication Service
     * 
     * @param request
     *            The request
     * @return The URL
     */
    public static String getResetPasswordUrl( HttpServletRequest request )
    {
        return AppPathService.getBaseUrl( request ) + AppPropertiesService.getProperty( PROPERTY_MYLUTECE_RESET_PASSWORD_URL );
    }

    /**
     * Returns the Default redirect URL of the Authentication Service
     * 
     * @return The URL
     */
    public static String getDefaultRedirectUrl( )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_DEFAULT_REDIRECT_URL );
    }

    /**
     * This method is call by the JSP named DoMyLuteceLogout.jsp
     * 
     * @param request
     *            The HTTP request
     * @return The URL to forward depending of the result of the login.
     */
    public String doLogout( HttpServletRequest request )
    {
        SecurityService.getInstance( ).logoutUser( request );

        return getDefaultRedirectUrl( );
    }

    /**
     * Build the CreateAccount page
     * 
     * @param page
     *            The XPage object to fill
     * @param request
     *            the request
     * @return The XPage object containing the page content
     */
    private XPage getCreateAccountPage( XPage page, HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<String, Object>( );

        if ( SecurityService.getInstance( ).isMultiAuthenticationSupported( ) )
        {
            model.put( MARK_LIST_AUTHENTICATIONS,
                    ( (MultiLuteceAuthentication) SecurityService.getInstance( ).getAuthenticationService( ) ).getListLuteceAuthentication( ) );
        }

        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_CREATE_ACCOUNT_PAGE, _locale, model );
        page.setContent( t.getHtml( ) );
        // page.setPathLabel( "Create Account" );
        // page.setTitle( "Create Account" );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_CREATE_ACCOUNT_LABEL, _locale ) );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_CREATE_ACCOUNT_TITLE, _locale ) );

        return page;
    }

    /**
     * Build the ViewAccount page
     * 
     * @param page
     *            The XPage object to fill
     * @return The XPage object containing the page content
     */
    private XPage getViewAccountPage( XPage page )
    {
        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_VIEW_ACCOUNT_PAGE, _locale );
        page.setContent( t.getHtml( ) );
        // page.setPathLabel( "View Account" );
        // page.setTitle( "View Account" );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_VIEW_ACCOUNT_LABEL, _locale ) );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_VIEW_ACCOUNT_TITLE, _locale ) );

        return page;
    }

    /**
     * Build the default Lost password page
     * 
     * @param page
     *            The XPage object to fill
     * @return The XPage object containing the page content
     */
    private XPage getLostPasswordPage( XPage page )
    {
        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_LOST_PASSWORD_PAGE, _locale );
        page.setContent( t.getHtml( ) );
        // page.setPathLabel( "Lost password" );
        // page.setTitle( "Lost password" );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_LOST_PASSWORD_LABEL, _locale ) );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_LOST_PASSWORD_TITLE, _locale ) );

        return page;
    }

    /**
     * Build the default Lost password page
     * 
     * @param page
     *            The XPage object to fill
     * @return The XPage object containing the page content
     */
    private XPage getLostLoginPage( XPage page )
    {
        HtmlTemplate t = AppTemplateService.getTemplate( TEMPLATE_LOST_LOGIN_PAGE, _locale );
        page.setContent( t.getHtml( ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_LOST_LOGIN_LABEL, _locale ) );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_LOST_LOGIN_TITLE, _locale ) );

        return page;
    }

    /**
     * Returns the template for access denied
     * 
     * @return The template path
     */
    public static String getAccessDeniedTemplate( )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_TEMPLATE_ACCESS_DENIED );
    }

    /**
     * Returns the template for access controled
     * 
     * @return The template path
     */
    public static String getAccessControledTemplate( )
    {
        return AppPropertiesService.getProperty( PROPERTY_MYLUTECE_TEMPLATE_ACCESS_CONTROLED );
    }

    /**
     * Reset the connection log of an IP.
     * 
     * @param request
     *            The request
     * @return The URL of the next page to display
     */
    public String doResetConnectionLog( HttpServletRequest request )
    {
        String strIp = request.getParameter( PARAMETER_IP );
        String strDateLogin = request.getParameter( PARAMETER_DATE_LOGIN );
        String strInterval = request.getParameter( PARAMETER_INTERVAL );
        String strKey = request.getParameter( PARAMETER_KEY );

        if ( StringUtils.isNotBlank( strIp ) && StringUtils.isNotBlank( strDateLogin ) && StringUtils.isNotBlank( strKey )
                && StringUtils.isNotBlank( strInterval ) )
        {
            String strCryptoKey = CryptoService.getCryptoKey( );
            String strComputedKey = CryptoService.encrypt( strIp + strDateLogin + strInterval + strCryptoKey,
                    AppPropertiesService.getProperty( PROPERTY_DEFAULT_ENCRYPTION_ALGORITHM, CONSTANT_DEFAULT_ENCRYPTION_ALGORITHM ) );

            if ( StringUtils.equals( strKey, strComputedKey ) )
            {
                Plugin plugin = PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );
                ConnectionLogHome.resetConnectionLogs( strIp, new Timestamp( Long.parseLong( strDateLogin ) ), Integer.parseInt( strInterval ), plugin );
            }
        }

        return "../../../../" + getLoginPageUrl( );
    }

    /**
     * Returns the current url
     * 
     * @param request
     *            The Http request
     * @return The current url
     * 
     */
    public static String getCurrentUrl( HttpServletRequest request )
    {
        HttpSession session = request.getSession( );
        String strNextUrl = (String) session.getAttribute( ATTRIBUTE_CURRENT_URL );

        return strNextUrl;
    }

    /**
     * Set the current url
     * 
     * @param request
     *            The Http request
     * 
     */
    public static void setCurrentUrl( HttpServletRequest request )
    {
        String strCurrentUrl = request.getRequestURI( );
        UrlItem url = new UrlItem( strCurrentUrl );
        Enumeration enumParams = request.getParameterNames( );

        while ( enumParams.hasMoreElements( ) )
        {
            String strParamName = (String) enumParams.nextElement( );
            url.addParameter( strParamName, request.getParameter( strParamName ) );
        }

        HttpSession session = request.getSession( true );
        session.setAttribute( ATTRIBUTE_CURRENT_URL, url.getUrl( ) );
    }
}
