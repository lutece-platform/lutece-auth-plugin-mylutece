package fr.paris.lutece.plugins.mylutece.service.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.mylutece.business.attribute.MyLuteceUserFieldListener;
import fr.paris.lutece.portal.business.user.AdminUser;

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
     * @param user AdminUser
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
     * Modify user fields
     * @param user AdminUser
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
     * Remove user fields
     * @param user Adminuser
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
}
