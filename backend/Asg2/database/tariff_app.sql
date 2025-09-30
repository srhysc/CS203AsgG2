-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: tariff_app
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `country_codes`
--

DROP TABLE IF EXISTS `country_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `country_codes` (
  `country_name` varchar(100) NOT NULL,
  `iso3` char(3) NOT NULL,
  `un_numeric` char(3) NOT NULL,
  PRIMARY KEY (`iso3`),
  UNIQUE KEY `ux_un_numeric` (`un_numeric`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `country_codes`
--

LOCK TABLES `country_codes` WRITE;
/*!40000 ALTER TABLE `country_codes` DISABLE KEYS */;
INSERT INTO `country_codes` VALUES ('Aruba','ABW','533'),('Afghanistan','AFG','004'),('Angola','AGO','024'),('Anguila','AIA','660'),('Albania','ALB','008'),('Andorra','AND','020'),('Netherlands Antilles','ANT','530'),('United Arab Emirates','ARE','784'),('Argentina','ARG','032'),('Armenia','ARM','051'),('American Samoa','ASM','016'),('Fr. So. Ant. Tr','ATF','260'),('Antigua and Barbuda','ATG','028'),('Australia','AUS','036'),('Austria','AUT','040'),('Azerbaijan','AZE','031'),('Br. Antr. Terr','BAT','080'),('Burundi','BDI','108'),('Belgium','BEL','056'),('Benin','BEN','204'),('Burkina Faso','BFA','854'),('Bangladesh','BGD','050'),('Bulgaria','BGR','100'),('Bahrain','BHR','048'),('Bahamas, The','BHS','044'),('Bosnia and Herzegovina','BIH','070'),('Belarus','BLR','112'),('Belgium-Luxembourg','BLX','058'),('Belize','BLZ','084'),('Bermuda','BMU','060'),('Bolivia','BOL','068'),('Brazil','BRA','076'),('Barbados','BRB','052'),('Brunei','BRN','096'),('Bhutan','BTN','064'),('Botswana','BWA','072'),('Central African Republic','CAF','140'),('Canada','CAN','124'),('Cocos (Keeling) Islands','CCK','166'),('Switzerland','CHE','756'),('Chile','CHL','152'),('China','CHN','156'),('Cote d\'Ivoire','CIV','384'),('Cameroon','CMR','120'),('Congo, Rep.','COG','178'),('Cook Islands','COK','184'),('Colombia','COL','170'),('Comoros','COM','174'),('Cape Verde','CPV','132'),('Costa Rica','CRI','188'),('Czechoslovakia','CSK','200'),('Cuba','CUB','192'),('Christmas Island','CXR','162'),('Cayman Islands','CYM','136'),('Cyprus','CYP','196'),('Czech Republic','CZE','203'),('German Democratic Republic','DDR','278'),('Germany','DEU','276'),('Djibouti','DJI','262'),('Dominica','DMA','212'),('Denmark','DNK','208'),('Dominican Republic','DOM','214'),('Algeria','DZA','012'),('Ecuador','ECU','218'),('Egypt, Arab Rep.','EGY','818'),('Eritrea','ERI','232'),('Western Sahara','ESH','732'),('Spain','ESP','724'),('Estonia','EST','233'),('Ethiopia (includes Eritrea)','ETF','230'),('Ethiopia (excludes Eritrea)','ETH','231'),('European Union','EUN','918'),('Finland','FIN','246'),('Fiji','FJI','242'),('Falkland Island','FLK','238'),('France','FRA','250'),('Free Zones','FRE','838'),('Faeroe Islands','FRO','234'),('Micronesia, Fed. Sts.','FSM','583'),('Gabon','GAB','266'),('Gaza Strip','GAZ','274'),('United Kingdom','GBR','826'),('Georgia','GEO','268'),('Ghana','GHA','288'),('Gibraltar','GIB','292'),('Guinea','GIN','324'),('Guadeloupe','GLP','312'),('Gambia, The','GMB','270'),('Guinea-Bissau','GNB','624'),('Equatorial Guinea','GNQ','226'),('Greece','GRC','300'),('Grenada','GRD','308'),('Greenland','GRL','304'),('Guatemala','GTM','320'),('French Guiana','GUF','254'),('Guam','GUM','316'),('Guyana','GUY','328'),('Hong Kong, China','HKG','344'),('Honduras','HND','340'),('Croatia','HRV','191'),('Haiti','HTI','332'),('Hungary','HUN','348'),('Indonesia','IDN','360'),('India','IND','356'),('British Indian Ocean Ter.','IOT','086'),('Ireland','IRL','372'),('Iran, Islamic Rep.','IRN','364'),('Iraq','IRQ','368'),('Iceland','ISL','352'),('Israel','ISR','376'),('Italy','ITA','380'),('Jamaica','JAM','388'),('Jordan','JOR','400'),('Japan','JPN','392'),('Jhonston Island','JTN','396'),('Kazakhstan','KAZ','398'),('Kenya','KEN','404'),('Kyrgyz Republic','KGZ','417'),('Cambodia','KHM','116'),('Kiribati','KIR','296'),('Saint Kitts-Nevis-Anguilla-Aru','KN1','658'),('St. Kitts and Nevis','KNA','659'),('Korea, Rep.','KOR','410'),('Kuwait','KWT','414'),('Lao PDR','LAO','418'),('Lebanon','LBN','422'),('Liberia','LBR','430'),('Libya','LBY','434'),('St. Lucia','LCA','662'),('Liechtenstein','LIE','438'),('Sri Lanka','LKA','144'),('Lesotho','LSO','426'),('Lithuania','LTU','440'),('Luxembourg','LUX','442'),('Latvia','LVA','428'),('Macao','MAC','446'),('Morocco','MAR','504'),('Monaco','MCO','492'),('Moldova','MDA','498'),('Madagascar','MDG','450'),('Maldives','MDV','462'),('Mexico','MEX','484'),('Marshall Islands','MHL','584'),('Midway Islands','MID','488'),('Macedonia, FYR','MKD','807'),('Mali','MLI','466'),('Malta','MLT','470'),('Myanmar','MMR','104'),('Mongolia','MNG','496'),('Northern Mariana Islands','MNP','580'),('Mozambique','MOZ','508'),('Mauritania','MRT','478'),('Montserrat','MSR','500'),('Martinique','MTQ','474'),('Mauritius','MUS','480'),('Malawi','MWI','454'),('Malaysia','MYS','458'),('Namibia','NAM','516'),('New Caledonia','NCL','540'),('Niger','NER','562'),('Norfolk Island','NFK','574'),('Nigeria','NGA','566'),('Nicaragua','NIC','558'),('Niue','NIU','570'),('Netherlands','NLD','528'),('Norway','NOR','578'),('Nepal','NPL','524'),('Nauru','NRU','520'),('Neutral Zone','NZE','536'),('New Zealand','NZL','554'),('Oman','OMN','512'),('Pakistan','PAK','586'),('Panama','PAN','591'),('Pacific Islands','PCE','582'),('Pitcairn','PCN','612'),('Fm Panama Cz','PCZ','592'),('Peru','PER','604'),('Philippines','PHL','608'),('Palau','PLW','585'),('Pen Malaysia','PMY','459'),('Papua New Guinea','PNG','598'),('Poland','POL','616'),('Puerto Rico','PRI','630'),('Korea, Dem. Rep.','PRK','408'),('Portugal','PRT','620'),('Paraguay','PRY','600'),('French Polynesia','PYF','258'),('Qatar','QAT','634'),('Reunion','REU','638'),('Romania','ROM','642'),('Russian Federation','RUS','643'),('Rwanda','RWA','646'),('Ryukyu Is','RYU','647'),('Saudi Arabia','SAU','682'),('Sabah','SBH','461'),('Sudan','SDN','736'),('Senegal','SEN','686'),('Yugoslavia','SER','891'),('Singapore','SGP','702'),('Saint Helena','SHN','654'),('SIKKIM','SIK','698'),('Svalbard and Jan Mayen Is','SJM','744'),('Solomon Islands','SLB','090'),('Sierra Leone','SLE','694'),('El Salvador','SLV','222'),('San Marino','SMR','674'),('Somalia','SOM','706'),('Special Categories','SPE','839'),('Saint Pierre and Miquelon','SPM','666'),('Sao Tome and Principe','STP','678'),('Suriname','SUR','740'),('Slovak Republic','SVK','703'),('Slovenia','SVN','705'),('Fm Vietnam Rp','SVR','866'),('Soviet Union','SVU','810'),('Sweden','SWE','752'),('Sarawak','SWK','457'),('Swaziland','SWZ','748'),('Seychelles','SYC','690'),('Syrian Arab Republic','SYR','760'),('Fm Tanganyik','TAN','835'),('Turks and Caicos Isl.','TCA','796'),('Chad','TCD','148'),('Togo','TGO','768'),('Thailand','THA','764'),('Tajikistan','TJK','762'),('Tokelau','TKL','772'),('Turkmenistan','TKM','795'),('East Timor','TMP','626'),('Tonga','TON','776'),('Trinidad and Tobago','TTO','780'),('Tunisia','TUN','788'),('Turkey','TUR','792'),('Tuvalu','TUV','798'),('Taiwan','TWN','158'),('Tanzania','TZA','834'),('Uganda','UGA','800'),('Ukraine','UKR','804'),('Unspecified','UNS','898'),('Uruguay','URY','858'),('United States','USA','840'),('Us Msc.Pac.I','USP','849'),('Uzbekistan','UZB','860'),('Holy See','VAT','336'),('St. Vincent and the Grenadines','VCT','670'),('Fm Vietnam Dr','VDR','868'),('Venezuela','VEN','862'),('British Virgin Islands','VGB','092'),('Virgin Islands (U.S.)','VIR','850'),('Vietnam','VNM','704'),('Vanuatu','VUT','548'),('Wake Island','WAK','872'),('World','WLD','000'),('Wallis and Futura Isl.','WLF','876'),('Samoa','WSM','882'),('Yemen Democratic','YDR','720'),('Yemen, Rep.','YEM','887'),('Yugoslavia, FR (Serbia/Montene','YUG','890'),('South Africa','ZAF','710'),('Congo, Dem. Rep.','ZAR','180'),('Zambia','ZMB','894'),('Fm Zanz-Pemb','ZPM','836'),('Fm Rhod Nyas','ZW1','717'),('Zimbabwe','ZWE','716');
/*!40000 ALTER TABLE `country_codes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `country_view`
--

