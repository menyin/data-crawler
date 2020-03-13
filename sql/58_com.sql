-- phpMyAdmin SQL Dump
-- version 4.8.3
-- https://www.phpmyadmin.net/
--
-- 主机： 192.168.1.20
-- 生成日期： 2020-02-05 11:04:11
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
-- 表的结构 `58_com`
--

CREATE TABLE `58_com` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `cname` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '公司名称',
  `isVip` tinyint(1) UNSIGNED NOT NULL DEFAULT '0' COMMENT '是否会员：0否，1是',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '',
  `data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '数据',
  `modTime` int(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '修改时间',
  `caijiTime` int(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '采集时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;

--
-- 转储表的索引
--

--
-- 表的索引 `58_com`
--
ALTER TABLE `58_com`
  ADD PRIMARY KEY (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
