-- --------------------------------------------------------
-- Host:                         192.168.1.55
-- Server version:               10.3.29-MariaDB-0ubuntu0.20.04.1 - Ubuntu 20.04
-- Server OS:                    debian-linux-gnu
-- HeidiSQL Version:             9.5.0.5196
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for data
CREATE DATABASE IF NOT EXISTS `data` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `data`;

-- Dumping structure for table data.historicos
CREATE TABLE IF NOT EXISTS `historicos` (
  `ID` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL,
  `weekly` decimal(15,2) DEFAULT NULL,
  `monthly` decimal(15,2) NOT NULL DEFAULT 0.00,
  `udate` date DEFAULT NULL,
  `special` char(1) COLLATE utf8mb4_unicode_ci DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Data exporting was unselected.
-- Dumping structure for table data.ofertas
CREATE TABLE IF NOT EXISTS `ofertas` (
  `ID` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL,
  `vendor_id` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `articulo` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `url` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` decimal(15,2) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Data exporting was unselected.
-- Dumping structure for table data.productos
CREATE TABLE IF NOT EXISTS `productos` (
  `ID` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL,
  `vendor_id` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `articulo` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `url` varchar(350) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` decimal(15,2) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Data exporting was unselected.
-- Dumping structure for table data.productos_diario_ml
CREATE TABLE IF NOT EXISTS `productos_diario_ml` (
  `ID` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL,
  `vendor_id` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `articulo` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `url` varchar(350) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `day_price` decimal(15,2) DEFAULT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`ID`,`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table data.productos_diario_sitios
CREATE TABLE IF NOT EXISTS `productos_diario_sitios` (
  `ID` varchar(45) COLLATE utf8mb4_unicode_ci NOT NULL,
  `vendor_id` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `articulo` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `url` varchar(350) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `day_price` decimal(15,2) DEFAULT NULL,
  `date` date NOT NULL,
  PRIMARY KEY (`ID`,`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=COMPACT;

-- Data exporting was unselected.
-- Dumping structure for table data.vendors
CREATE TABLE IF NOT EXISTS `vendors` (
  `vendors_id` int(11) NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `display` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Data exporting was unselected.
-- Dumping structure for trigger data.before_price_insert_ML
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `before_price_insert_ML` BEFORE INSERT ON `productos_diario_ml` FOR EACH ROW BEGIN
set @myPrice = NEW.day_price;
	IF ((select 1 from historicos where id=NEW.ID) <=> 1) THEN
	 IF ((select 1 from historicos where id=NEW.ID and special=0) <=> 1) THEN
		INSERT INTO historicos (ID,weekly,monthly,udate) 
 			select ID,@myPrice,weekly,(CURDATE() - interval 1 day) from historicos
			where id=NEW.ID
		ON DUPLICATE KEY UPDATE ID = VALUES(ID),weekly = VALUES(weekly),monthly = VALUES(monthly),udate = VALUES(udate);
	 ELSE
	   UPDATE historicos set special=0 where id=NEW.ID;	
	 END IF;
	ELSE
		INSERT INTO historicos (ID,weekly,monthly,udate) VALUES (NEW.ID,NEW.day_price,NEW.day_price,(CURDATE() - interval 1 day));
	END IF;	
	END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Dumping structure for trigger data.before_price_insert_Sitios
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `before_price_insert_Sitios` BEFORE INSERT ON `productos_diario_sitios` FOR EACH ROW BEGIN
set @myPrice = NEW.day_price;
	IF ((select 1 from historicos where id=NEW.ID) <=> 1) THEN
	 IF ((select 1 from historicos where id=NEW.ID and special=0) <=> 1) THEN
		INSERT INTO historicos (ID,weekly,monthly,udate) 
 			select ID,@myPrice,weekly,(CURDATE() - interval 1 day) from historicos
			where id=NEW.ID
		ON DUPLICATE KEY UPDATE ID = VALUES(ID),weekly = VALUES(weekly),monthly = VALUES(monthly),udate = VALUES(udate);
	 ELSE
	   UPDATE historicos set special=0 where id=NEW.ID;	
	 END IF;
	ELSE
		INSERT INTO historicos (ID,weekly,monthly,udate) VALUES (NEW.ID,NEW.day_price,NEW.day_price,(CURDATE() - interval 1 day));
	END IF;	
	END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

-- Dumping structure for trigger data.before_price_update
SET @OLDTMP_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_ENGINE_SUBSTITUTION';
DELIMITER //
CREATE TRIGGER `before_price_update` BEFORE UPDATE ON `productos` FOR EACH ROW BEGIN
	IF NEW.price > OLD.price THEN
		SET NEW.price = OLD.price;
	ELSEIF NEW.price = OLD.price THEN
		SET NEW.price = OLD.price;
	ELSEIF NEW.price < (select weekly from historicos where ID=NEW.ID) THEN
		REPLACE INTO ofertas (ID, vendor_id, articulo, url, price) VALUES (NEW.ID,NEW.vendor_id,NEW.articulo,NEW.url,NEW.price);
    END IF;
    END//
DELIMITER ;
SET SQL_MODE=@OLDTMP_SQL_MODE;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
