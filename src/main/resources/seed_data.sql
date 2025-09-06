INSERT INTO ai_agent_factory.bed
(bed_id, room_id, bed_label, status, out_of_service_since, last_cleaned_at) VALUES
(1, 1, 'A', 'OUT_OF_SERVICE', '2025-08-25 12:43:20', '2025-08-25 10:51:36');

INSERT INTO ai_agent_factory.bed
(bed_id, room_id, bed_label, status, out_of_service_since, last_cleaned_at) VALUES
(2, 1, 'B', 'OUT_OF_SERVICE', '2025-08-24 00:42:47', '2025-08-23 06:59:48');

INSERT INTO ai_agent_factory.bed
(bed_id, room_id, bed_label, status, out_of_service_since, last_cleaned_at) VALUES
(3, 2, 'A', 'AVAILABLE', NULL, '2025-08-23 06:59:48');

INSERT INTO ai_agent_factory.bed
(bed_id, room_id, bed_label, status, out_of_service_since, last_cleaned_at) VALUES
(4, 3, 'A', 'AVAILABLE', NULL, '2025-08-23 06:59:48');

INSERT INTO ai_agent_factory.bed
(bed_id, room_id, bed_label, status, out_of_service_since, last_cleaned_at) VALUES
(5, 4, 'A', 'OCCUPIED', NULL, '2025-08-23 06:59:48');

INSERT INTO ai_agent_factory.bed_event
(event_id, bed_id, from_status, to_status, at_ts, reason) VALUES
(1, 1, 'OUT_OF_SERVICE', 'OUT_OF_SERVICE', '2025-08-25 12:43:20', 'pump failure');

INSERT INTO ai_agent_factory.department (department_id, code, name) VALUES
(1, 'ED', 'Emergency Department'),
(2, 'MS', 'Med-Surg'),
(3, 'CCU', 'Critical Care Unit'),
(4, 'OR', 'Operating Room'),
(5, 'RAD', 'Radiology'),
(6, 'PHARM', 'Pharmacy'),
(7, 'EVS', 'Environmental Services');

INSERT INTO ai_agent_factory.encounter
(encounter_id, patient_id, encounter_no, type, admit_dt, discharge_dt, current_unit_id, current_bed_id, bed_assigned_at, status)
VALUES
(1, 1, 'ENC-10001', 'INPATIENT', '2025-08-23 04:59:48', NULL, 2, 1, NULL, 'ADMITTED'),
(2, 2, 'ENC-10002', 'ED',        '2025-08-23 06:39:48', NULL, 2, 3, '2025-08-25 15:12:18', 'ADMITTED'),
(3, 3, 'ENC-1756142496', 'INPATIENT', '2025-08-24 12:21:36', '2025-08-25 09:21:36', 2, 1, NULL, 'DISCHARGED'),
(4, 4, 'ENC-20004', 'ED',        '2025-08-25 09:20:00', NULL, 1, NULL, NULL, 'REGISTERED'),
(5, 5, 'ENC-20005', 'INPATIENT', '2025-08-25 09:45:00', NULL, 3, NULL, NULL, 'ADMITTED'),
(6, 6, 'ENC-20006', 'ED',        '2025-08-25 10:10:00', NULL, 1, NULL, NULL, 'REGISTERED'),
(7, 7, 'ENC-20007', 'OUTPATIENT','2025-08-25 10:25:00', NULL, 2, NULL, NULL, 'REGISTERED'),
(8, 8, 'ENC-20008', 'ED',        '2025-08-25 10:40:00', NULL, 1, NULL, NULL, 'REGISTERED'),
(9, 9, 'ENC-20009', 'ED',        '2025-08-25 11:00:00', NULL, 1, NULL, NULL, 'REGISTERED'),
(10,10,'ENC-20010','INPATIENT',  '2025-08-25 11:15:00', NULL, 3, NULL, NULL, 'ADMITTED'),
(11,11,'ENC-20011','OUTPATIENT', '2025-08-25 11:35:00', NULL, 2, NULL, NULL, 'REGISTERED'),
(12,12,'ENC-20012','ED',        '2025-08-25 11:50:00', NULL, 1, NULL, NULL, 'REGISTERED'),
(13,13,'ENC-20013','ED',        '2025-08-25 12:05:00', NULL, 1, NULL, NULL, 'REGISTERED'),
(14,14,'ENC-20014','INPATIENT', '2025-08-25 12:25:00', NULL, 3, NULL, NULL, 'ADMITTED'),
(15,15,'ENC-20015','ED',        '2025-08-25 12:40:00', NULL, 1, NULL, NULL, 'REGISTERED'),
(16,16,'ENC-20016','OUTPATIENT','2025-08-25 13:00:00', NULL, 2, NULL, NULL, 'REGISTERED'),
(17,17,'ENC-20017','ED',        '2025-08-25 13:15:00', NULL, 1, NULL, NULL, 'REGISTERED'),
(18,18,'ENC-20018','ED',        '2025-08-25 13:30:00', NULL, 1, NULL, NULL, 'REGISTERED'),
(19,19,'ENC-20019','INPATIENT', '2025-08-25 13:45:00', NULL, 3, NULL, NULL, 'ADMITTED'),
(20,20,'ENC-20020','ED',        '2025-08-25 14:00:00', NULL, 1, NULL, NULL, 'REGISTERED');

