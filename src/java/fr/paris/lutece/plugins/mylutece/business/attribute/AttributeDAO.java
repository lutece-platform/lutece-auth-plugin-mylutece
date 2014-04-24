/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 *
 * AttributeDAO
 *
 */
public class AttributeDAO implements IAttributeDAO
{
    // NEW PK
    private static final String SQL_QUERY_NEW_PK = " SELECT max(id_attribute) FROM mylutece_attribute ";

    // NEW POSITION
    private static final String SQL_QUERY_NEW_POSITION = "SELECT MAX(attribute_position)" +
        " FROM mylutece_attribute ";

    // SELECT
    private static final String SQL_QUERY_SELECT = " SELECT type_class_name, id_attribute, title, help_message, is_mandatory, is_shown_in_search, attribute_position, plugin_name " +
        " FROM mylutece_attribute WHERE id_attribute = ? ";
    private static final String SQL_QUERY_SELECT_ALL = " SELECT id_attribute, type_class_name, title, help_message, is_mandatory, is_shown_in_search, attribute_position, anonymize, plugin_name " +
        " FROM mylutece_attribute ORDER BY attribute_position ";
    private static final String SQL_QUERY_SELECT_PLUGIN_ATTRIBUTES = " SELECT id_attribute, type_class_name, title, help_message, is_mandatory, is_shown_in_search, attribute_position " +
        " FROM mylutece_attribute WHERE plugin_name = ? ORDER BY attribute_position ";
    private static final String SQL_QUERY_SELECT_CORE_ATTRIBUTES = " SELECT id_attribute, type_class_name, title, help_message, is_mandatory, is_shown_in_search, attribute_position " +
        " FROM mylutece_attribute WHERE plugin_name IS NULL OR plugin_name = '' ORDER BY attribute_position ";

    // INSERT
    private static final String SQL_QUERY_INSERT = " INSERT INTO mylutece_attribute (id_attribute, type_class_name, title, help_message, is_mandatory, is_shown_in_search, attribute_position)" +
        " VALUES (?,?,?,?,?,?,?) ";
    private static final String SQL_INSERT_ANONYMIZATION_STATUS_USER_FILED = " INSERT INTO mylutece_user_anonymize_field (field_name, anonymize) VALUES (?,?) ";

    // UPDATE
    private static final String SQL_QUERY_UPDATE = " UPDATE mylutece_attribute SET title = ?, help_message = ?, is_mandatory = ?, is_shown_in_search = ?, attribute_position = ? " +
        " WHERE id_attribute = ? ";
    private static final String SQL_QUERY_UPDATE_ANONYMIZATION = " UPDATE mylutece_attribute SET anonymize = ? WHERE id_attribute = ? ";

    // DELETE
    private static final String SQL_QUERY_DELETE = " DELETE FROM mylutece_attribute WHERE id_attribute = ?";
    private static final String SQL_DELETE_ANONYMIZATION_STATUS_USER_FILED = " DELETE FROM mylutece_user_anonymize_field WHERE field_name = ? ";

    // NEW PK

    // Anonymization static field
    private static final String SQL_SELECT_ANONYMIZATION_STATUS_USER_FILED = "SELECT field_name, anonymize from mylutece_user_anonymize_field";
    private static final String SQL_UPDATE_ANONYMIZATION_STATUS_USER_FILED = "UPDATE mylutece_user_anonymize_field SET anonymize = ? WHERE field_name = ? ";

    /**
     * Generate a new PK
     * @param plugin The plugin
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
     * Generates a new field position
     * @param plugin The plugin
     * @return the new entry position
     */
    private int newPosition( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_POSITION, plugin );
        daoUtil.executeQuery(  );

        int nPos;

        if ( !daoUtil.next(  ) )
        {
            // if the table is empty
            nPos = 1;
        }

        nPos = daoUtil.getInt( 1 ) + 1;
        daoUtil.free(  );

        return nPos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAttribute load( int nIdAttribute, Locale locale, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nIdAttribute );
        daoUtil.executeQuery(  );

        IAttribute attribute = null;

        if ( daoUtil.next(  ) )
        {
            try
            {
                attribute = (IAttribute) Class.forName( daoUtil.getString( 1 ) ).newInstance(  );
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
                // can't access to rhe class
                AppLogService.error( e );
            }

            attribute.setIdAttribute( daoUtil.getInt( 2 ) );
            attribute.setTitle( daoUtil.getString( 3 ) );
            attribute.setHelpMessage( daoUtil.getString( 4 ) );
            attribute.setMandatory( daoUtil.getBoolean( 5 ) );
            attribute.setShownInSearch( daoUtil.getBoolean( 6 ) );
            attribute.setPosition( daoUtil.getInt( 7 ) );
            attribute.setAttributeType( locale );

            Plugin pluginAttribute = PluginService.getPlugin( daoUtil.getString( 8 ) );
            attribute.setPlugin( pluginAttribute );
        }

        daoUtil.free(  );

