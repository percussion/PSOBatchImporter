<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:x="http://www.w3.org/1999/xhtml" exclude-result-prefixes="x">
  <!-- Whenever you match any node or any attribute -->
  <xsl:import href="pathutils.xsl"/>
  <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>
  <xsl:param name="communityName"/>
  <xsl:param name="importRoot"/>

 <!-- Identity transform templates.  These just copy everything from
 the source xml to destination by default.  These special versions
 just remove any namespaces specified as the importer currently does
 not handle these. -->
 <xsl:template match="comment()|processing-instruction()">
    <xsl:copy>
      <xsl:apply-templates/>
    </xsl:copy>
</xsl:template>

<xsl:template match="*">
    <xsl:element name="{local-name()}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
</xsl:template>

<xsl:template match="@*">
    <xsl:attribute name="{local-name()}">
      <xsl:value-of select="."/>
    </xsl:attribute>
</xsl:template>

 <xsl:template match="importer">
	 <importer>
		<xsl:apply-templates select="@*|node()"/>
		<!-- These following calls create items and references defined in the
		ephox fields.  The first finds all the images and creates an <item> that
		represent the image,  the second creates references to other pages in the system -->
		<xsl:apply-templates select="/importer/item/fields/field//img" mode="item"/>
		<xsl:apply-templates select="/importer/item/fields/field//a" mode="item"/>
	 </importer>
 </xsl:template>
 
 
 <!-- Modify inline links to specify type and template -->
 <!-- We find the inline elements and specify the type and template etc.
 These are linked to the actual item references by the objectId attribute
 that is uninqe for the particular element we are selecting from the source document.
 Each of these templates needs a corresponding template with mode="item"  -->
 <!--  inline type attribute is required and can be rximage,rxhyperlink, or rxvariant
 objectId is required to reference the <item> and templatename is optional.  If
 template name is not specified then it will use the template specified in the spring
 configuration of UpdateInlineLinksProcessor as default for the destination  content type and inline type
 If the referenced item is not found then the extra attributes are removed and the
 original html will be restored. -->
 <!-- Need to make sure this inline image template does not match the images
 with the SnInlineLink template we are pulling out below -->
 <xsl:template match="/importer/item/fields/field//img[not(parent::a)]">
      <xsl:variable name="id" select="generate-id(.)"/>
      <img objectId="{$id}" inlinetype="rximage" >
		<xsl:apply-templates select="@*"/> 
      </img>
 </xsl:template>

 <xsl:template match="/importer/item/fields/field//a[@href][not(img)]">
      <xsl:variable name="id" select="generate-id(.)"/>
      <a objectId="{$id}" inlinetype="rxhyperlink" >
		  <xsl:apply-templates select="@*|node()"/>
      </a>
 </xsl:template> 
 
 <!-- This only matches a tags that directly contain an image.  Care
 must be taken with these rules to make sure there is consistent structure
 from the source xml -->
 <xsl:template match="/importer/item/fields/field//a[@href][img]" priority="2">
      <xsl:variable name="id" select="generate-id(.)"/>
      
      <div templatename="rffSnImageLink" objectId="{$id}" inlinetype="rxvariant" >
        <xsl:element name="{local-name()}">
		  <xsl:apply-templates select="@*|node()"/>
		</xsl:element>
      </div>
 </xsl:template> 
 
 
 <xsl:template match="/importer/item/fields/field//a[starts-with(@href,'http://')]" priority="3">
      <xsl:variable name="id" select="generate-id(.)"/>
      <div templatename="rffSnLink" objectId="{$id}" inlinetype="rxvariant" >
       <xsl:element name="{local-name()}">
		  <xsl:apply-templates select="@*|node()"/>
		</xsl:element>
      </div>
 </xsl:template> 
 
 <!-- Inline Referenced items below -->
 <!-- If we need to create an item to represent an inline image/file/link etc 
 then we define updateType="update" which is the default and define
 at a minimum all the required fields and the folder path to place the item 
 All inline referenced items need the objectId attribute so it can be linked
 to the reference in the body field. -->
 <xsl:template match="img[not(parent::a)]" mode="item">
	 <xsl:variable name="img_title">
		<xsl:call-template name="filename">
			<xsl:with-param name="path">
				<xsl:value-of select="@src"/>
			</xsl:with-param>
		</xsl:call-template>
	 </xsl:variable>
 
	<item type="rffImage" objectId="{generate-id(.)}" updateType="update" importRoot="{$importRoot}" keyField="sys_title" communityName="{$communityName}">
		<paths>
			<path><xsl:value-of select="concat($importRoot,'/Images')"/></path>
		</paths>
		<fields>
			<field name="img1" value="{@src}"/>  
			<field name="sys_title" value="{$img_title}"/>
			<field name="displaytitle" value="{$img_title}"/>
			<field name="filename" value="{$img_title}"/>
			<field name="img_alt" value="{@alt}"/>
			<!--  We can automatically generate these
			<field name="img1_height" value="{@height}"/>
			<field name="img1_width" value="{@width}"/>
			 -->
		</fields>
	</item>
 </xsl:template>
<!-- Create external links.  If the same item is referenced in the batch i.e. the same
keyField (url in this case ) the item
will be created or updated once and the body will link to the single item -->
 <xsl:template match="/importer/item/fields/field//a[starts-with(@href,'http://')]" mode="item" priority="3">
    <item  type="rffExternalLink"  objectId="{generate-id(.)}" updateType="update" importRoot="{$importRoot}" keyField="url" communityName="{$communityName}">
		<paths><path><xsl:value-of select="concat($importRoot,'/ExternalLinks')"/></path></paths>
		<fields>
			<!-- This example has the sys_title pulled from a title attribute on the a -->
			<field name="sys_title" value="{@href}"/>
			<field name="displaytitle" value="{.}"/>
			<field name="url" value="{@href}"/>
		</fields>
	</item>
 </xsl:template> 
<!-- If we know the items already exist or are created elsewhere in the batch we can
just set updateType="ref"  and only define the key field. -->
  <xsl:template match="a[@href]" mode="item">
  <!-- If we can get a unique id from the file path we can use that as the key field.  Using the content id as a key field does not
  work when we are creating the item.  You should store the content id from the source system into a unique field e.g. import_id and use
  that as the key field.  This would not work on an out of the box Fast Forward though. 
 	<xsl:variable name="import_id"><xsl:value-of select="substring-before(substring-after(@href,'/item'),'.html')"/></xsl:variable>
 	-->
	<item objectId="{generate-id(.)}" updateType="ref" importRoot="{$importRoot}" keyField="sys_title">
		<fields>
			<!-- The key field can be pulled from any logic using the context of the current xml element
			This example assumes the page sys_title is stored in the body of the a tag.   -->
			<field name="sys_title" value="{.}"/>
		</fields>
	</item>
	
 </xsl:template>

 <xsl:template match="a[@href][img]" mode="item" priority="2">
 	<xsl:variable name="contentid"><xsl:value-of select="substring-before(substring-after(@href,'/item'),'.html')"/></xsl:variable>
	<item objectId="{generate-id(.)}" updateType="ref" importRoot="{$importRoot}" keyField="sys_title">
		<fields>
			<!-- This example has the sys_title pulled from a title attribute on the a -->
			<field name="sys_title" value="{@title}"/>
		</fields>
	</item>
	
 </xsl:template>
 
 

</xsl:stylesheet>