INSERT INTO ai_agent_factory.evs_ticket
(evs_ticket_id, room_id, priority, status, eta_minutes, created_by, created_at, closed_at)
VALUES
(1, 1, 'NORMAL', 'QUEUED', 45, 2, '2025-08-23 06:59:48', NULL),
(2, 1, 'HIGH',   'DONE',   60, NULL, '2025-08-25 09:52:32', '2025-08-25 10:52:32');

INSERT INTO ai_agent_factory.inventory_item
(item_id, sku, name, uom, par_level)
VALUES
(1, 'IV-100',   'IV kits',       'EA', 40),
(2, 'MASK-N95', 'N95 masks',     'EA', 100),
(3, 'GLOVE-M',  'Gloves Medium', 'BX', 60);


INSERT INTO ai_agent_factory.patient
(patient_id, mrn, first_name, last_name, dob, sex, phone, email)
VALUES
(1,  'MRN001',       'Pat',     'Lee',       '1984-07-10', 'F', '555-5001', 'pat.lee@demo.test'),
(2,  'MRN002',       'Chris',   'Wong',      '1976-03-21', 'M', '555-5002', 'chris.wong@demo.test'),
(3,  'MRN-TEST-001', 'Test',    'Discharge', '1985-05-20', 'M', '555-0100', 'test.discharge@example.com'),
(4,  'MRN-10001',    'Alice',   'Johnson',   '1985-03-12', 'F', '555-1001', 'alice.johnson@example.com'),
(5,  'MRN-10002',    'Bob',     'Smith',     '1978-07-22', 'M', '555-1002', 'bob.smith@example.com'),
(6,  'MRN-10003',    'Carla',   'Nguyen',    '1992-11-05', 'F', '555-1003', 'carla.nguyen@example.com'),
(7,  'MRN-10004',    'David',   'Martinez',  '1969-01-15', 'M', '555-1004', 'david.martinez@example.com'),
(8,  'MRN-10005',    'Ethan',   'Brown',     '2001-04-09', 'M', '555-1005', 'ethan.brown@example.com'),
(9,  'MRN-10006',    'Fiona',   'Garcia',    '1995-06-28', 'F', '555-1006', 'fiona.garcia@example.com'),
(10, 'MRN-10007',    'George',  'Lee',       '1983-09-30', 'M', '555-1007', 'george.lee@example.com'),
(11, 'MRN-10008',    'Hannah',  'Kim',       '1990-02-11', 'F', '555-1008', 'hannah.kim@example.com'),
(12, 'MRN-10009',    'Isaac',   'Wilson',    '1975-12-19', 'M', '555-1009', 'isaac.wilson@example.com'),
(13, 'MRN-10010',    'Julia',   'White',     '1988-08-24', 'F', '555-1010', 'julia.white@example.com'),
(14, 'MRN-10011',    'Kevin',   'Patel',     '1993-03-17', 'M', '555-1011', 'kevin.patel@example.com'),
(15, 'MRN-10012',    'Laura',   'Davis',     '1971-10-02', 'F', '555-1012', 'laura.davis@example.com'),
(16, 'MRN-10013',    'Michael', 'Anderson',  '1965-05-21', 'M', '555-1013', 'michael.anderson@example.com'),
(17, 'MRN-10014',    'Nina',    'Hernandez', '1999-09-14', 'F', '555-1014', 'nina.hernandez@example.com'),
(18, 'MRN-10015',    'Oliver',  'Wong',      '2002-07-07', 'M', '555-1015', 'oliver.wong@example.com'),
(19, 'MRN-10016',    'Priya',   'Shah',      '1986-01-29', 'F', '555-1016', 'priya.shah@example.com'),
(20, 'MRN-10017',    'Quinn',   'Taylor',    '1994-04-18', 'X', '555-1017', 'quinn.taylor@example.com'),
(21, 'MRN-10018',    'Raj',     'Singh',     '1979-06-26', 'M', '555-1018', 'raj.singh@example.com'),
(22, 'MRN-10019',    'Sophia',  'Lopez',     '1997-11-12', 'F', '555-1019', 'sophia.lopez@example.com'),
(23, 'MRN-10020',    'Thomas',  'Clark',     '1982-02-03', 'M', '555-1020', 'thomas.clark@example.com');

