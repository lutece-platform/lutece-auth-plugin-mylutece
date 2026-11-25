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
package fr.paris.lutece.plugins.mylutece.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.plugins.mylutece.web.attribute.AttributeJspBean;
import fr.paris.lutece.plugins.mylutece.web.security.AuthenticationFilterJspBean;
import fr.paris.lutece.portal.service.security.SecurityTokenHandler;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;
import jakarta.enterprise.inject.spi.CDI;

/**
 * The back office controllers of the attributes and of the authentication filter (ported to MVC by LUT-35559) must refuse every action posted
 * without a security token.
 */
public class MyluteceAdminSecurityTokenTest extends LuteceTestCase
{
    private static final List<Class<? extends MVCAdminJspBean>> CONTROLLERS = List.of( AttributeJspBean.class, AuthenticationFilterJspBean.class );

    private static List<Method> methods( Class<?> clazz )
    {
        List<Method> listMethods = new ArrayList<>( );
        for ( Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass( ) )
        {
            listMethods.addAll( List.of( c.getDeclaredMethods( ) ) );
        }
        return listMethods;
    }

    /**
     * Every action of the two controllers, posted without token to its back office JSP, is checked by the security token filter and refused
     */
    @Test
    public void testEveryActionWithoutTokenIsRefused( )
    {
        SecurityTokenHandler handler = CDI.current( ).select( SecurityTokenHandler.class ).get( );
        handler.registerAdminControllers( null );
        int nActions = 0;
        for ( Class<? extends MVCAdminJspBean> clazz : CONTROLLERS )
        {
            Controller controller = clazz.getAnnotation( Controller.class );
            assertTrue( controller.securityTokenEnabled( ), clazz.getSimpleName( ) + " does not enable the security token" );
            for ( Method method : methods( clazz ) )
            {
                Action action = method.getAnnotation( Action.class );
                if ( action == null )
                {
                    continue;
                }
                nActions++;
                assertFalse( action.securityTokenDisabled( ), clazz.getSimpleName( ) + "." + method.getName( ) );
                MockHttpServletRequest request = new MockHttpServletRequest( );
                request.setMethod( "POST" );
                request.setServletPath( "/" + controller.controllerPath( ) + controller.controllerJsp( ) );
                request.addParameter( "action_" + action.value( ), "" );
                String strName = clazz.getSimpleName( ) + "." + method.getName( );
                assertFalse( handler.shouldNotFilter( request ), strName + " is not checked by the security token filter" );
                assertFalse( handler.validate( request, handler.resolveTokenAction( request, action.value( ) ) ), strName + " accepted without token" );
            }
        }
        assertTrue( nActions >= 14, "actions found: " + nActions );
    }

    /**
     * The core disables the token of an action when the controller has a method of the same name starting with "do" without @Action nor @View (rule
     * of the pages that do not use the MVC model): no such method may hide an action of the two controllers
     */
    @Test
    public void testNoMethodDisablesTheTokenOfAnAction( )
    {
        for ( Class<? extends MVCAdminJspBean> clazz : CONTROLLERS )
        {
            Set<String> setActions = new HashSet<>( );
            List<Method> listMethods = methods( clazz );
            for ( Method method : listMethods )
            {
                if ( method.isAnnotationPresent( Action.class ) )
                {
                    setActions.add( method.getAnnotation( Action.class ).value( ) );
                }
            }
            for ( Method method : listMethods )
            {
                if ( method.getName( ).startsWith( "do" ) && !method.isAnnotationPresent( Action.class ) && !method.isAnnotationPresent( View.class ) )
                {
                    assertFalse( setActions.contains( method.getName( ) ),
                            clazz.getSimpleName( ) + "." + method.getName( ) + " disables the security token of the action " + method.getName( ) );
                }
            }
        }
    }
}