        return attribute;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public int insert( IAttribute attribute, Plugin plugin )
    {
        int nNewPrimaryKey = newPrimaryKey( plugin );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        daoUtil.setInt( 1, nNewPrimaryKey );
        daoUtil.setString( 2, attribute.getClass(  ).getName(  ) );
        daoUtil.setString( 3, attribute.getTitle(  ) );
        daoUtil.setString( 4, attribute.getHelpMessage(  ) );
        daoUtil.setBoolean( 5, attribute.isMandatory(  ) );
        daoUtil.setBoolean( 6, attribute.isShownInSearch(  ) );
        daoUtil.setInt( 7, newPosition( plugin ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );

        return nNewPrimaryKey;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void store( IAttribute attribute, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setString( 1, attribute.getTitle(  ) );
        daoUtil.setString( 2, attribute.getHelpMessage(  ) );
        daoUtil.setBoolean( 3, attribute.isMandatory(  ) );
        daoUtil.setBoolean( 4, attribute.isShownInSearch(  ) );
        daoUtil.setInt( 5, attribute.getPosition(  ) );
        daoUtil.setInt( 6, attribute.getIdAttribute(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void delete( int nIdAttribute, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdAttribute );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public List<IAttribute> selectAll( Locale locale, Plugin plugin )
    {
        List<IAttribute> listAttributes = new ArrayList<IAttribute>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_ALL, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            IAttribute attribute = null;

            try
            {
                attribute = (IAttribute) Class.forName( daoUtil.getString( 2 ) ).newInstance(  );
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
                // can't access to rhe class
                AppLogService.error( e );
            }

            attribute.setIdAttribute( daoUtil.getInt( 1 ) );
            attribute.setTitle( daoUtil.getString( 3 ) );
            attribute.setHelpMessage( daoUtil.getString( 4 ) );
            attribute.setMandatory( daoUtil.getBoolean( 5 ) );
            attribute.setShownInSearch( daoUtil.getBoolean( 6 ) );
            attribute.setPosition( daoUtil.getInt( 7 ) );
            attribute.setAnonymize( daoUtil.getBoolean( 8 ) );
            attribute.setAttributeType( locale );

            Plugin pluginAttribute = PluginService.getPlugin( daoUtil.getString( 9 ) );
            attribute.setPlugin( pluginAttribute );

            listAttributes.add( attribute );
        }

        daoUtil.free(  );

        return listAttributes;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public List<IAttribute> selectPluginAttributes( String strPluginName, Locale locale, Plugin plugin )
    {
        List<IAttribute> listAttributes = new ArrayList<IAttribute>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_PLUGIN_ATTRIBUTES, plugin );
        daoUtil.setString( 1, strPluginName );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            IAttribute attribute = null;

            try
            {
                attribute = (IAttribute) Class.forName( daoUtil.getString( 2 ) ).newInstance(  );
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
                // can't access to rhe class
                AppLogService.error( e );
            }

            attribute.setIdAttribute( daoUtil.getInt( 1 ) );
            attribute.setTitle( daoUtil.getString( 3 ) );
            attribute.setHelpMessage( daoUtil.getString( 4 ) );
            attribute.setMandatory( daoUtil.getBoolean( 5 ) );
            attribute.setShownInSearch( daoUtil.getBoolean( 6 ) );
            attribute.setPosition( daoUtil.getInt( 7 ) );
            attribute.setAttributeType( locale );

            Plugin pluginAttribute = PluginService.getPlugin( strPluginName );
            attribute.setPlugin( pluginAttribute );

            listAttributes.add( attribute );
        }

        daoUtil.free(  );

        return listAttributes;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public List<IAttribute> selectMyLuteceAttributes( Locale locale, Plugin plugin )
    {
        List<IAttribute> listAttributes = new ArrayList<IAttribute>(  );
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_CORE_ATTRIBUTES, plugin );
        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            IAttribute attribute = null;

            try
            {
                attribute = (IAttribute) Class.forName( daoUtil.getString( 2 ) ).newInstance(  );
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
                // can't access to rhe class
                AppLogService.error( e );
            }

            attribute.setIdAttribute( daoUtil.getInt( 1 ) );
            attribute.setTitle( daoUtil.getString( 3 ) );
            attribute.setHelpMessage( daoUtil.getString( 4 ) );
            attribute.setMandatory( daoUtil.getBoolean( 5 ) );
            attribute.setShownInSearch( daoUtil.getBoolean( 6 ) );
            attribute.setPosition( daoUtil.getInt( 7 ) );
            attribute.setAttributeType( locale );

            listAttributes.add( attribute );
        }

        daoUtil.free(  );

        return listAttributes;
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public void updateAttributeAnonymization( int nIdAttribute, boolean bAnonymize, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE_ANONYMIZATION, plugin );
        daoUtil.setBoolean( 1, bAnonymize );
        daoUtil.setInt( 2, nIdAttribute );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Boolean> selectAnonymizationStatusUserStaticField( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_SELECT_ANONYMIZATION_STATUS_USER_FILED, plugin );
        daoUtil.executeQuery(  );

        Map<String, Boolean> resultMap = new HashMap<String, Boolean>(  );

        while ( daoUtil.next(  ) )
        {
            resultMap.put( daoUtil.getString( 1 ), daoUtil.getBoolean( 2 ) );
        }

        daoUtil.free(  );

        return resultMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAnonymizationStatusUserField( String strFieldName, boolean bAnonymizeFiled, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_INSERT_ANONYMIZATION_STATUS_USER_FILED, plugin );
        daoUtil.setString( 1, strFieldName );
        daoUtil.setBoolean( 2, bAnonymizeFiled );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAnonymizationStatusUserField( String strFieldName, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_DELETE_ANONYMIZATION_STATUS_USER_FILED, plugin );
        daoUtil.setString( 1, strFieldName );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAnonymizationStatusUserStaticField( String strFieldName, boolean bAnonymizeFiled, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_UPDATE_ANONYMIZATION_STATUS_USER_FILED, plugin );
        daoUtil.setBoolean( 1, bAnonymizeFiled );
        daoUtil.setString( 2, strFieldName );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
