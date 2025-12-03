# Sistema de Información Académica (S. I. A)

Este proyecto era originalmente de Jonathan Bray (por eso algunas cosas están en inglés),
pero el profe Fede me encargo implementar algunos features. Además documenté lo que pude del Java
y un poco del JavaScript porque hay mucho código in-line (o sea en los JSP). Aca hay algunas cosas
que yo considero que le falta:

## Cosas que faltan por desarrollar/arreglar

- Formularios ABM para las tablas. Ahora mismo se tienen que insertar manualmente en la base de datos,
  solo se pueden cargar tareas y puntajes
- El boton de eliminar (en editar tarea) tiene problemas en la logica, cuando hosteo local funciona, pero en el server no.
- Adaptar a las planillas del proyecto de Lujan (si se va a implementar)
- El pooling ahora mismo es muy estricto (corta conexiones pasado una hora), capaz tengan que hacer 
  que sea mas flexible

## Como compilar y correr el proyecto

El proyecto compila un .war, no un .jar como los demas proyectos. Entonces el trato es distinto.
Por un lado les facilita el hecho de que no tienen que incorporar los jar de sus dependencias para
armar un .exe, pero por otro configurar el TomCat es muy diferente en Windows (tiene una GUI) y
Linux/Mac (hay que configurar variables de entorno para el script de inicio). NetBeans NO configura
en ninguno de los dos casos.

### Documentación

#### Jsdoc

1. Instalar npm
2. Ejectuar

```sh
    npm i -g jsdoc # (Instala globalmente, capaz pida permisos de sudo en linux)
```

```sh
    jsdoc -c jsdoc.json # Cambiar 'jsdoc.json' si hay otro path a la configuacion
```

#### Javadoc

> [!TIP]
> Al compilar el proyecto, tambien se compila la documentacion

Si quieres compilar solo la documentacion

```sh
    mvn javadoc:javadoc
```

### Proyecto

1. Instalar y configurar Apache TomCat 9 (o superior).
2. Compilar su proyecto (sin ejecutar).
3. Mover el .war generado a la carpeta webapps, en donde instalaron el TomCat.
