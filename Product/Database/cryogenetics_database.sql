-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 02, 2023 at 04:35 PM
-- Server version: 10.4.22-MariaDB
-- PHP Version: 8.1.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;


CREATE DATABASE cryogenetics_database;

--
-- Database: `cryogenetics_database`
--

-- --------------------------------------------------------

USE cryogenetics_database;

--
-- Table structure for table `admin`
--

CREATE TABLE `admin` (
  `admin_id` int(11) NOT NULL,
  `email` varchar(64) COLLATE utf8mb4_danish_ci NOT NULL,
  `password_hash` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

--
-- Dumping data for table `admin`
--

INSERT INTO `admin` (`admin_id`, `email`, `password_hash`) VALUES
(1, 'test@testmail.test', 123),
(2, 'bingo@bongo.com', 432),
(203, 'jonasTest@outlook.com', 7337),
(204, 'admin123@test.com', 123);

-- --------------------------------------------------------

--
-- Table structure for table `client`
--

CREATE TABLE `client` (
  `client_id` int(11) NOT NULL,
  `client_name` varchar(64) COLLATE utf8mb4_danish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

--
-- Dumping data for table `client`
--

INSERT INTO `client` (`client_id`, `client_name`) VALUES
(1, 'Greg\'s fish'),
(2, 'Lars\' lox'),
(3, 'Hugh\'s haul'),
(4, 'First rate fish'),
(5, 'Canada fish'),
(6, 'Salmon sages');

-- --------------------------------------------------------

--
-- Table structure for table `container`
--

CREATE TABLE `container` (
  `serial_number` varchar(32) COLLATE utf8mb4_danish_ci NOT NULL,
  `model` varchar(32) COLLATE utf8mb4_danish_ci NOT NULL,
  `country_iso3` varchar(3) COLLATE utf8mb4_danish_ci NOT NULL,
  `last_filled` date DEFAULT NULL,
  `status` varchar(32) COLLATE utf8mb4_danish_ci NOT NULL,
  `at_client` int(11) DEFAULT NULL,
  `address` varchar(64) COLLATE utf8mb4_danish_ci DEFAULT NULL,
  `at_inventory` int(11) DEFAULT NULL,
  `invoice` date DEFAULT NULL,
  `id` varchar(8) COLLATE utf8mb4_danish_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

--
-- Dumping data for table `container`
--

INSERT INTO `container` (`serial_number`, `model`, `country_iso3`, `last_filled`, `status`, `at_client`, `address`, `at_inventory`, `invoice`, `id`) VALUES
('111111111', 'verySmall60', 'USA', '2014-03-12', 'At client', 3, '47 Maple Street\r\nManchester, NH 03101', NULL, '2023-03-09', '13'),
('123456789', 'large200', 'NOR', '2023-03-07', 'Available', NULL, NULL, 1, NULL, '12'),
('2222222222', 'small100', 'CHL', '2023-03-23', 'In use', 6, 'Salmon Sages\r\nAvenida Providencia 2309\r\nProvidencia, Santiago, C', NULL, '2023-03-18', '67');

-- --------------------------------------------------------

--
-- Table structure for table `container_model`
--

CREATE TABLE `container_model` (
  `model_name` varchar(32) COLLATE utf8mb4_danish_ci NOT NULL,
  `refill_interval` float DEFAULT NULL,
  `liter_capacity` float DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

--
-- Dumping data for table `container_model`
--

INSERT INTO `container_model` (`model_name`, `refill_interval`, `liter_capacity`) VALUES
('large200', 4, 200),
('medium150', 5, 150),
('small100', 6, 100),
('verySmall60', 7, 60);

-- --------------------------------------------------------

--
-- Table structure for table `container_status`
--

CREATE TABLE `container_status` (
  `status_name` varchar(32) COLLATE utf8mb4_danish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

--
-- Dumping data for table `container_status`
--

INSERT INTO `container_status` (`status_name`) VALUES
('At client'),
('Available'),
('In use'),
('Quarantine'),
('Sent out');

-- --------------------------------------------------------

--
-- Table structure for table `employee`
--

CREATE TABLE `employee` (
  `employee_id` int(11) NOT NULL,
  `employee_name` varchar(64) COLLATE utf8mb4_danish_ci NOT NULL,
  `login_code` int(11) NOT NULL,
  `location_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

--
-- Dumping data for table `employee`
--

INSERT INTO `employee` (`employee_id`, `employee_name`, `login_code`, `location_id`) VALUES
(1, 'Lars L. Ruud', 101, 1),
(2, 'Jonas Ødegaar', 102, 2),
(3, 'Per Pilk', 111, 1),
(4, 'Jonathan Brando', 810, 2),
(101, 'Jesse Ehrmantraut', 444, 101),
(102, 'Walter Fring', 555, 101),
(103, 'Huell McGill', 666, 101),
(201, 'Everly B. Jackson', 587, 201),
(301, 'Jhonny Chill', 917, 301);

-- --------------------------------------------------------

--
-- Table structure for table `location`
--

CREATE TABLE `location` (
  `location_id` int(11) NOT NULL,
  `name` varchar(64) COLLATE utf8mb4_danish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

--
-- Dumping data for table `location`
--

INSERT INTO `location` (`location_id`, `name`) VALUES
(1, 'Norway: Hamar'),
(2, 'Norway: Trondheim'),
(101, 'USA: New Hampshire'),
(201, 'Canada: Black Creek'),
(301, 'Chile: Puerto Montt');

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

CREATE TABLE `transaction` (
  `transaction_id` int(11) NOT NULL,
  `responsible_id` int(11) NOT NULL,
  `client_id` int(11) DEFAULT NULL,
  `address` varchar(64) COLLATE utf8mb4_danish_ci DEFAULT NULL,
  `inventory` int(11) DEFAULT NULL,
  `container` varchar(32) COLLATE utf8mb4_danish_ci DEFAULT NULL,
  `comment` varchar(512) COLLATE utf8mb4_danish_ci DEFAULT NULL,
  `date` date NOT NULL,
  `action` varchar(32) COLLATE utf8mb4_danish_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

--
-- Dumping data for table `transaction`
--

INSERT INTO `transaction` (`transaction_id`, `responsible_id`, `client_id`, `address`, `inventory`, `container`, `comment`, `date`, `action`) VALUES
(101, 103, 3, '123 Warehouse Lane\r\nMerrimack, NH 03054', 101, '123456789', 'Sent container to customer', '2023-03-08', 'Sent out'),
(102, 1, NULL, '456 Lagerveien\r\nHamar, 2316', 1, '111111111', 'Patched hole underneath container, used duct tape so might not last. ', '2023-03-16', 'Maint. completed'),
(103, 101, 5, '1010 Main Street West\r\nNorth Bay, ON P1B 2W1', 201, '2222222222', 'Recieved container from customer', '2023-03-08', 'Returned');

-- --------------------------------------------------------

--
-- Table structure for table `transaction_action`
--

CREATE TABLE `transaction_action` (
  `action_name` varchar(32) COLLATE utf8mb4_danish_ci NOT NULL,
  `description` varchar(64) COLLATE utf8mb4_danish_ci DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_danish_ci;

--
-- Dumping data for table `transaction_action`
--

INSERT INTO `transaction_action` (`action_name`, `description`, `is_active`) VALUES
('Discarded', 'The container has been discarded due to age or damage.', 1),
('Internal', 'The container has been transferred to another warehouse.', 1),
('Maint. completed', 'The container has completed maintenance.', 1),
('Maint. needed', 'The container has damages and requires maintenance.', 1),
('Returned', 'The container has returned from the customer', 1),
('Sent out', 'The container has been sent out to the customer.', 1),
('Sold', 'The container has been sold to an external client.', 1),
('Weekly filling', 'The container has been filled.', 1);

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
  MODIFY `admin_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=205;

--
-- AUTO_INCREMENT for table `client`
--
ALTER TABLE `client`
  MODIFY `client_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `employee`
--
ALTER TABLE `employee`
  MODIFY `employee_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=302;

--
-- AUTO_INCREMENT for table `location`
--
ALTER TABLE `location`
  MODIFY `location_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=302;

--
-- AUTO_INCREMENT for table `transaction`
--
ALTER TABLE `transaction`
  MODIFY `transaction_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=104;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `container`
--
ALTER TABLE `container`
  ADD CONSTRAINT `container_fk1` FOREIGN KEY (`model`) REFERENCES `container_model` (`model_name`) ON UPDATE CASCADE,
  ADD CONSTRAINT `container_fk2` FOREIGN KEY (`status`) REFERENCES `container_status` (`status_name`) ON UPDATE CASCADE,
  ADD CONSTRAINT `container_fk3` FOREIGN KEY (`at_client`) REFERENCES `client` (`client_id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `container_fk4` FOREIGN KEY (`at_inventory`) REFERENCES `location` (`location_id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Constraints for table `employee`
--
ALTER TABLE `employee`
  ADD CONSTRAINT `employee_fk1` FOREIGN KEY (`location_id`) REFERENCES `location` (`location_id`) ON UPDATE CASCADE;

--
-- Constraints for table `transaction`
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
