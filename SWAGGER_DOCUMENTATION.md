# 📚 Documentación API con Swagger/OpenAPI

## 🚀 Acceso a la Documentación

Una vez que el servidor Spring Boot esté ejecutándose, la documentación Swagger estará disponible en:

### URLs de Acceso:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **OpenAPI YAML**: `http://localhost:8080/v3/api-docs.yaml`

## 🔧 Cómo Ejecutar

### 1. Iniciar el Backend
```bash
cd inventory-system
./mvnw spring-boot:run
```

### 2. Acceder a Swagger UI
Abrir en el navegador: `http://localhost:8080/swagger-ui.html`

## 🔐 Autenticación en Swagger

### Configurar JWT Token:
1. Hacer login en `/auth/login` para obtener el token
2. En Swagger UI, hacer clic en el botón **"Authorize"** 🔓
3. Ingresar el token en formato: `Bearer tu_jwt_token_aqui`
4. Hacer clic en **"Authorize"**

### Ejemplo:
```
Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 📋 Endpoints Documentados

### 🔑 Authentication (`/auth`)
- `POST /auth/login` - Iniciar sesión
- `POST /auth/register` - Registrar usuario
- `POST /auth/logout` - Cerrar sesión
- `POST /auth/refresh` - Renovar token
- `GET /auth/validate` - Validar sesión

### 📦 Products (`/products`)
- `GET /products` - Obtener todos los productos
- `GET /products/{id}` - Obtener producto por ID
- `POST /products` - Crear nuevo producto
- `PUT /products/{id}` - Actualizar producto
- `DELETE /products/{id}` - Eliminar producto
- `GET /products/low-inventory` - Productos con stock bajo

### 📊 Reports (`/reports`)
- `GET /reports/low-inventory` - Reporte PDF de inventario bajo

## ⚙️ Configuración

### application.properties
```properties
# Configuración de OpenAPI/Swagger
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.disable-swagger-default-url=true
```

### Dependencia Maven
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.9</version>
</dependency>
```

## 🌟 Características

- ✅ **Documentación Automática**: Se genera automáticamente desde el código
- ✅ **Interfaz Interactiva**: Probar APIs directamente desde el navegador
- ✅ **Autenticación JWT**: Configurada con Bearer token
- ✅ **Schemas de Datos**: Modelos de request/response documentados
- ✅ **Validaciones**: Muestra validaciones de campos requeridos
- ✅ **Códigos de Respuesta**: Documenta todos los códigos HTTP posibles

## 💡 Tips de Uso

1. **Explorar Schemas**: Clic en los modelos para ver estructura de datos
2. **Try it out**: Usar el botón "Try it out" para probar endpoints
3. **Copiar cURL**: Swagger genera comandos cURL automáticamente
4. **Descargar OpenAPI**: Exportar definición en JSON/YAML para otros tools

## 🐛 Troubleshooting

### Swagger UI no carga:
- Verificar que el servidor esté ejecutándose en puerto 8080
- Revisar logs del servidor para errores
- Verificar que las dependencias estén correctamente instaladas

### Token de autenticación no funciona:
- Verificar formato: debe incluir "Bearer " antes del token
- Token debe estar válido (no expirado)
- Verificar que el endpoint usado genere tokens válidos

---
**Desarrollado con ❤️ usando SpringDoc OpenAPI**