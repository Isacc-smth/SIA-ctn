import com.mycompany.loginproject.clases.conexion;
import com.mycompany.loginproject.dao.StudentRowDao;
import com.mycompany.loginproject.dao.TareaDao;
import com.mycompany.loginproject.model.Planilla;
import com.mycompany.loginproject.model.StudentRow;
import com.mycompany.loginproject.model.Tarea;
import java.util.List;
import java.util.Map;
import java.sql.SQLException;
import java.util.HashMap;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba unitaria para StudentRowDao
 *
 * @see com.mycompany.loginproject.dao.StudentRowDao
 */
public class StudentRowDaoEmailTest {

    // TODO: Terminar y validar para las demas espes si hace falta
    //
    // private static StudentRowDao studentRowDao;
    // private static TareaDao tareaDao;
    // private static conexion db;
    // private static Planilla planilla;
    // private static List<Tarea> tareas;
    //
    // @BeforeAll
    // static void setup() throws SQLException {
    //     db = new conexion();
    //     studentRowDao = new StudentRowDao();
    //     tareaDao = new TareaDao();
    //     Planilla planilla = new Planilla();
    //     planilla.setId(13);
    //     planilla.setCursoId(5);
    //     planilla.setEtapa("Primera Etapa");
    //     tareas = tareaDao.consultarTarea(13);
    // }
    //
    // @Test
    // void testAllPrimaryEmails() throws SQLException {
    //     Map<Integer, Integer> tareaMax = new HashMap<>();
    //     int totalPossiblePoints = 0;
    //     for (Tarea t : tareas) {
    //         tareaMax.put(t.getId(), t.getTotal());
    //         totalPossiblePoints += t.getTotal();
    //     }
    //     List<StudentRow> rows = studentRowDao.loadRowsForPlanilla(planilla, tareaMax, totalPossiblePoints);
    //
    //     for (StudentRow row : rows) {
    //         assertNotNull(row.getCorreoTutor1());
    //         assertFalse(
    //             row.getCorreoTutor1().isBlank() || row.getCorreoTutor2().isBlank(),
    //             "Ambos correoes estan vacios para el alumno" + row.getAlumnoNombre()
    //         );
    //     }
    // }
}
