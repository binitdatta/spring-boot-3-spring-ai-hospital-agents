# curl -X POST localhost:8025/api/rag/ingest

curl -X POST localhost:8025/api/rag/search \
-H 'Content-Type: application/json' \
-d '{"q":"What is STAT EVS target time?"}'

curl -X POST localhost:8025/api/chat/ask \
-H 'Content-Type: application/json' \
-d '{"input":"Create a safe plan to prepare room for new admission on MedSurg."}'

curl -X POST localhost:8025/api/chat/ask \
-H 'Content-Type: application/json' \
-d '{"input":"Create a safe plan to prepare room for new admission on MedSurg."}'

Great question 👍 — your schema is rich enough to support a **wide range of operational and management queries**. Let’s break it down by **functional area**, so you can see what kinds of questions hospital managers and operators could realistically ask (and answer) with this schema.

---

## 1. **Capacity Management (Beds, Rooms, Units)**

* *How many beds are currently available vs occupied by unit/department?*
* *Which rooms are under isolation, and how many negative-pressure rooms are free?*
* *What is the average time taken to clean and release a bed after discharge (last\_cleaned\_at vs discharge\_dt)?*
* *Which units are running at >90% occupancy right now?*
* *How many beds are out of service and for how long?*

---

## 2. **Patient Flow (Encounters)**

* *How many patients are currently admitted, and how is that distributed across units?*
* *What’s the median ED boarding time (ED admit\_dt → time assigned to inpatient bed)?*
* *Which encounters have been registered but not yet admitted to a bed?*
* *How many discharges occurred today, and from which units?*
* *What’s the average length of stay by unit/acuity level?*

Great — here are **clear, plain-English question prompts** you can feed into your hospital ops SQL-QA system for those use cases. They’re phrased like what a charge nurse, operations lead, or hospitalist might actually ask:

---

### 📌 Patient Census & Admissions

**Prompt:**
“How many patients are currently admitted in the hospital right now, and how are they distributed across each unit?”

---

### 📌 ED Boarding Time

**Prompt:**
“What is the median boarding time in the Emergency Department, from when a patient is admitted in the ED until they are assigned to an inpatient bed?”

---

### 📌 Registered but Not Yet Admitted

**Prompt:**
“Which patient encounters have been registered but are still waiting for a bed assignment and not yet admitted?”

---

### 📌 Discharges

**Prompt:**
“How many patient discharges have occurred today, and from which units did they happen?”

---

### 📌 Length of Stay

**Prompt:**
“What is the average length of stay for patients, broken down by unit and by acuity level?”

---

👉 These are already in **natural English**, not SQL-ish. That way your planner/LLM layer can translate them into SQL with the right joins.

Would you like me to also provide the **expected SQL patterns** for each prompt (so you can test your pipeline end-to-end like we did for beds)?

---

## 3. **EVS (Environmental Services) Operations**

* *How many EVS tickets are open (queued or in-progress) by unit?*
* *What percentage of EVS STAT tickets were completed within the SLA (10 minutes)?*
* *Which rooms are waiting longest for cleaning?*
* *Who (staff) created the most EVS tickets in the last 24 hours?*
* *What’s the average turnaround time from discharge → bed marked “AVAILABLE”?*

Got it ✅ — here are **plain English prompts** you could give your AI UI (or curl form) for those **EVS (Environmental Services)** use cases.

---

### EVS English Questions

1. **How many EVS tickets are currently open in each unit, broken down by queued or in-progress status?**

2. **What percentage of EVS STAT tickets were completed within the 10-minute SLA?**

3. **Which rooms have been waiting the longest for cleaning service?**

4. **Which staff member created the highest number of EVS tickets in the last 24 hours?**

5. **On average, how long does it take from a patient being discharged until the bed is cleaned and marked as available again?**

---

These are phrased conversationally (like a hospital operations team member might ask), but still map cleanly to your schema-driven SQL.

👉 Do you also want me to generate **sample SQL skeletons** for each so you can preload them into your planner (like you did for beds/encounters)?

---

## 4. **Transport Jobs**

