/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.mylutece.authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.mylutece.web.MyLuteceApp;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LoginRedirectException;
import fr.paris.lutece.portal.service.security.LuteceAuthentication;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.LocalVariables;

/**
 * Manages serveral {@link MyLuteceAuthentication}. Call {@link #registerAuthentication(LuteceAuthentication)} to register your authentication and {@link #removeAuthentication(String)} to unregister
 * it.
 */
public class MultiLuteceAuthentication implements LuteceAuthentication
{
	private static final String PROPERTY_MESSAGE_NO_AUTHENTICATION_SELECTED = "mylutece.message.noAuthenticationSelected";

	private static final String PARAMETER_AUTH_PROVIDER = "auth_provider";

	private static final Map<String, LuteceAuthentication> _mapAuthentications = new HashMap<String, LuteceAuthentication>();

	/**
	 * Registers an authentication. Should be called at plugin init/install.
	 * @param authentication the authentication to register.
	 */
	public static void registerAuthentication( LuteceAuthentication authentication )
	{
		AppLogService.info( "MultiLuteceAuthentication : Registering authentication " + authentication.getName() );
		_mapAuthentications.put( authentication.getName(), authentication );
	}

	/**
	 * Removes the authentication from managed authentication
	 * @param strAuthenticationName the authentication key
	 */
	public static void removeAuthentication( String strAuthenticationName )
	{
		if ( _mapAuthentications.containsKey( strAuthenticationName ) )
		{
			AppLogService.info( "MultiLuteceAuthentication : Unregistering authentication " + strAuthenticationName );
			_mapAuthentications.remove( strAuthenticationName );
		}
		else
		{
			AppLogService.error( "Unable to remove authentication " + strAuthenticationName + ". Authentication not found. Available values are " + _mapAuthentications.keySet() );
		}
	}

	/**
	 * Returns the Login page URL of the Authentication Service. <br>
	 * Tries to get authentication specific login page url form request (passed through {@link LocalVariables} ), default otherswise.
	 * @return The URL authentication specific login page url, default otherswise.
	 */
	public String getLoginPageUrl()
	{
		String strLoginUrl = MyLuteceApp.getLoginPageUrl();
		HttpServletRequest request = LocalVariables.getRequest();
		if ( request != null )
		{
			String strAuthentication = request.getParameter( PARAMETER_AUTH_PROVIDER );
			if ( StringUtils.isNotBlank( strAuthentication ) )
			{
				LuteceAuthentication authentication = _mapAuthentications.get( strAuthentication );
				strLoginUrl = authentication.getLoginPageUrl();
			}
		}
		return strLoginUrl;
	}

	/**
	 * Returns the DoLogin URL of the Authentication Service. <br>
	 * Tries to get authentication specific dologin page url form request (passed through {@link LocalVariables} ), default otherswise.
	 * @return The URL
	 */
	public String getDoLoginUrl()
	{
		String strLoginUrl = MyLuteceApp.getDoLoginUrl();
		HttpServletRequest request = LocalVariables.getRequest();
		if ( request != null )
		{
			String strAuthentication = request.getParameter( PARAMETER_AUTH_PROVIDER );
			if ( StringUtils.isNotBlank( strAuthentication ) )
			{
				LuteceAuthentication authentication = _mapAuthentications.get( strAuthentication );
				strLoginUrl = authentication.getDoLoginUrl();
			}
		}

		return strLoginUrl;

	}

	/**
	 * Returns the new account page URL of the Authentication Service <br>
	 * Tries to get authentication specific new account page url form request (passed through {@link LocalVariables} ), default otherswise.
	 * @return The URL
	 */
	public String getNewAccountPageUrl()
	{
		String strNewAccountUrl = MyLuteceApp.getNewAccountUrl();
		HttpServletRequest request = LocalVariables.getRequest();
		if ( request != null )
		{
			String strAuthentication = request.getParameter( PARAMETER_AUTH_PROVIDER );
			if ( StringUtils.isNotBlank( strAuthentication ) )
			{
				LuteceAuthentication authentication = _mapAuthentications.get( strAuthentication );
				strNewAccountUrl = authentication.getNewAccountPageUrl();
			}
		}

		return strNewAccountUrl;
	}

