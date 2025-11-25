<%-- 
    Document   : index
    Created on : Aug 5, 2025, 5:25:54 PM
    Author     : jonat
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE html>
<html>

  <head>
    <title>CTNPortal</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="styles/header.css">
    <link rel="stylesheet" href="styles/general.css">
    <link rel="stylesheet" href="styles/login.css">
    <link rel="icon" type="image/x-icon" href="images/ctn-logo.svg">
  </head>

  <!-- as convention the class names must be in english -->

  <body>
    <header>
      <div class="header-logo-container">
        <img class="header-logo" src="images/ctn-logo.svg">
      </div>
      <div class="header-school-name">
        Colegio Técnico Nacional
      </div>
      <div id="counterbalance">
      </div>
    </header>

    <main>
      <div class="login-wrapper">
        <div class="login-card">
          <div class="login-logo-container">
            <img class="login-logo" src="images/ctn-logo-2.svg">
          </div>
          <c:if test="${loginError}">
              <div class="login-error">Nombre de usuario o contraseña incorrectos.</div>
          </c:if>
          <form class="login-form" action="LoginServlet" method="post">
            <input class="form-username" placeholder="Usuario" type="text" name="username">
            <input class="form-password" placeholder="Contraseña" type="password" name="password">
            <input autofocus class="form-submit" type="submit" value="Iniciar Sessión">
          </form>
        </div>
      </div>
    </main>
  </body>

</html>