* *How many patient transport requests are pending vs in-progress?*
* *What is the average time between REQUESTED → DONE for transport jobs?*
* *Which units generate the most transport requests?*
* *Which transport staff member handled the most transfers last week?*
* *What percentage of jobs are cancelled before completion?*

Perfect 👍 — here’s how we can phrase those **Patient Transport use cases** into **plain English questions** that will work well with your RAG + SQL planner:

---

### Patient Transport English Questions

1. **How many patient transport requests are currently pending compared to those that are in progress?**

2. **On average, how much time does it take to complete a transport job, from the time it is requested until it is marked as done?**

3. **Which hospital units generate the highest number of patient transport requests?**

4. **Which transport staff member completed the most patient transfers during the last week?**

5. **What percentage of patient transport jobs were cancelled before they were completed?**

---

These follow the same **hospital-ops conversational style** as your earlier EVS and Bed questions, while still mapping cleanly to SQL.

👉 Do you also want me to generate **sample SQL queries** for these transport prompts (the way we did for beds and EVS) so you can validate them against your schema?

---

## 5. **Staffing**

* *How many active staff by role (e.g., Charge Nurse, EVS, Transport)?*
* *Which staff members are mapped to multiple roles?*
* *How many staff are inactive (offboarding, leave)?*
* *Do we have enough EVS staff on active roster to meet the average cleaning load per shift?*

Got it ✅ — here are those **Staffing use cases** rewritten into clear, natural **English questions** you can feed into your AI + SQL pipeline:

---

### Staffing English Questions

1. **How many staff members are currently active, broken down by role (for example, Charge Nurse, EVS, Transport)?**

2. **Which staff members are assigned to more than one role?**

3. **How many staff members are currently inactive, either offboarded or on leave?**

4. **Do we have enough active EVS staff on the roster to handle the average cleaning workload per shift?**

---

These keep the **human-friendly conversational tone** while still being precise enough for SQL planning.

👉 Do you want me to also design **example SQL queries** for each of these (assuming your `staff`, `staff_role_map`, `evs_ticket`, etc. tables exist), so you can test them directly against your schema?


---

## 6. **Inventory & Supplies**

* *Which units are below par level for critical items (e.g., N95 masks, IV kits)?*
* *What’s the top 10 most under-stocked items across the hospital?*
* *Which unit has the highest stockout frequency?*
* *How close is current on-hand to par level → predict upcoming orders needed?*

Perfect — here’s your **Inventory Management set** expressed as **plain English prompts** you can feed into your AI-to-SQL layer:

---

### Inventory English Questions

1. **Which hospital units are currently below par level for critical items like N95 masks or IV kits?**

2. **What are the top 10 items that are most under-stocked across the hospital right now?**

3. **Which unit has the highest frequency of stockouts?**

4. **How close is our current on-hand inventory to each unit’s par level, and which items will likely need to be reordered soon?**

---

👉 Would you like me to also **sketch SQL query templates** for each of these, assuming your schema has `inventory`, `unit`, and `item` tables with columns like `on_hand_qty`, `par_level`, `item_name`, `unit_id`? That way your RAG + Tools layer will have working examples to ground against.


---

## 7. **RAG Knowledge Base**

* *What SOPs, policies, and checklists are active, and which versions are in use?*
* *Which documents have the most chunks (largest content)?*
* *Which embeddings were last updated, and with which model?*
* *Do we have EVS-related policies embedded (chunk search by keyword)?*
* *Show me all discharge-related policies/checklists to build a discharge planning assistant.*

Here’s how to phrase your **Knowledge / Policies & SOPs domain** questions in **plain English prompts** that your RAG + SQL + embeddings pipeline can handle:

---

### Knowledge Management English Questions

1. **What standard operating procedures, policies, and checklists are currently active, and which versions are being used in the hospital?**

2. **Which documents are the largest by content size (i.e., have the most chunks stored in the database)?**

3. **Which embeddings were updated most recently, and with which model were they created?**

4. **Do we have any policies or SOPs related to EVS (environmental services) in the knowledge base?**

5. **Show me all discharge-related policies and checklists that we could use to build a discharge planning assistant.**

---