INSERT INTO ai_agent_factory.role
(role_id, code, name, category, clinical, description, created_at)
VALUES
(1,  'RN',            'Registered Nurse',       'CLINICAL', 1, 'Direct patient care; ratios apply',          '2025-08-27 06:59:08'),
(2,  'CHARGE_NURSE',  'Charge Nurse',           'CLINICAL', 1, 'Unit lead; coordinates staffing',             '2025-08-27 06:59:08'),
(3,  'EVS',           'Environmental Services', 'SUPPORT',  0, 'Cleaning & turnover',                         '2025-08-27 06:59:08'),
(4,  'TRANSPORT',     'Patient Transport',      'SUPPORT',  0, 'Moves patients between units',                '2025-08-27 06:59:08'),
(5,  'PHARMACIST',    'Pharmacist',             'CLINICAL', 1, 'Medication verification & dispensing',        '2025-08-27 06:59:08'),
(6,  'LAB_TECH',      'Laboratory Technician',  'CLINICAL', 1, 'Specimen processing & testing',               '2025-08-27 06:59:08'),
(7,  'RESP_THERAPIST','Respiratory Therapist',  'CLINICAL', 1, 'Ventilation & respiratory care',              '2025-08-27 06:59:08'),
(8,  'RAD_TECH',      'Radiology Technologist', 'CLINICAL', 1, 'Imaging procedures',                          '2025-08-27 06:59:08'),
(9,  'SECURITY',      'Security Officer',       'SUPPORT',  0, 'Safety & access control',                     '2025-08-27 06:59:08'),
(10, 'UNIT_CLERK',    'Unit Clerk',             'ADMIN',    0, 'Unit coordination & admin tasks',             '2025-08-27 06:59:08');

INSERT INTO ai_agent_factory.room
(room_id, unit_id, room_number, isolation_type, has_negative_pressure)
VALUES
(1, 2, '201', 'NONE', 0),
(2, 2, '202', 'CONTACT', 0),
(3, 3, '12',  'AIRBORNE', 1),
(4, 1, 'B05', 'NONE', 0);