DROP TABLE IF EXISTS `country_view`;
/*!50001 DROP VIEW IF EXISTS `country_view`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `country_view` AS SELECT 
 1 AS `iso3n`,
 1 AS `name`,
 1 AS `vatRate`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','USER') NOT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vat_rates`
--

DROP TABLE IF EXISTS `vat_rates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vat_rates` (
  `iso3` char(3) NOT NULL,
  `vatRate` decimal(5,2) DEFAULT NULL,
  `last_reviewed` date DEFAULT NULL,
  PRIMARY KEY (`iso3`),
  CONSTRAINT `fk_vat_iso3` FOREIGN KEY (`iso3`) REFERENCES `country_codes` (`iso3`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vat_rates`
--

LOCK TABLES `vat_rates` WRITE;
/*!40000 ALTER TABLE `vat_rates` DISABLE KEYS */;
INSERT INTO `vat_rates` VALUES ('AGO',14.00,'2025-07-01'),('ALB',20.00,'2025-06-30'),('ARE',5.00,'2025-08-26'),('ARG',21.00,'2025-02-05'),('ARM',20.00,'2025-09-12'),('AUS',10.00,'2025-06-27'),('AUT',20.00,'2025-07-15'),('AZE',18.00,'2025-06-30'),('BEL',NULL,'2025-07-17'),('BGD',15.00,'2025-07-14'),('BGR',20.00,'2025-09-09'),('BHR',10.00,'2025-02-14'),('BHS',10.00,'2025-09-16'),('BIH',17.00,'2025-08-19'),('BMU',NULL,'2025-01-31'),('BOL',13.00,'2025-07-17'),('BRA',NULL,'2025-05-02'),('BRB',17.50,'2025-09-22'),('BWA',14.00,'2025-04-17'),('CAN',NULL,'2025-06-13'),('CHE',8.10,'2025-07-15'),('CHL',19.00,'2025-08-13'),('COL',19.00,'2025-07-14'),('CRI',13.00,'2025-06-30'),('CYM',NULL,'2025-07-15'),('CYP',19.00,'2025-07-01'),('CZE',21.00,'2025-07-24'),('DEU',19.00,'2025-06-30'),('DNK',25.00,'2025-09-05'),('DOM',18.00,'2025-05-30'),('DZA',19.00,'2025-07-14'),('ECU',15.00,'2025-03-13'),('ESP',21.00,'2025-06-30'),('EST',22.00,'2025-05-29'),('FIN',25.50,'2025-08-26'),('FRA',20.00,'2025-06-05'),('GAB',18.00,'2024-03-04'),('GBR',20.00,'2025-07-01'),('GEO',18.00,'2025-07-14'),('GHA',NULL,'2025-08-28'),('GIB',NULL,'2025-09-09'),('GNQ',15.00,'2024-05-07'),('GRC',24.00,'2025-07-21'),('GRL',NULL,'2025-08-15'),('GTM',12.00,'2025-07-15'),('GUY',14.00,'2025-07-15'),('HND',15.00,'2025-09-17'),('HRV',25.00,'2025-06-30'),('HUN',27.00,'2025-08-07'),('IDN',12.00,'2025-06-24'),('IND',NULL,'2025-05-06'),('IRL',23.00,'2025-07-11'),('IRQ',NULL,'2025-06-01'),('ISL',24.00,'2025-08-20'),('ISR',18.00,'2025-08-02'),('ITA',22.00,'2025-09-05'),('JAM',15.00,'2025-08-12'),('JOR',16.00,'2025-08-13'),('JPN',10.00,'2025-07-02'),('KAZ',12.00,'2025-05-30'),('KEN',16.00,'2025-08-05'),('KHM',10.00,'2025-09-08'),('KWT',NULL,'2025-06-30'),('LAO',10.00,'2025-08-14'),('LBN',11.00,'2025-06-30'),('LBY',NULL,'2025-06-01'),('LIE',8.10,'2025-06-17'),('LTU',21.00,'2025-03-05'),('LUX',17.00,'2025-07-29'),('LVA',21.00,'2025-07-03'),('MAR',20.00,'2025-03-13'),('MDA',20.00,'2025-07-02'),('MDG',20.00,'2025-03-26'),('MEX',16.00,'2025-07-31'),('MLT',18.00,'2025-08-27'),('MMR',5.00,'2025-06-03'),('MNG',10.00,'2025-07-02'),('MOZ',16.00,'2025-07-17'),('MRT',16.00,'2024-07-18'),('MUS',15.00,'2025-06-30'),('MYS',NULL,'2025-07-07'),('NCL',11.00,'2025-07-25'),('NGA',7.50,'2025-04-14'),('NIC',15.00,'2025-07-16'),('NLD',21.00,'2025-07-07'),('NOR',25.00,'2025-09-09'),('NZL',15.00,'2025-07-09'),('OMN',5.00,'2025-08-31'),('PAK',NULL,'2025-08-04'),('PAN',7.00,'2025-08-19'),('PER',18.00,'2025-07-15'),('PHL',12.00,'2025-09-13'),('PNG',10.00,'2025-06-16'),('POL',23.00,'2025-07-20'),('PRI',NULL,'2025-06-30'),('PRT',23.00,'2025-07-09'),('PRY',NULL,'2025-07-16'),('QAT',NULL,'2025-03-06'),('ROM',19.00,'2025-05-14'),('RWA',18.00,'2025-07-28'),('SAU',NULL,'2025-08-20'),('SEN',18.00,'2024-07-19'),('SGP',9.00,'2025-07-29'),('SLV',13.00,'2025-09-05'),('SVK',23.00,'2025-02-06'),('SVN',22.00,'2025-06-27'),('SWE',25.00,'2025-08-15'),('TCD',18.00,'2024-08-12'),('THA',7.00,'2025-06-30'),('TTO',12.50,'2025-01-31'),('TUN',19.00,'2025-08-15'),('TUR',20.00,'2025-09-11'),('TWN',5.00,'2025-07-03'),('TZA',NULL,'2025-01-20'),('UGA',18.00,'2025-02-10'),('UKR',20.00,'2025-07-18'),('URY',NULL,'2025-08-21'),('USA',NULL,'2025-08-15'),('VEN',16.00,'2025-08-20'),('VNM',10.00,'2025-03-26'),('ZAF',15.00,'2025-05-30'),('ZMB',16.00,'2025-06-02');
/*!40000 ALTER TABLE `vat_rates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vat_rates_stage`
--

DROP TABLE IF EXISTS `vat_rates_stage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vat_rates_stage` (
  `territory` varchar(100) DEFAULT NULL,
  `vat_txt` varchar(32) DEFAULT NULL,
  `last_reviewed_txt` varchar(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vat_rates_stage`
--

LOCK TABLES `vat_rates_stage` WRITE;
/*!40000 ALTER TABLE `vat_rates_stage` DISABLE KEYS */;
INSERT INTO `vat_rates_stage` VALUES ('Albania','20','30-Jun-25'),('Algeria','19','14-Jul-25'),('Angola','14','1-Jul-25'),('Argentina','21','5-Feb-25'),('Armenia','20','12-Sept-25'),('Australia','10','27-Jun-25'),('Austria','20','15-Jul-25'),('Azerbaijan','18','30-Jun-25'),('Bahamas, The','10','16-Sept-25'),('Bahrain','10','14-Feb-25'),('Bangladesh','15','14-Jul-25'),('Barbados','17.5','22-Sept-25'),('Belgium','','17-Jul-25'),('Bermuda','','31-Jan-25'),('Bolivia','13','17-Jul-25'),('Bosnia and Herzegovina','17','19-Aug-25'),('Botswana','14','17-Apr-25'),('Brazil','','2-May-25'),('Brunei Darussalam','','9-Jun-25'),('Bulgaria','20','9-Sept-25'),('Cabo Verde','15','15-Jul-25'),('Cambodia','10','8-Sept-25'),('Cameroon, Republic of','19.25','4-Mar-25'),('Canada','','13-Jun-25'),('Cayman Islands','','15-Jul-25'),('Chad','18','12-Aug-24'),('Chile','19','13-Aug-25'),('China, People\'s Republic of','','2-Jul-25'),('Colombia','19','14-Jul-25'),('Congo, Democratic Republic of the','16','15-Oct-24'),('Congo, Republic of','','22-Jan-25'),('Costa Rica','13','30-Jun-25'),('Croatia','25','30-Jun-25'),('Cyprus','19','1-Jul-25'),('Czech Republic','21','24-Jul-25'),('Denmark','25','5-Sept-25'),('Dominican Republic','18','30-May-25'),('Ecuador','15','13-Mar-25'),('Egypt','14','13-Aug-25'),('El Salvador','13','5-Sept-25'),('Equatorial Guinea','15','7-May-24'),('Estonia','22','29-May-25'),('Eswatini','15','13-Aug-25'),('Ethiopia','15','22-Jul-25'),('Finland','25.5','26-Aug-25'),('France','20','5-Jun-25'),('Gabon','18','4-Mar-24'),('Georgia','18','14-Jul-25'),('Germany','19','30-Jun-25'),('Ghana','','28-Aug-25'),('Gibraltar','','9-Sept-25'),('Greece','24','21-Jul-25'),('Greenland','','15-Aug-25'),('Guatemala','12','15-Jul-25'),('Guernsey, Channel Islands','','27-Jun-25'),('Guyana','14','15-Jul-25'),('Honduras','15','17-Sept-25'),('Hong Kong SAR','','2-Jul-25'),('Hungary','27','7-Aug-25'),('Iceland','24','20-Aug-25'),('India','','6-May-25'),('Indonesia','12','24-Jun-25'),('Iraq','','1-Jun-25'),('Ireland','23','11-Jul-25'),('Isle of Man','20','31-Jul-25'),('Israel','18','2-Aug-25'),('Italy','22','5-Sept-25'),('Ivory Coast (C├â┬┤te d\'Ivoire)','18','20-Nov-24'),('Jamaica','15','12-Aug-25'),('Japan','10','2-Jul-25'),('Jersey, Channel Islands','5','30-Jun-25'),('Jordan','16','13-Aug-25'),('Kazakhstan','12','30-May-25'),('Kenya','16','5-Aug-25'),('Korea, Republic of','10','25-Jul-25'),('Kosovo','18','11-Sept-25'),('Kuwait','','30-Jun-25'),('Lao PDR','10','14-Aug-25'),('Latvia','21','3-Jul-25'),('Lebanon','11','30-Jun-25'),('Liberia, Republic of','12','2-Sept-25'),('Libya','','1-Jun-25'),('Liechtenstein','8.1','17-Jun-25'),('Lithuania','21','5-Mar-25'),('Luxembourg','17','29-Jul-25'),('Macau SAR','','9-Jun-25'),('Madagascar','20','26-Mar-25'),('Malaysia','','7-Jul-25'),('Malta','18','27-Aug-25'),('Mauritania','16','18-Jul-24'),('Mauritius','15','30-Jun-25'),('Mexico','16','31-Jul-25'),('Moldova','20','2-Jul-25'),('Mongolia','10','2-Jul-25'),('Montenegro','21','15-Jul-25'),('Morocco','20','13-Mar-25'),('Mozambique','16','17-Jul-25'),('Myanmar','5','3-Jun-25'),('Namibia, Republic of','15','6-Jul-25'),('Netherlands','21','7-Jul-25'),('New Caledonia','11','25-Jul-25'),('New Zealand','15','9-Jul-25'),('Nicaragua','15','16-Jul-25'),('Nigeria','7.5','14-Apr-25'),('North Macedonia','18','24-Aug-25'),('Norway','25','9-Sept-25'),('Oman','5','31-Aug-25'),('Pakistan','','4-Aug-25'),('Palestinian territories','16','21-Jul-25'),('Panama','7','19-Aug-25'),('Papua New Guinea','10','16-Jun-25'),('Paraguay','','16-Jul-25'),('Peru','18','15-Jul-25'),('Philippines','12','13-Sept-25'),('Poland','23','20-Jul-25'),('Portugal','23','9-Jul-25'),('Puerto Rico','','30-Jun-25'),('Qatar','','6-Mar-25'),('Romania','19','14-May-25'),('Rwanda','18','28-Jul-25'),('Saint Lucia','12.5','17-Feb-25'),('Saudi Arabia','','20-Aug-25'),('Senegal','18','19-Jul-24'),('Serbia','20','15-Jul-25'),('Singapore','9','29-Jul-25'),('Slovak Republic','23','6-Feb-25'),('Slovenia','22','27-Jun-25'),('South Africa','15','30-May-25'),('Spain','21','30-Jun-25'),('Sweden','25','15-Aug-25'),('Switzerland','8.1','15-Jul-25'),('Taiwan','5','3-Jul-25'),('Tanzania','','20-Jan-25'),('Thailand','7','30-Jun-25'),('Timor-Leste','','29-Jul-25'),('Trinidad and Tobago','12.5','31-Jan-25'),('Tunisia','19','15-Aug-25'),('Turkey','20','11-Sept-25'),('Uganda','18','10-Feb-25'),('Ukraine','20','18-Jul-25'),('United Arab Emirates','5','26-Aug-25'),('United Kingdom','20','1-Jul-25'),('United States','','15-Aug-25'),('Uruguay','','21-Aug-25'),('Uzbekistan, Republic of','12','6-Sept-25'),('Venezuela','16','20-Aug-25'),('Vietnam','10','26-Mar-25'),('Zambia','16','2-Jun-25'),('','',''),('','',''),('','','');
/*!40000 ALTER TABLE `vat_rates_stage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `petroleum`
--

DROP TABLE IF EXISTS `petroleum`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;

CREATE TABLE `petroleum` (
  `hscode` varchar(10) NOT NULL,
  `product_name` varchar(255) NOT NULL UNIQUE,
  `price_per_unit` decimal(10,2) DEFAULT NULL,
  `unit` varchar(16) NOT NULL,
  PRIMARY KEY (`hscode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*!40101 SET character_set_client = @saved_cs_client */;


