# Compresor LZ78 - Soporte de Archivos

## Tipos de Archivos Soportados

El compresor LZ78 ahora puede comprimir y descomprimir los siguientes tipos de archivos:

### Archivos de Texto
- **`.txt`** - Archivos de texto plano

### Documentos de Oficina
- **`.docx`** - Microsoft Word (formato moderno)
- **`.doc`** - Microsoft Word (formato antiguo)
- **`.xlsx`** - Microsoft Excel (formato moderno)
- **`.xls`** - Microsoft Excel (formato antiguo)

### Archivos PDF
- **`.pdf`** - Documentos PDF

### Imágenes
- **`.jpg`** / **`.jpeg`** - Imágenes JPEG
- **`.png`** - Imágenes PNG
- **`.gif`** - Imágenes GIF

### Archivos Comprimidos
- **`.zip`** - Archivos ZIP
- **`.rar`** - Archivos RAR

## Funcionamiento

### Compresión
1. Haz clic en **"📁 Cargar Archivo"** en la pestaña de Compresión
2. Selecciona el archivo que deseas comprimir (puede ser de cualquier tipo soportado)
3. Haz clic en **"🗜️ Comprimir"**
4. Haz clic en **"💾 Guardar Comprimido"**
5. El archivo se guardará como `nombrearchivo_comprimido.lz78`

### Descompresión
1. Haz clic en **"📁 Cargar Archivo Comprimido"** en la pestaña de Descompresión
2. Selecciona el archivo `.lz78` que deseas descomprimir
3. Haz clic en **"📦 Descomprimir"**
4. Haz clic en **"💾 Guardar Descomprimido"**
5. El archivo se restaurará con su extensión original: `nombrearchivo_descomprimido.ext`

## Notas Importantes

- Los **archivos de texto** (`.txt`) se procesan como texto UTF-8
- Los **archivos binarios** (Word, Excel, PDF, imágenes, etc.) se procesan como datos binarios
- El formato `.lz78` guarda la extensión original del archivo para restaurarlo correctamente
- Los nombres de archivo se conservan automáticamente con sufijos `_comprimido` y `_descomprimido`

## Formato del Archivo .lz78

El archivo comprimido `.lz78` contiene:
1. Número mágico "LZ78"
2. Extensión original del archivo (ej: ".docx", ".pdf")
3. Tamaño original del archivo
4. Número de pares codificados
5. Pares (índice, carácter) del algoritmo LZ78

## Ejemplo de Uso

```
archivo.docx  →  [Comprimir]  →  archivo_comprimido.lz78
archivo_comprimido.lz78  →  [Descomprimir]  →  archivo_comprimido_descomprimido.docx
```