INSERT INTO ai_agent_factory.staff
(staff_id, staff_no, first_name, last_name, email, phone, active)
VALUES
(1,  'S100', 'Avery',   'Nguyen',   'avery.nguyen@hospital.test',   '555-1000', 1),
(2,  'S200', 'Jordan',  'Patel',    'jordan.patel@hospital.test',  '555-2000', 1),
(3,  'S300', 'Sam',     'Lopez',    'sam.lopez@hospital.test',     '555-3000', 1),
(4,  'S400', 'Riley',   'Kim',      'riley.kim@hospital.test',     '555-4000', 1),
(5,  'S500', 'Morgan',  'Lee',      'morgan.lee@hospital.test',    '555-5000', 1),
(6,  'S501', 'Taylor',  'Adams',    'taylor.adams@hospital.test',  '555-5001', 1),
(7,  'S502', 'Casey',   'Brooks',   'casey.brooks@hospital.test',  '555-5002', 1),
(8,  'S503', 'Drew',    'Carter',   'drew.carter@hospital.test',   '555-5003', 1),
(9,  'S504', 'Alex',    'Diaz',     'alex.diaz@hospital.test',     '555-5004', 1),
(10, 'S505', 'Jamie',   'Evans',    'jamie.evans@hospital.test',   '555-5005', 1),
(11, 'S506', 'Reese',   'Foster',   'reese.foster@hospital.test',  '555-5006', 1),
(12, 'S507', 'Jordan',  'Gray',     'jordan.gray@hospital.test',   '555-5007', 1),
(13, 'S508', 'Riley',   'Hughes',   'riley.hughes@hospital.test',  '555-5008', 1),
(14, 'S509', 'Skyler',  'Ingram',   'skyler.ingram@hospital.test', '555-5009', 1),
(15, 'S510', 'Cameron', 'James',    'cameron.james@hospital.test', '555-5010', 1),
(16, 'S511', 'Quinn',   'Kennedy',  'quinn.kennedy@hospital.test', '555-5011', 1),
(17, 'S512', 'Rowan',   'Lopez',    'rowan.lopez@hospital.test',   '555-5012', 1),
(18, 'S513', 'Hayden',  'Mitchell', 'hayden.mitchell@hospital.test','555-5013',1),
(19, 'S514', 'Avery',   'Nguyen',   'avery2.nguyen@hospital.test', '555-5014', 1),
(20, 'S515', 'Peyton',  'Ortiz',    'peyton.ortiz@hospital.test',  '555-5015', 1),
(21, 'S516', 'Dakota',  'Perez',    'dakota.perez@hospital.test',  '555-5016', 1),
(22, 'S517', 'Emerson', 'Quintana', 'emerson.quintana@hospital.test','555-5017',1),
(23, 'S518', 'Harper',  'Reed',     'harper.reed@hospital.test',   '555-5018', 1),
(24, 'S519', 'Finley',  'Shaw',     'finley.shaw@hospital.test',   '555-5019', 0);

