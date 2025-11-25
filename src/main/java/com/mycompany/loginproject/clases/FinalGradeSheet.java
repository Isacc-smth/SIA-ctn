package com.mycompany.loginproject.clases;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mycompany.loginproject.dao.PlanillaDao.PlanillaInfo;
import com.mycompany.loginproject.dao.StudentRowDao;
import com.mycompany.loginproject.model.Planilla;

/**
 * Clase para generar la última hoja de la planilla general (La que solo el
 * admin puede acceder)
 *
 * @author Isacc-smth
 */
public class FinalGradeSheet {
    /**
     * Obtener todas las notas finales (del 1 al 5) de un alumno en un libro excel
     *
     * @param wb         el libro de excel a procesar
     * @param planillas  las planillas a adjuntar en la hoja
     * @param sheetNames una lista con todos los nombres de las planillas
     * @param out        el stream de salida a donde se escribe el libro
     *
     * @throws NumberFormatException cuando uno de los campos numéricos es inválido
     * @throws SQLException          cuando hay errores relacionado a la base de
     *                               datos
     *                               (e.j: Consulta mal formada o errores de
     *                               conexión, etc).
     * @throws IOException           cuando hay errores relacionados a la escritura
     *                               de la planilla al stream
     *                               de salida (del servlet)
     *
     */
    public static void generate(
            XSSFWorkbook wb,
            List<PlanillaInfo> planillas,
            ArrayList<String> sheetNames,
            OutputStream out) throws NumberFormatException, SQLException, IOException {

        Sheet finalSheet = wb.createSheet("Planilla Final");
        Row header = finalSheet.createRow(0);

        int col = 0;
        header.createCell(col++).setCellValue("#");
        header.createCell(col++).setCellValue("Alumno");
        XSSFCellStyle style = wb.createCellStyle();
        // style.setRotation((short) 90);
        style.setWrapText(true);
        // Id de planilla -> total
        int planillaId = 0;

        for (PlanillaInfo planillaInfo : planillas) {
            Planilla planilla = planillaInfo.getPlanilla();
            planillaId = planilla.getId();

            String cellValue = planillaInfo.getMateriaNombre() + " \n" + planilla.getEtapa() + " e.";
            Cell headerCell = header.createCell(col++);
            headerCell.setCellValue(cellValue);
            headerCell.setCellStyle(style);
        }

        if (planillaId == 0) {
            throw new IllegalStateException("It looks like no planillas were found (planilla Id is 0)");
        }

        ArrayList<String> studentNames = new StudentRowDao().getStudentNames(planillaId);
        if (studentNames == null) {
            throw new NullPointerException("It looks like no students were found (studentNames is null)");
        }

        int rownum = 1;
        col = 0;
        int count = 0;
        for (String name : studentNames) {
            Row row = finalSheet.createRow(rownum);
            row.createCell(col++).setCellValue(count);
            finalSheet.autoSizeColumn(col);
            Cell cell = row.createCell(col);
            cell.setCellValue(name);
            col++;

            // Como todas las celdas tienen una misma posición, puedo hardcodear las celdas
            for (String sheetName : sheetNames) {
                String cellpos = String.format("'%s'!E%d", sheetName, count + 2);
                row.createCell(col++).setCellFormula(cellpos);
            }

            col = 0;
            count++;
            rownum++;
        }

    }
}
