package fr.paris.lutece.plugins.mylutece.service;

import java.util.List;
import java.util.Locale;

import fr.paris.lutece.plugins.mylutece.business.LuteceUserRoleDescription;


/**
 * The Interface ILuteceUserAttributesProvided.
 */
public interface ILuteceUserRolesProvidedDescription {
	
	/**
	 * Gets the lutece user attributes provided.
	 *
	 * @return the lutece user attributes provided
	 */
	default List<LuteceUserRoleDescription> getLuteceUserRolesProvided(Locale locale)
	{
		 // Default implementation doesn't do anything. This is used for backward compatibility
	     return null;
	}

}
