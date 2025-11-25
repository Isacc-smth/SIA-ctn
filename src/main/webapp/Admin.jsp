<%-- 
    Document   : Admin
    Created on : Oct 2, 2025, 6:45:45 AM
    Author     : jonat
--%>

<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDateTime"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>



<html>

<head>
  <title>CTNAdmin</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" href="styles/header.css">
  <link rel="stylesheet" href="styles/home-header.css">
  <link rel="stylesheet" href="styles/general.css">
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
  <header>
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
      <div class="top-section">
        <h1>
          Descargar Planillas
        </h1>
      </div>

      <c:url var="exportUrl" value="/ExportCoursePlanillasServlet" />

      <form id="exportCourseForm" action="${exportUrl}" method="get">
        <div class="tareas-grid">
        <div class="table-header">Etapa</div>
          <div class="cell">
            <select name="etapa" required>
              <option value="" selected disabled>--Seleccione una etapa--</option>
              <option value="primera">Primera etapa</option>
              <option value="segunda">Segunda etapa</option>
            </select>
          </div>

          <div class="table-header">Especialidad</div>
          <div class="cell">
            <select name="especialidad" required>
              <option value="" selected disabled>--Seleccione una especialidad--</option>
              <c:forEach var="e" items="${especialidades}">
                <option value="${e.id}"
                    <c:if test="${not empty selEspecialidad and e.id == selEspecialidad.id}">selected</c:if>>
                  <c:out value="${e}" />
                </option>
              </c:forEach>
            </select>
          </div>

          <div class="table-header">Curso</div>
          <div class="cell">
            <select name="curso" id="curso-select" required>
              <option value="" selected disabled>--Seleccione un curso--</option>
              <option value="1">Primero</option>
              <option value="2">Segundo</option>
              <option value="3">Tercero</option>
            </select>
          </div>

          <div class="table-header">Sección</div>
          <div class="cell">
            <select name="seccion" id="seccion-select" required>
              <option value="" selected disabled>--Seleccione una sección--</option>
              <option value="A">A</option>
              <option value="B">B</option>
              <option value="C">C</option>
            </select>
          </div>

          <div class="table-header">Periodo</div>
          <div class="cell">
            <!-- required: admin must enter periodo (used to compute promocion) -->
            <input type="number" name="periodo" id="periodo-input" placeholder="2025" min="2000" required />
          </div>

          <div class="buttons-row table-header">
            <c:url var="backUrl" value="/PlanillaServlet">
              <c:param name="planillaId" value="${planillaId}" />
            </c:url>

            <button type="submit" id="downloadCourseBtn" class="download-button" title="Descargar planillas del curso">
              <img class="download-icon" src="${pageContext.request.contextPath}/icons/download-icon.svg" alt="Descargar">
              Descargar
            </button>
          </div>
        </div>
      </form>

    </section>



    <div class="footer">
      <hr>
      <p>
        Colegio Tecnico Nacional
      </p>
    </div>


  </main>

<script src="./scripts/session-dropdown.js"></script>

</body>

</html>
