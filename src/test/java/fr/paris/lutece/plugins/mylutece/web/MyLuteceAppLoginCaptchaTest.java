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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * Login captcha (asked after failed logins): a wrong answer must stop the login, whatever the credentials
 */
public class MyLuteceAppLoginCaptchaTest extends LuteceTestCase
{
    @AfterEach
    public void acceptCaptcha( )
    {
        TestCaptchaService.setRefuse( false );
    }

    @Test
    public void testWrongCaptchaStopsTheLogin( ) throws Exception
    {
        TestCaptchaService.setRefuse( true );
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setRemoteAddr( "127.0.0.1" );
        request.getSession( true ).setAttribute( "mylutece_is_active_captcha", Boolean.TRUE );
        request.setParameter( "username", "bh_captcha_user" );
        request.setParameter( "password", "bh_captcha_password" );

        String strUrl = new MyLuteceApp( ).doLogin( request );

        assertEquals( "../../../../" + MyLuteceApp.getLoginPageUrl( ) + "&error=error_captcha", strUrl,
                "a wrong captcha must send back to the login page with the captcha error only, without trying the credentials" );
        assertEquals( Boolean.TRUE, request.getSession( ).getAttribute( "mylutece_is_active_captcha" ), "the captcha stays asked" );
    }
}
