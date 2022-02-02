
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
package fr.paris.lutece.plugins.mylutece.business;



/**
 * The Class LuteceUserAttributeDescription.
 */
public class LuteceUserAttributeDescription {

	
	
	/**
	 * Instantiates a new lutece user attribute description.
	 *
	 * @param strAttributeKey the str attribute key
	 * @param strMappingKey the str mapping key
	 * @param strDescription the str description
	 */
	public LuteceUserAttributeDescription(String strAttributeKey, String strMappingKey, String strDescription) {
		super();
		this._strAttributeKey = strAttributeKey;
		this._strMappingKey = strMappingKey;
		this._strDescription = strDescription;
	}

	/** The str attribute key. */
	private String _strAttributeKey;
	
	/** The str mapping key. */
	private String _strMappingKey;
	
	/** The str description. */
	private String _strDescription;
	
	
	/**
	 * Gets the attribute key.
	 *
	 * @return the attribute key
	 */
	public String getAttributeKey() {
		return _strAttributeKey;
	}
	
	/**
	 * Sets the attribute key.
	 *
	 * @param strAttributeKey the new attribute key
	 */
	public void setAttributeKey(String strAttributeKey) {
		this._strAttributeKey = strAttributeKey;
	}
	
	/**
	 * Gets the mapping key.
	 *
	 * @return the mapping key
	 */
	public String getMappingKey() {
		return _strMappingKey;
	}
	
	/**
	 * Sets the mapping key.
	 *
	 * @param strMappingKey the new mapping key
	 */
	public void setMappingKey(String strMappingKey) {
		this._strMappingKey = strMappingKey;
	}
	
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return _strDescription;
	}
	
	/**
	 * Sets the description.
	 *
	 * @param strDescription the new description
	 */
	public void setDescription(String strDescription) {
		this._strDescription = strDescription;
	}
}
