package fr.paris.lutece.plugins.mylutece.business;

import fr.paris.lutece.portal.business.role.Role;

/**
 * The Class LuteceUserRoleDescription.
 */
/*
 * Copyright (c) 2002-2022, City of Paris
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
public class LuteceUserRoleDescription extends Role {
    
	


	
	/** The Constant TYPE_MANUAL_ASSIGNMENT. */
	public static final String TYPE_MANUAL_ASSIGNMENT = "MANUAL_ASSIGNMENT";
	
	/** The Constant TYPE_CONDITIONAL_ASSIGNMENT. */
	public static final String TYPE_CONDITIONAL_ASSIGNMENT = "CONDITIONAL_ASSIGNMENT";
	
	/** The Constant TYPE_AUTOMATIC_ASSIGNMENT. */
	public static final String TYPE_AUTOMATIC_ASSIGNMENT = "AUTOMATIC_ASSIGNMENT";
	
	
	/** The str type. */
	private String _strType;
	
	/** The str complementary description. */
	private String _strComplementaryDescription;

	
	/**
	 * Instantiates a new lutece user role description.
	 *
	 * @param role the role
	 * @param strType the str type
	 * @param strComplementaryDescription the str complementary description
	 */
	public LuteceUserRoleDescription(Role role,String strType,String strComplementaryDescription) {
		this.setRole(role.getRole());
		this.setRoleDescription(role.getRoleDescription());
		this.setWorkgroup(role.getWorkgroup());	
		this.setType(strType);
		this.setComplementaryDescription(strComplementaryDescription);
	}

	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return _strType;
	}

	/**
	 * Sets the type.
	 *
	 * @param _strType the new type
	 */
	public void setType(String _strType) {
		this._strType = _strType;
	}

	/**
	 * Gets the complementary description.
	 *
	 * @return the complementary description
	 */
	public String getComplementaryDescription() {
		return _strComplementaryDescription;
	}

	/**
	 * Sets the complementary description.
	 *
	 * @param _strComplementaryDescription the new complementary description
	 */
	public void setComplementaryDescription(String _strComplementaryDescription) {
		this._strComplementaryDescription = _strComplementaryDescription;
	}
}
