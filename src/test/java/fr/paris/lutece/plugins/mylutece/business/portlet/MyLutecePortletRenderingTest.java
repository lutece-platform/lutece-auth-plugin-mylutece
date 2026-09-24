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
package fr.paris.lutece.plugins.mylutece.business.portlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletTemplate;
import fr.paris.lutece.portal.business.portlet.PortletTemplateHome;
import fr.paris.lutece.portal.service.portal.PortalService;
import fr.paris.lutece.portal.service.security.SecurityService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import fr.paris.lutece.test.mocks.MockHttpServletResponse;

/**
 * Renders a MyLutece portlet with every template registered in the core for the MYLUTECE_PORTLET type
 */
public class MyLutecePortletRenderingTest extends LuteceTestCase
{
    private static final String PORTLET_NAME = "MyLutecePortletRenderingTest heading";
    private static final String MARKER_PORTLET = "portlet-mylutece";
    private static final int UNKNOWN_TEMPLATE_ID = 99999;

    private MyLutecePortlet _portlet;

    @BeforeEach
    @Override
    protected void setUp( ) throws Exception
    {
        super.setUp( );

        _portlet = new MyLutecePortlet( );
        _portlet.setPageId( PortalService.getRootPageId( ) );
        _portlet.setStyleId( 0 );
        _portlet.setColumn( 1 );
        _portlet.setOrder( 1 );
        _portlet.setName( PORTLET_NAME );
        _portlet.setStatus( Portlet.STATUS_PUBLISHED );
        _portlet.setDisplayPortletTitle( 0 );
        _portlet.setDeviceDisplayFlags( Portlet.FLAG_DISPLAY_ON_NORMAL_DEVICE | Portlet.FLAG_DISPLAY_ON_LARGE_DEVICE | Portlet.FLAG_DISPLAY_ON_XLARGE_DEVICE );

        MyLutecePortletHome.getInstance( ).create( _portlet );
    }

    @AfterEach
    @Override
    protected void tearDown( ) throws Exception
    {
        if ( _portlet != null )
        {
            _portlet.remove( );
        }

        LocalVariables.remove( );
        super.tearDown( );
    }

    @Test
    public void testShippedTemplateRegistered( )
    {
        List<PortletTemplate> listTemplates = PortletTemplateHome.findByPortletType( MyLutecePortletHome.getInstance( ).getPortletTypeId( ) );

        assertEquals( 1, listTemplates.size( ), "the shipped template should be registered in the core for the MYLUTECE_PORTLET type" );
        assertEquals( MyLutecePortlet.TEMPLATE_PORTLET_MYLUTECE_DEFAULT, listTemplates.get( 0 ).getTemplatePath( ) );
    }

    @Test
    public void testTemplateStoredWithThePortlet( )
    {
        PortletTemplate template = PortletTemplateHome.findByPortletType( MyLutecePortletHome.getInstance( ).getPortletTypeId( ) ).get( 0 );
        _portlet.setIdTemplate( template.getId( ) );
        _portlet.update( );

        Portlet stored = PortletHome.findByPrimaryKey( _portlet.getId( ) );

        assertEquals( template.getId( ), stored.getIdTemplate( ), "the chosen template should be stored by the core with the portlet" );
        assertTrue( PortletTemplateHome.isTemplateUsed( template.getId( ) ), "a template chosen by a portlet is used" );
    }

    @Test
    public void testRenderEveryShippedTemplate( )
    {
        assertTrue( SecurityService.isAuthenticationEnable( ), "the MyLutece portlet is only rendered when the authentication is enabled" );

        MockHttpServletRequest request = new MockHttpServletRequest( );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );

        for ( PortletTemplate template : PortletTemplateHome.findByPortletType( MyLutecePortletHome.getInstance( ).getPortletTypeId( ) ) )
        {
            _portlet.setIdTemplate( template.getId( ) );
            String strContent = _portlet.getHtmlContent( request );

            assertTrue( strContent.contains( MARKER_PORTLET ), "template " + template.getTemplatePath( ) + " should render the portlet wrapper" );
            assertTrue( strContent.contains( "portlet_" + _portlet.getId( ) ), "template " + template.getTemplatePath( ) + " should render the portlet id" );
            assertTrue( strContent.contains( PORTLET_NAME ), "template " + template.getTemplatePath( ) + " should render the portlet title" );
            assertTrue( strContent.contains( "d-md-block" ), "template " + template.getTemplatePath( ) + " should render the device classes" );
        }
    }

    @Test
    public void testFallbackToDefaultTemplate( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        LocalVariables.setLocal( null, request, new MockHttpServletResponse( ) );

        _portlet.setIdTemplate( UNKNOWN_TEMPLATE_ID );
        _portlet.setDisplayPortletTitle( 1 );
        String strContent = _portlet.getHtmlContent( request );

        assertTrue( strContent.contains( MARKER_PORTLET ), "the default template should render the portlet wrapper" );
        assertFalse( strContent.contains( PORTLET_NAME ), "a hidden portlet title should not be rendered" );
    }
}
