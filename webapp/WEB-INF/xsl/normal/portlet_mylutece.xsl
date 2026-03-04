<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="portlet">
<div class="portlet card">
<xsl:choose>
	<xsl:when test="not(string(display-portlet-title)='1')">
		<div class="card-title">
			<h3><xsl:value-of disable-output-escaping="yes" select="portlet-name" /></h3>
		</div>
		<div class="card-body">
			<xsl:apply-templates select="mylutece-portlet" />
		</div>
	</xsl:when>
	<xsl:otherwise>
		<div class="card-body">
			<xsl:apply-templates select="mylutece-portlet" />
		</div>
	</xsl:otherwise>
</xsl:choose>
</div>
</xsl:template>

<xsl:template match="mylutece-portlet">
<xsl:apply-templates select="user-not-signed" />
<xsl:apply-templates select="lutece-user" />
</xsl:template>

<xsl:template match="user-not-signed">
<form action="jsp/site/plugins/mylutece/DoMyLuteceLogin.jsp" method="post">
<xsl:apply-templates select="lutece-user-authentication-service[@delegated='true']" />
<xsl:apply-templates select="lutece-user-authentication-service[@loginpassword-required='true']" />
<xsl:if test="count(lutece-user-authentication-service[@loginpassword-required='true']) &gt;= 1">
<div class="mb-3">
	<label for="username">Code d'accès :</label>
	<input name="username" class="form-control" id="username" autocomplete="off" type="text"/>
</div>
<div class="mb-3">
	<label for="password">Mot de passe :</label>
	<input name="password" class="form-control" id="password" autocomplete="off"  type="password" />
</div>
<div class="mb-3 text-center">
	<button class="btn btn-primary btn-sm w-100" tabindex="3" type="submit"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="currentColor" class="icon icon-tabler icons-tabler-filled icon-tabler-check me-2"><path stroke="none" d="M0 0h24v24H0z" fill="none"/><path d="M20.707 6.293a1 1 0 0 1 0 1.414l-10 10a1 1 0 0 1 -1.414 0l-5 -5a1 1 0 0 1 1.414 -1.414l4.293 4.293l9.293 -9.293a1 1 0 0 1 1.414 0" /></svg>Connexion</button>
</div>
</xsl:if>
</form>
<div class="row">
<xsl:apply-templates select="lutece-user-new-account-url" />
<xsl:apply-templates select="lutece-user-lost-password-url" />
</div>
</xsl:template>

<xsl:template match="lutece-user-authentication-service[@loginpassword-required='true']">
<input type="radio" name="auth_provider" value="{name}" checked="checked" class="d-none"/>
</xsl:template>

<xsl:template match="lutece-user-authentication-service[@delegated='true']">
<p class="text-center"><a href="{url}?auth_provider={name}"> <img src="{icon-url}" class="img-fluid" alt="{display-name}" title="{display-name}"/> {display-name} </a></p>
</xsl:template>

<xsl:template match="lutece-user">
Bienvenue <xsl:value-of disable-output-escaping="yes" select="lutece-user-name-given" />&#160;<xsl:value-of disable-output-escaping="yes" select="lutece-user-name-family" />
<div class="row g-3">
<xsl:apply-templates select="lutece-user-view-account-url" />
<xsl:apply-templates select="lutece-user-logout-url" />
</div>
</xsl:template>

<xsl:template match="lutece-user-logout-url">
<form name="logout" action="{.}" method="post">
<button type="submit" class="btn btn-primary btn-sm w-100">D&#233;connexion</button> 
</form>
</xsl:template>

<xsl:template match="lutece-user-new-account-url">
<div class="col">
	<form name="logout" action="{.}" method="post">
	   	<button type="submit" class="btn btn-primary btn-sm fs-6 px-3 w-100"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon icon-tabler icons-tabler-outline icon-tabler-plus me-2"><path stroke="none" d="M0 0h24v24H0z" fill="none"/><path d="M12 5l0 14" /><path d="M5 12l14 0" /></svg> Cr&#233;er un compte</button>
    </form>
</div>
</xsl:template>

<xsl:template match="lutece-user-lost-password-url">
<div class="col">
	<form name="logout" action="{.}" method="post">
    	<button type="submit" class="btn btn-primary btn-sm fs-6 px-3 w-100"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon icon-tabler icons-tabler-outline icon-tabler-password-user me-2"><path stroke="none" d="M0 0h24v24H0z" fill="none"/><path d="M12 17v4" /><path d="M10 20l4 -2" /><path d="M10 18l4 2" /><path d="M5 17v4" /><path d="M3 20l4 -2" /><path d="M3 18l4 2" /><path d="M19 17v4" /><path d="M17 20l4 -2" /><path d="M17 18l4 2" /><path d="M9 6a3 3 0 1 0 6 0a3 3 0 0 0 -6 0" /><path d="M7 14a2 2 0 0 1 2 -2h6a2 2 0 0 1 2 2" /></svg> Mot de passe perdu</button>
   </form>
</div>
</xsl:template>

<xsl:template match="lutece-user-view-account-url">
<div class="col">
	<form name="logout" action="{.}" method="post">
    	<button type="submit" class="btn btn-primary btn-sm fs-6 px-3 w-100"><svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon icon-tabler icons-tabler-outline icon-tabler-user-circle me-2 "><path stroke="none" d="M0 0h24v24H0z" fill="none"/><path d="M3 12a9 9 0 1 0 18 0a9 9 0 1 0 -18 0" /><path d="M9 10a3 3 0 1 0 6 0a3 3 0 1 0 -6 0" /><path d="M6.168 18.849a4 4 0 0 1 3.832 -2.849h4a4 4 0 0 1 3.834 2.855" /></svg>Voir mon compte</button>
   </form>
</div>
</xsl:template>
</xsl:stylesheet>