👉 If you’d like, I can also generate **schema-aware SQL query templates** for each of these — assuming you’ve got tables like `document`, `document_chunk`, `embedding`, with metadata such as `doc_type`, `version`, `keywords`, `last_updated`, `embedding_model`. Would you like me to draft those SQL templates next?


---

## 8. **Cross-Functional (Integrated Ops Metrics)**

* *How long (average) from discharge\_dt → EVS ticket creation → bed available?*
* *How many admitted patients are waiting for bed assignment vs EVS cleaning bottleneck?*
* *For each encounter, what is the chain of events: admit\_dt → bed assignment → transport job → EVS cleaning after discharge?*
* *How many transport delays directly contributed to ED boarding times >4 hours?*
* *Which units have both inventory shortages **and** high occupancy (risk for care disruption)?*

Absolutely — here are clean, natural **English prompts** (two variants each) you can drop into your UI for those cross-workflow questions:

---

### Discharge → EVS → Bed Availability

1. **Simple:**
   **On average, how long is it from a patient’s discharge until the EVS ticket is created, and then until the bed is marked available?**
   **Detailed:**
   **What is the average duration for (a) discharge time → EVS ticket creation and (b) EVS ticket creation → bed status changing to AVAILABLE?**

### Bed Assignment vs. EVS Bottleneck

2. **Simple:**
   **How many admitted patients are waiting for a bed assignment versus those waiting for EVS cleaning?**
   **Detailed:**
   **Among current inpatients, how many are (a) admitted without a bed assigned and (b) blocked by rooms/beds still in CLEANING or awaiting EVS?**

### End-to-End Chain per Encounter

3. **Simple:**
   **For each encounter, show the timeline: admit time → bed assignment time → transport job completion → EVS cleaning after discharge.**
   **Detailed:**
   **List each encounter’s event chain with timestamps: admit\_dt, bed\_assigned\_at, transport (requested/started/done), discharge\_dt, EVS (created/started/done), and when the bed returned to AVAILABLE.**

### Transport Delays and ED Boarding

4. **Simple:**
   **How many transport delays contributed to ED boarding times longer than 4 hours?**
   **Detailed:**
   **Count ED encounters where boarding time (admit in ED → bed assigned) exceeded 4 hours and where any associated transport job had a delay (requested → done) overlapping that window.**

### Inventory Shortages + High Occupancy (Risk Signal)

5. **Simple:**
   **Which units have both high occupancy and items below par level?**
   **Detailed:**
   **Identify units with occupancy above 90% that also have critical inventory below par (e.g., N95s, IV kits), and rank them by risk.**

---

## 9. **Strategic/Executive Reporting**

* *Hospital-wide bed occupancy % by day, week, month.*
* *SLA compliance rates for EVS and transport jobs (e.g., 90% within target).*
* *Staff utilization: avg # tickets/jobs handled per role per shift.*
* *Throughput KPIs: ED → admission lag time, discharge → bed turnover time.*
* *Policy coverage: how many RAG documents exist for each department, and are they embedded for AI-driven support?*

Here’s a polished set of **English-only prompt variants** for those operational KPI use cases — phrased so a hospital ops user can ask them naturally in your system:

---

### 📊 Bed Occupancy

1. **Simple:**
   **What is the hospital-wide bed occupancy percentage by day, week, and month?**
   **Detailed:**
   **Show trends of bed occupancy rates (occupied ÷ total) aggregated daily, weekly, and monthly across the hospital.**

---

### ✅ SLA Compliance (EVS + Transport)

2. **Simple:**
   **What percentage of EVS and transport jobs were completed within SLA?**
   **Detailed:**
   **Give SLA compliance rates (e.g., % of tickets done within 10 minutes for EVS, within 15 minutes for transport) and compare them against target thresholds.**

---

### 👩‍⚕️ Staff Utilization

3. **Simple:**
   **On average, how many EVS tickets or transport jobs does each role handle per shift?**
   **Detailed:**
   **Break down staff utilization by role (Charge Nurse, EVS, Transport, etc.) — average # of tasks per shift and identify under/over-utilization.**

---

### ⚡ Throughput KPIs