INSERT INTO ai_agent_factory.staff_assignment
(assignment_id, staff_id, role_id, unit_id, start_dt, end_dt, status, note)
VALUES
(1,  1, 1, 1, '2025-08-25 07:00:00', '2025-08-25 15:00:00', 'ACTIVE',   'Day RN'),
(2,  2, 1, 1, '2025-08-25 07:00:00', '2025-08-25 15:00:00', 'ACTIVE',   'Day RN'),
(3,  3, 2, 1, '2025-08-25 07:00:00', '2025-08-25 15:00:00', 'ACTIVE',   'Charge Nurse'),
(4,  4, 3, 1, '2025-08-25 07:00:00', '2025-08-25 15:00:00', 'ACTIVE',   'Morning EVS'),
(5,  5, 1, 2, '2025-08-25 07:00:00', '2025-08-25 15:00:00', 'ACTIVE',   'RN Med-Surg'),
(6,  6, 1, 2, '2025-08-25 15:00:00', '2025-08-25 23:00:00', 'PLANNED',  'Evening RN'),
(7,  7, 2, 2, '2025-08-25 23:00:00', '2025-08-26 07:00:00', 'PLANNED',  'Night Charge'),
(8,  8, 1, 3, '2025-08-25 07:00:00', '2025-08-25 15:00:00', 'ACTIVE',   'RN CCU'),
(9,  9, 7, 3, '2025-08-25 07:00:00', '2025-08-25 15:00:00', 'ACTIVE',   'Resp Therapist'),
(10, 10, 2, 3, '2025-08-25 23:00:00', '2025-08-26 07:00:00', 'PLANNED', 'Charge Nurse CCU'),
(11, 11, 1, 4, '2025-08-25 07:00:00', '2025-08-25 15:00:00', 'ACTIVE',  'OR Nurse'),
(12, 12, 6, 4, '2025-08-25 07:00:00', '2025-08-25 15:00:00', 'ACTIVE',  'Lab Tech Support'),
(13, 13, 8, 5, '2025-08-25 07:00:00', '2025-08-25 15:00:00', 'ACTIVE',  'Radiology Tech'),
(14, 14, 9, 5, '2025-08-25 15:00:00', '2025-08-25 23:00:00', 'PLANNED', 'Security Coverage'),
(15, 15, 10, 5, '2025-08-25 15:00:00', '2025-08-25 23:00:00', 'PLANNED','Evening Clerk'),
(16,  2, 3, 1, '2025-08-25 09:00:00', '2025-08-25 17:00:00', 'ACTIVE',  'Balance ED understaffing for DAY shift'),
(17,  1, 1, 1, '2025-08-28 14:00:00', '2025-08-28 22:00:00', 'ACTIVE',  'Day shift RN in ED'),
(18,  2, 2, 2, '2025-08-28 14:00:00', '2025-08-28 22:00:00', 'ACTIVE',  'Day shift EVS in Med-Surg'),
(19,  2, 3, 1, '2025-09-01 14:00:00', '2025-09-01 22:00:00', 'ACTIVE',  'NL move');

INSERT INTO ai_agent_factory.staff_role (role_id, code, name) VALUES
(1, 'CHARGE_NURSE', 'Charge Nurse'),
(2, 'RN', 'Registered Nurse'),
(3, 'EVS', 'Environmental Services'),
(4, 'TRANSPORT', 'Patient Transporter');

INSERT INTO ai_agent_factory.staffing_target
(target_id, unit_id, role_id, shift_name, target_count, min_count, max_count, nurse_to_patient_ratio, effective_date) VALUES
(1, 1, 1, 'DAY', 8, 6, 10, 1.00, '2025-08-25'),
(2, 1, 2, 'DAY', 2, 1, 3, NULL, '2025-08-25'),
(3, 1, 3, 'DAY', 4, 3, 6, NULL, '2025-08-25'),
(4, 1, 4, 'EVE', 3, 2, 5, NULL, '2025-08-25'),
(5, 2, 1, 'DAY', 12, 10, 15, 4.00, '2025-08-25'),
(6, 2, 2, 'EVE', 1, 1, 2, NULL, '2025-08-25'),
(7, 2, 3, 'NIGHT', 3, 2, 4, NULL, '2025-08-25'),
(8, 3, 1, 'DAY', 6, 5, 8, 2.00, '2025-08-25'),
(9, 3, 7, 'DAY', 2, 1, 3, NULL, '2025-08-25'),
(10, 3, 2, 'NIGHT', 1, 1, 1, NULL, '2025-08-25'),
(11, 4, 1, 'DAY', 4, 3, 6, 1.50, '2025-08-25'),
(12, 4, 6, 'DAY', 2, 1, 3, NULL, '2025-08-25'),
(13, 5, 8, 'DAY', 3, 2, 4, NULL, '2025-08-25'),
(14, 5, 9, 'DAY', 1, 1, 2, NULL, '2025-08-25'),
(15, 5, 10, 'EVE', 1, 1, 1, NULL, '2025-08-25'),
(16, 1, 1, 'DAY', 5, 4, 6, NULL, '2025-08-28'),
(17, 2, 2, 'DAY', 3, 2, 5, NULL, '2025-08-28');

