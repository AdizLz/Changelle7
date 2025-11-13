# Cómo generar los diagramas en PDF

## Opción 1: Usando PlantUML JAR (Recomendado)

1. Descargar PlantUML:
   ```
   https://plantuml.com/download
   ```
   Guardar en: `C:\tools\plantuml\plantuml.jar`

2. Generar PDFs desde cmd:
   ```cmd
   cd C:\Users\Soporte\Downloads\Reto6\docs\diagrams
   java -jar C:\tools\plantuml\plantuml.jar -tpdf system-architecture.puml
   java -jar C:\tools\plantuml\plantuml.jar -tpdf class-diagram.puml
   ```

Los PDFs se crearán en la misma carpeta.

## Opción 2: Usando PlantUML Online

1. Ir a: https://www.plantuml.com/plantuml/uml/
2. Copiar el contenido de `system-architecture.puml`
3. Pegar en el editor online
4. Hacer clic en "PNG" o "PDF" para descargar
5. Repetir con `class-diagram.puml`

## Opción 3: Usando Visual Studio Code

1. Instalar extensión "PlantUML" en VS Code
2. Abrir archivos .puml
3. Presionar `Alt+D` para previsualizar
4. Clic derecho > Export Current Diagram > PDF

## Resultado esperado

Archivos generados:
- `docs/diagrams/system-architecture.pdf`
- `docs/diagrams/class-diagram.pdf`

