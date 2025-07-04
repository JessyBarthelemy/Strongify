Insert INTO ExerciseGroup(label, groupIcon, sortOrder) VALUES('group_chest', 'ic_chest', 0);
Insert INTO ExerciseGroup(label, groupIcon, sortOrder) VALUES('group_arm', 'ic_arm', 1);
Insert INTO ExerciseGroup(label, groupIcon, sortOrder) VALUES('group_back', 'ic_back', 2);
Insert INTO ExerciseGroup(label, groupIcon, sortOrder) VALUES('group_shoulder', 'ic_shoulder', 3);
Insert INTO ExerciseGroup(label, groupIcon, sortOrder) VALUES('group_leg', 'ic_leg', 4);
Insert INTO ExerciseGroup(label, groupIcon, sortOrder) VALUES('group_abs', 'ic_abs', 5);
Insert INTO ExerciseGroup(label, groupIcon, sortOrder) VALUES('group_cardio', 'ic_cardio', 6);

INSERT INTO Exercise(title, description, groupId, base) VALUES('ab_wheel', '', 6, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('hip_abduction', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('bench_to_skull', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('decline_triceps_extension', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('ankle_circles', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('iron_cross_dumbbells', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('cross_over', '', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('cross_body_crunch', '', 6, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('crunch_stability', '', 6, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('decline_crunch', '', 6, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('decline_ob_crunch', '', 6, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('curl_machine', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('cable_curl', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('al_curl_dumb', '@curl_dumb', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('al_hammer_curl_dumb', '@hammer_curl_dumb', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('spider_curl_bar', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('ez_bar_curl', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('wide_grip_curl_bar', '@curl_bar', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('concentration_curl_stab', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('al_inc_curl_dumb', '@curl_dumb', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('reverse_curl_dumb', '@curl_dumb', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('preacher_hammer_dumb', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('curl_cable', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('overhead_curl_cable', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('preacher_curl_machine', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('preacher_curl_bar', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('preacher_curl_cable', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('close_grip_ez_curl', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('curl_stability', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('zot_curl_dumb', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('zot_preacher_curl_dumb', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('inc_bench_press', '', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('bar_neck_press', '@bench_press', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('rev_bench_press', '@bench_press', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('mach_bench_press', '@bench_press', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('wide_grip_bench_press', '@bench_press', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('nar_grip_bench_press', '', 2, 2);
INSERT INTO Exercise(title, description, groupId, base) VALUES('smith_close_grip_bench_press', '@nar_grip_bench_press', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('smith_bench_press', '@bench_press', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('one_arm_bench_press', '', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('cuban_dumb', '', 4, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('decl_chest_press', '@dec_bar_bench_press', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('dec_dumb_bench_press', '@dec_bar_bench_press', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('wide_grip_decline_bench_press', '@dec_bar_bench_press', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('inc_chest_press', '@bench_press', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('dumb_inc_bench_press', '@bench_press_dumbell', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('smith_machine_inc_bench_press', '@bench_press', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('one_arm_dumbbell', '@dumb_shoulder_press', 4, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('dips_machine', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('drag_curl', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('flat_bench_cable_fly', '@dumb_fly', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('decline_dumb_fly', '@dumb_fly', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('hammer_grip_inc_bench_press', '@dumb_fly', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('front_cable_raises', '@lat_dumb_raise', 4, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('front_dumb_raises', '', 4, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('back_extension_stability', '', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('triceps_ext_cable', '@standing_triceps_extension', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('one_arm_pulley_extension', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('over_triceps_ext_bar', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('dec_triceps_ext_dumb', '@standing_triceps_extension', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('inc_triceps_ext_bar', '@standing_triceps_extension', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('inc_triceps_ext_cable', '@standing_triceps_extension', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('rear_lunges_dumb', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('dumb_lunges', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('lat_lunges_curl', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('side_bend_dumb', '', 6, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('smith_good_morning', '@bar_good_morning', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('hack_squat_machine', '@hack_squat_bar', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('smith_hack_squat', '@hack_squat_bar', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('stand_leg_curl', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('leg_extension', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('back_fly_elas', '', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('bent_over_lat_cable_raise', '', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('over_squat_bar', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('balance_board', '', 7, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('side_plank', '', 6, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('pushup_ball', '@pushup', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('bridging', '', 6, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('dumb_upright_row', '', 4, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('smith_upright_row', '', 4, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('seated_cable_row', '', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('smith_row', '@rear_row_barbell', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('cable_shrug', '@shrug', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('bar_shrug', '@shrug', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('smith_shrug', '@shrug', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('dumb_deadlift', '@deadlift', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('smith_deadlift', '@deadlift', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('single_leg_squat_bar', '@barbell_squat', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('lying_squat', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('front_squat_bar', '@barbell_squat', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('squat_dumb', '@barbell_squat', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('jeff_squat_bar', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('side_squat_bar', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('smith_squat', '@barbell_squat', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('front_squat_bench_bar', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('squat_bench_dumb', '@barbell_squat', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('zercher_squat', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('v_bar_pulldown', '@underhand_pulldown', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('gironda_chin', '', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('inc_push_down_cable', '@underhand_pulldown', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('wide_grip_lat_pulldown', '@underhand_pulldown', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('nar_par_grip_chinups', '@underhand_pulldown', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('chin_ups', '@underhand_pulldown', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('over_pulldown', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('wide_grip_chinup', '@underhand_pulldown', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('triceps_kickback_dumb', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('ball_wall_circles', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('ab_rollout_bar', '', 6, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('bent_arm_pullover', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('butterfly_mach', '', 1, 1);ex_wide_grip_curl_bar_2
INSERT INTO Exercise(title, description, groupId, base) VALUES('crunches', '', 6, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('curl_bar', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('concentration_curl_dumb', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('curl_dumb', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('flexor_inc_curl_dumb', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('hammer_curl_cables', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('hammer_curl_dumb', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('one_arm_preacher_curl', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('arnold_press', '', 4, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('bench_press', '', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('bench_press_dumbell', '', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('close_grip_press_chin_bar', '@nar_grip_bench_press', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('dec_bar_bench_press', '', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('dumb_shoulder_press', '', 4, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('bench_dips', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('chest_dips', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('dumb_fly', '', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('front_bar_raises', '', 4, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('lat_dumb_raise', '', 4, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('standing_triceps_extension', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('rear_lunges_bar', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('bar_lunges', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('walking_lunges', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('bar_good_morning', '', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('hack_squat_bar', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('stat_abd_draw_in', '', 7, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('pushup', '', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('close_pushup', '', 2, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('calves_press_leg_mach', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('dumb_bent_arm_pullover', '', 1, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('bent_knee_hip_raise', '', 6, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('rear_row_barbell', '', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('rear_row_dumb', '', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('t_bar_row', '', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('shrug', '', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('deadlift', '', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('barbell_squat', '', 5, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('superman', '', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('underhand_pulldown', '', 3, 1);
INSERT INTO Exercise(title, description, groupId, base) VALUES('cable_rows', '', 7, 1);

INSERT INTO Equipment(title, base, isSelected) VALUES('eq_abs_bench', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_barbell', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_bench_press', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_calf_machine', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_dipping_bar', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_dumbbells', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_elliptic', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_ez_bar', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_foam_roller', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_hack_squat', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_inc_bench_press', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_kettlebell', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_leg_abd', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_leg_curl', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_leg_extension', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_peck_deck', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_pull_up', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_pull_up_machine', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_pulley', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_rowing', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_smith_machine', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_squat_rack', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_stability_ball', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_treadmills', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('eq_triceps_bar', 1, 0);
INSERT INTO Equipment(title, base, isSelected) VALUES('ex_press', 1, 0);


INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(1, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(1, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(3, 3);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(3, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(3, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(4, 3);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(4, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(4, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(7, 19);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(9, 23);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(10, 1);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(11, 1);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(13, 19);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(14, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(15, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(16, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(17, 8);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(18, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(19, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(20, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(21, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(22, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(23, 19);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(24, 19);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(26, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(27, 19);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(28, 8);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(29, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(29, 23);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(30, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(31, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(32, 11);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(33, 3);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(34, 3);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(36, 3);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(37, 3);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(38, 21);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(39, 21);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(40, 3);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(40, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(41, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(42, 3);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(43, 3);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(43, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(44, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(46, 11);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(46, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(47, 21);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(48, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(49, 5);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(50, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(51, 1);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(51, 19);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(52, 1);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(52, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(53, 11);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(53, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(54, 19);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(55, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(57, 19);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(58, 19);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(59, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(60, 1);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(60, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(61, 1);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(61, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(62, 1);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(62, 19);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(63, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(64, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(65, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(66, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(67, 21);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(68, 10);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(69, 21);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(70, 14);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(71, 15);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(73, 19);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(74, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(74, 22);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(77, 23);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(79, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(80, 21);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(81, 19);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(82, 21);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(83, 19);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(84, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(85, 21);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(86, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(87, 21);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(88, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(88, 22);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(90, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(90, 22);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(91, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(91, 22);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(92, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(93, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(93, 22);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(94, 21);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(95, 22);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(95, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(95, 1);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(96, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(97, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(98, 18);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(99, 17);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(99, 18);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(100, 1);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(100, 17);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(101, 18);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(102, 17);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(102, 18);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(103, 17);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(103, 18);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(104, 18);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(105, 17);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(105, 18);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(106, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(107, 23);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(108, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(109, 1);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(109, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(110, 16);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(112, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(113, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(114, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(115, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(115, 11);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(116, 19);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(117, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(118, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(119, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(120, 3);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(121, 3);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(121, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(122, 3);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(123, 3);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(124, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(125, 1);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(127, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(128, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(129, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(130, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(131, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(132, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(133, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(134, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(135, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(139, 26);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(140, 1);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(140, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(142, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(143, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(145, 6);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(146, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(147, 2);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(147, 22);
INSERT INTO ExerciseEquipment(exerciseId, equipmentId) VALUES(149, 18);