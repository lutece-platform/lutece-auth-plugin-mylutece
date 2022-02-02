package fr.paris.lutece.plugins.mylutece.service;

import java.util.Collection;
import java.util.List;

import fr.paris.lutece.portal.service.security.LuteceUser;


/**
 * The Interface IMyluteceExternalRoleService.
 */
public interface IMyluteceExternalRoleService {

	
	/**
	 * Provides roles.
	 *
	 * @param user the user
	 * @return the collection
	 */
	Collection<String> providesRoles( final LuteceUser user );
	
	/**
	 * Gets the providers.
	 *
	 * @return the providers
	 */
	List<IMyLuteceExternalRolesProvider> getProviders();
	
	
}
