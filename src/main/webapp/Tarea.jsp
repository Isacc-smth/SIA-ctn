<%-- 
    Document   : Tareas
    Created on : Sep 3, 2025, 8:45:48 PM
    Author     : jonat
--%>

<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDateTime"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>

<head>
  <title>Tareas</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" href="styles/header.css">
  <link rel="stylesheet" href="styles/home-header.css">
  <link rel="stylesheet" href="styles/general.css">
  <link rel="stylesheet" href="styles/tareas-general.css">
  <link rel="stylesheet" href="styles/top-section.css">
  <link rel="stylesheet" href="styles/tareas-grid.css">
  <link rel="stylesheet" href="styles/buttons.css">
  <link rel="stylesheet" href="styles/flash.css">
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
      <c:choose>
          <c:when test="${not empty editingTarea}"><h1>Modificar Tarea</h1></c:when>
          <c:otherwise><h1>Agregar Tarea</h1></c:otherwise>
      </c:choose>
      
      <c:if test="${not empty errors}">
        <c:forEach var="err" items="${errors}">
            <div class="flash-errors" data-timeout="4000">
              <c:out value="${err}" />
              <button type="button" class="flash-close" aria-label="Cerrar mensajes de error">&times;</button>
            </div>
        </c:forEach>
        <c:remove var="flashErrors" scope="session"/>
      </c:if>

      
      <form id="tareaForm" action="${pageContext.request.contextPath}/TareaServlet" method="post">
        <c:if test="${not empty editingTarea}">
            <input type="hidden" name="planillaId" value="${planillaId}" />
            <input type="hidden" name="tareaId" value="${editingTarea.id}" />
            <input type="hidden" name="_action" value="save" id="_action_input" />
            <input type="hidden" id="originalTotal" name="originalTotal"
                   value="${editingTarea != null ? editingTarea.total : ''}" />
            <input type="hidden" id="clearGrades" name="clearGrades" value="false" />
        </c:if>

        <div class="tareas-grid">
          <div class="table-header">Etapa</div>
          <div class="cell">
            ${etapaFormated} Etapa - Desde: 30/06/2025 - Hasta: 26/11/2025
          </div>

          <div class="table-header">Materia</div>
          <div class="cell">
            <select name="planillaId" required
                    <c:if test="${not empty editingTarea}">disabled</c:if>>
              <option value="" disabled>--Seleccione una Materia--</option>
              <c:forEach var="p" items="${planillas}">
                  <option value="${p.id}"
                          <c:if test="${p.id == selPlanilla.id}">selected</c:if>>
                    ${p.toString()}
                  </option>
              </c:forEach>
            </select>
          </div>

          <div class="table-header">Instrumento</div>
          <div class="cell">
            <select name="instrumentoId" required>
              <option value="" selected disabled>--Seleccione un Instrumento--</option><!-- TODO -->
              <c:forEach var="ins" items="${instrumentos}">
                  <option value="${ins.id}"
                          <c:if test="${ins.id == instrumentoId}">selected</c:if>>
                    <c:out value="${ins.nombre}" />
                  </option>
              </c:forEach>
            </select>
          </div>

          <div class="table-header">Fecha de tarea</div>
          <div class="cell">
            <input
              type="date"
              name="fecha"
              value="${fecha}"
              required />
          </div>

          <div class="table-header">Total de Puntos</div>
          <div class="cell">
            <input
              class="no-spinner"
              name="total" 
              type="number"
              placeholder="Ingrese el Total de Puntos"
              min="0"
              value="${total}"
              required />
          </div>

          <div class="table-header">Título</div>
          <div class="cell">
            <input
              type="text"
              name="titulo"
              placeholder="Ingrese el Título de la Tarea"
              value="${titulo}"
              required />
          </div>

          <div class="buttons-row table-header">

            <c:url var="backUrl" value="/PlanillaServlet">
                <c:param name="planillaId" value="${planillaId}" />
            </c:url>

            <div class="button-group">
              <!-- Back link (anchor) — not a form submit -->
              <a id="backBtn" class="back-button" href="${backUrl}">
                <img class="back-icon" src="${pageContext.request.contextPath}/icons/back-arrow.svg" alt="Atrás">
                Atrás
              </a>

              <button class="save-button" id="saveBtn" type="submit" onclick="document.getElementById('_action_input').value='save'">
                <img class="save-icon" src="${pageContext.request.contextPath}/icons/add.svg">
                <c:choose>
                    <c:when test="${not empty editingTarea}">Guardar</c:when>
                    <c:otherwise>Grabar</c:otherwise>
                </c:choose>
              </button>
    
            </div>
            <button class="delete-button" id="deleteBtn" onclick="return confirmDelete();" >
              <img class="delete-icon" src="${pageContext.request.contextPath}/icons/delete-icon.svg">
              Eliminar
            </button>
          </div>
        </div>
      </form>


    </section>
  </main>

<script src="./scripts/tarea-menu.js"></script>
<script src="./scripts/flash-messages.js"></script>
<script src="./scripts/session-dropdown.js"></script>
<script src="./scripts/tarea-form.js"></script>
</body>

</html>
