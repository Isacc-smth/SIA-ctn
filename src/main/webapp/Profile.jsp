<%-- 
    Document   : Profile
    Created on : Sep 15, 2025, 6:08:05 PM
    Author     : jonat
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>

<head>
  <title>Mi Perfil</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" href="styles/header.css">
  <link rel="stylesheet" href="styles/home-header.css">
  <link rel="stylesheet" href="styles/general.css">
  <link rel="stylesheet" href="styles/tareas-general.css">
  <link rel="stylesheet" href="styles/top-section.css">
  <link rel="stylesheet" href="styles/tareas-grid.css">
  <link rel="stylesheet" href="styles/buttons.css">
  <link rel="stylesheet" href="styles/flash.css">
  <link rel="stylesheet" href="styles/profile-grid.css">
  <link rel="icon" type="image/x-icon" href="images/ctn-logo.svg">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,100..900;1,100..900&display=swap"
    rel="stylesheet">
</head>

<body>
  <header><!-- TODO turn this into navbar -->
    <div class="header-logo-container">
      <img class="header-logo" src="images/ctn-logo.svg">
    </div>
    <div class="header-school-name">
      Colegio Técnico Nacional
    </div>
    <c:url var="profileUrl" value="/ProfileServlet" />
    <c:url var="logoutUrl" value="/LogoutServlet" />

    <div class="right-section">
      <div class="manual-container">
        <a class="manual-link" href="pdfs/manual.pdf" target="_blank">Manual</a>
      </div>

      <div class="session-dropdown" id="sessionDropdown">
        <button
          class="session-button"
          id="sessionButton"
          aria-haspopup="true"
          aria-expanded="false"
          aria-controls="sessionMenu">
          Sesión
          <svg class="dropdown-icon" viewBox="0 0 24 24" fill="currentColor">
            <path d="M7 10l5 5 5-5z"/>
          </svg>
        </button>

        <nav class="session-menu" id="sessionMenu" role="menu" aria-labelledby="sessionButton">
          <a role="menuitem" class="session-item" href="${profileUrl}">Mi Perfil</a>
          <a role="menuitem" class="session-item session-logout" href="${logoutUrl}">Cerrar Sesión</a>
        </nav>
      </div>
    </div>
  </header>



  <main>
    <section class="container">
      <div class="info-bar">
        <span>Bienvenido/a ${sessionScope.user.fullName}</span>
        <span>
          <c:out value="${nowFormatted}" />
        </span>
      </div>
      <c:if test="${not empty sessionScope.flashMessage}">
        <div class="flash" data-timeout="4000">
          ${sessionScope.flashMessage}
          <button type="button" class="flash-close" aria-label="Cerrar mensaje">&times;</button>
        </div>
        <c:remove var="flashMessage" scope="session"/>
      </c:if>

      <c:if test="${not empty errors}">
        <c:forEach var="err" items="${errors}">
            <div class="flash-errors" data-timeout="4000">
              <c:out value="${err}" />
              <button type="button" class="flash-close" aria-label="Cerrar mensajes de error">&times;</button>
            </div>
        </c:forEach>
        <c:remove var="flashErrors" scope="session"/>
      </c:if>

      <h1>Mis Datos</h1>

      <form id="profileForm" action="${pageContext.request.contextPath}/ProfileServlet" method="post">

        <div class="profile-grid">
          <div class="table-header">${sessionScope.user.fullName}</div>
          <div class="cell">
            ${profesor.fullName}
          </div>

          <div class="table-header">Cédula</div>
          <div class="cell">
            ${profesor.ci}
          </div>

          <div class="table-header">Correo</div>
          <div class="cell">
            <input
              type="email"
              name="correo"
              value="${profesor.correo}" />
          </div>

          <div class="table-header">Telefono</div>
          <div class="cell">
            <input
              class="no-spinner"
              type="number"
              name="telefono"
              value="${profesor.telefono}" />
          </div>

          <div class="table-header">Celular</div>
          <div class="cell">
            <input
              class="no-spinner"
              type="number"
              name="celular"
              value="${profesor.celular}" />
          </div>

          <div class="table-header">Usuario del Sistema</div>
          <div class="cell">
            <input type="text" name="usuario" value="${profesor.usuario}" />
          </div>

          <div class="buttons-row table-header">

            <c:url var="HomeUrl" value="/HomeServlet" />

            <div class="button-group">
              <!-- Back link (anchor) — not a form submit -->
              <a id="backBtn" class="back-button" href="${HomeUrl}">
                <img class="back-icon" src="${pageContext.request.contextPath}/icons/back-arrow.svg" alt="Atrás">
                Atrás
              </a>

              <button class="save-button" id="saveBtn" type="submit">
                <img class="save-icon" src="${pageContext.request.contextPath}/icons/add.svg">
                Grabar
              </button>

            </div>

          </div>
        </div>
      </form>


    </section>
  </main>

<script src="./scripts/session-dropdown.js"></script>
<script src="./scripts/profile-menu.js"></script>
<script src="./scripts/flash-messages.js"></script>

</body>

</html>
