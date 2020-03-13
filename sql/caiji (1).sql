-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- 主机： 192.168.1.20
-- 生成日期： 2020-02-05 11:04:51
-- 服务器版本： 8.0.13
-- PHP 版本： 7.0.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- 数据库： `zy_spider`
--

-- --------------------------------------------------------

--
-- 表的结构 `caiji`
--

CREATE TABLE `caiji` (
  `id` int(10) UNSIGNED NOT NULL,
  `type` tinyint(4) UNSIGNED NOT NULL DEFAULT '0' COMMENT '网站：1：智联,2：58，',
  `province` varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '省份',
  `city` varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采集城市',
  `url` varchar(1024) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '采集url',
  `caijiTime` int(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '采集时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;

--
-- 转储表的索引
--

--
-- 表的索引 `caiji`
--
ALTER TABLE `caiji`
  ADD PRIMARY KEY (`id`),
  ADD KEY `type` (`type`),
  ADD KEY `province` (`province`),
  ADD KEY `caijiTime` (`caijiTime`);

--
-- 在导出的表使用AUTO_INCREMENT
--

--
-- 使用表AUTO_INCREMENT `caiji`
--
ALTER TABLE `caiji`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
