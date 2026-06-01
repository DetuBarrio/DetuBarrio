# Accesibilidad y usabilidad del proyecto DeTuBarrio

## Portada

**Título del documento:** Accesibilidad y usabilidad del proyecto DeTuBarrio

**Proyecto:** DeTuBarrio

**Tecnologías principales:** Spring Boot, Vue 3, MySQL, JWT, Flyway

**Curso:** 2DAW

**Tipo de entrega:** Memoria técnica de accesibilidad y usabilidad

**Fecha:** 1 de junio de 2026

## Resumen

Este documento presenta una evaluación formal de la accesibilidad y la usabilidad del proyecto DeTuBarrio. El análisis se ha realizado a partir de la documentación del repositorio y de la revisión del frontend Vue 3 y del backend Spring Boot. El objetivo es justificar de forma técnica los puntos fuertes, los problemas detectados y las mejoras aplicadas, especialmente en navegación, formularios, lectura por lector de pantalla y coherencia de la interfaz.

## 1. Objetivo del informe

Este documento analiza la accesibilidad y la usabilidad del proyecto DeTuBarrio a partir de la documentacion del repositorio y de la implementacion real del frontend Vue 3 y del backend Spring Boot.

Documentacion revisada:

- [README.md](README.md)
- [ARQUITECTURA.md](ARQUITECTURA.md)
- [MANUAL_USO.md](MANUAL_USO.md)
- [AIVEN_SETUP.md](AIVEN_SETUP.md)

La lectura de esos documentos confirma que el proyecto es una plataforma de comercio de barrio con autentificacion JWT, dashboards por rol y un frontend principal en Vue. Eso implica una carga importante de formularios, navegacion entre vistas y estados dinamicos, asi que la accesibilidad no es un anexo: afecta directamente a la experiencia de registro, busqueda, detalle de comercio y gestion por rol.

## 2. Metodologia de analisis

El analisis se ha hecho sobre el codigo y la documentacion del repositorio, con foco en:

- Principios WCAG: perceptible, operable, comprensible y robusto.
- Heuristicas de Nielsen aplicadas a la interfaz.
- Patrones de formularios, navegacion y lectura por lector de pantalla.
- Coherencia entre la documentacion funcional y la experiencia de uso real.

## 3. Resumen ejecutivo

El proyecto parte de una base razonable: usa HTML semantico en varias vistas, incluye `alt` en la mayoria de las imagenes relevantes, usa breadcrumbs, tiene botones claros y estructura por roles. Sin embargo, tambien he detectado debilidades tipicas de una SPA en produccion: varios controles dependian demasiado del placeholder, habia enlaces externos y legales poco definidos, faltaban asociaciones explicitas en formularios, algunas zonas eran solo visuales y no estaban optimizadas para lector de pantalla, y existian puntos mejorables de foco y navegacion por teclado.

Como resultado, el proyecto es funcional, pero aun no estaria listo para defender una experiencia completamente accesible sin una revision especifica y varias mejoras puntuales.

## 4. Documentacion revisada

La revision se ha apoyado en la documentacion ya existente del repositorio, que aporta contexto tecnico y funcional suficiente para evaluar la experiencia real del producto:

- [README.md](README.md)
- [ARQUITECTURA.md](ARQUITECTURA.md)
- [MANUAL_USO.md](MANUAL_USO.md)
- [AIVEN_SETUP.md](AIVEN_SETUP.md)

Estas memorias ya explican el stack, el flujo de autenticacion, los roles, la arquitectura cliente-servidor y el despliegue local. A partir de esa base, el informe se centra en la calidad de uso y no solo en la viabilidad tecnica.

## 5. Evaluacion por principios WCAG

### 5.1 Perceptible

Fortalezas:

- Las imagenes principales de tarjetas y comercios suelen incluir texto alternativo descriptivo.
- Hay uso de breadcrumb para dar contexto de ubicacion.
- El home usa jerarquia visual clara con encabezado principal, subtitulo y bloques de contenido.

Problemas detectados:

