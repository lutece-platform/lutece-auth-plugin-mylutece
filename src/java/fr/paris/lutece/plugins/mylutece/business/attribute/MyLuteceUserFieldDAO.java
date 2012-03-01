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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * 
 * MyLuteceUserFieldDAO
 *
 */
public class MyLuteceUserFieldDAO implements IMyLuteceUserFieldDAO
{
	// CONSTANTS
	private static final String CONSTANT_PERCENT = "%";
	private static final String CONSTANT_OPEN_BRACKET = "(";
	private static final String CONSTANT_CLOSED_BRACKET = ")";
	
	// NEW PK
	private static final String SQL_QUERY_NEW_PK = " SELECT max(id_user_field) FROM mylutece_user_field ";
	
	// SELECT
	private static final String SQL_QUERY_SELECT = " SELECT auf.id_user_field, auf.id_user, auf.id_attribute, auf.id_field, auf.user_field_value, " +
			" a.type_class_name, a.title, a.help_message, a.is_mandatory, a.attribute_position, " +
			" af.title, af.DEFAULT_value, af.is_DEFAULT_value, af.field_position " +
			" FROM mylutece_user_field auf " +
			" INNER JOIN mylutece_attribute a ON auf.id_attribute = a.id_attribute " +
			" INNER JOIN mylutece_attribute_field af ON auf.id_field = af.id_field " +
			" WHERE auf.id_user_field = ? ";
	private static final String SQL_QUERY_SELECT_USER_FIELDS_BY_ID_USER_ID_ATTRIBUTE = " SELECT auf.id_user_field, auf.id_user, auf.id_attribute, auf.id_field, auf.user_field_value, " +
			" a.type_class_name, a.title, a.help_message, a.is_mandatory, a.attribute_position " +
			" FROM mylutece_user_field auf " +
			" INNER JOIN mylutece_attribute a ON a.id_attribute = auf.id_attribute " +
			" WHERE auf.id_user = ? AND auf.id_attribute = ? ";
	private static final String SQL_QUERY_SELECT_ID_USER = " SELECT id_user FROM mylutece_user_field WHERE id_attribute = ? AND id_field = ? AND user_field_value LIKE ? ";
	private static final String SQL_ID_ATTRIBUTE_AND_USER_FIELD_VALUE = " WHERE id_attribute = ? AND id_field = ? AND user_field_value LIKE ? ";
	private static final String SQL_AND_ID_USER_IN = " AND id_user IN ";
	
	// INSERT
	private static final String SQL_QUERY_INSERT = " INSERT INTO mylutece_user_field (id_user_field, id_user, id_attribute, id_field, user_field_value) " +
			" VALUES (?,?,?,?,?) ";
	
	// UPDATE
	private static final String SQL_QUERY_UPDATE = " UPDATE mylutece_user_field SET user_field_value = ? WHERE id_user_field = ? ";
	
	// DELETE
	private static final String SQL_QUERY_DELETE = " DELETE FROM mylutece_user_field WHERE id_user_field = ? ";
	private static final String SQL_QUERY_DELETE_FROM_ID_FIELD = " DELETE FROM mylutece_user_field WHERE id_field = ? ";
	private static final String SQL_QUERY_DELETE_FROM_ID_USER = " DELETE FROM mylutece_user_field WHERE id_user = ? ";
	private static final String SQL_QUERY_DELETE_FROM_ID_ATTRIBUTE = " DELETE FROM mylutece_user_field WHERE id_attribute = ? ";
	
