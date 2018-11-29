INSERT INTO `cms_role_info` (`ID`, `ROLE_CODE`, `ROLE_NAME`, `CREATE_BY`, `CREATE_DATE`, `MODIFIED_BY`, `MODIFIED_DATE`, `SORTNO`, `STATE`) VALUES (1, 'admin', '系统管理员', 'admin', '2017-12-8 19:06:35', 'admin', '2017-12-8 19:06:39', 0, 1);
INSERT INTO `cms_role_info` (`ID`, `ROLE_CODE`, `ROLE_NAME`, `CREATE_BY`, `CREATE_DATE`, `MODIFIED_BY`, `MODIFIED_DATE`, `SORTNO`, `STATE`) VALUES (2, 'guest', '客人', 'admin', '2017-12-8 19:06:35', 'admin', '2017-12-8 19:06:35', 1, 1);

INSERT INTO `cms_user_info` (`ID`, `USER_CODE`, `USER_NAME`, `USER_PWD`, `REMARK`, `CREATE_BY`, `CREATE_DATE`, `MODIFIED_BY`, `MODIFIED_DATE`, `SORTNO`, `STATE`) VALUES (1, 'admin', '管理员', '21232f297a57a5a743894a0e4a801fc3', '测试备注', 'admin', '2017-12-8 20:32:02', 'admin', '2017-12-8 20:32:09', 0, 1);
INSERT INTO `cms_user_info` (`ID`, `USER_CODE`, `USER_NAME`, `USER_PWD`, `REMARK`, `CREATE_BY`, `CREATE_DATE`, `MODIFIED_BY`, `MODIFIED_DATE`, `SORTNO`, `STATE`) VALUES (2, '17040406', '张三', 'd0970714757783e6cf17b26fb8e2298f', '测试备注', 'admin', '2017-12-8 20:32:02', 'admin', '2017-12-8 20:32:09', 1, 1);


INSERT INTO `cms_user_role_r` (`ID`, `USER_CODE`, `ROLE_CODE`, `CREATE_BY`, `CREATE_DATE`, `MODIFIED_BY`, `MODIFIED_DATE`, `SORTNO`, `STATE`) VALUES (1, 'admin', 'admin', 'admin', '2017-12-8 20:19:34', 'admin', '2017-12-8 20:19:34', 0, 1);
INSERT INTO `cms_user_role_r` (`ID`, `USER_CODE`, `ROLE_CODE`, `CREATE_BY`, `CREATE_DATE`, `MODIFIED_BY`, `MODIFIED_DATE`, `SORTNO`, `STATE`) VALUES (2, '17040406', 'guest', 'admin', '2017-12-8 20:19:34', 'admin', '2017-12-8 20:19:34', 0, 1);


INSERT INTO `menu` (`id`, `menu_name`, `parent_id`, `menu_url`) VALUES (1, '主菜单', 1, '');
INSERT INTO `menu` (`id`, `menu_name`, `parent_id`, `menu_url`) VALUES (2, '权限系统', 1, '');
INSERT INTO `menu` (`id`, `menu_name`, `parent_id`, `menu_url`) VALUES (3, '内容管理', 1, '');
INSERT INTO `menu` (`id`, `menu_name`, `parent_id`, `menu_url`) VALUES (4, '用户管理', 2, '');
INSERT INTO `menu` (`id`, `menu_name`, `parent_id`, `menu_url`) VALUES (5, '角色管理', 2, '');


INSERT INTO `user` (`id`, `user_name`, `password`, `age`) VALUES (1, '1', '1', 1);
