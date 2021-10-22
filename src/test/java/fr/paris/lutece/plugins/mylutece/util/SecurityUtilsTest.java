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
package fr.paris.lutece.plugins.mylutece.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockHttpServletRequest;

import fr.paris.lutece.test.LuteceTestCase;

public class SecurityUtilsTest extends LuteceTestCase
{
    public void testBuildResetConnectionLogUrl( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.setRemoteAddr( "127.0.0.1" );
        String strUrl = SecurityUtils.buildResetConnectionLogUrl( 1, request );
        assertNotNull( strUrl );
        assertTrue( strUrl.contains( "jsp/site/plugins/mylutece/DoResetConnectionLog.jsp" ) );
        Pattern pattern = Pattern.compile( "[\\?&]([^=]+)=([^&]+)" );
        Matcher matcher = pattern.matcher( strUrl );
        int matchCount = 0;
        for ( int i = 0; i < 4; i++ )
        {
            assertTrue( matcher.find( ) );
            String strParamName = matcher.group( 1 );
            String strParamValue = matcher.group( 2 );
            if ( "ip".equals( strParamName ) )
            {
                assertEquals( "127.0.0.1", strParamValue );
                matchCount++;
            }
            else
                if ( "date_login".equals( strParamName ) )
                {
                    assertTrue( StringUtils.isNumeric( strParamValue ) );
                    matchCount++;
                }
                else
                    if ( "interval".equals( strParamName ) )
                    {
                        assertEquals( "1", strParamValue );
                        matchCount++;
                    }
                    else
                        if ( "key".equals( strParamName ) )
                        {
                            assertTrue( strParamValue.matches( "[0-9a-f]{64}" ) );
                            matchCount++;
                        }
        }
        assertEquals( 4, matchCount );
    }
}
