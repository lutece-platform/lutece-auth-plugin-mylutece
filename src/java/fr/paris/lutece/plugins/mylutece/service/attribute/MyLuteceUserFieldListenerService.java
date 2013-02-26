package fr.paris.lutece.plugins.mylutece.service.attribute;

import fr.paris.lutece.plugins.mylutece.business.attribute.MyLuteceUserField;
import fr.paris.lutece.plugins.mylutece.business.attribute.MyLuteceUserFieldListener;
import fr.paris.lutece.portal.business.user.AdminUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * MyLuteceUserFieldListenerService
 *
 */
public class MyLuteceUserFieldListenerService 
{
	private List<MyLuteceUserFieldListener> _listRegisteredListeners = new ArrayList<MyLuteceUserFieldListener>(  );
	
    /**
     * Register a new Removal listener
     * @param listener The listener to register
     */
    public void registerListener( MyLuteceUserFieldListener listener )
    {
        _listRegisteredListeners.add( listener );
    }

    /**
     * Create user fields
     * @param nIdUser The id of the user
     * @param request HttpServletRequest
     * @param locale locale
     */
    public void doCreateUserFields( int nIdUser, HttpServletRequest request, Locale locale )
    {
    	for ( MyLuteceUserFieldListener listener : _listRegisteredListeners )
    	{
    		listener.doCreateUserFields( nIdUser, request, locale );
    	}
    }

    /**
     * Create user fields
     * @param nIdUser The id of the user
     * @param listUserFields The list of user fields to update
     * @param locale locale
     */
    public void doCreateUserFields( int nIdUser, List<MyLuteceUserField> listUserFields, Locale locale )
    {
        for ( MyLuteceUserFieldListener listener : _listRegisteredListeners )
        {
            listener.doCreateUserFields( nIdUser, listUserFields, locale );
        }
    }

    /**
     * Modify user fields
     * @param nIdUser The id of the user
     * @param request HttpServletRequest
     * @param locale locale
     * @param currentUser current user
     */
    public void doModifyUserFields( int nIdUser, HttpServletRequest request, Locale locale, AdminUser currentUser )
    {
    	for ( MyLuteceUserFieldListener listener : _listRegisteredListeners )
    	{
    		listener.doModifyUserFields( nIdUser, request, locale, currentUser );
    	}
    }

    /**
     * Modify user fields
     * @param nIdUser The id of the user
     * @param listUserFields The list of user fields to modify
     * @param locale locale
     * @param currentUser current user
     */
    public void doModifyUserFields( int nIdUser, List<MyLuteceUserField> listUserFields, Locale locale,
            AdminUser currentUser )
    {
        for ( MyLuteceUserFieldListener listener : _listRegisteredListeners )
        {
            listener.doModifyUserFields( nIdUser, listUserFields, locale, currentUser );
        }
    }

    /**
     * Remove user fields
     * @param nIdUser The id of the user
     * @param request HttpServletRequest
     * @param locale locale
     */
    public void doRemoveUserFields( int nIdUser, HttpServletRequest request, Locale locale )
    {
    	for ( MyLuteceUserFieldListener listener : _listRegisteredListeners )
    	{
    		listener.doRemoveUserFields( nIdUser, request, locale );
    	}
    }

    /**
     * Remove user fields
     * @param nIdUser The id of the user
     * @param locale locale
     */
    public void doRemoveUserFields( int nIdUser, Locale locale )
    {
        for ( MyLuteceUserFieldListener listener : _listRegisteredListeners )
        {
            listener.doRemoveUserFields( nIdUser, locale );
        }
    }
}
