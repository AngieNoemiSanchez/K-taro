CREATE KtaroDB

INSERT INTO horario_admin (dias, hora_inicio, hora_fin) VALUES
('LUN-VIE', '08:00', '16:00'),   -- turno mañana
('LUN-VIE', '14:00', '22:00'),   -- turno tarde
('LUN-SAB', '09:00', '15:00'),   -- medio turno con sábado
('SAB-DOM', '10:00', '18:00'),   -- fin de semana
('MAR-SAB', '12:00', '20:00');   -- turno intermedio


INSERT INTO administrador (nombre, apellido, correo, password, fecha_nacimiento, telefono, dni, id_horario) VALUES
('Lucía', 'Fernández Quispe', 'lucia.fernandez@ktaro.com',
 '$2a$10$oOf8gxmBLZ0rnml2GwfC4.ergV7f.6ZJ.aFIvyHnu911BN77hS07i',
 '1992-03-14', '987654321', '45218736',
 (SELECT id_horario FROM horario_admin WHERE dias='LUN-VIE' AND hora_inicio='08:00')),

('Carlos', 'Mendoza Ríos', 'carlos.mendoza@ktaro.com',
 '$2a$10$aJE96BVzTXirNlFYgxFPyumFarUfTbifAVn2UDpY7LRq/p5SB3EQW',
 '1988-11-02', '956781234', '40873519',
 (SELECT id_horario FROM horario_admin WHERE dias='LUN-VIE' AND hora_inicio='14:00')),

('Valeria', 'Huamán Torres', 'valeria.huaman@ktaro.com',
 '$2a$10$KP1410CuoqyEj91RzgvyZezj7CMb8kUpwu8wJOKNPUVcEhR/.JYn6',
 '1995-07-25', '912345678', '72904518',
 (SELECT id_horario FROM horario_admin WHERE dias='LUN-SAB' AND hora_inicio='09:00')),

