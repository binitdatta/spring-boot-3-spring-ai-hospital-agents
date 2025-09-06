SET FOREIGN_KEY_CHECKS = 0;

-- Departments
INSERT INTO department (code, name) VALUES
 ('ED','Emergency Department'),
 ('MS','Med-Surg'),
 ('CCU','Critical Care Unit'),
 ('OR','Operating Room'),
 ('RAD','Radiology'),
 ('PHARM','Pharmacy'),
 ('EVS','Environmental Services');

-- Units
INSERT INTO unit (department_id, code, name, acuity_level) VALUES
 ((SELECT department_id FROM department WHERE code='ED'),   'ED-1','ED Main','ED'),
 ((SELECT department_id FROM department WHERE code='MS'),   'MS-1','Med-Surg North','GEN'),
 ((SELECT department_id FROM department WHERE code='CCU'),  'CCU-1','CCU East','ICU'),
 ((SELECT department_id FROM department WHERE code='OR'),   'OR-1','OR Block A','OR'),
 ((SELECT department_id FROM department WHERE code='RAD'),  'RAD-1','Radiology Core','GEN');

-- Rooms
INSERT INTO room (unit_id, room_number, isolation_type, has_negative_pressure) VALUES
 ((SELECT unit_id FROM unit WHERE code='MS-1'),'201','NONE',FALSE),
 ((SELECT unit_id FROM unit WHERE code='MS-1'),'202','CONTACT',FALSE),
 ((SELECT unit_id FROM unit WHERE code='CCU-1'),'12','AIRBORNE',TRUE),
 ((SELECT unit_id FROM unit WHERE code='ED-1'),'B05','NONE',FALSE);

-- Beds
INSERT INTO bed (room_id, bed_label, status, last_cleaned_at) VALUES
 ((SELECT room_id FROM room WHERE room_number='201'),'A','AVAILABLE', NOW()),
 ((SELECT room_id FROM room WHERE room_number='201'),'B','CLEANING', NOW()),
 ((SELECT room_id FROM room WHERE room_number='202'),'A','AVAILABLE', NOW()),
 ((SELECT room_id FROM room WHERE room_number='12'),'A','AVAILABLE', NOW()),
 ((SELECT room_id FROM room WHERE room_number='B05'),'A','OCCUPIED', NOW());

-- Staff roles
INSERT INTO staff_role (code, name) VALUES
 ('CHARGE_NURSE','Charge Nurse'),
 ('RN','Registered Nurse'),
 ('EVS','Environmental Services'),
 ('TRANSPORT','Patient Transporter');

-- Staff
INSERT INTO staff (staff_no, first_name, last_name, email, phone) VALUES
 ('S100','Avery','Nguyen','avery.nguyen@hospital.test','555-1000'),
 ('S200','Jordan','Patel','jordan.patel@hospital.test','555-2000'),
 ('S300','Sam','Lopez','sam.lopez@hospital.test','555-3000'),
 ('S400','Riley','Kim','riley.kim@hospital.test','555-4000');

INSERT INTO staff_role_map (staff_id, role_id)
SELECT s.staff_id, r.role_id
FROM staff s JOIN staff_role r
WHERE (s.staff_no='S100' AND r.code='CHARGE_NURSE')
   OR (s.staff_no='S200' AND r.code='EVS')
   OR (s.staff_no='S300' AND r.code='TRANSPORT')
   OR (s.staff_no='S400' AND r.code='RN');

-- Patients
INSERT INTO patient (mrn, first_name, last_name, dob, sex, phone, email) VALUES
 ('MRN001','Pat','Lee','1984-07-10','F','555-5001','pat.lee@demo.test'),
 ('MRN002','Chris','Wong','1976-03-21','M','555-5002','chris.wong@demo.test');

-- Encounters
INSERT INTO encounter (patient_id, encounter_no, type, admit_dt, discharge_dt, current_unit_id, current_bed_id, status)
VALUES
 ((SELECT patient_id FROM patient WHERE mrn='MRN001'),
  'ENC-10001','INPATIENT', NOW() - INTERVAL 2 HOUR, NULL,
  (SELECT unit_id FROM unit WHERE code='MS-1'),
  (SELECT bed_id FROM bed  JOIN room USING(room_id) WHERE room_number='201' AND bed_label='A'),
  'ADMITTED'),
 ((SELECT patient_id FROM patient WHERE mrn='MRN002'),
  'ENC-10002','ED', NOW() - INTERVAL 20 MINUTE, NULL,
  (SELECT unit_id FROM unit WHERE code='ED-1'),
  (SELECT bed_id FROM bed  JOIN room USING(room_id) WHERE room_number='B05' AND bed_label='A'),
  'REGISTERED');

