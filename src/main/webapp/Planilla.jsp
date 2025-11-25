<%-- 
    Document   : Planillas
    Created on : Aug 10, 2025, 5:17:28 PM
    Author     : jonat
--%>

<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>${planilla.nombre}</title>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/7.0.1/css/all.min.css" integrity="sha512-2SwdPD6INVrV/lHTZbO2nodKhrnDdJK9/kg2XD1r9uGqPo1cUbujc+IYdlYdEErWNu69gVcYgdxlmVmzTWnetw==" crossorigin="anonymous" referrerpolicy="no-referrer" />
  <link rel="stylesheet" href="styles/header.css">
  <link rel="stylesheet" href="styles/home-header.css">
  <link rel="stylesheet" href="styles/general.css">
  <link rel="stylesheet" href="styles/top-section.css">
  <link rel="stylesheet" href="styles/planilla.css">
  <link rel="stylesheet" href="styles/planilla-table.css">
  <link rel="stylesheet" href="styles/buttons.css">
  <link rel="stylesheet" href="styles/flash.css">
  <link rel="stylesheet" href="styles/grade-table.css">
  <link rel="stylesheet" href="styles/email-form.css">
  <script src="scripts/planilla.js"></script>
  <script src="scripts/session-dropdown.js"></script>
  <script src="scripts/email-form.js"></script>
  <link rel="icon" type="image/x-icon" href="images/ctn-logo.svg">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link href="https://fonts.googleapis.com/css2?family=Roboto:ital,wght@0,100..900;1,100..900&display=swap" rel="stylesheet">

  <style>
    .table {
      grid-template-columns:
        30px
        max-content
        95px
        <c:forEach var="t" items="${tareas}">
          95px
        </c:forEach>
        1fr
    }
  </style>
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

        <c:if test="${not empty sessionScope.flashErrors}">
            <c:forEach var="err" items="${sessionScope.flashErrors}">
                <div class="flash-errors" data-timeout="4000">
                  <c:out value="${err}" />
                  <button type="button" class="flash-close" aria-label="Cerrar mensajes de error">&times;</button>
                </div>
            </c:forEach>
            <c:remove var="flashErrors" scope="session"/>
        </c:if>

      <div class="top-section">
        <div class="menu-container">

          <form action="" method="get">
            <input type="hidden" name="cursoId" value="${cursoId}" />
            <input type="hidden" name="materiaId" value="${materiaId}" />

            <select class="selEtapa" id="etapaSelect" name="etapa" onchange="this.form.submit()">
              <option value="">--Seleccione una etapa--</option>
              <option value="1" ${ etapa == 1? "selected" : ""}>primera etapa</option>
              <option value="2" ${ etapa == 2? "selected" : ""}>segunda etapa</option>
            </select>
          </form>

          <span id="date-range"><i><b>Desde:</b><!-- code here -->-<b>Hasta:</b><!-- code here --></i></span>
        </div>
        <h1>${planilla.nombre} - ${curso.especialidad} ${curso.getCurso()}.<sup>o</sup> "${curso.seccion}" - ${planilla.etapa} etapa</h1>
        <div class="buttons-container">
          <%-- create a URL back to HomeServlet preserving cursoId + etapa --%>
          <c:url var="backUrl" value="/HomeServlet">
              <c:param name="cursoId" value="${cursoId}" />
              <c:param name="etapa" value="${etapa}" />
          </c:url>

          <!-- Back link (anchor) — not a form submit -->
          <div class="button-group">
            <a id="backBtn" class="back-button" href="${backUrl}">
              <img class="back-icon" src="${pageContext.request.contextPath}/icons/back-arrow.svg" alt="Atrás">
              Atrás
            </a>
            <c:url var="downloadUrl" value="/ExportPlanillaServlet">
                <c:param name="planillaId" value="${planilla.id}" />
            </c:url>

            <a id="downloadBtn" class="download-button" href="${downloadUrl}">
              <img class="download-icon" src="${pageContext.request.contextPath}/icons/download-icon.svg" alt="Descargar">
              Descargar
            </a>
          </div>
          <span>TIP: Haz click en el nombre del alumno para tener una planilla con todas sus tareas</span>

          <!-- New-task button (left as-is) -->

          <c:url var="tareaUrl" value="/TareaServlet">
              <c:param name="planillaId" value="${planilla.id}" />
              <c:param name="etapa" value="${etapa}" />
          </c:url>

          <a id="addBtn" class="add-button" href="${tareaUrl}">
            <img class="add-icon" src="${pageContext.request.contextPath}/icons/add.svg" alt="Nueva Tarea">
            Nueva Tarea
          </a>
        </div>
      </div>

      <form method="post" action="${pageContext.request.contextPath}/PlanillaServlet">
        <input type="hidden" name="cursoId" value="${cursoId}" />
        <input type="hidden" name="materiaId" value="${materiaId}" />
        <input type="hidden" name="etapa" value="${etapa}" />


        <div class="planilla-info-bar">
          <div class="grade-ranges-container">
            <span class="five-highlight">${gradeRanges['5'][0]} a ${gradeRanges['5'][1]}</span>
            <span class="four-highlight">${gradeRanges['4'][0]} a ${gradeRanges['4'][1]}</span>
            <span class="three-highlight">${gradeRanges['3'][0]} a ${gradeRanges['3'][1]}</span>
            <span class="two-highlight">${gradeRanges['2'][0]} a ${gradeRanges['2'][1]}</span>
            <span class="one-highlight">${gradeRanges['2'][0] - 1} y menos</span>

            <label class="freeze-toggle" title="Fijar columnas # y Alumno">
              <input type="checkbox" id="freezeCheckbox" data-ignore-dirty/>
              Inmovilizar alumnos
            </label>
          </div>
          <div class="escala-info">
            Escala: (Total de Puntos ${totalPossiblePoints}) - Porcentaje de Exigencia: ${exigencia}&percnt;
          </div>
          <button class="save-button">
            <img class="save-icon" src="icons/save.svg">
            Guardar Cambios
          </button>
        </div>



        <div class="table-container">
          <div class="table-responsive">
            <div class="table">
              <div class="table-row">
                <div class="table-heading">
                  Tareas - ${planilla.nombre}
                </div>
              </div>
              <div class="table-row">
                <div class="cell col-index">#</div>
                <div class="cell col-alumno">Alumno</div>
                <div class="cell">Total de Puntos (${totalPossiblePoints})</div>
                <c:forEach var="t" items="${tareas}">
                  <div class="cell">
                    <a class="tarea-edit-link"
                       href="${pageContext.request.contextPath}/TareaServlet?planillaId=${planilla.id}&amp;tareaId=${t.id}&amp;etapa=${etapa}">
                      <c:out value="${t.titulo}" /> (TP:<c:out value="${t.total}" />)
                    </a>
                  </div>
                </c:forEach>
                <c:url var="tareaUrl" value="/TareaServlet">
                  <c:param name="planillaId" value="${planilla.id}" />
                  <c:param name="etapa" value="${etapa}" />
                </c:url>
                <div class="cell new-tarea">
                  <a id="new-tarea-link" href="${tareaUrl}">Nueva Tarea</a>
                </div>
              </div>

              <c:forEach var="row" items="${rows}" varStatus="rs">
                <div class="table-row">
                  <div class="cell col-index">${rs.index + 1}</div>
                  <div class="cell col-alumno">
                      <a href="${pageContext.request.contextPath}/ExportAlumnoSheetServlet?registroID=${row.registroId}">
                          ${row.alumnoNombre}
                      </a>
                  </div>
                  <div class="cell">${row.total} (${row.porcentaje}%) <br> Nota: ${row.nota}</div>
                  <c:forEach var="t" items="${tareas}">
                    <div class="cell">
                      <input class="no-spinner"
                             type="number"
                             min="0"
                             max="${t.total}"
                             name="grade_${row.alumnoId}_${t.id}"
                             value="${row.grades[t.id] != null ? row.grades[t.id] : ''}" /> <br>
                             <c:if test="${t.instrumentoId != 13}">
                         <a class="pdf-button"
                         href="${pageContext.request.contextPath}/ExportAlumnoServlet?registroId=${row.registroId}&tareaId=${t.id}&tareaTotal=${t.total}&titulo=${t.titulo}">
                         <i class="fa-solid fa-file-import"></i>
                         <!-- NOTE: lgm porque me pidio el profe nm puse esto, pero hoy día pocos son los que revisan sus emails-->
                         </a>
                         <c:choose>
                           <c:when test="${empty row.correoTutor1 && empty row.correoTutor2}">
                               <button id="email-button">
                                   <i class="fa-solid fa-envelope"></i>
                               </button>
                           </c:when>
                           <c:otherwise>
                             <a class="email-submit"
                                href="${pageContext.request.contextPath}/ExportEmailServlet?registroId=${row.registroId}&tareaId=${t.id}&tareaTotal=${t.total}&titulo=${t.titulo}&planillaId=${planilla.id}">
                             <i class="fa-solid fa-envelope"></i>
                             </a>
                           </c:otherwise>
                         </c:choose>
                     </c:if>
                    </div>
                  </c:forEach>
                  <div class="cell"></div>
                </div>
              </c:forEach>

            </div>
          </div>
        </div>
      </form>

      <dialog id="email-form">
          <form action="/EmailSendServlet?registroId=${row.registroId}&tareaId=${t.id}&tareaTotal=${t.total}&titulo=${t.titulo}" method="post">
            <h3>Correo no encontrado</h3>
            <label>No se encontraron emails asociados con el alumno, ingrese la direccion a la que desea enviar</label> <br>
            <input type=email name=email placeholder="Email" id="emailInput" required>
            <button id="dialogClose" type=submit>Enviar</button>
        </form>
      </dialog>

    </section>

    <section class="container">
      <table class="grade-table">
        <thead>
          <tr>
            <td colspan="3" class="text-uppercase">Escala: (TP: ${totalPossiblePoints})</td>
          </tr>
        </thead>
        <tbody>
          <tr>
            <th>DESDE</th>
            <th>HASTA</th>
            <th>NOTA</th>
          </tr>
          <tr class="five-color">
            <td>${gradeRanges['5'][0]}</td>
            <td>${gradeRanges['5'][1]}</td>
            <td>5</td>
          </tr>
          <tr class="four-color">
            <td>${gradeRanges['4'][0]}</td>
            <td>${gradeRanges['4'][1]}</td>
            <td>4</td>
          </tr>
          <tr class="three-color">
            <td>${gradeRanges['3'][0]}</td>
            <td>${gradeRanges['3'][1]}</td>
            <td>3</td>
          </tr>
          <tr class="two-color">
            <td>${gradeRanges['2'][0]}</td>
            <td>${gradeRanges['2'][1]}</td>
            <td>2</td>
          </tr>
          <tr class="one-color">
            <td>${gradeRanges['2'][0] - 1}</td>
            <td>y menos</td>
            <td>1</td>
          </tr>
        </tbody>
        <tfoot>
          <tr>
            <td colspan="3">Exigencia: ${exigencia}&percnt;</td>
          </tr>
        </tfoot>
      </table>
    </section>

  <div class="footer">
    <hr>
    <p>
      Colegio Tecnico Nacional
    </p>
  </div>

  </main>
</body>

</html>
