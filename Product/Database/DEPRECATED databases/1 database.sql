-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: 26. Feb, 2023 04:37 AM
-- Tjener-versjon: 10.4.27-MariaDB
-- PHP Version: 8.2.0

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `database`
--

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `admin`
--

CREATE TABLE `admin` (
  `admin_id` int(11) NOT NULL,
  `email` varchar(64) NOT NULL,
  `password_hash` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

--
-- Dataark for tabell `admin`
--

INSERT INTO `admin` (`admin_id`, `email`, `password_hash`) VALUES
(1, 'test@testmail.test', 123);

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `client`
--

CREATE TABLE `client` (
  `client_id` int(11) NOT NULL,
  `client_name` varchar(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `container`
--

CREATE TABLE `container` (
  `serial_number` varchar(32) NOT NULL,
  `model` varchar(32) NOT NULL,
  `country_iso3` varchar(3) NOT NULL,
  `last_filled` date DEFAULT NULL,
  `status` varchar(32) NOT NULL,
  `at_client` int(11) DEFAULT NULL,
  `address` varchar(64) DEFAULT NULL,
  `at_inventory` int(11) DEFAULT NULL,
  `invoice` date DEFAULT NULL,
  `id` varchar(8) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `container_model`
--

CREATE TABLE `container_model` (
  `model_name` varchar(32) NOT NULL,
  `refill_interval` float DEFAULT NULL,
  `liter_capacity` float DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `container_status`
--

CREATE TABLE `container_status` (
  `status_name` varchar(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `employee`
--

CREATE TABLE `employee` (
  `employee_id` int(11) NOT NULL,
  `employee_name` varchar(64) NOT NULL,
  `login_code` int(11) NOT NULL,
  `location_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `location`
--

CREATE TABLE `location` (
  `location_id` int(11) NOT NULL,
  `name` varchar(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `transaction`
--

CREATE TABLE `transaction` (
  `transaction_id` int(11) NOT NULL,
  `responsible_id` int(11) NOT NULL,
  `client_id` int(11) DEFAULT NULL,
  `address` varchar(64) DEFAULT NULL,
  `inventory` int(11) DEFAULT NULL,
  `container` varchar(32) DEFAULT NULL,
  `comment` varchar(512) DEFAULT NULL,
  `date` date NOT NULL,
  `action` varchar(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

-- --------------------------------------------------------

--
-- Tabellstruktur for tabell `transaction_action`
--

CREATE TABLE `transaction_action` (
  `action_name` varchar(32) NOT NULL,
  `description` varchar(64) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`admin_id`);

--
-- Indexes for table `client`
--
ALTER TABLE `client`
  ADD PRIMARY KEY (`client_id`);

--
-- Indexes for table `container`
--
ALTER TABLE `container`
  ADD PRIMARY KEY (`serial_number`),
  ADD KEY `container_fk1` (`model`),
  ADD KEY `container_fk2` (`status`),
  ADD KEY `container_fk3` (`at_client`),
  ADD KEY `container_fk4` (`at_inventory`);

--
-- Indexes for table `container_model`
--
ALTER TABLE `container_model`
  ADD PRIMARY KEY (`model_name`);

--
-- Indexes for table `container_status`
--
ALTER TABLE `container_status`
  ADD PRIMARY KEY (`status_name`);

--
-- Indexes for table `employee`
--
ALTER TABLE `employee`
  ADD PRIMARY KEY (`employee_id`),
  ADD KEY `employee_fk1` (`location_id`);

--
-- Indexes for table `location`
--
ALTER TABLE `location`
  ADD PRIMARY KEY (`location_id`);

--
-- Indexes for table `transaction`
--
ALTER TABLE `transaction`
  ADD PRIMARY KEY (`transaction_id`),
  ADD KEY `transaction_fk1` (`action`),
  ADD KEY `transaction_fk2` (`responsible_id`),
  ADD KEY `transaction_fk3` (`client_id`),
  ADD KEY `transaction_fk4` (`container`),
  ADD KEY `transaction_fk5` (`inventory`);

--
-- Indexes for table `transaction_action`
--
ALTER TABLE `transaction_action`
  ADD PRIMARY KEY (`action_name`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `admin`
--
ALTER TABLE `admin`
  MODIFY `admin_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `client`
--
ALTER TABLE `client`
  MODIFY `client_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `employee`
--
ALTER TABLE `employee`
  MODIFY `employee_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `location`
--
ALTER TABLE `location`
  MODIFY `location_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `transaction`
--
ALTER TABLE `transaction`
  MODIFY `transaction_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Begrensninger for dumpede tabeller
--

--
-- Begrensninger for tabell `container`
--
ALTER TABLE `container`
  ADD CONSTRAINT `container_fk1` FOREIGN KEY (`model`) REFERENCES `container_model` (`model_name`) ON UPDATE CASCADE,
  ADD CONSTRAINT `container_fk2` FOREIGN KEY (`status`) REFERENCES `container_status` (`status_name`) ON UPDATE CASCADE,
  ADD CONSTRAINT `container_fk3` FOREIGN KEY (`at_client`) REFERENCES `client` (`client_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `container_fk4` FOREIGN KEY (`at_inventory`) REFERENCES `location` (`location_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Begrensninger for tabell `employee`
--
ALTER TABLE `employee`
  ADD CONSTRAINT `employee_fk1` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`) ON UPDATE CASCADE;

--
-- Begrensninger for tabell `transaction`
--
ALTER TABLE `transaction`
  ADD CONSTRAINT `transaction_fk1` FOREIGN KEY (`action`) REFERENCES `transaction_action` (`action_name`) ON UPDATE CASCADE,
  ADD CONSTRAINT `transaction_fk2` FOREIGN KEY (`responsible_id`) REFERENCES `employee` (`employee_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `transaction_fk3` FOREIGN KEY (`client_id`) REFERENCES `client` (`client_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `transaction_fk4` FOREIGN KEY (`container`) REFERENCES `container` (`serial_number`),
  ADD CONSTRAINT `transaction_fk5` FOREIGN KEY (`inventory`) REFERENCES `location` (`location_id`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
