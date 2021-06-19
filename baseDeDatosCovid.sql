
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;


-- Base de datos: `MapaCovid`


create database mapacovid;
USE mapacovid;

-- Tabla usuario

CREATE TABLE `usuario` (
  `email` varchar(50) NOT NULL,
  `nombre_usuario` varchar(50) NOT NULL,
  `contraseña` varchar(255) NOT NULL,
  `rol` tinyint(1) NOT NULL,
  `activo` tinyint(1) NOT NULL,
  `clave_publica` varchar(500) NOT NULL,
  `clave_privada` varchar(1000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `usuario`
  ADD PRIMARY KEY (`email`);

-- Tabla roles

CREATE TABLE `roles` (
  `id` tinyint(1) NOT NULL,
  `rol` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`);

INSERT INTO `roles` (`id`, `rol`) VALUES
(0, 'Administrador'),
(1, 'Gestor');


-- Tabla region
-- La idea era poner un id autoIncrementable pero por alguna razon no le da la gana de crear tablas con autoIncrementables

CREATE TABLE `region` (
  `nombre_region` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `region`
  ADD PRIMARY KEY (`nombre_region`);
  
INSERT INTO region (nombre_region)
VALUES ('Galicia');

  
-- Tabla datos region

CREATE TABLE `datos_semanales_region` (
  `id_region_num_semana` varchar(70) NOT NULL,
  `id_region` varchar(50) NOT NULL,
  `numero_semana` int(10) NOT NULL,
  `muertos` int(10) NOT NULL,
  `infectados` int(10) NOT NULL,
  `alta` int(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `datos_semanales_region`
  ADD PRIMARY KEY (`id_region_num_semana`);

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
