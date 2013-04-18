<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Welcome to Forge</title>
<link rel="icon" href="<c:url value="/resources/favicon.ico"/>" />
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/bootstrap.css"/>" />
<link type="text/css" rel="stylesheet" href="<c:url value="/resources/forge-style.css"/>" />
</head>
<body>
   <div class="navbar navbar-fixed-top">
      <div class="navbar-inner">
         <div class="container">
            <a id="brandLink" href="<c:url value='/'/>" class="brand">@{appName}</a>
            <div class="nav-collapse collapse">
               <ul class="nav">
                  <li><a href="http://forge.jboss.org/docs/important_plugins/ui-scaffolding.html">How to Customize</a></li>
               </ul>
            </div>
         </div>
      </div>
   </div>

   <div class="container forgecontainer">
      <div id="navigation">
         <a id="homeLink" href="<c:url value='/'/>"> <img src="<c:url value="/resources/forge-logo.png"/>"
            alt="Forge... get hammered" border="0" />
         </a>
         <ul>
            @{navigation}
         </ul>
         <a id="vraptorLink" href="http://vraptor.caelum.com.br/en/" target="_blank">
            <img src="<c:url value="/resources/vraptor-logo.png"/>" alt="Java MVC Framework" border="0" />
         </a>
      </div>

      <div id="content">