4. **Simple:**
   **What is the average time from ED admission to inpatient bed assignment, and from discharge to bed turnover?**
   **Detailed:**
   **Show throughput metrics: (a) lag between ED admit\_dt → bed\_assigned\_at, and (b) discharge\_dt → bed available again (after EVS cleaning).**

---

### 📚 Policy & RAG Coverage

5. **Simple:**
   **How many policies, SOPs, or checklists exist for each department, and are they embedded in the AI knowledge base?**
   **Detailed:**
   **List RAG documents per department (e.g., ED, ICU, EVS, Transport), showing total docs, active versions, and which are embedded for AI-driven support.**

---

👉 Do you want me to also **map each of these prompts to SQL starting points** (like we did for bed availability), so you’ll have both the plain-English question **and** the generated SQL patterns side by side?


---

✅ This schema already covers **the “flow of care” from patient arrival → bed assignment → treatment → discharge → cleaning/transport/inventory → knowledge support**.

If you’d like, I can **write out example SQL queries** for the top 5 questions (like *“Which units are at >90% occupancy?”* or *“Which EVS STAT tickets missed SLA?”*) so you have ready-to-run analytics queries for hospital managers.

Would you like me to generate those SQL examples next?

Sweet — since your `/api/sqlqa/ask` endpoint takes a natural-language **question** and (optionally) a `limit`, here are ready-to-run `curl` calls for each capacity use-case. They’re phrased to steer the SQL planner toward clean MySQL 8 `SELECT` queries over your schema.

> Replace `localhost:8088` if you run on a different host/port.

---

### 1) Beds available vs occupied by **unit** and **department**

```bash
curl -sS -X POST http://localhost:8025/api/sqlqa/ask \
  -H 'Content-Type: application/json' \
  -d '{
    "question": "Show beds available vs occupied by UNIT and DEPARTMENT. Use bed -> room -> unit -> department. Return department.code, unit.code, total_beds, available_beds (status=AVAILABLE), occupied_beds (status=OCCUPIED). Order by department.code, unit.code.",
    "limit": 500
  }'
  
  {"sql":"SELECT d.code AS department_code, u.code AS unit_code, COUNT(b.bed_id) AS total_beds, SUM(CASE WHEN b.status = 'AVAILABLE' THEN 1 ELSE 0 END) AS available_beds, SUM(CASE WHEN b.status = 'OCCUPIED' THEN 1 ELSE 0 END) AS occupied_beds FROM department d JOIN unit u ON d.department_id = u.department_id JOIN room r ON u.unit_id = r.unit_id JOIN bed b ON r.room_id = b.room_id GROUP BY d.code, u.code ORDER BY d.code, u.code LIMIT 500","params":{},"rowCount":3,"answer":"Here are the bed metrics by unit and department:\n\n| Department Code | Unit Code | Total Beds | Available Beds | Occupied Beds |\n|------------------|-----------|------------|----------------|----------------|\n| CCU              | CCU-1    | 1          | 1              | 0              |\n| ED               | ED-1     | 1          | 0              | 1              |\n| MS               | MS-1     | 3          | 2              | 0              |\n\n**Key Metrics:**\n- Total Beds: 5\n- Available Beds: 3\n- Occupied Beds: 1\n\n**Reasoning:**\n- The data shows the distribution of beds across departments and units, indicating availability and occupancy status. The CCU unit has all its beds available, while the ED unit is fully occupied. The MS unit has a mix of available and occupied beds. \n\nIf you need further analysis or details, please specify!"}
```

**Variant (department roll-up only):**

