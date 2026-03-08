USE defaultdb;

create table if not exists categoria (
  id_categoria INT NOT NULL AUTO_INCREMENT,
  descripcion VARCHAR(50) NOT NULL,
  ruta_imagen varchar(1024),
  activo boolean,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_categoria),
  unique (descripcion),
  index ndx_descripcion (descripcion))
  ENGINE = InnoDB;

-- Tabla de productos
create table if not exists producto (
  id_producto INT NOT NULL AUTO_INCREMENT,
  id_categoria INT NOT NULL,
  descripcion VARCHAR(50) NOT NULL,  
  detalle text, 
  precio decimal(12,2) CHECK (precio >= 0),
  existencias int unsigned CHECK (existencias >= 0),
  ruta_imagen varchar(1024),
  activo boolean,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_producto),
  unique (descripcion),
  index ndx_descripcion (descripcion),
  foreign key fk_producto_categoria (id_categoria) references categoria(id_categoria))
  ENGINE = InnoDB;

-- Tabla de usuarios
CREATE TABLE if not exists usuario (
  id_usuario INT NOT NULL AUTO_INCREMENT,
  username varchar(30) NOT NULL UNIQUE,
  password varchar(512) NOT NULL,
  nombre VARCHAR(20) NOT NULL,
  apellidos VARCHAR(30) NOT NULL,
  correo VARCHAR(75) NULL UNIQUE,
  telefono VARCHAR(25) NULL,
  ruta_imagen varchar(1024),
  activo boolean,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_usuario`),
  CHECK (correo REGEXP '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$'),
  index ndx_username (username))
  ENGINE = InnoDB;

-- Tabla de facturas
create table if not exists factura (
  id_factura INT NOT NULL AUTO_INCREMENT,
  id_usuario INT NOT NULL,
  fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  
  total decimal(12,2) check (total>0),
  estado ENUM('Activa', 'Pagada', 'Anulada') NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_factura),  
  index ndx_id_usuario (id_usuario),
  foreign key fk_factura_usuario (id_usuario) references usuario(id_usuario))
  ENGINE = InnoDB;

-- Tabla de ventas
create table if not exists venta (
  id_venta INT NOT NULL AUTO_INCREMENT,
  id_factura INT NOT NULL,
  id_producto INT NOT NULL,
  precio_historico decimal(12,2) check (precio_historico>= 0), 
  cantidad int unsigned check (cantidad> 0),
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_venta),
  index ndx_factura (id_factura),
  index ndx_producto (id_producto),
  UNIQUE (id_factura, id_producto),
  foreign key fk_venta_factura (id_factura) references factura(id_factura),
  foreign key fk_venta_producto (id_producto) references producto(id_producto))
  ENGINE = InnoDB;

-- Tabla de roles
create table if not exists rol (
  id_rol INT NOT NULL AUTO_INCREMENT,
  rol varchar(20) unique,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  primary key (id_rol))
  ENGINE = InnoDB;

-- Tabla de relación entre usuarios y roles
create table if not exists usuario_rol (
  id_usuario int not null,
  id_rol INT NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id_usuario,id_rol),
  foreign key fk_usuarioRol_usuario (id_usuario) references usuario(id_usuario),
  foreign key fk_usuarioRol_rol (id_rol) references rol(id_rol))
  ENGINE = InnoDB;

-- Tabla de rutas
CREATE TABLE if not exists ruta (
    id_ruta INT AUTO_INCREMENT NOT NULL,
    ruta VARCHAR(255) NOT NULL,
    id_rol INT NULL,
    requiere_rol boolean NOT NULL DEFAULT TRUE,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    check (id_rol IS NOT NULL OR requiere_rol = FALSE),
    PRIMARY KEY (id_ruta),
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol))
    ENGINE = InnoDB;

-- Tabla de constantes de la aplicación
CREATE TABLE if not exists constante (
    id_constante INT AUTO_INCREMENT NOT NULL,
    atributo VARCHAR(25) NOT NULL,
    valor VARCHAR(150) NOT NULL,
  fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  fecha_modificacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id_constante),
    UNIQUE (atributo))
    ENGINE = InnoDB;

-- Vaciar tablas si es necesario 
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE venta;
TRUNCATE TABLE factura;
TRUNCATE TABLE usuario_rol;
TRUNCATE TABLE ruta;
TRUNCATE TABLE constante;
TRUNCATE TABLE producto;
TRUNCATE TABLE categoria;
TRUNCATE TABLE usuario;
TRUNCATE TABLE rol;
SET FOREIGN_KEY_CHECKS = 1;

-- Insertar categorías
INSERT INTO categoria (descripcion, ruta_imagen, activo) VALUES 
('Monitores', 'https://image.benq.com/is/image/benqco/monitor-all-series-kv-m?$ResponsivePreset$&fmt=png-alpha', true), 
('Teclados',  'https://cnnespanol.cnn.com/wp-content/uploads/2022/04/teclado-mecanico.jpg', true),
('Tarjeta Madre','https://static-geektopia.com/storage/thumbs/784x311/788/7884251b/98c0f4a5.webp',true),
('Celulares','https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSyVjIkJkEs1CqujaBs6G0PUxGJJ4vlCTFxsQ&s', false);

-- Insertar productos
INSERT INTO producto (id_categoria, descripcion, detalle, precio, existencias, ruta_imagen, activo) VALUES
(1, 'Monitor AOC 19', 'Monitor de 19 pulgadas AOC', 23000, 5, 'https://c.pxhere.com/images/ec/fd/d67b367ed6467eb826842ac81d3b-1453591.jpg!d', true),
(1, 'Monitor MAC', 'Monitor Apple 27"', 27000, 2, 'https://c.pxhere.com/photos/17/77/Art_Calendar_Cc0_Creative_Design_High_Resolution_Mac_Stock-1622403.jpg!d', true),
(1, 'Monitor Flex 21', 'Monitor flexible 21"', 24000, 5, 'https://www.trustedreviews.com/wp-content/uploads/sites/54/2022/09/LG-OLED-Flex-7-scaled.jpg', true),
(1, 'Monitor Flex 36', 'Monitor flexible 36"', 27600, 2, 'https://www.lg.com/us/images/tvs/md08003300/gallery/D-01.jpg', true),
(2, 'Teclado español everex', 'Teclado mecánico español', 45000, 5, 'https://http2.mlstatic.com/D_NQ_NP_984317-MLA43206062255_082020-O.webp', true),
(2, 'Teclado fisico gamer', 'Teclado gamer RGB', 57000, 2, 'https://cyberteamcr.com/wp-content/uploads/2024/02/16064_11399.webp', true),
(2, 'Teclado usb compacto', 'Teclado compacto USB', 25000, 5, 'https://live.staticflickr.com/7010/26783973491_3e2043edda_b.jpg', true),
(2, 'Teclado Monitor Flex', 'Teclado flexible', 27600, 2, 'https://hardzone.es/app/uploads-hardzone.es/2020/10/Mejores-KVM.jpg', true),
(3, 'CPU Intel 7i', 'Procesador Intel Core i7', 15780, 5, 'https://live.staticflickr.com/7391/9662276651_f4aa27d5ca_b.jpg', true),
(3, 'CPU Intel Core 5i', 'Procesador Intel Core i5', 15000, 2, 'https://live.staticflickr.com/1473/24714440462_31a0fcdfba_b.jpg', true),
(3, 'AMD 7500', 'Procesador AMD Ryzen 5', 25400, 5, 'https://upload.wikimedia.org/wikipedia/commons/0/0c/AMD_Ryzen_9_3900X_-_ISO.jpg', true),
(3, 'AMD 670', 'Procesador AMD Ryzen 3', 45000, 3, 'https://upload.wikimedia.org/wikipedia/commons/a/a0/AMD_Duron_850_MHz_D850AUT1B.jpg', true),
(4, 'Samsung S22', 'Teléfono Samsung Galaxy S22', 285000, 0, 'https://www.trustedreviews.com/wp-content/uploads/sites/54/2022/08/S22-app-drawer-scaled.jpg', true),
(4, 'Motorola X23', 'Teléfono Motorola', 154000, 0, 'https://www.trustedreviews.com/wp-content/uploads/sites/54/2021/10/motorola-2.jpg', true),
(4, 'Nokia 5430', 'Teléfono Nokia', 330000, 0, 'https://www.trustedreviews.com/wp-content/uploads/sites/54/2021/08/nokia-xr20-1.jpg', true),
(4, 'Xiami x45', 'Teléfono Xiaomi', 273000, 0, 'https://www.trustedreviews.com/wp-content/uploads/sites/54/2022/03/20220315_104812-1-scaled.jpg', true);

-- Insertar usuarios
INSERT INTO usuario (username, password, nombre, apellidos, correo, telefono, ruta_imagen, activo) VALUES 
('juan', '$2a$10$P1.w58XvnaYQUQgZUCk4aO/RTRl8EValluCqB3S2VMLTbRt.tlre.', 'Juan', 'Castro Mora', 'jcastro@gmail.com', '4556-8978', 'https://img2.rtve.es/i/?w=1600&i=1677587980597.jpg', true),
('rebeca', '$2a$10$GkEj.ZzmQa/aEfDmtLIh3udIH5fMphx/35d0EYeqZL5uzgCJ0lQRi', 'Rebeca', 'Contreras Mora', 'acontreras@gmail.com', '5456-8789', 'https://media.licdn.com/dms/image/v2/C5603AQGwjJ5ht4bWXQ/profile-displayphoto-shrink_200_200/profile-displayphoto-shrink_200_200/0/1661476259292?e=2147483647&v=beta&t=9_i5zTdqHRMSXlb9H4TuWkWeRGQXmaZLjxkBlWsg2lg', true),
('pedro', '$2a$10$koGR7eS22Pv5KdaVJKDcge04ZB53iMiw76.UjHPY.XyVYlYqXnPbO', 'Pedro', 'Mena Loria', 'lmena@gmail.com', '7898-8936', 'https://upload.wikimedia.org/wikipedia/commons/thumb/f/fd/Eduardo_de_Pedro_2019.jpg/480px-Eduardo_de_Pedro_2019.jpg?20200109230854', true);

-- Insertar roles
INSERT INTO rol (rol) VALUES ('ADMIN'), ('VENDEDOR'), ('USER');

-- Asignar roles
INSERT INTO usuario_rol (id_usuario, id_rol) VALUES
(1, 1), (1, 2), (1, 3),
(2, 2), (2, 3),
(3, 3);

-- Insertar rutas (opcional, para seguridad)
INSERT INTO ruta (ruta, id_rol) VALUES 
('/producto/nuevo', 1),
('/producto/guardar', 1),
('/producto/modificar/**', 1),
('/producto/eliminar/**', 1),
('/categoria/nuevo', 1),
('/categoria/guardar', 1),
('/categoria/modificar/**', 1),
('/categoria/eliminar/**', 1),
('/usuario/**', 1),
('/producto/listado', 2),
('/categoria/listado', 2);

-- Insertar constantes
INSERT INTO constante (atributo, valor) VALUES 
('dominio', 'localhost'),
('dolar', '520.75');

-- Verificación final
SELECT 'DATOS CARGADOS CORRECTAMENTE EN AIVEN' AS 'MENSAJE';
SELECT COUNT(*) as 'Total Categorías' FROM categoria;
SELECT COUNT(*) as 'Total Productos' FROM producto;
SELECT COUNT(*) as 'Total Usuarios' FROM usuario;