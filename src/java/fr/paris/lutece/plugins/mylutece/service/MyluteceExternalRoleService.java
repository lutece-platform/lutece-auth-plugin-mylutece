package fr.paris.lutece.plugins.mylutece.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.spring.SpringContextService;


/**
 * The Class MyluteceExternalRoleService.
 */
public class MyluteceExternalRoleService implements IMyluteceExternalRoleService{

	
     /** The Constant BEAN_MY_LUTECE_ROLE_SERVICE. */
     private static final String BEAN_MY_LUTECE_ROLE_SERVICE = "mylutece.myLuteceExternalRoleService";
	 
 	/** The singleton. */
 	private static volatile IMyluteceExternalRoleService  _singleton;
	 
	 
	 
	 
	 /**
 	 * Get the instance of the MyLuteceExternalIdentityService service.
 	 *
 	 * @return the instance of the MyLuteceExternalIdentityService service
 	 */
	    public static IMyluteceExternalRoleService getInstance( )
	    {
	        if ( _singleton == null )
	        {
	            synchronized( IMyluteceExternalRoleService.class )
	            {
	            	IMyluteceExternalRoleService service = SpringContextService.getBean( BEAN_MY_LUTECE_ROLE_SERVICE );
	                _singleton = service;
	                
	            }
	        }

	        return _singleton;
	    }
	/**
     * {@inheritDoc}
     */
	@Override
	public Collection<String> providesRoles(LuteceUser user) {
	    // Get the external roles
        Set<String> listRoles = new HashSet<>( );
        for ( IMyLuteceExternalRolesProvider roleProvider : getProviders() )
        {
            listRoles.addAll( roleProvider.providesRoles( user ) );
        }
        
        return listRoles;
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public List<IMyLuteceExternalRolesProvider> getProviders() {

		return SpringContextService.getBeansOfType( IMyLuteceExternalRolesProvider.class );
	}

	

}