	/**
	 * Returns the View account page URL of the Authentication Service <br>
	 * Tries to get authentication specific view account page url form request (passed through {@link LocalVariables} ), default otherswise.
	 * @return The URL
	 */
	public String getViewAccountPageUrl()
	{
		String strViewAccountUrl = MyLuteceApp.getViewAccountUrl();
		HttpServletRequest request = LocalVariables.getRequest();
		if ( request != null )
		{
			String strAuthentication = request.getParameter( PARAMETER_AUTH_PROVIDER );
			if ( StringUtils.isNotBlank( strAuthentication ) )
			{
				LuteceAuthentication authentication = _mapAuthentications.get( strAuthentication );
				strViewAccountUrl = authentication.getViewAccountPageUrl();
			}
		}

		return strViewAccountUrl;
	}

	/**
	 * Returns the lost password URL of the Authentication Service. <br>
	 * Tries to get authentication specific lost password page url form request (passed through {@link LocalVariables} ), default otherswise.
	 * @return The URL
	 */
	public String getLostPasswordPageUrl()
	{
		String strLostPasswordUrl = MyLuteceApp.getLostPasswordUrl();
		HttpServletRequest request = LocalVariables.getRequest();
		if ( request != null )
		{
			String strAuthentication = request.getParameter( PARAMETER_AUTH_PROVIDER );
			if ( StringUtils.isNotBlank( strAuthentication ) )
			{
				LuteceAuthentication authentication = _mapAuthentications.get( strAuthentication );
				strLostPasswordUrl = authentication.getLostPasswordPageUrl();
			}
		}

		return strLostPasswordUrl;
	}

	/**
	 * Returns the disconnect URL of the Authentication Service. <br>
	 * Tries to get authentication specific dologout page url form request (passed through {@link LocalVariables} ), default otherswise.
	 * @return The URL
	 */
	public String getDoLogoutUrl()
	{
		String strDoLogoutUrl = MyLuteceApp.getDoLogoutUrl();
		HttpServletRequest request = LocalVariables.getRequest();
		if ( request != null )
		{
			String strAuthentication = request.getParameter( PARAMETER_AUTH_PROVIDER );
			if ( StringUtils.isNotBlank( strAuthentication ) )
			{
				LuteceAuthentication authentication = _mapAuthentications.get( strAuthentication );
				strDoLogoutUrl = authentication.getDoLogoutUrl();
			}
		}

		return strDoLogoutUrl;
	}

	/**
	 * Returns the access denied template
	 * @return The template
	 */
	public String getAccessDeniedTemplate()
	{
		return MyLuteceApp.getAccessDeniedTemplate();
	}

