# K-TARO

Estructura inicial de K-TARO, una tienda web de productos asiáticos. El backend usa
Spring Boot con Java 21 y Maven; las vistas se preparan con Thymeleaf, y los archivos
estáticos pueden organizarse en CSS, JavaScript e imágenes.

## Requisitos

- JDK 21
- Node.js y npm

## Ejecutar en desarrollo

Desde la carpeta `demo/`, en PowerShell:

```powershell
.\mvnw.cmd "-Dspring-boot.run.profiles=dev" spring-boot:run
```

El perfil `dev` desactiva temporalmente la autoconfiguración de JPA y del origen de
datos, por lo que la aplicación puede iniciar sin una instancia de MySQL. No se
configuran tablas ni conexiones reales. Para usar la configuración normal de
persistencia en el futuro, configura las credenciales fuera del repositorio y ejecuta
sin el perfil `dev`.

## TypeScript

Desde la carpeta `demo/`:

```powershell
npm install
npm run build
```

Para recompilar automáticamente al guardar cambios en `frontend/ts/`:

```powershell
npm run watch
```

Los archivos `.ts` se compilan en `src/main/resources/static/js/ts/`, que se excluye
de Git para no mezclar ni sobrescribir JavaScript escrito manualmente en
`src/main/resources/static/js/`.

## Estructura

```text
demo/
├── frontend/
│   └── ts/
│       ├── detalle.ts              # Selector de cantidad del detalle
│       ├── index.ts                # Módulo inicial sin funcionalidad
│       ├── inicio.ts
│       └── snacks.ts
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── model/
│   │   │   │   └── Product.java
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   │   └── ProductCatalogService.java
│   │   │   └── DemoApplication.java
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── css/
│   │       │   ├── images/
│   │       │   └── js/
│   │       │       └── ts/         # Salida compilada (ignorada por Git)
│   │       ├── templates/
│   │       │   ├── admin/
│   │       │   ├── carrito/
│   │       │   ├── fragments/
│   │       │   ├── pedidos/
│   │       │   ├── productos/
│   │       │   └── usuario/
│   │       ├── application-dev.properties
│   │       └── application.properties
│   └── test/
├── package.json
├── package-lock.json
├── pom.xml
└── tsconfig.json
```

Los directorios funcionales se dejan vacíos intencionalmente hasta que el equipo
implemente las vistas y componentes correspondientes.

## Página de inicio

La ruta `/` renderiza la vista Thymeleaf `inicio.html`, con el catálogo destacado y
las promociones alimentados desde `HomeController`. El carrito de demostración se
guarda en `localStorage`; no requiere autenticación ni persistencia en la base de
datos. Las imágenes locales de los productos se mantienen en `static/images/`; los
productos sin recurso disponible muestran un marcador temporal.

Los cambios de `frontend/ts/inicio.ts` se validan y empaquetan con `npm run build`.
La salida generada queda en `static/js/ts/` y está excluida de Git.

## Catálogo y detalle de productos

`ProductCatalogService` mantiene la fuente local compartida de productos para Inicio,
el catálogo y la página de detalle. Cada producto tiene un identificador estable y se
abre con `/productos/detalle/{id}`; los detalles y productos relacionados usan una
misma plantilla Thymeleaf. Las descripciones y fotografías que aún no están
documentadas se muestran como pendientes. El carrito continúa compartido mediante
`localStorage` y conserva el contrato `{ id, name, price, quantity }`.

El selector de cantidad y la página de detalle se compilan junto con los módulos
existentes mediante `npm run build`.