```bash
curl -sS -X POST http://localhost:8025/api/sqlqa/ask \
  -H 'Content-Type: application/json' \
  -d '{
    "question": "By DEPARTMENT only: total beds, available (status=AVAILABLE), occupied (status=OCCUPIED). Use joins bed->room->unit->department. Group by department.code. Order by department.code.",
    "limit": 200
  }'
  
 {"sql":"SELECT d.code AS department_code, COUNT(b.bed_id) AS total_beds, SUM(b.status = 'AVAILABLE') AS available_beds, SUM(b.status = 'OCCUPIED') AS occupied_beds FROM department d JOIN unit u ON d.department_id = u.department_id JOIN room r ON u.unit_id = r.unit_id JOIN bed b ON r.room_id = b.room_id GROUP BY d.code ORDER BY d.code LIMIT 200","params":{},"rowCount":3,"answer":"Here are the total, available, and occupied beds by department:\n\n| Department Code | Total Beds | Available Beds | Occupied Beds |\n|------------------|------------|----------------|----------------|\n| CCU              | 1          | 1              | 0              |\n| ED               | 1          | 0              | 1              |\n| MS               | 3          | 2              | 0              |\n\n**Key Metrics:**\n- Total Beds: 5\n- Available Beds: 3\n- Occupied Beds: 1\n\n**Reasoning:** The data shows the distribution of beds across departments, indicating where capacity is available and where beds are occupied. If further analysis is needed, consider looking into specific units or room utilization rates."}
```

---

### 2) Which rooms are under **isolation**, and how many **negative-pressure** rooms are free?

```bash
curl -sS -X POST http://localhost:8025/api/sqlqa/ask \
  -H 'Content-Type: application/json' \
  -d '{
    "question": "List rooms under isolation (room.isolation_type in (CONTACT,DROPLET,AIRBORNE)) with unit.code and room_number. Also compute a hospital-wide count of negative-pressure rooms that currently have at least one AVAILABLE bed (room.has_negative_pressure = true and bed.status=AVAILABLE). Return two result sets if needed, or one table with flags and counts.",
    "limit": 500
  }'
  
 {"sql":"SELECT r.room_number, u.code AS unit_code, (SELECT COUNT(*) FROM room r2 JOIN bed b ON r2.room_id = b.room_id WHERE r2.has_negative_pressure = TRUE AND b.status = 'AVAILABLE') AS negative_pressure_count FROM room r JOIN unit u ON r.unit_id = u.unit_id WHERE r.isolation_type IN ('CONTACT', 'DROPLET', 'AIRBORNE') LIMIT 500","params":{},"rowCount":2,"answer":"**Result Set: Rooms Under Isolation**\n\n| room_number | unit_code | negative_pressure_count |\n|-------------|-----------|-------------------------|\n| 202         | MS-1      | 1                       |\n| 12          | CCU-1     | 1                       |\n\n**Key Metrics:**\n- Total rooms under isolation: 2\n- Count of negative-pressure rooms with at least one available bed: 1\n\n**Reasoning:**\nThe query successfully retrieved rooms under isolation with their corresponding unit codes. Additionally, it computed the count of negative-pressure rooms that have at least one available bed, which is reflected in the result set."}
```

**Variant (just the free negative-pressure count by unit):**

```bash
curl -sS -X POST http://localhost:8025/api/sqlqa/ask \
  -H 'Content-Type: application/json' \
  -d '{
    "question": "For negative-pressure rooms (room.has_negative_pressure=TRUE), show by unit.code the count of AVAILABLE beds (bed.status=AVAILABLE). Order by unit.code.",
    "limit": 200
  }'
  
{"sql":"SELECT u.code AS unit_code, COUNT(b.bed_id) AS available_beds_count FROM room r JOIN bed b ON r.room_id = b.room_id JOIN unit u ON u.unit_id = r.unit_id WHERE r.has_negative_pressure = TRUE AND b.status = 'AVAILABLE' GROUP BY u.code ORDER BY u.code LIMIT 200","params":{},"rowCount":1,"answer":"The count of AVAILABLE beds in negative-pressure rooms by unit code is as follows:\n\n- **Unit Code:** CCU-1\n  - **Available Beds Count:** 1\n\nThis indicates that there is 1 available bed in the negative-pressure room located in the CCU-1 unit. If you need further details or additional units, please specify."}

```

---

### 3) Average time to **clean and release** a bed after **discharge**

> ⚠️ Your current schema doesn’t track a “status change timestamp” for beds, only `bed.last_cleaned_at`. We’ll approximate **clean-release lag** as `TIMESTAMPDIFF(MINUTE, encounter.discharge_dt, bed.last_cleaned_at)` for encounters that have a `current_bed_id` (the bed the patient occupied at discharge). This is a reasonable proxy but not perfect.

