CREATE KtaroDB

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


