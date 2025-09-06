-- Charset & SQL mode
SET NAMES utf8mb4 COLLATE utf8mb4_0900_ai_ci;
SET sql_mode = 'STRICT_ALL_TABLES';

-- ----------------------------
-- Reference & Master Data
-- ----------------------------
CREATE TABLE department (
  department_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  code            VARCHAR(32) NOT NULL UNIQUE,
  name            VARCHAR(128) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE unit (
  unit_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  department_id   BIGINT NOT NULL,
  code            VARCHAR(32) NOT NULL UNIQUE,     -- e.g., CCU-1, MS-2
  name            VARCHAR(128) NOT NULL,
  acuity_level    ENUM('GEN','STEPDOWN','ICU','ED','OR') NOT NULL,
  CONSTRAINT fk_unit_dept FOREIGN KEY (department_id) REFERENCES department(department_id)
) ENGINE=InnoDB;

CREATE TABLE room (
  room_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  unit_id         BIGINT NOT NULL,
  room_number     VARCHAR(16) NOT NULL,
  isolation_type  ENUM('NONE','CONTACT','DROPLET','AIRBORNE') NOT NULL DEFAULT 'NONE',
  has_negative_pressure BOOLEAN NOT NULL DEFAULT FALSE,
  UNIQUE KEY uq_room_unit_num (unit_id, room_number),
  CONSTRAINT fk_room_unit FOREIGN KEY (unit_id) REFERENCES unit(unit_id)
) ENGINE=InnoDB;

CREATE TABLE bed (
  bed_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  room_id         BIGINT NOT NULL,
  bed_label       VARCHAR(16) NOT NULL,           -- e.g., A/B
  status          ENUM('AVAILABLE','OCCUPIED','CLEANING','OUT_OF_SERVICE') NOT NULL DEFAULT 'AVAILABLE',
  last_cleaned_at DATETIME NULL,
  UNIQUE KEY uq_bed_room_label (room_id, bed_label),
  CONSTRAINT fk_bed_room FOREIGN KEY (room_id) REFERENCES room(room_id)
) ENGINE=InnoDB;

CREATE TABLE staff_role (
  role_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  code            VARCHAR(32) NOT NULL UNIQUE,    -- CHARGE_NURSE, EVS, TRANSPORT, PHARM
  name            VARCHAR(128) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE staff (
  staff_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
  staff_no        VARCHAR(32) NOT NULL UNIQUE,
  first_name      VARCHAR(64) NOT NULL,
  last_name       VARCHAR(64) NOT NULL,
  email           VARCHAR(128) UNIQUE,
  phone           VARCHAR(32),
  active          BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE staff_role_map (
  staff_id        BIGINT NOT NULL,
  role_id         BIGINT NOT NULL,
  PRIMARY KEY (staff_id, role_id),
  CONSTRAINT fk_srm_staff FOREIGN KEY (staff_id) REFERENCES staff(staff_id),
  CONSTRAINT fk_srm_role  FOREIGN KEY (role_id)  REFERENCES staff_role(role_id)
) ENGINE=InnoDB;

-- ----------------------------
-- Patients & Encounters
-- ----------------------------
CREATE TABLE patient (
  patient_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
  mrn             VARCHAR(32) NOT NULL UNIQUE,    -- medical record number
  first_name      VARCHAR(64) NOT NULL,
  last_name       VARCHAR(64) NOT NULL,
  dob             DATE NOT NULL,
  sex             ENUM('F','M','X') NOT NULL,
  phone           VARCHAR(32),
  email           VARCHAR(128)
) ENGINE=InnoDB;

CREATE TABLE encounter (
  encounter_id    BIGINT PRIMARY KEY AUTO_INCREMENT,
  patient_id      BIGINT NOT NULL,
  encounter_no    VARCHAR(32) NOT NULL UNIQUE,
  type            ENUM('ED','INPATIENT','OUTPATIENT') NOT NULL,
  admit_dt        DATETIME NULL,
  discharge_dt    DATETIME NULL,
  current_unit_id BIGINT NULL,
  current_bed_id  BIGINT NULL,
  status          ENUM('REGISTERED','ADMITTED','DISCHARGED') NOT NULL DEFAULT 'REGISTERED',
  CONSTRAINT fk_enc_patient FOREIGN KEY (patient_id) REFERENCES patient(patient_id),
  CONSTRAINT fk_enc_unit    FOREIGN KEY (current_unit_id) REFERENCES unit(unit_id),
  CONSTRAINT fk_enc_bed     FOREIGN KEY (current_bed_id) REFERENCES bed(bed_id)
) ENGINE=InnoDB;

-- ----------------------------
-- Operations (EVS / Transport / Orders / Labs minimal)
-- ----------------------------
CREATE TABLE evs_ticket (
  evs_ticket_id   BIGINT PRIMARY KEY AUTO_INCREMENT,
  room_id         BIGINT NOT NULL,
  priority        ENUM('NORMAL','HIGH','STAT') NOT NULL DEFAULT 'NORMAL',
  status          ENUM('QUEUED','IN_PROGRESS','DONE','CANCELLED') NOT NULL DEFAULT 'QUEUED',
  eta_minutes     INT NULL,
  created_by      BIGINT NULL,                    -- staff
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  closed_at       DATETIME NULL,
  CONSTRAINT fk_evs_room  FOREIGN KEY (room_id) REFERENCES room(room_id),
  CONSTRAINT fk_evs_staff FOREIGN KEY (created_by) REFERENCES staff(staff_id)
) ENGINE=InnoDB;

CREATE TABLE transport_job (
  transport_job_id BIGINT PRIMARY KEY AUTO_INCREMENT,
  encounter_id    BIGINT NOT NULL,
  from_unit_id    BIGINT NOT NULL,
  to_unit_id      BIGINT NOT NULL,
  status          ENUM('REQUESTED','ASSIGNED','IN_TRANSIT','DONE','CANCELLED') NOT NULL DEFAULT 'REQUESTED',
  requested_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at    DATETIME NULL,
  assigned_to     BIGINT NULL,                    -- staff transporter
  CONSTRAINT fk_tj_enc   FOREIGN KEY (encounter_id) REFERENCES encounter(encounter_id),
  CONSTRAINT fk_tj_from  FOREIGN KEY (from_unit_id) REFERENCES unit(unit_id),
  CONSTRAINT fk_tj_to    FOREIGN KEY (to_unit_id)   REFERENCES unit(unit_id),
  CONSTRAINT fk_tj_staff FOREIGN KEY (assigned_to)  REFERENCES staff(staff_id)
) ENGINE=InnoDB;

-- Minimal inventory/par levels to test supply agent
CREATE TABLE inventory_item (
  item_id         BIGINT PRIMARY KEY AUTO_INCREMENT,
  sku             VARCHAR(64) NOT NULL UNIQUE,
  name            VARCHAR(128) NOT NULL,
  uom             VARCHAR(16) NOT NULL,
  par_level       INT NOT NULL DEFAULT 50
) ENGINE=InnoDB;

CREATE TABLE unit_inventory (
  unit_id         BIGINT NOT NULL,
  item_id         BIGINT NOT NULL,
  on_hand_qty     INT NOT NULL DEFAULT 0,
  PRIMARY KEY (unit_id, item_id),
  CONSTRAINT fk_ui_unit FOREIGN KEY (unit_id) REFERENCES unit(unit_id),
  CONSTRAINT fk_ui_item FOREIGN KEY (item_id) REFERENCES inventory_item(item_id)
) ENGINE=InnoDB;

-- ----------------------------
-- RAG: Documents → Chunks → Embeddings
-- ----------------------------
CREATE TABLE rag_document (
  doc_id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  external_id     VARCHAR(128) UNIQUE,         -- e.g., file path / policy id
  title           VARCHAR(256) NOT NULL,
  doc_type        ENUM('SOP','POLICY','CHECKLIST','FAQ','GUIDELINE') NOT NULL,
  version         VARCHAR(32) DEFAULT '1.0',
  source_uri      VARCHAR(512),                -- where it came from
  created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE rag_chunk (
  chunk_id        BIGINT PRIMARY KEY AUTO_INCREMENT,
  doc_id          BIGINT NOT NULL,
  chunk_index     INT NOT NULL,
  heading         VARCHAR(256),
  text            MEDIUMTEXT NOT NULL,
  UNIQUE KEY uq_chunk (doc_id, chunk_index),
  FULLTEXT KEY ft_chunk_text (text),           -- for fallback keyword search
  CONSTRAINT fk_chunk_doc FOREIGN KEY (doc_id) REFERENCES rag_document(doc_id)
) ENGINE=InnoDB;

-- Embeddings for each chunk
-- MySQL 8 has no vector type: store as BLOB (float32 little-endian) or JSON text.
-- We'll use BLOB + dimension + model metadata; app does cosine search.
CREATE TABLE rag_embedding (
  chunk_id        BIGINT PRIMARY KEY,
  model           VARCHAR(64) NOT NULL,        -- e.g., "text-embedding-3-large"
  dimension       INT NOT NULL,
  vector_blob     LONGBLOB NOT NULL,           -- float32[] (little-endian), length = 4*dimension
  l2_norm         DOUBLE NULL,                 -- optional precomputed norm
  updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT fk_emb_chunk FOREIGN KEY (chunk_id) REFERENCES rag_chunk(chunk_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Helpful view for bed availability
CREATE OR REPLACE VIEW v_bed_availability AS
SELECT
  b.bed_id, u.code AS unit_code, r.room_number, b.bed_label, b.status,
  r.isolation_type, r.has_negative_pressure, b.last_cleaned_at
FROM bed b
JOIN room r ON r.room_id = b.room_id
JOIN unit u ON u.unit_id = r.unit_id
WHERE b.status IN ('AVAILABLE','CLEANING');