```bash
curl -sS -X POST http://localhost:8025/api/sqlqa/ask \
  -H 'Content-Type: application/json' \
  -d '{
    "question": "Compute average minutes from patient DISCHARGE (encounter.discharge_dt) to bed last_cleaned_at for today. Join encounter(current_bed_id) -> bed -> room -> unit. Filter discharge_dt IS NOT NULL and DATE(discharge_dt)=CURDATE(). Return unit.code, AVG(TIMESTAMPDIFF(MINUTE, encounter.discharge_dt, bed.last_cleaned_at)) as avg_clean_release_min, COUNT(*) as cases. Exclude rows where last_cleaned_at IS NULL.",
    "limit": 200
  }'
  
  {"sql":"SELECT u.code, AVG(TIMESTAMPDIFF(MINUTE, e.discharge_dt, b.last_cleaned_at)) AS avg_clean_release_min, COUNT(*) AS cases FROM encounter e JOIN bed b ON e.current_bed_id = b.bed_id JOIN room r ON b.room_id = r.room_id JOIN unit u ON r.unit_id = u.unit_id WHERE e.discharge_dt IS NOT NULL AND DATE(e.discharge_dt) = CURDATE() AND b.last_cleaned_at IS NOT NULL GROUP BY u.code LIMIT 200","params":{},"rowCount":0,"answer":"The query returned no results. This indicates that there were no patient discharges today with a corresponding bed cleaning record.\n\nNext steps:\n1. Verify if there were any patient discharges today.\n2. Check if the beds were cleaned today.\n3. Review the data for any discrepancies or missing records."}
  
```

**(If you want by department too):**

```bash
curl -sS -X POST http://localhost:8025/api/sqlqa/ask \
  -H 'Content-Type: application/json' \
  -d '{
    "question": "By DEPARTMENT then UNIT: average minutes from encounter.discharge_dt to bed.last_cleaned_at for discharges today. Return department.code, unit.code, avg_minutes, n. Only include rows where both timestamps exist.",
    "limit": 200
  }'
  
  {"sql":"SELECT d.code AS department_code, u.code AS unit_code, AVG(TIMESTAMPDIFF(MINUTE, e.discharge_dt, b.last_cleaned_at)) AS avg_minutes, COUNT(*) AS n FROM encounter e JOIN bed b ON e.current_bed_id = b.bed_id JOIN room r ON b.room_id = r.room_id JOIN unit u ON r.unit_id = u.unit_id JOIN department d ON u.department_id = d.department_id WHERE DATE(e.discharge_dt) = CURDATE() AND e.discharge_dt IS NOT NULL AND b.last_cleaned_at IS NOT NULL GROUP BY d.code, u.code LIMIT 200","params":{},"rowCount":0,"answer":"No data was returned for the specified query. This indicates that there were no discharges today with both `discharge_dt` and `last_cleaned_at` timestamps available.\n\nNext steps:\n1. Verify if there were any discharges today.\n2. Check if the cleaning records for beds are being properly logged."}
```

---

### 4) Units currently running at **> 90% occupancy**

> Occupancy = `occupied_beds / total_beds`. Here, “occupied” = `bed.status='OCCUPIED'`.

```bash
curl -sS -X POST http://localhost:8025/api/sqlqa/ask \
  -H 'Content-Type: application/json' \
  -d '{
    "question": "Find units with occupancy > 0.9. Use bed -> room -> unit. Compute total_beds per unit, occupied_beds where bed.status=OCCUPIED, occupancy = occupied_beds/total_beds. Return unit.code, total_beds, occupied_beds, ROUND(occupied_beds/total_beds,3) AS occupancy. Filter occupancy > 0.9. Order by occupancy DESC.",
    "limit": 200
  }'
  
  {"sql":"SELECT u.code, COUNT(b.bed_id) AS total_beds, SUM(CASE WHEN b.status = 'OCCUPIED' THEN 1 ELSE 0 END) AS occupied_beds, ROUND(SUM(CASE WHEN b.status = 'OCCUPIED' THEN 1 ELSE 0 END) / COUNT(b.bed_id), 3) AS occupancy FROM unit u JOIN room r ON u.unit_id = r.unit_id JOIN bed b ON r.room_id = b.room_id GROUP BY u.code HAVING occupancy > 0.9 ORDER BY occupancy DESC LIMIT 200","params":{},"rowCount":1,"answer":"The query returned the following unit with occupancy greater than 0.9:\n\n- **Unit Code**: ED-1\n- **Total Beds**: 1\n- **Occupied Beds**: 1\n- **Occupancy**: 1.000\n\nThis indicates that the unit ED-1 is fully occupied, with all available beds occupied. If you need further analysis or additional metrics, please specify."}
```