-- 
-- Dumping data for table `petroleum`
--
LOCK TABLES `petroleum` WRITE;
/*!40000 ALTER TABLE `petroleum` DISABLE KEYS */;
INSERT INTO `petroleum` (`hscode`, `product_name`, `price_per_unit`, `unit`) VALUES
('270111', 'Anthracite, not agglomerated', 117.00, 'USD/ton'),
('270112', 'Bituminous coal, not agglomerated', 106.75, 'USD/ton'),
('270119', 'Other coal, not agglomerated, nes', 106.75, 'USD/ton'),
('270120', 'Briquettes, ovoids solid fuels manu', 110.00, 'USD/ton'),
('270210', 'Lignite, not agglomerated', 85.00, 'USD/ton'),
('270220', 'Agglomerated lignite', 90.00, 'USD/ton'),
('270300', 'Peat (incl. peat litter)', 40.00, 'USD/ton'),
('270400', 'Coke and semi-coke of coal, lignite or peat', 130.00, 'USD/ton'),
('270500', 'Coal gas, water gas, producer gas and similar', 48.00, 'USD/ton'),
('270600', 'Tar distilled from coal, lignite or peat', 90.00, 'USD/ton'),
('270710', 'Benzole', 685.00, 'USD/ton'),
('270720', 'Toluole', 735.00, 'USD/ton'),
('270730', 'Xylole', 765.00, 'USD/ton'),
('270740', 'Naphthalene', 700.00, 'USD/ton'),
('270750', 'Aromatic hydrocarbon mixtures >=65% distill', 750.00, 'USD/ton'),
('270760', 'Phenols', 1400.00, 'USD/ton'),
('270791', 'Creosote oils', 1250.00, 'USD/ton'),
('270799', 'Other oils and oil products, nes', 850.00, 'USD/ton'),
('270810', 'Pitch obtained from coal tar or other mine', 400.00, 'USD/ton'),
('270820', 'Pitch coke obtained from coal tar or other', 420.00, 'USD/ton'),
('270900', 'Petroleum oils and oils obtained from bituminous minerals, crude', 68.80, 'USD/barrel'),
('271000', 'Petroleum oils, etc, (excl. crude); preparations', 730.00, 'USD/ton'),
('271111', 'Natural gas, liquefied', 473.00, 'USD/ton'),
('271112', 'Propane, liquefied', 473.00, 'USD/ton'),
('271113', 'Butanes, liquefied', 473.00, 'USD/ton'),
('271114', 'Ethylene, propylene, butylene and butadiene, liq', 473.00, 'USD/ton'),
('271119', 'Other petroleum gases and liquefied hydrocarbons', 473.00, 'USD/ton'),
('271121', 'Natural gas, gaseous', 440.00, 'USD/ton'),
('271129', 'Other petroleum gases and gaseous hydrocarbons', 440.00, 'USD/ton'),
('271210', 'Petroleum jelly', 1400.00, 'USD/ton'),
('271220', 'Paraffin wax, <0.75% oil', 1350.00, 'USD/ton'),
('271290', 'Other paraffin wax... and similar products, nes', 1500.00, 'USD/ton'),
('271311', 'Petroleum coke, not calcined', 130.00, 'USD/ton'),
('271312', 'Calcined petroleum coke', 130.00, 'USD/ton'),
('271320', 'Petroleum bitumen', 430.00, 'USD/ton'),
('271390', 'Other residues of petroleum oils , etc', 120.00, 'USD/ton'),
('271410', 'Bituminous or oil shale and tar sands', 460.00, 'USD/ton'),
('271490', 'Bitumen and asphalt; natural asphaltites and as', 460.00, 'USD/ton'),
('271500', 'Bituminous mixtures base natural asphalt, bitumen', 300.00, 'USD/ton'),
('271600', 'Electrical energy', 150.00, 'USD/MWh');
/*!40000 ALTER TABLE `vat_rates_stage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `country_view`
--

/*!50001 DROP VIEW IF EXISTS `country_view`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
CREATE ALGORITHM=UNDEFINED SQL SECURITY INVOKER VIEW `country_view` AS
SELECT
  CAST(cc.un_numeric AS UNSIGNED) AS iso3n,
  cc.country_name                 AS name,
  CAST(vr.vatRate AS DOUBLE)      AS vatRate
FROM country_codes cc
LEFT JOIN vat_rates vr ON (vr.iso3 = cc.iso3);
-- /*!50001 CREATE ALGORITHM=UNDEFINED */
-- /*!50013 DEFINER=`root`@`localhost` SQL SECURITY INVOKER */
-- /*!50001 VIEW `country_view` AS select `cc`.`un_numeric` AS `iso3n`,`cc`.`country_name` AS `name`,`vr`.`vatRate` AS `vatRate` from (`country_codes` `cc` left join `vat_rates` `vr` on((`vr`.`iso3` = `cc`.`iso3`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-24 23:30:51
