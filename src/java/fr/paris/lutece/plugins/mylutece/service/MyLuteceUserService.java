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
package fr.paris.lutece.plugins.mylutece.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.portal.business.role.Role;
import fr.paris.lutece.portal.business.role.RoleHome;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.util.AppLogService;

public class MyLuteceUserService
{
    /**
     * This method is used to provide external infos to the user, such as the roles, and the identity informations
     * 
     * @param user
     *            The LuteceUser
     */
    public static void provideUserExternalInfos( LuteceUser user )
    {
        // add external identities informations
        Map<String, String> identityInformations = MyLuteceExternalIdentityService.getInstance( ).getIdentityInformations( user.getName( ) );

        if ( identityInformations != null && !identityInformations.isEmpty( ) )
        {
            user.getUserInfos( ).putAll( identityInformations );
        }

        // Get the external roles
        Collection<String> listRoles = MyluteceExternalRoleService.getInstance().providesRoles(user);
        // Check existence of each front role
        
        
        List<String> listCurrentUserRoles=user.getRoles()!=null?Arrays.asList(user.getRoles()):null;
        List<String> listRolesToRemove = new ArrayList<>( );
        for ( String strRole : listRoles )
        {
            Role role = RoleHome.findByPrimaryKey( strRole );
            if ( role == null )
            {
                listRolesToRemove.add( strRole );
                AppLogService.error( "The role " + strRole + " doesn't exist in BO." );
            }
            else if(listCurrentUserRoles!=null && listCurrentUserRoles.contains(strRole) )
            {
            	listRolesToRemove.add( strRole );
            }
        }
        listRoles.removeAll( listRolesToRemove );

        // Add to the user the existing roles
        user.addRoles( listRoles );
    }
}
