package fr.paris.lutece.plugins.mylutece.service;

import java.util.List;
import java.util.Locale;

import fr.paris.lutece.plugins.mylutece.business.LuteceUserAttributeDescription;


/**
 * The Interface ILuteceUserAttributesProvided.
 */
public interface ILuteceUserAttributesProvidedDescription {
	
	/**
	 * Gets the lutece user attributes provided.
	 *
	 * @return the lutece user attributes provided
	 */
	default List<LuteceUserAttributeDescription> getLuteceUserAttributesProvided(Locale locale)
	{
		 // Default implementation doesn't do anything. This is used for backward compatibility
	     return null;
	}

}