	/**
	 * Returns the access controled template
	 * @return The template
	 */
	public String getAccessControledTemplate()
	{
		return MyLuteceApp.getAccessControledTemplate();
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public LuteceUser getAnonymousUser()
	{
		final class AnonymousUser extends LuteceUser
		{
			AnonymousUser(  )
			{
				super( LuteceUser.ANONYMOUS_USERNAME, MultiLuteceAuthentication.this );
			}
		}
		return new AnonymousUser();
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public String getAuthServiceName()
	{
		return "Lutece Multi Authentication Service";
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public String getAuthType( HttpServletRequest request )
	{
		return HttpServletRequest.BASIC_AUTH;
	}

	/**
	 * 
	 * Finds the http authenticated user. <br>
	 * @param request the reuqest
	 * @return the first successfully recovered user, <code>null</code> otherwise.
	 */
	public LuteceUser getHttpAuthenticatedUser( HttpServletRequest request )
	{
		LuteceUser luteceUser = null;
		for ( LuteceAuthentication luteceAuthentication : getListLuteceAuthentication() )
		{
			if ( luteceAuthentication.isExternalAuthentication() )
			{
				luteceUser = luteceAuthentication.getHttpAuthenticatedUser( request );
				if ( luteceUser != null )
				{
					break;
				}
			}
		}
		return luteceUser;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public String[] getRolesByUser( LuteceUser user )
	{
		LuteceAuthentication userAuthentication = user.getLuteceAuthenticationService();
		if ( userAuthentication != null )
		{
			return userAuthentication.getRolesByUser( user );
		}
		return null;
	}

	/**
	 * 
	 * Tries to get user from any authentication. <br>
	 * Due to huge calculation, this method should not be called often.
	 * @param strUserLogin user login
	 * @return the LuteceUser found, <code>null</code> otherwise.
	 */
	public LuteceUser getUser( String strUserLogin )
	{
		// try to get user from any authentication
		for ( LuteceAuthentication authentication : getListLuteceAuthentication() )
		{
			LuteceUser user = authentication.getUser( strUserLogin );
			if ( user != null )
			{
				return user;
			}
		}
		return null;
	}

	/**
	 * 
	 * Gets all known users from all authentications.
	 * @return all kown users list.
	 */
	public Collection<LuteceUser> getUsers()
	{
		List<LuteceUser> listUsers = new ArrayList<LuteceUser>();

		for ( LuteceAuthentication luteceAuthentication : getListLuteceAuthentication() )
		{
			listUsers.addAll( luteceAuthentication.getUsers() );
		}

		return listUsers;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public boolean isExternalAuthentication()
	{
		return true;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public boolean isUserInRole( LuteceUser user, HttpServletRequest request, String strRole )
	{
		if ( user == null )
		{
			return false;
		}
		LuteceAuthentication authentication = user.getLuteceAuthenticationService();
		if ( authentication != null )
		{
			return authentication.isUserInRole( user, request, strRole );
		}
		return false;
	}

	/**
	 * 
	 * Returns false. User list should not be directly recovered, due to use CPU usage.
	 * @return false.
	 */
	public boolean isUsersListAvailable()
	{
		return false;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public LuteceUser login( String strUserName, String strUserPassword, HttpServletRequest request ) throws LoginException, LoginRedirectException
	{
		LuteceUser luteceUser = null;
		String strAuthProvider = request.getParameter( PARAMETER_AUTH_PROVIDER );

		if ( strAuthProvider != null )
		{
			LuteceAuthentication myLuteceAuthentication = _mapAuthentications.get( strAuthProvider );
			if ( myLuteceAuthentication != null )
			{
				if ( AppLogService.isDebugEnabled() )
				{
					AppLogService.debug( "Using " + myLuteceAuthentication.getAuthServiceName() + " for user " + strUserName );
				}
				luteceUser = myLuteceAuthentication.login( strUserName, strUserPassword, request );
			}
			else
			{
				AppLogService.error( "Authentication null for key " + strAuthProvider );
				throw new LoginException( I18nService.getLocalizedString( PROPERTY_MESSAGE_NO_AUTHENTICATION_SELECTED, request.getLocale() ) );
			}
		}
		else
		{
			throw new LoginException( I18nService.getLocalizedString( PROPERTY_MESSAGE_NO_AUTHENTICATION_SELECTED, request.getLocale() ) );
		}

		return luteceUser;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void logout( LuteceUser user )
	{
		if ( user != null )
		{
			LuteceAuthentication luteceAuthentication = user.getLuteceAuthenticationService();
			if ( luteceAuthentication != null )
			{
				luteceAuthentication.logout( user );
			}
			else
			{
				AppLogService.error( "No auth provider found for " + user.getName() + ". Brute force logout." );
				for ( LuteceAuthentication authentication : _mapAuthentications.values() )
				{
					authentication.logout( user );
				}
			}
		}
		else
		{
			AppLogService.error( "Tried to logout null user" );
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public boolean isMultiAuthenticationSupported()
	{
		return true;
	}

	/**
	 * Gets the authentication by its key
	 * @param strKey the key
	 * @return the {@link LuteceAuthentication} found, <code>null</code> otherwise.
	 */
	public LuteceAuthentication getLuteceAuthentication( String strKey )
	{
		return _mapAuthentications.get( strKey );
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public boolean isDelegatedAuthentication()
	{
		return false;
	}

	/**
	 * Returns all known security authentication services
	 * @return all known security authentication services
	 */
	public List<LuteceAuthentication> getListLuteceAuthentication()
	{
		List<LuteceAuthentication> listAuthentications = new ArrayList<LuteceAuthentication>();
		for ( LuteceAuthentication authentication : _mapAuthentications.values() )
		{
			Plugin plugin = PluginService.getPlugin( authentication.getPluginName() );
			if ( plugin != null && plugin.isInstalled() )
			{
				listAuthentications.add( authentication );
			}
			else if ( AppLogService.isDebugEnabled() )
			{
				AppLogService.debug( "Authentication : Plugin not found or not installed for plugin name " + authentication.getPluginName() );
			}
		}
		return listAuthentications;
	}

	/**
	 * No icon directlty shown for this authentication.
	 * @return icon url
	 */
	public String getIconUrl()
	{
		return null;
	}

	/**
	 * 
	 * Always <code>null</code>, not supposed to be identifiable
	 * @return null
	 */
	public String getName()
	{
		return null;
	}

	/**
	 * 
	 * Always <code>null</code>, this implementation is not plugin related.
	 * @return null
	 */
	public String getPluginName()
	{
		return null;
	}

}
