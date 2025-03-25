/*
 Navicat Premium Dump SQL

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80040 (8.0.40)
 Source Host           : localhost:3306
 Source Schema         : service

 Target Server Type    : MySQL
 Target Server Version : 80040 (8.0.40)
 File Encoding         : 65001

 Date: 25/03/2025 16:48:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for phone_records
-- ----------------------------
DROP TABLE IF EXISTS `phone_records`;
CREATE TABLE `phone_records`  (
  `phone_id` bigint NOT NULL AUTO_INCREMENT COMMENT '唯一标识符',
  `phone_number` bigint NULL DEFAULT NULL COMMENT '手机号码',
  `line_status` int NULL DEFAULT 1 COMMENT '线路状态：1(在线) 、 0(离线)',
  `country_code` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '号码归属国家',
  `registration_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `usage_status` int NULL DEFAULT 1 COMMENT '状态：1(正常)、0(停用）',
  `project` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '项目名称',
  PRIMARY KEY (`phone_id`) USING BTREE,
  UNIQUE INDEX `idx_phone_number`(`phone_number`) USING BTREE COMMENT '手机号唯一索引'
) ENGINE = InnoDB AUTO_INCREMENT = 16126 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_tr_0900_as_cs ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `user_id` varchar(30) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL,
  `account` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '账号',
  `nick_name` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '昵称',
  `password` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NOT NULL COMMENT '密码',
  `email` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL COMMENT '邮箱',
  `created_at` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `updated_at` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `user_avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_bin NULL DEFAULT NULL,
  INDEX `users_user_id_index`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_bin ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
