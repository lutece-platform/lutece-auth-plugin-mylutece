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
package fr.paris.lutece.plugins.mylutece.web.attribute;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.mylutece.business.attribute.AttributeField;
import fr.paris.lutece.plugins.mylutece.business.attribute.AttributeFieldHome;
import fr.paris.lutece.plugins.mylutece.business.attribute.AttributeHome;
import fr.paris.lutece.plugins.mylutece.business.attribute.AttributeType;
import fr.paris.lutece.plugins.mylutece.business.attribute.IAttribute;
import fr.paris.lutece.plugins.mylutece.business.attribute.MyLuteceUserFieldHome;
import fr.paris.lutece.plugins.mylutece.service.MyLutecePlugin;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.url.UrlItem;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Management of the attributes of the MyLutece users and of the choices of the attributes that have some (combo boxes). Every mutation is
 * an action posted with the security token of the view that shows its form.
 */
@RequestScoped
@Named( "myluteceAttributeJspBean" )
@Controller( controllerJsp = AttributeJspBean.CONTROLLER_JSP, controllerPath = AttributeJspBean.CONTROLLER_PATH, right = AttributeJspBean.RIGHT_MANAGE_MYLUTECE, securityTokenEnabled = true )
public class AttributeJspBean extends MVCAdminJspBean
{
    /**
     * Right to manage MyLutece
     */
    public static final String RIGHT_MANAGE_MYLUTECE = "MYLUTECE_MANAGEMENT";
    static final String CONTROLLER_JSP = "ManageAttributes.jsp";
    static final String CONTROLLER_PATH = "jsp/admin/plugins/mylutece/attribute/";
    private static final long serialVersionUID = 4386421549384731024L;

    // Views
    static final String VIEW_MANAGE_ATTRIBUTES = "manageAttributes";
    static final String VIEW_CREATE_ATTRIBUTE = "createAttribute";
    static final String VIEW_MODIFY_ATTRIBUTE = "modifyAttribute";
    static final String VIEW_CONFIRM_REMOVE_ATTRIBUTE = "confirmRemoveAttribute";
    static final String VIEW_CREATE_ATTRIBUTE_FIELD = "createAttributeField";
    static final String VIEW_MODIFY_ATTRIBUTE_FIELD = "modifyAttributeField";
    static final String VIEW_CONFIRM_REMOVE_ATTRIBUTE_FIELD = "confirmRemoveAttributeField";

    // Actions: a form view and its action share their name, so the token of the view is the token of the action
    static final String ACTION_CREATE_ATTRIBUTE = VIEW_CREATE_ATTRIBUTE;
    static final String ACTION_MODIFY_ATTRIBUTE = VIEW_MODIFY_ATTRIBUTE;
    static final String ACTION_REMOVE_ATTRIBUTE = "removeAttribute";
    static final String ACTION_MOVE_UP_ATTRIBUTE = "moveUpAttribute";
    static final String ACTION_MOVE_DOWN_ATTRIBUTE = "moveDownAttribute";
    static final String ACTION_CREATE_ATTRIBUTE_FIELD = VIEW_CREATE_ATTRIBUTE_FIELD;
    static final String ACTION_MODIFY_ATTRIBUTE_FIELD = VIEW_MODIFY_ATTRIBUTE_FIELD;
    static final String ACTION_REMOVE_ATTRIBUTE_FIELD = "removeAttributeField";
    static final String ACTION_MOVE_UP_ATTRIBUTE_FIELD = "moveUpAttributeField";
    static final String ACTION_MOVE_DOWN_ATTRIBUTE_FIELD = "moveDownAttributeField";

    // Token of the list of the attributes, shared by the two moves posted from it
    static final String TOKEN_ORDER_ATTRIBUTES = "orderAttributes";

    // Parameters
    static final String PARAMETER_ATTRIBUTE_TYPE_CLASS_NAME = "attribute_type_class_name";
    static final String PARAMETER_ID_ATTRIBUTE = "id_attribute";
    static final String PARAMETER_ID_FIELD = "id_field";
    private static final String PARAMETER_CANCEL = "cancel";
    private static final String PARAMETER_APPLY = "apply";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_VALUE = "value";
    private static final String PARAMETER_DEFAULT_VALUE = "default_value";

