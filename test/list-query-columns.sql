SELECT *
FROM patients
LEFT JOIN donorships ON patients.id = donorships.patient_id
LEFT JOIN (
    SELECT
        patient_id,
        MAX(icd_code)
    FROM diagnoses
    GROUP BY patient_id
) diagnoses ON diagnoses.patient_id = patients.id;