**Variant (show department roll-up too):**

```bash
curl -sS -X POST http://localhost:8025/api/sqlqa/ask \
  -H 'Content-Type: application/json' \
  -d '{
    "question": "For each department.code and unit.code, compute occupancy = occupied/total (status=OCCUPIED vs all). Return those with occupancy > 0.9.",
    "limit": 200
  }'
  
  {"sql":"SELECT d.code AS department_code, u.code AS unit_code, SUM(CASE WHEN b.status = 'OCCUPIED' THEN 1 ELSE 0 END) / COUNT(b.bed_id) AS occupancy FROM department d JOIN unit u ON d.department_id = u.department_id JOIN room r ON u.unit_id = r.unit_id JOIN bed b ON r.room_id = b.room_id GROUP BY d.code, u.code HAVING occupancy > 0.9 LIMIT 200","params":{},"rowCount":1,"answer":"The query returned the following result:\n\n- **Department Code:** ED\n- **Unit Code:** ED-1\n- **Occupancy:** 1.0000 (100%)\n\nThis indicates that the ED-1 unit in the ED department is fully occupied. \n\n**Key Metrics:**\n- **Occupancy Rate > 0.9:** Yes, for ED-1.\n\nIf you need further analysis or additional metrics, please specify."}
```

---

### 5) How many beds are **out of service** and **for how long**?

> ⚠️ The schema lacks an explicit `status_changed_at` for beds. As a proxy, we can report **OOS counts** and, if you wish, show days since `last_cleaned_at` (not perfect but often correlated). I’ll give both variants.

**(Counts by unit):**

```bash
curl -sS -X POST http://localhost:8025/api/sqlqa/ask \
  -H 'Content-Type: application/json' \
  -d '{
    "question": "Show count of OUT_OF_SERVICE beds by unit.code. Use bed -> room -> unit. Return unit.code, COUNT(*) as oos_beds. Filter bed.status=OUT_OF_SERVICE. Order by oos_beds DESC.",
    "limit": 200
  }'
  
  {
  "sql": "SELECT u.code, COUNT(*) AS oos_beds FROM bed b JOIN room r ON r.room_id = b.room_id JOIN unit u ON u.unit_id = r.unit_id WHERE b.status = 'OUT_OF_SERVICE' GROUP BY u.code ORDER BY oos_beds DESC LIMIT 200",
  "params": {},
  "rowCount": 0,
  "answer": "The query returned no results, indicating that there are currently no OUT_OF_SERVICE beds in any unit. \n\nNext steps could include:\n1. Verifying the status of beds to ensure they are correctly categorized.\n2. Checking for any recent changes in bed status or maintenance schedules."
}

```

**(Proxy “how long” using last\_cleaned\_at):**

```bash
curl -sS -X POST http://localhost:8025/api/sqlqa/ask \
  -H 'Content-Type: application/json' \
  -d '{
    "question": "List OUT_OF_SERVICE beds with unit.code, room_number, bed_label, and days_since_last_cleaned = TIMESTAMPDIFF(DAY, bed.last_cleaned_at, NOW()). Use bed -> room -> unit. Order by days_since_last_cleaned DESC NULLS LAST.",
    "limit": 500
  }'
  
  {"sql":"SELECT u.code AS unit_code, r.room_number, b.bed_label, TIMESTAMPDIFF(DAY, b.last_cleaned_at, NOW()) AS days_since_last_cleaned FROM bed b JOIN room r ON r.room_id = b.room_id JOIN unit u ON u.unit_id = r.unit_id WHERE b.status = 'OUT_OF_SERVICE' ORDER BY days_since_last_cleaned DESC LIMIT 500","params":{},"rowCount":0,"answer":"There are currently no OUT_OF_SERVICE beds in the database. \n\nNext steps could include reviewing the status of beds or checking for any recent updates in the bed management system."}
```

