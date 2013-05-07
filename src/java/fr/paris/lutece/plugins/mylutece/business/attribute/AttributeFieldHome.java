/*
 * Copyright (c) 2002-2013, Mairie de Paris
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

/**
 * 
 * AttributeFieldHome
 *
 */
public class AttributeFieldHome 
{
    private static IAttributeFieldDAO _dao = SpringContextService.getBean( "mylutece.myLuteceAttributeFieldDAO" );

    /**
     * Load attribute field
     * @param nIdField ID Field
     * @param plugin The plugin
     * @return Attribute Field
     */
	public static AttributeField findByPrimaryKey( int nIdField, Plugin plugin )
	{
		return _dao.load( nIdField, plugin );
	}

    /**
     * Load the lists of attribute field associated to an attribute
     * @param nIdAttribute the ID attribute
     * @param plugin The plugin
     * @return the list of attribute fields
     */
	public static List<AttributeField> selectAttributeFieldsByIdAttribute( int nIdAttribute, Plugin plugin )
	{
		return _dao.selectAttributeFieldsByIdAttribute( nIdAttribute, plugin );
	}

    /**
     * Load the attribute associated to the id field
     * @param nIdField the id field
     * @param plugin The plugin
     * @return attribute
     */
	public static IAttribute selectAttributeByIdField( int nIdField, Plugin plugin )
	{
		return _dao.selectAttributeByIdField( nIdField, plugin );
	}

    /**
     * Insert an new attribute field
     * @param attributeField the attribute field
     * @param plugin The plugin
     * @return @return new PK
     */
	public static int create( AttributeField attributeField, Plugin plugin )
	{
		return _dao.insert( attributeField, plugin );
	}

    /**
     * Update an attribute field
     * @param attributeField the attribute field
     * @param plugin The plugin
     */
	public static void update( AttributeField attributeField, Plugin plugin )
	{
		_dao.store( attributeField, plugin );
	}

    /**
     * Delete an attribute field
     * @param nIdField the attribute field id
     * @param plugin The plugin
     */
	public static void remove( int nIdField, Plugin plugin )
	{
		_dao.delete( nIdField, plugin );
	}

    /**
     * Delete all attribute field from an attribute id
     * @param nIdAttribute the ID attribute
     * @param plugin The plugin
     */
	public static void removeAttributeFieldsFromIdAttribute( int nIdAttribute, Plugin plugin )
	{
		_dao.deleteAttributeFieldsFromIdAttribute( nIdAttribute, plugin );
	}
}
