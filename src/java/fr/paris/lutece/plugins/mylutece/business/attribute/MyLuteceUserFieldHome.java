/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.mylutece.business.attribute;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.List;
import java.util.Locale;

/**
 * 
 * MyLuteceUserFieldHome
 *
 */
public class MyLuteceUserFieldHome 
{
    private static IMyLuteceUserFieldDAO _dao = SpringContextService.getBean( "mylutece.myLuteceUserFieldDAO" );

	/**
	 * Load the user field
	 * @param nIdUserField ID
	 * @return MyLuteceUserField
	 */
	public static MyLuteceUserField findByPrimaryKey( int nIdUserField, Locale locale, Plugin plugin )
	{
		return _dao.load( nIdUserField, locale, plugin );
	}
		
	/**
	 * Insert a new user field
	 * @param userField the user field
	 */
	public static void create( MyLuteceUserField userField, Plugin plugin )
	{
		_dao.insert( userField, plugin );
	}
	
	/**
	 * Update an user field 
	 * @param attribute the attribute
	 */
	public static void update( MyLuteceUserField userField, Plugin plugin )
	{
		_dao.store( userField, plugin );
	}
	
	/**
	 * Delete an attribute
	 * @param nIdUserField the ID of the user field
	 */
	public static void remove( int nIdUserField, Plugin plugin )
	{
		_dao.delete( nIdUserField, plugin );
	}
	
	/**
	 * Delete all user fields from given id field
	 * @param nIdField id field
	 */
	public static void removeUserFieldsFromIdField( int nIdField, Plugin plugin )
	{
		_dao.deleteUserFieldsFromIdField( nIdField, plugin );
	}
	
	/**
	 * Delete all user fields from given id user
	 * @param nIdUser id user
	 */
	public static void removeUserFieldsFromIdUser( int nIdUser, Plugin plugin )
	{
		_dao.deleteUserFieldsFromIdUser( nIdUser, plugin );
	}
	
	/**
	 * Delete all user fields from given id attribute
	 * @param nIdAttribute id attribute
	 */
	public static void removeUserFieldsFromIdAttribute( int nIdAttribute, Plugin plugin )
	{
		_dao.deleteUserFieldsFromIdAttribute( nIdAttribute, plugin );
	}

	/**
	 * Load all the user field by a given ID user and a given ID attribute
	 * @param nIdUser the ID user
	 * @param nIdAttribute
	 * @return a list of adminuserfield
	 */
	public static List<MyLuteceUserField> selectUserFieldsByIdUserIdAttribute( int nIdUser, int nIdAttribute, Plugin plugin )
	{
		return _dao.selectUserFieldsByIdUserIdAttribute( nIdUser, nIdAttribute, plugin );
	}
	
	/**
	 * Load users by a given filter
	 * @param mlFieldFilter the filter
	 * @param plugin Plugin
	 * @return a list of users
	 */
	public static List<Integer> findUsersByFilter( MyLuteceUserFieldFilter mlFieldFilter, Plugin plugin )
	{
		return _dao.selectUsersByFilter( mlFieldFilter, plugin );
	}
}