INSERT INTO ai_agent_factory.transport_job
(transport_job_id, encounter_id, from_unit_id, to_unit_id, status, requested_at, completed_at, assigned_to) VALUES
(1, 2, 1, 2, 'REQUESTED', '2025-08-23 06:59:48', NULL, NULL),
(12, 1, 1, 2, 'REQUESTED', '2025-08-26 10:21:28', NULL, 1),
(13, 2, 2, 3, 'REQUESTED', '2025-08-26 10:36:28', NULL, 2),
(14, 3, 3, 1, 'REQUESTED', '2025-08-26 10:41:28', NULL, 3),
(15, 4, 1, 4, 'REQUESTED', '2025-08-26 10:46:28', NULL, 4),
(16, 5, 3, 5, 'REQUESTED', '2025-08-26 10:51:28', NULL, 1),
(17, 6, 1, 2, 'REQUESTED', '2025-08-26 10:26:28', NULL, 2),
(18, 7, 2, 4, 'REQUESTED', '2025-08-26 10:31:28', NULL, 3),
(19, 8, 1, 3, 'REQUESTED', '2025-08-26 10:48:28', NULL, 4),
(20, 9, 1, 5, 'REQUESTED', '2025-08-26 10:54:28', NULL, 1),
(21, 10, 3, 2, 'REQUESTED', '2025-08-26 10:11:28', NULL, 2),
(22, 11, 2, 1, 'DONE', '2025-08-26 09:56:35', '2025-08-26 10:06:35', 3),
(23, 12, 1, 3, 'DONE', '2025-08-26 09:46:35', '2025-08-26 10:01:35', 4),
(24, 13, 3, 4, 'DONE', '2025-08-26 09:36:35', '2025-08-26 09:51:35', 1),
(25, 14, 4, 5, 'DONE', '2025-08-26 09:26:35', '2025-08-26 09:41:35', 2),
(26, 15, 1, 2, 'DONE', '2025-08-26 10:06:35', '2025-08-26 10:16:35', 3),
(27, 16, 2, 5, 'DONE', '2025-08-26 10:21:35', '2025-08-26 10:31:35', 4),
(28, 17, 1, 4, 'DONE', '2025-08-26 10:36:35', '2025-08-26 10:46:35', 1),
(29, 18, 1, 3, 'DONE', '2025-08-26 09:51:35', '2025-08-26 10:03:35', 2),
(30, 19, 3, 2, 'DONE', '2025-08-26 10:41:35', '2025-08-26 10:51:35', 3),
(31, 20, 1, 5, 'DONE', '2025-08-26 10:16:35', '2025-08-26 10:28:35', 4);

INSERT INTO ai_agent_factory.unit
(unit_id, department_id, code, name, acuity_level) VALUES
(1, 1, 'ED-1', 'ED Main', 'ED'),
(2, 2, 'MS-1', 'Med-Surg North', 'GEN'),
(3, 3, 'CCU-1', 'CCU East', 'ICU'),
(4, 4, 'OR-1', 'OR Block A', 'OR'),
(5, 5, 'RAD-1', 'Radiology Core', 'GEN');


INSERT INTO ai_agent_factory.unit_inventory
(unit_id, item_id, on_hand_qty) VALUES
(1, 3, 10),
(2, 1, 22),
(2, 2, 80),
(3, 2, 20);
