/*
 Navicat Premium Dump SQL

 Source Server         : book
 Source Server Type    : MySQL
 Source Server Version : 80034 (8.0.34)
 Source Host           : localhost:3306
 Source Schema         : book

 Target Server Type    : MySQL
 Target Server Version : 80034 (8.0.34)
 File Encoding         : 65001

 Date: 22/09/2025 17:46:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chapter
-- ----------------------------
DROP TABLE IF EXISTS `chapter`;
CREATE TABLE `chapter`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `video_id` bigint NOT NULL,
  `chapter_index` int NOT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `summary` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `start_time` double NULL DEFAULT NULL,
  `end_time` double NULL DEFAULT NULL,
  `display_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_chapter_video`(`video_id` ASC) USING BTREE,
  CONSTRAINT `fk_chapter_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1137 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for practice_question
-- ----------------------------
DROP TABLE IF EXISTS `practice_question`;
CREATE TABLE `practice_question`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `video_id` bigint NOT NULL COMMENT '关联 video.id',
  `summary_id` bigint NOT NULL COMMENT '关联 video_summary.id',
  `question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题干',
  `option_a` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '选项 A',
  `option_b` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '选项 B',
  `option_c` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '选项 C',
  `correct_answer` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '正确答案：A/B/C',
  `explanation` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '题解',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 408 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for static_video
-- ----------------------------
DROP TABLE IF EXISTS `static_video`;
CREATE TABLE `static_video`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `file_path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `cover_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `duration` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for subtitle_segment
-- ----------------------------
DROP TABLE IF EXISTS `subtitle_segment`;
CREATE TABLE `subtitle_segment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `video_id` bigint NOT NULL COMMENT '外键：关联视频ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '中文字幕句子',
  `start_time` double NULL DEFAULT NULL COMMENT '字幕起始时间（秒）',
  `end_time` double NULL DEFAULT NULL COMMENT '字幕结束时间（秒）',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_subtitle_video`(`video_id` ASC) USING BTREE,
  CONSTRAINT `fk_subtitle_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8605 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '无名用户',
  `account` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`user_id` DESC, `account`) USING BTREE,
  INDEX `id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33330 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_answer
-- ----------------------------
DROP TABLE IF EXISTS `user_answer`;
CREATE TABLE `user_answer`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `question_id` bigint NOT NULL COMMENT '关联 practice_question.id',
  `selected_answer` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户选择：A/B/C',
  `is_correct` tinyint(1) NOT NULL COMMENT '是否作答正确',
  `answered_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for video
-- ----------------------------
DROP TABLE IF EXISTS `video`;
CREATE TABLE `video`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 84 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for video_metadata
-- ----------------------------
DROP TABLE IF EXISTS `video_metadata`;
CREATE TABLE `video_metadata`  (
  `video_id` bigint NOT NULL,
  `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `duration` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`video_id`) USING BTREE,
  CONSTRAINT `fk_video_id` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for video_note
-- ----------------------------
DROP TABLE IF EXISTS `video_note`;
CREATE TABLE `video_note`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '笔记段主键ID',
  `video_id` bigint NOT NULL COMMENT '所属视频ID，关联 video 表',
  `note_index` int NOT NULL COMMENT '笔记段序号，从1开始',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '笔记段标题，例如“一、南京大桥简介”',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '笔记正文内容（结构化段落）',
  `parent_index` int NULL DEFAULT NULL COMMENT '父节点序号（一级章节序号），顶级目录为 NULL',
  `sub_index` int NULL DEFAULT NULL COMMENT '子节点序号（二级小节序号），仅当 parent_index 不为 NULL 时有效',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_video_note_video`(`video_id` ASC) USING BTREE,
  CONSTRAINT `fk_video_note_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for video_summary
-- ----------------------------
DROP TABLE IF EXISTS `video_summary`;
CREATE TABLE `video_summary`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `video_id` bigint NOT NULL,
  `chapter_index` int NOT NULL,
  `chapter_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_summary_video`(`video_id` ASC) USING BTREE,
  CONSTRAINT `fk_summary_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3950 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for videos
-- ----------------------------
DROP TABLE IF EXISTS `videos`;
CREATE TABLE `videos`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `cover_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `duration` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