1. La imagen hero del home se leia como contenido, aunque en realidad es decorativa. Eso introducia ruido en lector de pantalla y no aportaba informacion util.
2. Varios formularios dependian mucho del placeholder como apoyo visual y no siempre de una asociacion programatica robusta entre etiqueta y control.
3. Los iconos visuales de algunas zonas no siempre explicaban su funcion por si solos.
4. En algunos estados de carga se mostraba solo un spinner sin suficiente explicacion adicional del estado.

### 5.2 Operable

Fortalezas:

- La interfaz usa botones reales y enlaces reales en la mayoria de flujos.
- La navegacion principal es consistente y visible.
- El breadcrumb facilita orientacion y vuelta atras.

Problemas detectados:

1. No habia un salto de contenido principal rapido para teclado.
2. El orden de tabulacion era correcto en la estructura general, pero mejorable para llegar al contenido sin recorrer siempre la cabecera completa.
3. Algunos controles se apoyaban en tabulacion visual, pero sin una capa adicional de ayuda para usuarios de teclado y lector de pantalla.
4. Enlaces del pie con `#` no aportaban una accion real, lo que rompia la expectativa de operabilidad.

### 5.3 Comprensible

Fortalezas:

- Los textos de botones y secciones son bastante descriptivos.
- El login distingue entre acceso y registro de forma clara.
- El manual de uso ya organiza flujos por rol y tareas frecuentes.

Problemas detectados:

1. Algunos mensajes de error no estaban asociados a un rol ARIA de alerta, por lo que un lector de pantalla podia no anunciar el cambio de estado con la prioridad adecuada.
2. Habia enlaces que no expresaban bien su destino o su efecto real, especialmente en el footer.
3. En el home, el buscador mostraba un label visual y un placeholder, pero faltaba una asociacion mas explicita y un rol de busqueda.
4. En varias zonas la navegacion era coherente, pero no siempre predecible si el usuario no conoce la logica de la SPA.

### 5.4 Robusto

Fortalezas:

- Se usa estructura semantica con `nav`, `main`, `section`, `header`, `footer` y listas.
- Hay componentes Vue con separacion clara entre vista, logica y estilos.
- El breadcrumb usa `aria-label` y `aria-current` correctamente.

Problemas detectados:

1. Algunos enlaces abrian en nueva pestaña sin `rel="noopener noreferrer"`, lo que no es ideal desde el punto de vista de robustez y seguridad.
2. Varias etiquetas no estaban vinculadas por `for` e `id`, reduciendo la fuerza semantica del formulario.
3. El logo del navbar tenia un texto alternativo demasiado generico.
4. Habia oportunidades de reforzar el anuncio de cambios dinamicos mediante `role`, `aria-live` y estados mas claros.

## 6. Problemas detectados

### 6.1 Lista de problemas con criterio, motivo y mejora

1. Imagen hero del home con texto alternativo poco util.
- Principio afectado: perceptible.
- Problema: un lector de pantalla podia interpretar una imagen decorativa como contenido relevante.
- Mejora: marcarla como decorativa con `alt=""` y `aria-hidden="true"`.

2. Buscador principal sin etiqueta explicita vinculada.
- Principio afectado: comprensible y robusto.
- Problema: el usuario dependia del placeholder y del contexto visual.
- Mejora: añadir `label` visible para lector de pantalla y `role="search"` al formulario.

3. Mensajes de error y exito sin rol de alerta.
- Principio afectado: comprensible.
- Problema: los cambios dinamicos podian pasar desapercibidos para lector de pantalla.
- Mejora: usar `role="alert"` para errores y `aria-live` en mensajes dinamicos.

4. Labels de formularios sin asociacion explicita con campos.
- Principio afectado: robusto.
- Problema: el texto de la etiqueta no quedaba programaticamente ligado al control.
- Mejora: enlazar `for` e `id` en todos los campos clave.

5. Falta de salto rapido al contenido principal.
- Principio afectado: operable.
- Problema: los usuarios de teclado deben recorrer cabecera y menu cada vez.
- Mejora: introducir un skip link visible al recibir foco.

6. Imagen del logo con alt generico.
- Principio afectado: perceptible.
- Problema: no informaba del contexto de marca.
- Mejora: `alt="Logo de DetuBarrio"`.