-- EVS ticket (existing)
INSERT INTO evs_ticket (room_id, priority, status, eta_minutes, created_by)
VALUES ((SELECT room_id FROM room WHERE room_number='201'),'NORMAL','QUEUED',45,
        (SELECT staff_id FROM staff WHERE staff_no='S200'));

-- Transport job (requested transfer ED -> MS)
INSERT INTO transport_job (encounter_id, from_unit_id, to_unit_id, status)
VALUES (
  (SELECT encounter_id FROM encounter WHERE encounter_no='ENC-10002'),
  (SELECT unit_id FROM unit WHERE code='ED-1'),
  (SELECT unit_id FROM unit WHERE code='MS-1'),
  'REQUESTED'
);

-- Inventory & stock
INSERT INTO inventory_item (sku, name, uom, par_level) VALUES
 ('IV-100','IV Start Kit','EA',40),
 ('MASK-N95','N95 Respirator','EA',100),
 ('GLOVE-M','Gloves Medium','BX',60);

INSERT INTO unit_inventory (unit_id, item_id, on_hand_qty)
SELECT u.unit_id, i.item_id, t.qty
FROM (SELECT 'MS-1' unit_code, 'IV-100' sku, 22 qty UNION ALL
      SELECT 'MS-1','MASK-N95', 80 UNION ALL
      SELECT 'CCU-1','MASK-N95', 20 UNION ALL
      SELECT 'ED-1','GLOVE-M', 10) t
JOIN unit u ON u.code=t.unit_code
JOIN inventory_item i ON i.sku=t.sku;

-- ----------------------------
-- RAG seed documents & chunks
-- ----------------------------
INSERT INTO rag_document (external_id, title, doc_type, version, source_uri)
VALUES
 ('docs/bed-management-sop.md','Bed Management SOP','SOP','1.0','classpath:/docs/bed-management-sop.md'),
 ('docs/discharge-checklist.md','Discharge Checklist','CHECKLIST','1.0','classpath:/docs/discharge-checklist.md'),
 ('docs/evs-sla.md','EVS Service Levels','POLICY','1.0','classpath:/docs/evs-sla.md');

-- Chunks (keep short for demo; real system chunks by ~800-1200 tokens)
INSERT INTO rag_chunk (doc_id, chunk_index, heading, text) VALUES
 ((SELECT doc_id FROM rag_document WHERE external_id='docs/bed-management-sop.md'),0,'Purpose',
  'Ensure timely assignment of beds considering isolation and acuity.'),
 ((SELECT doc_id FROM rag_document WHERE external_id='docs/bed-management-sop.md'),1,'Isolation Rules',
  'Airborne isolation requires single room with negative pressure. Contact isolation may cohort with same pathogen per policy.'),
 ((SELECT doc_id FROM rag_document WHERE external_id='docs/bed-management-sop.md'),2,'Turnover',
  'EVS targets: routine 45 min, high 20 min, STAT 10 min.'),

 ((SELECT doc_id FROM rag_document WHERE external_id='docs/discharge-checklist.md'),0,'Must Haves',
  'Medication reconciliation complete; follow-up scheduled; discharge summary drafted; instructions reviewed and signed.'),

 ((SELECT doc_id FROM rag_document WHERE external_id='docs/evs-sla.md'),0,'EVS SLA',
  'STAT cleaning ETA 10 minutes; HIGH priority 20 minutes; routine 45 minutes. Escalate if delay > 10 minutes.');

-- NOTE: embeddings left NULL to be filled by your Spring AI ingestor.
-- Example to attach an (empty) placeholder row so FKs are satisfied (optional):
-- INSERT INTO rag_embedding (chunk_id, model, dimension, vector_blob, l2_norm)
-- SELECT chunk_id, 'text-embedding-3-small', 1536, REPEAT(0x00, 1536*4), NULL
-- FROM rag_chunk WHERE chunk_id IN (/* ids you want */);

SET FOREIGN_KEY_CHECKS = 1;