---

## Pro tips for reliable results

* If the planner ever returns vague SQL, **nudge with column names** in your question (e.g., “use bed.status”, “join bed->room->unit->department”).
* You can see the generated SQL and row count in the JSON response (`sql`, `rowCount`, and the synthesized **answer**).
* For performance on larger datasets, you might add specific filters (“for today”, “this week”) or ask for **roll-ups** only.

If you want, I can also give you **golden SQL** for each query (so you can compare the planner’s output vs. a hand-written baseline) and wire a few as **prebuilt endpoints** (e.g., `/api/ops/occupancy`) that your agents can call directly.


Absolutely—you can drive the same tools from your English chat UI through your **orchestrator `ChatClient`**. The LLM will translate the user’s sentence into the right tool calls:

* `getStaffingSnapshot` → see current vs target
* `planStaffTransfers` → produce a balanced plan
* `applyTransfer` → write the assignments (guarded by your policy / X-Role)

Below are **ready-to-use English prompts** you can surface in your chat UI. They include all info the model needs (shift, window, units, role, intent).

---

### 1) See the situation (no changes)

> “For the **DAY** shift **today 2:00pm–10:00pm**, show **current vs target staffing by unit and role**. Highlight **overstaffed** and **understaffed** units, and quantify the gap.”

### 2) Ask for a plan (do not apply yet)

> “For the **DAY** shift **today 2:00pm–10:00pm**, **propose transfers** to fix understaffing. Include which staff to move (ID), from which unit → to which unit, role, and a short note. **Do not apply**—just return a plan that requires approval.”

### 3) Approve & apply the whole plan

> “**Approve and apply the proposed transfer plan** for the **DAY** shift **2:00pm–10:00pm**. If a staff member already has an assignment in the source unit, **split it** and create the destination assignment. Use note: ‘Balance ED understaffing for DAY shift.’”

### 4) Apply a specific targeted move

> “For the **DAY** shift **2:00pm–10:00pm**, **move staff ID 2** with **role ID 3** **from unit MS-1 (2)** **to unit ED-1 (1)**. **Split the existing assignment if needed**. Note: ‘Balance ED understaffing for DAY shift.’”

### 5) One-shot (plan + apply in one message)

> “**Move one Transport** from **Med-Surg North (MS-1)** to **ED-1** today **2:00pm–10:00pm**. Prefer **staff ID 2** if available; otherwise pick the **lowest-load Transport** from MS-1. **Split current assignment if needed.** Note: ‘Balance ED understaffing for DAY shift.’ **Apply the change.**”

---

## Backend tips for your chat UI

* When the user intends to **write**, set your request header (or tool context) with the authorization your **PolicyInterceptor** expects, e.g.:

  ```
  X-Role: OpsManager
  ```
* Keep your **system prompt** for the orchestrator strict, e.g.:

   * “Use tools for data/actions. Never write without approval. If user says ‘apply/approve/commit’, it counts as approval.”
* For safest UX, use a **two-step flow**: (1) user asks for a plan → bot shows JSON plan, (2) user says “approve/apply” → bot calls `applyTransfer`.

If you want, I can draft the small controller method that forwards chat messages to your **orchestrator `ChatClient`** and returns either the plan JSON or the `ApplyResult`—just say the word.


``` 
ids=$(docker ps -aq); [ -n "$ids" ] && docker stop $ids && docker rm -v $ids
imgs=$(docker images -aq); [ -n "$imgs" ] && docker rmi -f $imgs
vols=$(docker volume ls -q); [ -n "$vols" ] && docker volume rm $vols
nets=$(docker network ls --filter type=custom -q); [ -n "$nets" ] && docker network rm $nets
docker builder prune -af
docker system prune -af --volumes

```

