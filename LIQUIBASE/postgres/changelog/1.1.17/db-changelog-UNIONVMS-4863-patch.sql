UPDATE activity.activity_fishing_activity a
SET
	cfr = (
		SELECT vessel_identifier_id
		FROM activity.activity_vessel_identifier i
			JOIN activity.activity_vessel_transport_means t ON i.vessel_transport_mean_id=t.id
			JOIN activity.activity_fa_report_document d ON t.fa_report_document_id=d.id
			JOIN activity.activity_fishing_activity f ON d.id=f.fa_report_document_id
		WHERE t.fa_report_document_id = a.fa_report_document_id AND i.vessel_identifier_scheme_id = 'CFR'
		limit 1
	),
	uvi = (
		SELECT vessel_identifier_id
        FROM activity.activity_vessel_identifier i
            JOIN activity.activity_vessel_transport_means t ON i.vessel_transport_mean_id=t.id
            JOIN activity.activity_fa_report_document d ON t.fa_report_document_id=d.id
            JOIN activity.activity_fishing_activity f ON d.id=f.fa_report_document_id
        WHERE t.fa_report_document_id = a.fa_report_document_id AND i.vessel_identifier_scheme_id = 'UVI'
        limit 1
	),
	ircs = (
		SELECT vessel_identifier_id
        FROM activity.activity_vessel_identifier i
            JOIN activity.activity_vessel_transport_means t ON i.vessel_transport_mean_id=t.id
            JOIN activity.activity_fa_report_document d ON t.fa_report_document_id=d.id
            JOIN activity.activity_fishing_activity f ON d.id=f.fa_report_document_id
        WHERE t.fa_report_document_id = a.fa_report_document_id AND i.vessel_identifier_scheme_id = 'IRCS'
        limit 1
	),
	iccat = (
		SELECT vessel_identifier_id
        FROM activity.activity_vessel_identifier i
           JOIN activity.activity_vessel_transport_means t ON i.vessel_transport_mean_id=t.id
           JOIN activity.activity_fa_report_document d ON t.fa_report_document_id=d.id
           JOIN activity.activity_fishing_activity f ON d.id=f.fa_report_document_id
       WHERE t.fa_report_document_id = a.fa_report_document_id AND i.vessel_identifier_scheme_id = 'ICCAT'
       limit 1
	),
	gfcm = (
		SELECT vessel_identifier_id
        FROM activity.activity_vessel_identifier i
             JOIN activity.activity_vessel_transport_means t ON i.vessel_transport_mean_id=t.id
             JOIN activity.activity_fa_report_document d ON t.fa_report_document_id=d.id
             JOIN activity.activity_fishing_activity f ON d.id=f.fa_report_document_id
         WHERE t.fa_report_document_id = a.fa_report_document_id AND i.vessel_identifier_scheme_id = 'GFCM'
         limit 1
	),
	ext_mark = (
		SELECT vessel_identifier_id
        FROM activity.activity_vessel_identifier i
             JOIN activity.activity_vessel_transport_means t ON i.vessel_transport_mean_id=t.id
             JOIN activity.activity_fa_report_document d ON t.fa_report_document_id=d.id
             JOIN activity.activity_fishing_activity f ON d.id=f.fa_report_document_id
         WHERE t.fa_report_document_id = a.fa_report_document_id AND i.vessel_identifier_scheme_id = 'EXT_MARK'
         limit 1
	);