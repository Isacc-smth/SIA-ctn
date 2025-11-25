<%-- 
    Document   : Home
    Created on : Aug 3, 2025, 4:39:40 PM
    Author     : jonat
--%>

<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDateTime"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>



<html>

<head>
  <title>CTNPortal</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" href="styles/header.css">
  <link rel="stylesheet" href="styles/home-header.css">
  <link rel="stylesheet" href="styles/general.css">
  <link rel="stylesheet" href="styles/top-section.css">
  <link rel="stylesheet" href="styles/grid.css">
  <link rel="stylesheet" href="styles/buttons.css">
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
        <div class="menu-container">

          <form action="HomeServlet" method="get">
            <select class="selCurso" name="cursoId" onchange="this.form.submit()">
              <option value="">--Seleccione un curso--</option>
              <c:forEach var="c" items="${cursos}">
                  <option value="${c.id}"
                          ${c.id == selCurso.id ? 'selected="selected"' : ''}>
                    ${c.toString()}
                  </option>
              </c:forEach>
            </select>
            <select class="selEtapa" name="etapa" onchange="this.form.submit()">
              <option value="">--Seleccione una etapa--</option>
              <option value="1" ${ selEtapa == 1? "selected" : ""}>primera etapa</option>
              <option value="2" ${ selEtapa == 2? "selected" : ""}>segunda etapa</option>
            </select>
          </form>

          <span id="date-range"><i><b>Desde:</b><!-- code here -->-<b>Hasta:</b><!-- code here --></i></span>
        </div>
        <h1>
          Materias de ${selCurso.especialidad} ${selCurso.getCurso()}.<sup>o</sup> "${selCurso.seccion}" - ${ selEtapa == 2? "Segunda Etapa" : "Primera Etapa"}
        </h1>
      </div>

      <div class="grid-container">
        <div class="planilla-grid"><!-- grid -->
          <c:forEach var="p" items="${planillas}">
              <c:url var="planillaUrl" value="/PlanillaServlet">
                  <c:param name="planillaId" value="${p.id}" />
                  <c:param name="cursoId" value="${selCurso.id}" />
                  <c:param name="etapa" value="${selEtapa}" />
              </c:url>
              <a class="planilla-card-link" href="${planillaUrl}">
                <div class="planilla-card">
                  <div class="card-heading largeSize">
                    ${p.nombre}
                  </div>
                  <div class="card-body">
                    <div class="info-grid">
                      <span class="total-tareas label">Total Tareas</span>
                      <span class="total-tareas colon">:</span>
                      <span class="total-tareas value">${p.tareasCount}</span>
                      <span class="label">En Proceso</span>
                      <span class="colon">:</span>
                      <span class="value">-</span>
                      <span class="label">Confirmados</span>
                      <span class="colon">:</span>
                      <span class="value">-</span>
                    </div>
                    <div class="card-footer">
                      <b>Última Tarea Creada:</b>
                      ${p.ultimaTarea}
                    </div>
                  </div>
                </div>
              </a>
          </c:forEach>
        </div>
      </div>

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

