/*
 * Copyright (c) 2002-2026, City of Paris
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

import java.util.Arrays;
import java.util.Locale;

import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.test.LuteceTestCase;

public class MyLuteceDefaultRoleProviderTest extends LuteceTestCase
{
    private static final String KEY_DATASTORE_DEFAULT_ROLES = "mylutece.site_property.default_role_keys";

    private final MyLuteceDefaultRoleProvider _provider = new MyLuteceDefaultRoleProvider( );

    @Override
    protected void tearDown( ) throws Exception
    {
        DatastoreService.removeData( KEY_DATASTORE_DEFAULT_ROLES );
        super.tearDown( );
    }

    public void testProvidesRolesMissingKey( )
    {
        DatastoreService.removeData( KEY_DATASTORE_DEFAULT_ROLES );
        assertTrue( _provider.providesRoles( null ).isEmpty( ) );
        assertTrue( _provider.getLuteceUserRolesProvided( Locale.FRENCH ).isEmpty( ) );
    }

    public void testProvidesRolesEmptyValue( )
    {
        DatastoreService.setDataValue( KEY_DATASTORE_DEFAULT_ROLES, "" );
        assertTrue( _provider.providesRoles( null ).isEmpty( ) );
        assertTrue( _provider.getLuteceUserRolesProvided( Locale.FRENCH ).isEmpty( ) );
    }

    public void testProvidesRolesIgnoresBlankEntries( )
    {
        DatastoreService.setDataValue( KEY_DATASTORE_DEFAULT_ROLES, "role1,, ,role2" );
        assertEquals( Arrays.asList( "role1", "role2" ), _provider.providesRoles( null ) );
        assertEquals( 2, _provider.getLuteceUserRolesProvided( Locale.FRENCH ).size( ) );
    }
}
