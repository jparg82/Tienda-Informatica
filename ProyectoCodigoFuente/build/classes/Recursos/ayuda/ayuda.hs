<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<!DOCTYPE helpset PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN" "http://java.sun.com/products/javahelp/helpset_2_0.dtd">

<helpset version="2.0">
   <title>Ayuda en línea</title>
   <maps>
      <homeID>top</homeID>
      <mapref location="Map.jhm"/>
   </maps>
   <view>
      <name>toc</name>
      <label>TOC</label>
      <type>javax.help.TOCView </type>
      <data>ayudaTOC.xml </data>
   </view>
   <view>
      <name>buscar</name>
      <label>Buscar</label>
      <type> javax.help.SearchView </type>
      <data engine="com.sun.java.help.search.DefaultSearchEngine"> JavaHelpSearch </data>
   </view>
   <view>
      <name>favoritos</name>
      <label>Favoritos</label>
      <type>javax.help.FavoritesView</type>
  </view>
  <presentation default="true">
      <name>ventana principal</name>
      <size width="700" height="500" />
      <location x="100" y="100" />
      <title>Ayuda en línea</title>
      <toolbar>
         <helpaction>javax.help.BackAction</helpaction>
	 <helpaction>javax.help.ForwardAction</helpaction>
	 <helpaction>javax.help.SeparatorAction</helpaction>
	 <helpaction>javax.help.HomeAction</helpaction>
	 <helpaction>javax.help.ReloadAction</helpaction>
	 <helpaction>javax.help.SeparatorAction</helpaction>
	 <helpaction>javax.help.PrintAction</helpaction>
	 <helpaction>javax.help.PrintSetupAction</helpaction>
      </toolbar>
   </presentation>
</helpset>
