package fr.paris.lutece.plugins.mylutece.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.paris.lutece.portal.service.security.LuteceUser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Named;

/**
 * The Class MyluteceExternalRoleService.
 */
@ApplicationScoped
@Named( "mylutece.myLuteceExternalRoleService" )
public class MyluteceExternalRoleService implements IMyluteceExternalRoleService
{

    /**
     * Get the instance of the MyLuteceExternalIdentityService service.
     *
     * @return the instance of the MyLuteceExternalIdentityService service
     */
    @Deprecated
    public static IMyluteceExternalRoleService getInstance( )
    {
        return CDI.current( ).select( IMyluteceExternalRoleService.class ).get( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> providesRoles( LuteceUser user )
    {
        // Get the external roles
        Set<String> listRoles = new HashSet<>( );
        for ( IMyLuteceExternalRolesProvider roleProvider : getProviders( ) )
        {
            listRoles.addAll( roleProvider.providesRoles( user ) );
        }

        return listRoles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IMyLuteceExternalRolesProvider> getProviders( )
    {
        return CDI.current( ).select( IMyLuteceExternalRolesProvider.class ).stream( ).toList( );
    }

}
