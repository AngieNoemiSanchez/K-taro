-- CREATE KtaroDB

INSERT INTO horario_admin (dias, hora_inicio, hora_fin) VALUES
('LUN-VIE', '08:00', '16:00'),   -- turno mañana
('LUN-VIE', '14:00', '22:00'),   -- turno tarde
('LUN-SAB', '09:00', '15:00'),   -- medio turno con sábado
('SAB-DOM', '10:00', '18:00'),   -- fin de semana
('MAR-SAB', '12:00', '20:00');   -- turno intermedio


INSERT INTO administrador (nombre, apellido, correo, password, fecha_nacimiento, telefono, dni, id_horario) VALUES
('Lucía', 'Fernández Quispe', 'lucia.fernandez@ktaro.com',
 '$2a$10$oOf8gxmBLZ0rnml2GwfC4.ergV7f.6ZJ.aFIvyHnu911BN77hS07i', -- Lucia#2026
 '1992-03-14', '987654321', '45218736',
 (SELECT id_horario FROM horario_admin WHERE dias='LUN-VIE' AND hora_inicio='08:00')),

('Carlos', 'Mendoza Ríos', 'carlos.mendoza@ktaro.com',
 '$2a$10$aJE96BVzTXirNlFYgxFPyumFarUfTbifAVn2UDpY7LRq/p5SB3EQW', -- Carlos#2026
 '1988-11-02', '956781234', '40873519',
 (SELECT id_horario FROM horario_admin WHERE dias='LUN-VIE' AND hora_inicio='14:00')),

('Valeria', 'Huamán Torres', 'valeria.huaman@ktaro.com',
 '$2a$10$KP1410CuoqyEj91RzgvyZezj7CMb8kUpwu8wJOKNPUVcEhR/.JYn6', --Valeria#2026
 '1995-07-25', '912345678', '72904518',
 (SELECT id_horario FROM horario_admin WHERE dias='LUN-SAB' AND hora_inicio='09:00'));

 --PRODUCTOS

INSERT INTO producto (nombre, descripcion, precio, stock, tipo_producto, estado, imagen) VALUES
('Peperos Original','Descripción pendiente',6.50,30,'SNACK','ACTIVO','/images/peperos.png'),
('Honey Butter Chip','Las papitas fritas más famosas de Corea. Combinación adictiva de papas crujientes con mantequilla francesa y miel real.',12.00,30,'SNACK','ACTIVO','/images/honey butter chip.png'),
('Shrimp Crackers','Descripción pendiente',6.50,30,'SNACK','ACTIVO','/images/Shrimp Crackers.png'),
('Onion Rings Hot 40gr.','Descripción pendiente',4.02,30,'SNACK','ACTIVO','/images/onion.png'),
('Potato Snack 55gr.','Descripción pendiente',6.00,30,'SNACK','ACTIVO','/images/Potato Snack 55gr..png'),
('Cuttlefish Snack 45gr.','Descripción pendiente',6.00,30,'SNACK','ACTIVO','/images/Cuttlefish Snack 45gr..png'),
('Shrimp Flavored 75gr.','Descripción pendiente',6.00,30,'SNACK','ACTIVO','/images/flavored.png'),
('Banana 45gr.','Descripción pendiente',6.00,30,'SNACK','ACTIVO','/images/banana.png'),
('Korean Cracker 192gr.','Descripción pendiente',24.00,30,'SNACK','ACTIVO','/images/korean.png'),
('Kukhwapan 150ml','Descripción pendiente',8.71,30,'SNACK','ACTIVO','/images/Rectangle 9.png'),
('Jinro Lemon Soju 13% 360ml','Descripción pendiente',13.00,30,'BEBIDA','ACTIVO','/images/Rectangle 4.png'),
('Bibigo Stir-Fried Ramyun Smoky K-Chicken 122gr','Descripción pendiente',6.00,30,'RAMEN','ACTIVO','/images/bibigo.png'),
('Shin Cup Ramyun Superstar','Descripción pendiente',8.00,30,'RAMEN','ACTIVO','/images/Rectangle 16.png'),
('Shin Ramyun Toomba 137gr','Descripción pendiente',4.30,30,'RAMEN','ACTIVO','/images/Rectangle 7.png'),
('Shin Ramyun Non-Fried 97gr','Descripción pendiente',10.50,30,'RAMEN','ACTIVO','/images/Rectangle 12.png'),
('Shin Ramyun Spicy Chicken 120gr','Descripción pendiente',11.20,30,'RAMEN','ACTIVO','/images/shin.png'),
('Malang Soft Candy Milk 79gr','Descripción pendiente',8.71,30,'OTROS','ACTIVO','/images/Rectangle 15.png');