    // Marks
    private static final String MARK_ATTRIBUTE_TYPES_LIST = "attribute_types_list";
    private static final String MARK_ATTRIBUTE_TYPE = "attribute_type";
    private static final String MARK_ATTRIBUTES_LIST = "attributes_list";
    private static final String MARK_ATTRIBUTE = "attribute";
    private static final String MARK_ATTRIBUTE_FIELDS_LIST = "attribute_fields_list";
    private static final String MARK_ATTRIBUTE_FIELD = "attribute_field";

    // Properties
    private static final String PROPERTY_MANAGE_ATTRIBUTES_PAGETITLE = "mylutece.manage_attributes.pageTitle";
    private static final String PROPERTY_CREATE_ATTRIBUTE_FIELDS_PAGETITLE = "mylutece.create_attribute_field.pageTitle";
    private static final String PROPERTY_MODIFY_ATTRIBUTE_FIELDS_PAGETITLE = "mylutece.modify_attribute_field.pageTitle";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_ATTRIBUTE = "mylutece.manage_attributes.message.confirmRemoveAttribute";
    private static final String MESSAGE_CONFIRM_REMOVE_ATTRIBUTE_FIELD = "mylutece.modify_attribute.message.removeAttributeField";
    private static final String MESSAGE_ATTRIBUTE_NOT_FOUND = "mylutece.message.error.attributeNotFound";
    private static final String MESSAGE_ATTRIBUTE_FIELD_NOT_FOUND = "mylutece.message.error.attributeFieldNotFound";
    private static final String MESSAGE_ATTRIBUTE_TYPE_NOT_FOUND = "mylutece.message.error.attributeTypeNotFound";

    // Templates
    private static final String TEMPLATE_MANAGE_ATTRIBUTES = "admin/plugins/mylutece/attribute/manage_attributes.html";
    private static final String TEMPLATE_CREATE_ATTRIBUTE_FIELD = "admin/plugins/mylutece/attribute/create_attribute_field.html";
    private static final String TEMPLATE_MODIFY_ATTRIBUTE_FIELD = "admin/plugins/mylutece/attribute/modify_attribute_field.html";

    @Inject
    @Any
    private Instance<IAttribute> _attributeTypes;

    /**
     * Get the list of the attributes
     *
     * @param request
     *            HttpServletRequest
     * @param model
     *            the model of the page
     * @return the html of the list
     */
    @View( value = VIEW_MANAGE_ATTRIBUTES, defaultView = true, securityTokenAction = TOKEN_ORDER_ATTRIBUTES )
    public String getManageAttributes( HttpServletRequest request, Models model )
    {
        List<AttributeType> listAttributeTypes = new ArrayList<>( );

        for ( IAttribute attribute : _attributeTypes.stream( ).toList( ) )
        {
            attribute.setAttributeType( getLocale( ) );
            listAttributeTypes.add( attribute.getAttributeType( ) );
        }

        model.put( MARK_ATTRIBUTES_LIST, AttributeHome.findAll( getLocale( ), getMyLutecePlugin( ) ) );
        model.put( MARK_ATTRIBUTE_TYPES_LIST, listAttributeTypes );

        return getPage( PROPERTY_MANAGE_ATTRIBUTES_PAGETITLE, TEMPLATE_MANAGE_ATTRIBUTES );
    }

    /**
     * Get the creation form of an attribute of the type given by its class name
     *
     * @param request
     *            HttpServletRequest
     * @param model
     *            the model of the page
     * @return the html of the form
     */
    @View( VIEW_CREATE_ATTRIBUTE )
    public String getCreateAttribute( HttpServletRequest request, Models model )
    {
        IAttribute attribute = newAttribute( request.getParameter( PARAMETER_ATTRIBUTE_TYPE_CLASS_NAME ) );

        if ( attribute == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ATTRIBUTE_TYPE_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        attribute.setAttributeType( getLocale( ) );

        model.put( MARK_ATTRIBUTE_TYPE, attribute.getAttributeType( ) );

        return getPage( attribute.getPropertyCreatePageTitle( ), attribute.getTemplateCreateAttribute( ) );
    }

    /**
     * Create an attribute
     *
     * @param request
     *            HttpServletRequest
     * @return the url of the next page
     */
    @Action( ACTION_CREATE_ATTRIBUTE )
    public String doCreateAttribute( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_CANCEL ) != null )
        {
            return redirectView( request, VIEW_MANAGE_ATTRIBUTES );
        }