	/**
     * Generate a new PK 
     * @return The new ID
     */
	private int newPrimaryKey( Plugin plugin )
    {
    	StringBuilder sbSQL = new StringBuilder( SQL_QUERY_NEW_PK );
        DAOUtil daoUtil = new DAOUtil( sbSQL.toString(  ), plugin );
        daoUtil.executeQuery(  );

        int nKey = 1;

        if ( daoUtil.next(  ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free(  );

        return nKey;
    }
	
	/**
	 * Load the user field
	 * @param nIdUserField ID
	 * @return MyLuteceUserField
	 */
	public MyLuteceUserField load( int nIdUserField, Locale locale, Plugin plugin )
	{
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nIdUserField );
        daoUtil.executeQuery(  );
        
        MyLuteceUserField userField = null;
        if ( daoUtil.next(  ) )
        {
        	userField = new MyLuteceUserField(  );
        	userField.setIdUserField( daoUtil.getInt( 1 ) );
        	userField.setValue( daoUtil.getString( 5 ) );
        	
        	// USER
        	userField.setUserId( daoUtil.getInt( 2 ) );
        	
        	// ATTRIBUTE
        	IAttribute attribute = null;
        	try
            {
        		attribute = (IAttribute) Class.forName( daoUtil.getString( 6 ) ).newInstance(  );
            }
            catch ( ClassNotFoundException e )
            {
                // class doesn't exist
                AppLogService.error( e );
            }
            catch ( InstantiationException e )
            {
                // Class is abstract or is an interface or haven't accessible
                // builder
                AppLogService.error( e );
            }
            catch ( IllegalAccessException e )
            {
                // can't access to the class
                AppLogService.error( e );
            }
            attribute.setIdAttribute( daoUtil.getInt( 3 ) );
            attribute.setTitle( daoUtil.getString( 7 ) );
            attribute.setHelpMessage( daoUtil.getString( 8 ) );
            attribute.setMandatory( daoUtil.getBoolean( 9 ) );
            attribute.setPosition( daoUtil.getInt( 10 ) );
            attribute.setAttributeType( locale );
            userField.setAttribute( attribute );
            
            // ATTRIBUTEFIELD
            AttributeField attributeField = new AttributeField(  );
            attributeField.setIdField( daoUtil.getInt( 4 ) );
            attributeField.setTitle( daoUtil.getString( 11 ) );
            attributeField.setValue( daoUtil.getString( 12 ) );
            attributeField.setDefaultValue( daoUtil.getBoolean( 13 ) );
            attributeField.setPosition( daoUtil.getInt( 14 ) );
            userField.setAttributeField( attributeField );
        }
        
        daoUtil.free(  );
        
        return userField;
	}
		
	/**
	 * Insert a new user field
	 * @param userField the user field
	 * @return new PK
	 */
	public void insert( MyLuteceUserField userField, Plugin plugin )
	{
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
		daoUtil.setInt( 1, newPrimaryKey( plugin ) );
		daoUtil.setInt( 2, userField.getUserId(  ) );
		daoUtil.setInt( 3, userField.getAttribute(  ).getIdAttribute(  ) );
		daoUtil.setInt( 4, userField.getAttributeField(  ).getIdField(  ) );
		daoUtil.setString( 5, userField.getValue(  ) );
		
		daoUtil.executeUpdate(  );
		daoUtil.free(  );
	}
	
	/**
	 * Update an user field 
	 * @param attribute the attribute
	 */
	public void store( MyLuteceUserField userField, Plugin plugin )
	{
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
		daoUtil.setString( 1, userField.getValue(  ) );
		daoUtil.setInt( 2, userField.getIdUserField(  ) );
		
		daoUtil.executeUpdate(  );
		daoUtil.free(  );
	}
	
	/**
	 * Delete an attribute
	 * @param nIdUserField the ID of the user field
	 */
	public void delete( int nIdUserField, Plugin plugin )
	{
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
		daoUtil.setInt( 1, nIdUserField );
		
		daoUtil.executeUpdate(  );
		daoUtil.free(  );
	}
	
	/**
	 * Delete all user fields from given id field
	 * @param nIdField id field
	 */
	public void deleteUserFieldsFromIdField( int nIdField, Plugin plugin )
	{
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_FROM_ID_FIELD, plugin );
		daoUtil.setInt( 1, nIdField );
		
		daoUtil.executeUpdate(  );
		daoUtil.free(  );
	}
	
	/**
	 * Delete all user fields from given id user
	 * @param nIdUser id user
	 */
	public void deleteUserFieldsFromIdUser( int nIdUser, Plugin plugin )
	{
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_FROM_ID_USER, plugin );
		daoUtil.setInt( 1, nIdUser );
		