7. Enlaces del footer que abren en nueva pestaña sin indicarlo bien ni reforzar seguridad.
- Principio afectado: comprensible y robusto.
- Problema: el comportamiento real no quedaba suficientemente explicitado.
- Mejora: añadir `aria-label` y `rel="noopener noreferrer"`.

8. Enlaces legales con destino placeholder `#`.
- Principio afectado: operable y comprensible.
- Problema: el usuario espera una accion real y no la obtiene.
- Mejora: sustituirlos por paginas reales o deshabilitarlos de forma honesta hasta que existan.

9. Carga de secciones con spinner sin contexto adicional.
- Principio afectado: comprensible.
- Problema: no siempre se informa de que se esta cargando y que contenido se espera.
- Mejora: texto auxiliar o region live para anunciar estado de carga.

10. Algunos iconos son decorativos pero se presentan como si fueran parte del texto.
- Principio afectado: perceptible.
- Problema: ruido extra para lectores de pantalla.
- Mejora: marcar iconos no informativos con atributos de ocultacion semantica cuando proceda.

## 7. Pruebas con lector de pantalla

No se ha ejecutado un lector de pantalla completo en esta sesion de forma interactiva con voz real del sistema, asi que este apartado se documenta como prueba guiada a partir de la estructura del codigo, los cambios aplicados y la instalacion de la extension Screen Reader Mode para preparar la verificacion en VS Code.

### 7.1 Navegacion por la pagina principal

Lo que deberia leerse correctamente:

- El titulo principal del home.
- El menu superior y las opciones principales.
- Los botones de categoria y los comercios destacados.

Lo que puede no entenderse bien antes de los cambios:

- La imagen hero decorativa, que podia sonar como contenido relevante.
- El buscador, si el usuario dependia solo del placeholder.

Que cambiaria:

- Reducir el ruido de contenido decorativo.
- Anunciar el buscador como region de busqueda y no solo como caja de texto aislada.

### 7.2 Navegacion por un formulario

Lo que deberia leerse correctamente:

- Campos de acceso y registro en orden logico.
- Botones de envio y cambio de pestaña.
- Mensajes de error y exito cuando aparecen.

Lo que no se entendia del todo bien antes:

- Algunos labels no estaban enlazados de forma programatica.
- Los mensajes dinamicos no siempre tenian prioridad de anuncio.

Que cambiaria:

- Asociar etiquetas y campos.
- Reforzar los estados dinamicos con ARIA solo donde aporten valor real.

### 7.3 Lectura de botones y enlaces

Lo que se entiende correctamente:

- Enlaces de navegacion principal.
- Botones de accion como iniciar sesion, buscar o limpiar filtros.

Lo que no se entiende tan bien:

- Enlaces del footer con destino ficticio.
- Iconos aislados sin contexto suficiente.

Problemas de teclado detectables en la revision:

- Falta un acceso directo al contenido principal.
- El flujo de lectura inicial puede ser mas largo de lo necesario.

## 8. Usabilidad segun Nielsen

### 8.1 Visibilidad del estado del sistema

El proyecto muestra spinners y mensajes de carga en varias vistas, pero conviene complementar eso con textos y regiones anunciables para que el estado se entienda tambien sin vision.

### 8.2 Correspondencia entre el sistema y el mundo real

El lenguaje es cercano: barrio, comercios, reservas, contacto y dashboard. Eso ayuda a usuarios no tecnicos. Aun asi, el footer y algunos estados internos podrian ser mas explicitos.

### 8.3 Control y libertad del usuario

Hay navegacion clara para volver al inicio o usar breadcrumbs. El skip link refuerza este principio porque facilita salir de la cabecera sin esfuerzo extra.

### 8.4 Consistencia y estandares

La interfaz mantiene un estilo visual bastante uniforme. El riesgo principal estaba en enlaces de marcador de posicion y en formularios donde la semantica no era uniforme.

### 8.5 Prevencion de errores

El login y el registro ya validan campos obligatorios, pero pueden reforzarse mensajes de ayuda, estados invalidos y ayuda contextual en formularios mas largos.

## 9. Leyes de usabilidad aplicadas