        return redirect( request, createAttribute( request ) );
    }

    /**
     * Create the attribute described by the request
     *
     * @param request
     *            HttpServletRequest
     * @return the url of the next page: the error message, the modification form ("apply") or the list
     */
    String createAttribute( HttpServletRequest request )
    {
        String strClassName = request.getParameter( PARAMETER_ATTRIBUTE_TYPE_CLASS_NAME );
        IAttribute attribute = newAttribute( strClassName );

        if ( attribute == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_ATTRIBUTE_TYPE_NOT_FOUND, AdminMessage.TYPE_STOP );
        }

        UrlItem urlCreate = new UrlItem( AppPathService.getBaseUrl( request ) + getViewFullUrl( VIEW_CREATE_ATTRIBUTE ) );
        urlCreate.addParameter( PARAMETER_ATTRIBUTE_TYPE_CLASS_NAME, strClassName );

        String strError = attribute.setAttributeData( request, urlCreate.getUrl( ) );

        if ( strError != null )
        {
            return strError;
        }

        Plugin plugin = getMyLutecePlugin( );
        attribute.setIdAttribute( AttributeHome.create( attribute, plugin ) );

        if ( attribute.getListAttributeFields( ) != null )
        {
            for ( AttributeField attributeField : attribute.getListAttributeFields( ) )
            {
                attributeField.setAttribute( attribute );
                AttributeFieldHome.create( attributeField, plugin );
            }
        }

        if ( request.getParameter( PARAMETER_APPLY ) != null )
        {
            return getModifyAttributeUrl( attribute.getIdAttribute( ) );
        }

        return getViewUrl( VIEW_MANAGE_ATTRIBUTES );
    }

    /**
     * Get the modification form of an attribute, with the list of its choices
     *
     * @param request
     *            HttpServletRequest
     * @param model
     *            the model of the page
     * @return the html of the form
     */
    @View( VIEW_MODIFY_ATTRIBUTE )
    public String getModifyAttribute( HttpServletRequest request, Models model )
    {
        int nIdAttribute = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ATTRIBUTE ), -1 );
        IAttribute attribute = AttributeHome.findByPrimaryKey( nIdAttribute, getLocale( ), getMyLutecePlugin( ) );

        if ( attribute == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ATTRIBUTE_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        model.put( MARK_ATTRIBUTE, attribute );
        model.put( MARK_ATTRIBUTE_FIELDS_LIST, AttributeFieldHome.selectAttributeFieldsByIdAttribute( nIdAttribute, getMyLutecePlugin( ) ) );

        return getPage( attribute.getPropertyModifyPageTitle( ), attribute.getTemplateModifyAttribute( ) );
    }

    /**
     * Modify an attribute
     *
     * @param request
     *            HttpServletRequest
     * @return the url of the next page
     */
    @Action( ACTION_MODIFY_ATTRIBUTE )
    public String doModifyAttribute( HttpServletRequest request )
    {
        if ( request.getParameter( PARAMETER_CANCEL ) != null )
        {
            return redirectView( request, VIEW_MANAGE_ATTRIBUTES );
        }

        int nIdAttribute = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ATTRIBUTE ), -1 );
        Plugin plugin = getMyLutecePlugin( );
        IAttribute attribute = AttributeHome.findByPrimaryKey( nIdAttribute, getLocale( ), plugin );

        if ( attribute == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ATTRIBUTE_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        attribute.setListAttributeFields( AttributeFieldHome.selectAttributeFieldsByIdAttribute( nIdAttribute, plugin ) );

        String strError = attribute.setAttributeData( request, AppPathService.getBaseUrl( request ) + getControllerPath( ) + getModifyAttributeUrl( nIdAttribute ) );

        if ( strError != null )
        {
            return redirect( request, strError );
        }

        AttributeHome.update( attribute, plugin );

        if ( attribute.getListAttributeFields( ) != null )
        {
            for ( AttributeField attributeField : attribute.getListAttributeFields( ) )
            {
                attributeField.setAttribute( attribute );
                AttributeFieldHome.update( attributeField, plugin );
            }
        }

        if ( request.getParameter( PARAMETER_APPLY ) != null )
        {
            return redirect( request, VIEW_MODIFY_ATTRIBUTE, PARAMETER_ID_ATTRIBUTE, nIdAttribute );
        }

        return redirectView( request, VIEW_MANAGE_ATTRIBUTES );
    }

    /**
     * Ask the confirmation of the removal of an attribute
     *
     * @param request
     *            HttpServletRequest
     * @return the url of the confirmation message
     */
    @View( value = VIEW_CONFIRM_REMOVE_ATTRIBUTE, securityTokenAction = ACTION_REMOVE_ATTRIBUTE )
    public String getConfirmRemoveAttribute( HttpServletRequest request )
    {
        int nIdAttribute = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ATTRIBUTE ), -1 );

        if ( AttributeHome.findByPrimaryKey( nIdAttribute, getLocale( ), getMyLutecePlugin( ) ) == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ATTRIBUTE_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_ATTRIBUTE ) );
        url.addParameter( PARAMETER_ID_ATTRIBUTE, nIdAttribute );

        return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_ATTRIBUTE, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Remove an attribute, its choices and the values the users gave it
     *
     * @param request
     *            HttpServletRequest
     * @return the url of the list
     */
    @Action( ACTION_REMOVE_ATTRIBUTE )
    public String doRemoveAttribute( HttpServletRequest request )
    {
        int nIdAttribute = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ATTRIBUTE ), -1 );
        Plugin plugin = getMyLutecePlugin( );

        if ( nIdAttribute != -1 )
        {
            AttributeHome.remove( nIdAttribute, plugin );
            AttributeFieldHome.removeAttributeFieldsFromIdAttribute( nIdAttribute, plugin );
            MyLuteceUserFieldHome.removeUserFieldsFromIdAttribute( nIdAttribute, plugin );
        }

        return redirectView( request, VIEW_MANAGE_ATTRIBUTES );
    }

    /**
     * Move an attribute up in the list
     *
     * @param request
     *            HttpServletRequest
     * @return the url of the list
     */
    @Action( value = ACTION_MOVE_UP_ATTRIBUTE, securityTokenAction = TOKEN_ORDER_ATTRIBUTES )
    public String doMoveUpAttribute( HttpServletRequest request )
    {
        moveAttribute( NumberUtils.toInt( request.getParameter( PARAMETER_ID_ATTRIBUTE ), -1 ), -1 );

        return redirectView( request, VIEW_MANAGE_ATTRIBUTES );
    }

    /**
     * Move an attribute down in the list
     *
     * @param request
     *            HttpServletRequest
     * @return the url of the list
     */
    @Action( value = ACTION_MOVE_DOWN_ATTRIBUTE, securityTokenAction = TOKEN_ORDER_ATTRIBUTES )
    public String doMoveDownAttribute( HttpServletRequest request )
    {
        moveAttribute( NumberUtils.toInt( request.getParameter( PARAMETER_ID_ATTRIBUTE ), -1 ), 1 );

        return redirectView( request, VIEW_MANAGE_ATTRIBUTES );
    }

    /**
     * Get the creation form of a choice of an attribute
     *
     * @param request
     *            HttpServletRequest
     * @param model
     *            the model of the page
     * @return the html of the form
     */
    @View( VIEW_CREATE_ATTRIBUTE_FIELD )
    public String getCreateAttributeField( HttpServletRequest request, Models model )
    {
        IAttribute attribute = AttributeHome.findByPrimaryKey( NumberUtils.toInt( request.getParameter( PARAMETER_ID_ATTRIBUTE ), -1 ), getLocale( ),
                getMyLutecePlugin( ) );

        if ( attribute == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ATTRIBUTE_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        model.put( MARK_ATTRIBUTE, attribute );

        return getPage( PROPERTY_CREATE_ATTRIBUTE_FIELDS_PAGETITLE, TEMPLATE_CREATE_ATTRIBUTE_FIELD );
    }

    /**
     * Create a choice of an attribute
     *
     * @param request
     *            HttpServletRequest
     * @return the url of the next page
     */
    @Action( ACTION_CREATE_ATTRIBUTE_FIELD )
    public String doCreateAttributeField( HttpServletRequest request )
    {
        return redirect( request, createAttributeField( request ) );
    }

    /**
     * Create the choice described by the request
     *
     * @param request
     *            HttpServletRequest
     * @return the url of the next page: the error message or the modification form of the attribute
     */
    String createAttributeField( HttpServletRequest request )
    {
        int nIdAttribute = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ATTRIBUTE ), -1 );

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            String strTitle = request.getParameter( PARAMETER_TITLE );
            String strValue = request.getParameter( PARAMETER_VALUE );

            if ( StringUtils.isEmpty( strTitle ) || StringUtils.isEmpty( strValue ) )
            {
                UrlItem urlCreate = new UrlItem( AppPathService.getBaseUrl( request ) + getViewFullUrl( VIEW_CREATE_ATTRIBUTE_FIELD ) );
                urlCreate.addParameter( PARAMETER_ID_ATTRIBUTE, nIdAttribute );

                return AdminMessageService.getMessageUrlReturningTo( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP, urlCreate.getUrl( ) );
            }

            Plugin plugin = getMyLutecePlugin( );
            IAttribute attribute = AttributeHome.findByPrimaryKey( nIdAttribute, getLocale( ), plugin );

            if ( attribute == null )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_ATTRIBUTE_NOT_FOUND, AdminMessage.TYPE_STOP );
            }

            AttributeField attributeField = new AttributeField( );
            attributeField.setTitle( strTitle );
            attributeField.setValue( strValue );
            attributeField.setDefaultValue( request.getParameter( PARAMETER_DEFAULT_VALUE ) != null );
            attributeField.setAttribute( attribute );
            AttributeFieldHome.create( attributeField, plugin );
        }

        return getModifyAttributeUrl( nIdAttribute );
    }

    /**
     * Get the modification form of a choice of an attribute
     *
     * @param request
     *            HttpServletRequest
     * @param model
     *            the model of the page
     * @return the html of the form
     */
    @View( VIEW_MODIFY_ATTRIBUTE_FIELD )
    public String getModifyAttributeField( HttpServletRequest request, Models model )
    {
        Plugin plugin = getMyLutecePlugin( );
        int nIdAttribute = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ATTRIBUTE ), -1 );
        IAttribute attribute = AttributeHome.findByPrimaryKey( nIdAttribute, getLocale( ), plugin );
        AttributeField attributeField = AttributeFieldHome.findByPrimaryKey( NumberUtils.toInt( request.getParameter( PARAMETER_ID_FIELD ), -1 ), plugin );

        if ( attribute == null || attributeField == null || attributeField.getAttribute( ) == null
                || attributeField.getAttribute( ).getIdAttribute( ) != nIdAttribute )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ATTRIBUTE_FIELD_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        model.put( MARK_ATTRIBUTE_FIELD, attributeField );
        model.put( MARK_ATTRIBUTE, attribute );

        return getPage( PROPERTY_MODIFY_ATTRIBUTE_FIELDS_PAGETITLE, TEMPLATE_MODIFY_ATTRIBUTE_FIELD );
    }

    /**
     * Modify a choice of an attribute
     *
     * @param request
     *            HttpServletRequest
     * @return the url of the next page
     */
    @Action( ACTION_MODIFY_ATTRIBUTE_FIELD )
    public String doModifyAttributeField( HttpServletRequest request )
    {
        return redirect( request, modifyAttributeField( request ) );
    }

    /**
     * Modify the choice described by the request
     *
     * @param request
     *            HttpServletRequest
     * @return the url of the next page: the error message or the modification form of the attribute
     */
    String modifyAttributeField( HttpServletRequest request )
    {
        int nIdAttribute = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ATTRIBUTE ), -1 );
        int nIdField = NumberUtils.toInt( request.getParameter( PARAMETER_ID_FIELD ), -1 );

        if ( request.getParameter( PARAMETER_CANCEL ) == null )
        {
            String strTitle = request.getParameter( PARAMETER_TITLE );
            String strValue = request.getParameter( PARAMETER_VALUE );

            if ( StringUtils.isEmpty( strTitle ) || StringUtils.isEmpty( strValue ) )
            {
                UrlItem urlModify = new UrlItem( AppPathService.getBaseUrl( request ) + getViewFullUrl( VIEW_MODIFY_ATTRIBUTE_FIELD ) );
                urlModify.addParameter( PARAMETER_ID_ATTRIBUTE, nIdAttribute );
                urlModify.addParameter( PARAMETER_ID_FIELD, nIdField );

                return AdminMessageService.getMessageUrlReturningTo( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP, urlModify.getUrl( ) );
            }

            Plugin plugin = getMyLutecePlugin( );
            AttributeField attributeField = AttributeFieldHome.findByPrimaryKey( nIdField, plugin );

            if ( attributeField == null )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_ATTRIBUTE_FIELD_NOT_FOUND, AdminMessage.TYPE_STOP );
            }

            attributeField.setTitle( strTitle );
            attributeField.setValue( strValue );
            attributeField.setDefaultValue( request.getParameter( PARAMETER_DEFAULT_VALUE ) != null );
            AttributeFieldHome.update( attributeField, plugin );
        }

        return getModifyAttributeUrl( nIdAttribute );
    }

    /**
     * Ask the confirmation of the removal of a choice of an attribute
     *
     * @param request
     *            HttpServletRequest
     * @return the url of the confirmation message
     */
    @View( value = VIEW_CONFIRM_REMOVE_ATTRIBUTE_FIELD, securityTokenAction = ACTION_REMOVE_ATTRIBUTE_FIELD )
    public String getConfirmRemoveAttributeField( HttpServletRequest request )
    {
        int nIdAttribute = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ATTRIBUTE ), -1 );
        int nIdField = NumberUtils.toInt( request.getParameter( PARAMETER_ID_FIELD ), -1 );

        if ( AttributeFieldHome.findByPrimaryKey( nIdField, getMyLutecePlugin( ) ) == null )
        {
            return redirect( request, AdminMessageService.getMessageUrl( request, MESSAGE_ATTRIBUTE_FIELD_NOT_FOUND, AdminMessage.TYPE_STOP ) );
        }

        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_ATTRIBUTE_FIELD ) );
        url.addParameter( PARAMETER_ID_ATTRIBUTE, nIdAttribute );
        url.addParameter( PARAMETER_ID_FIELD, nIdField );

        return redirect( request,
                AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_ATTRIBUTE_FIELD, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION ) );
    }

    /**
     * Remove a choice of an attribute and the values the users gave it
     *
     * @param request
     *            HttpServletRequest
     * @return the url of the modification form of the attribute
     */
    @Action( ACTION_REMOVE_ATTRIBUTE_FIELD )
    public String doRemoveAttributeField( HttpServletRequest request )
    {
        int nIdField = NumberUtils.toInt( request.getParameter( PARAMETER_ID_FIELD ), -1 );
        Plugin plugin = getMyLutecePlugin( );

        if ( nIdField != -1 )
        {
            AttributeFieldHome.remove( nIdField, plugin );
            MyLuteceUserFieldHome.removeUserFieldsFromIdField( nIdField, plugin );
        }

        return redirect( request, VIEW_MODIFY_ATTRIBUTE, PARAMETER_ID_ATTRIBUTE, NumberUtils.toInt( request.getParameter( PARAMETER_ID_ATTRIBUTE ), -1 ) );
    }

    /**
     * Move a choice up in the list of its attribute. Posted from the modification form of the attribute, with its token
     *
     * @param request
     *            HttpServletRequest
     * @return the url of the modification form of the attribute
     */
    @Action( value = ACTION_MOVE_UP_ATTRIBUTE_FIELD, securityTokenAction = ACTION_MODIFY_ATTRIBUTE )
    public String doMoveUpAttributeField( HttpServletRequest request )
    {
        return moveAttributeField( request, -1 );
    }

    /**
     * Move a choice down in the list of its attribute. Posted from the modification form of the attribute, with its token
     *
     * @param request
     *            HttpServletRequest
     * @return the url of the modification form of the attribute
     */
    @Action( value = ACTION_MOVE_DOWN_ATTRIBUTE_FIELD, securityTokenAction = ACTION_MODIFY_ATTRIBUTE )
    public String doMoveDownAttributeField( HttpServletRequest request )
    {
        return moveAttributeField( request, 1 );
    }

    /**
     * Swap the position of an attribute with the one of its neighbour
     *
     * @param nIdAttribute
     *            the id of the attribute
     * @param nOffset
     *            -1 to swap with the previous one, 1 with the next one
     */
    private void moveAttribute( int nIdAttribute, int nOffset )
    {
        Plugin plugin = getMyLutecePlugin( );
        List<IAttribute> listAttributes = AttributeHome.findAll( getLocale( ), plugin );

        for ( int i = 0; i < listAttributes.size( ); i++ )
        {
            int nNeighbour = i + nOffset;

            if ( listAttributes.get( i ).getIdAttribute( ) == nIdAttribute && nNeighbour >= 0 && nNeighbour < listAttributes.size( ) )
            {
                IAttribute current = listAttributes.get( i );
                IAttribute neighbour = listAttributes.get( nNeighbour );
                int nPosition = current.getPosition( );
                current.setPosition( neighbour.getPosition( ) );
                neighbour.setPosition( nPosition );
                AttributeHome.update( current, plugin );
                AttributeHome.update( neighbour, plugin );

                return;
            }
        }
    }

    /**
     * Swap the position of a choice with the one of its neighbour
     *
     * @param request
     *            HttpServletRequest
     * @param nOffset
     *            -1 to swap with the previous one, 1 with the next one
     * @return the url of the modification form of the attribute
     */
    private String moveAttributeField( HttpServletRequest request, int nOffset )
    {
        int nIdAttribute = NumberUtils.toInt( request.getParameter( PARAMETER_ID_ATTRIBUTE ), -1 );
        int nIdField = NumberUtils.toInt( request.getParameter( PARAMETER_ID_FIELD ), -1 );
        Plugin plugin = getMyLutecePlugin( );
        List<AttributeField> listAttributeFields = AttributeFieldHome.selectAttributeFieldsByIdAttribute( nIdAttribute, plugin );

        for ( int i = 0; i < listAttributeFields.size( ); i++ )
        {
            int nNeighbour = i + nOffset;

            if ( listAttributeFields.get( i ).getIdField( ) == nIdField && nNeighbour >= 0 && nNeighbour < listAttributeFields.size( ) )
            {
                AttributeField current = listAttributeFields.get( i );
                AttributeField neighbour = listAttributeFields.get( nNeighbour );
                int nPosition = current.getPosition( );
                current.setPosition( neighbour.getPosition( ) );
                neighbour.setPosition( nPosition );
                AttributeFieldHome.update( current, plugin );
                AttributeFieldHome.update( neighbour, plugin );

                break;
            }
        }

        return redirect( request, VIEW_MODIFY_ATTRIBUTE, PARAMETER_ID_ATTRIBUTE, nIdAttribute );
    }

    /**
     * Create a new attribute of a type known to the application. The class name comes from the request: it is only instantiated when a
     * registered attribute type declares it
     *
     * @param strClassName
     *            the class name of the attribute type
     * @return a new attribute, or null when no attribute type has this class name
     */
    private IAttribute newAttribute( String strClassName )
    {
        if ( StringUtils.isBlank( strClassName ) )
        {
            return null;
        }

        for ( IAttribute attributeType : _attributeTypes.stream( ).toList( ) )
        {
            attributeType.setAttributeType( getLocale( ) );

            if ( strClassName.equals( attributeType.getAttributeType( ).getClassName( ) ) )
            {
                try
                {
                    return (IAttribute) Class.forName( strClassName ).getDeclaredConstructor( ).newInstance( );
                }
                catch( ReflectiveOperationException e )
                {
                    AppLogService.error( "Unable to create an attribute of type {}", strClassName, e );

                    return null;
                }
            }
        }

        return null;
    }

    /**
     * Get the url of the modification form of an attribute
     *
     * @param nIdAttribute
     *            the id of the attribute
     * @return the url, relative to the controller JSP, as a redirection of the controller expects it
     */
    private String getModifyAttributeUrl( int nIdAttribute )
    {
        UrlItem url = new UrlItem( getViewUrl( VIEW_MODIFY_ATTRIBUTE ) );
        url.addParameter( PARAMETER_ID_ATTRIBUTE, nIdAttribute );

        return url.getUrl( );
    }

    /**
     * Get the MyLutece plugin
     *
     * @return the plugin
     */
    private static Plugin getMyLutecePlugin( )
    {
        return PluginService.getPlugin( MyLutecePlugin.PLUGIN_NAME );
    }
}