		daoUtil.executeUpdate(  );
		daoUtil.free(  );
	}
	
	/**
	 * Delete all user fields from given id attribute
	 * @param nIdUser id user
	 */
	public void deleteUserFieldsFromIdAttribute( int nIdAttribute, Plugin plugin )
	{
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_FROM_ID_ATTRIBUTE, plugin );
		daoUtil.setInt( 1, nIdAttribute );
		
		daoUtil.executeUpdate(  );
		daoUtil.free(  );
	}

	/**
	 * Load all the user field by a given ID user
	 * @param nIdUser the ID user
	 * @param nIdAttribute the ID attribute
	 * @return a list of adminuserfield
	 */
	public List<MyLuteceUserField> selectUserFieldsByIdUserIdAttribute( int nIdUser, int nIdAttribute, Plugin plugin )
	{
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_USER_FIELDS_BY_ID_USER_ID_ATTRIBUTE, plugin );
        daoUtil.setInt( 1, nIdUser );
        daoUtil.setInt( 2, nIdAttribute );
        daoUtil.executeQuery(  );
        
        List<MyLuteceUserField> listUserFields = new ArrayList<MyLuteceUserField>(  );
        while ( daoUtil.next(  ) )
        {
        	MyLuteceUserField userField = new MyLuteceUserField(  );
        	userField.setIdUserField( daoUtil.getInt( 1 ) );
        	userField.setValue( daoUtil.getString( 5 ) );
        	
        	// USER
        	userField.setUserId( nIdUser );
        	
        	// ATTRIBUTE
        	IAttribute attribute = null;
        	try
            {
        		attribute = (IAttribute) Class.forName( daoUtil.getString( 6 ) ).newInstance(  );
            }
            catch ( ClassNotFoundException e )
            {
                // class doesn't exist
                AppLogService.error( e );
            }
            catch ( InstantiationException e )
            {
                // Class is abstract or is an interface or haven't accessible
                // builder
                AppLogService.error( e );
            }
            catch ( IllegalAccessException e )
            {
                // can't access to the class
                AppLogService.error( e );
            }
            attribute.setIdAttribute( nIdAttribute );
            attribute.setTitle( daoUtil.getString( 7 ) );
            attribute.setHelpMessage( daoUtil.getString( 8 ) );
            attribute.setMandatory( daoUtil.getBoolean( 9 ) );
            attribute.setPosition( daoUtil.getInt( 10 ) );
            userField.setAttribute( attribute );
            
            // ATTRIBUTEFIELD
            AttributeField attributeField = new AttributeField(  );
            attributeField.setIdField( daoUtil.getInt( 4 ) );
            userField.setAttributeField( attributeField );
            
            listUserFields.add( userField );
        }
        
        daoUtil.free(  );
        
        return listUserFields;
	}

	/**
	 * Load users by a given filter
	 * @param mlFieldFilter the filter
	 * @param plugin Plugin
	 * @return a list of users
	 */
	public List<Integer> selectUsersByFilter( MyLuteceUserFieldFilter mlFieldFilter, Plugin plugin )
	{
		List<MyLuteceUserField> listUserFields = mlFieldFilter.getListUserFields(  );
		if ( listUserFields == null || ( listUserFields != null && listUserFields.size(  ) == 0 ) ) 
		{
			return null;
		}
		
		List<Integer> listUsers = new ArrayList<Integer>(  );
		StringBuilder sbSQL = new StringBuilder(  );
		for ( int i = 1; i <= listUserFields.size(  ); i++ )
		{
			if ( i == 1 )
			{
				sbSQL.append( SQL_QUERY_SELECT_ID_USER );
			}
			else
			{
				sbSQL.append( CONSTANT_OPEN_BRACKET + SQL_QUERY_SELECT_ID_USER );
			}
			
			if ( i != listUserFields.size(  ) )
			{
				sbSQL.append( SQL_AND_ID_USER_IN );
			}
		}
		
		for ( int i = 2; i <= listUserFields.size(  ); i++ )
		{
			sbSQL.append( CONSTANT_CLOSED_BRACKET );
		}
		
		DAOUtil daoUtil = new DAOUtil( sbSQL.toString(  ), plugin );
		
		int nbCount = 1;
		for ( MyLuteceUserField userField : listUserFields )
		{
			daoUtil.setInt( nbCount++, userField.getAttribute(  ).getIdAttribute(  ) );
			daoUtil.setInt( nbCount++, userField.getAttributeField(  ).getIdField(  ) );
			daoUtil.setString( nbCount++, CONSTANT_PERCENT + userField.getValue(  ) + CONSTANT_PERCENT );
		}
		
		daoUtil.executeQuery(  );
		
		while( daoUtil.next(  ) )
		{
			listUsers.add( daoUtil.getInt( 1 ) );
		}
		
		daoUtil.free(  );
		
		return listUsers;
	}
}