### 9.1 Ley de Fitts

Los botones principales del proyecto estan bien dimensionados en general, sobre todo en login, buscador y acciones de dashboard. Aun asi, los enlaces pequenos del footer y algunos iconos de accion pueden ser dificiles de pulsar en pantallas tactiles.

### 9.2 Ley de Hick

El proyecto tiene varias rutas y roles, por lo que la cantidad de opciones puede crecer rapidamente. La jerarquia por rol, el menu principal y los breadcrumbs ayudan a reducir la carga cognitiva, pero los formularios extensos deberian dividirse mejor o apoyarse con ayudas contextuales si crecen mas.

## 10. Mejoras implementadas

Se han aplicado estas mejoras directamente en el frontend:

1. Se ha añadido un salto al contenido principal para teclado.
2. Se ha mejorado el texto alternativo del logo.
3. Se ha marcado la imagen hero del home como decorativa.
4. Se ha etiquetado el buscador principal como region de busqueda y se ha vinculado su label.
5. Se han asociado labels e inputs en login y registro.
6. Se han reforzado los mensajes de error y exito con roles y regiones live.
7. Se han mejorado los enlaces externos del footer con seguridad y descripcion de destino.

## 11. Evidencias visuales


### 11.1 Home principal

![Home principal de DetuBarrio](docs/accesibilidad/01-home.png)

La pagina principal muestra la estructura de bienvenida, el buscador accesible, las categorias y los comercios destacados. Es la vista mas importante para justificar la navegacion general y el orden visual.


### 11.2 Pantalla de login

![Pantalla de inicio de sesion de DetuBarrio](docs/accesibilidad/03-login.png)

La pantalla de acceso permite ver la separacion entre login y registro, la presencia de labels asociados y la disposicion de los controles principales.


### 11.3 Pantalla de registro

![Pantalla de registro de DetuBarrio](docs/accesibilidad/04-register.png)

La pantalla de alta muestra el formulario de registro con la seleccion de tipo de cuenta y la estructura de campos del formulario, que es uno de los puntos mas sensibles en accesibilidad.

## 12. Capturas sugeridas

Para completar la memoria de entrega, se recomiendan estas capturas antes y despues de la revision:

1. Home principal antes y despues de ocultar la imagen decorativa para lector de pantalla.
2. Buscador del home con su etiqueta accesible visible en inspeccion o en el lector de pantalla.
3. Login antes y despues de enlazar labels e inputs.
4. Registro antes y despues de añadir ids y roles ARIA en mensajes dinamicos.
5. Navegacion por teclado mostrando el skip link al recibir foco.
6. Footer antes y despues de corregir enlaces externos y atributos de seguridad.
7. Vista de comercios o detalle mostrando breadcrumbs y jerarquia semantica.
8. Prueba con lector de pantalla en una pantalla de formulario, registrando lo que lee correctamente y lo que no.

## 13. Mejoras recomendadas pendientes

1. Sustituir los enlaces legales `#` por paginas reales o quitar la accion hasta que existan.
2. Revisar todas las vistas con formularios largos para asegurar `for` e `id` en cada control.
3. Anadir textos auxiliares en los spinners y cargas asincronas para mejorar el anuncio por lector de pantalla.
4. Ejecutar una prueba real con lector de pantalla en Windows, por ejemplo NVDA, y registrar observaciones de lectura.
5. Revisar contraste de colores en estados secundarios y texto de bajo peso visual.

## 14. Conclusiones

DeTuBarrio tiene una base de accesibilidad aceptable para un proyecto funcional de TFG, pero necesita ajustes concretos para defender un nivel mas serio de calidad de interfaz. Los cambios aplicados mejoran la navegacion por teclado, la comprension de formularios y la lectura por lector de pantalla. A nivel metodologico, el punto mas importante no es solo la estetica, sino que cada control tenga nombre, proposito, estado y resultado entendibles sin depender de la vista.

Si necesitas evidencias visuales para la memoria, este informe ya te deja marcada la lista de capturas a hacer antes y despues de cada mejora. Para una entrega final, conviene adjuntar las capturas con titulo, fecha y breve descripcion en anexo o en el mismo documento.