--PROMOCIONES

INSERT INTO promocion (nombre, descripcion, precio, tiempo, tipo_promocion, estado, imagen) VALUES
('Combo K-Drama Marathon',
 'Disfruta tus series favoritas con nuestro combo especial. Incluye 1 ramen, 1 bebida coreana y 1 snack asiático.',
 35.00, 'siempre', 'COMBO', 'ACTIVO', '/images/promo1.png'),
('Gojar Night',
 '2x1 en Soju (sabores: original, uva, fresa, durazno), 2 snacks (salado, dulce o picante) y 1 bebida para mezclar (Milkis o soda).',
 50.00, 'siempre', 'COMBO', 'ACTIVO', '/images/promo2.png');

INSERT INTO promocion_producto (id_promocion, id_producto)
SELECT pr.id_promocion, p.id_producto
FROM promocion pr
JOIN producto p ON p.nombre IN ('Shin Cup Ramyun Superstar','Jinro Lemon Soju 13% 360ml','Korean Cracker 192gr.')
WHERE pr.nombre = 'Combo K-Drama Marathon';

INSERT INTO promocion_producto (id_promocion, id_producto)
SELECT pr.id_promocion, p.id_producto
FROM promocion pr
JOIN producto p ON p.nombre IN ('Jinro Lemon Soju 13% 360ml','Korean Cracker 192gr.','Malang Soft Candy Milk 79gr','Peperos Original')
WHERE pr.nombre = 'Gojar Night';


-- OFERTAS


-- 1) Precio NORMAL de los 4 productos (el que sale tachado)
UPDATE producto SET precio = 8.60  WHERE nombre = 'Shin Ramyun Toomba 137gr';
UPDATE producto SET precio = 15.00 WHERE nombre = 'Shin Ramyun Non-Fried 97gr';
UPDATE producto SET precio = 16.00 WHERE nombre = 'Shin Ramyun Spicy Chicken 120gr';
UPDATE producto SET precio = 13.00 WHERE nombre = 'Malang Soft Candy Milk 79gr';

-- 2) Las 4 ofertas (precio de oferta = el que sale en negrita)
INSERT INTO promocion (nombre, descripcion, precio, tiempo, estado, imagen, tipo_promocion) VALUES
('Oferta Shin Ramyun Toomba',      'Oferta en Shin Ramyun Toomba 137gr',      4.30,  'siempre', 'ACTIVO', NULL, 'OFERTA'),  -- 50% OFF
('Oferta Shin Ramyun Non-Fried',   'Oferta en Shin Ramyun Non-Fried 97gr',    10.50, 'siempre', 'ACTIVO', NULL, 'OFERTA'),  -- 30% OFF
('Oferta Shin Ramyun Spicy Chicken','Oferta en Shin Ramyun Spicy Chicken 120gr', 11.20, 'siempre', 'ACTIVO', NULL, 'OFERTA'),  -- 30% OFF
('Oferta Malang Soft Candy Milk',  'Oferta en Malang Soft Candy Milk 79gr',   8.71,  'siempre', 'ACTIVO', NULL, 'OFERTA');  -- 33% OFF

-- 3) Vincular cada oferta con su producto
INSERT INTO promocion_producto (id_promocion, id_producto)
SELECT pr.id_promocion, p.id_producto
FROM promocion pr
JOIN producto p ON p.nombre =
     CASE pr.nombre
        WHEN 'Oferta Shin Ramyun Toomba'        THEN 'Shin Ramyun Toomba 137gr'
        WHEN 'Oferta Shin Ramyun Non-Fried'     THEN 'Shin Ramyun Non-Fried 97gr'
        WHEN 'Oferta Shin Ramyun Spicy Chicken' THEN 'Shin Ramyun Spicy Chicken 120gr'
        WHEN 'Oferta Malang Soft Candy Milk'    THEN 'Malang Soft Candy Milk 79gr'
     END
WHERE pr.tipo_promocion = 'OFERTA'
  AND pr.nombre IN ('Oferta Shin Ramyun Toomba','Oferta Shin Ramyun Non-Fried',
                    'Oferta Shin Ramyun Spicy Chicken','Oferta Malang Soft Candy Milk');

COMMIT TRANSACTION;
GO

