# Description: This script is used to import glycemia data per 15 minutes after downsampling
#into redis cache.
## Glycemia value TimeSeries
bin/riot db-import "SELECT UNIX_TIMESTAMP(rounded_time)*1000 AS record_ts,\
 patient_id, (SELECT glycemia FROM glycemia WHERE CONCAT(DATE_FORMAT(record_time\
 , '%Y-%m-%d %H:'), LPAD(FLOOR(MINUTE(record_time) / 15) * 15, 2, '0'), ':00') =\
  rt.rounded_time LIMIT 1) AS glycemia FROM (SELECT DISTINCT CONCAT(DATE_FORMAT\
  (record_time, '%Y-%m-%d %H:'), LPAD(FLOOR(MINUTE(record_time) / 15) * 15, 2, '0')\
  , ':00') AS rounded_time, patient_id FROM glycemia) rt ORDER BY rounded_time;" \
  --url {JDBC_URL} --username **** --password ***************\
   --threads 3 ts.add --keyspace cache:glycemia --timestamp record_ts --key patient_id \
   --value glycemia

bin/riot db-import "SELECT CONCAT(patient_id,':min') as patient_id,unix_timestamp(record_date)*1000 as tim,min_glycemia FROM wegs.daily_glycemia_summary;" \
  --url {JDBC_URL} --username **** --password ***************\
   --threads 3 ts.add --keyspace cache:glycemia --timestamp tim --key patient_id \
   --value min_glycemia

bin/riot db-import "SELECT CONCAT(patient_id,':max') as patient_id,unix_timestamp(record_date)*1000 as tim,max_glycemia FROM wegs.daily_glycemia_summary;" \
  --url {JDBC_URL} --username **** --password ***************\
   --threads 3 ts.add --keyspace cache:glycemia --timestamp tim --key patient_id \
   --value max_glycemia

bin/riot db-import "SELECT CONCAT(patient_id,':avg') as patient_id,unix_timestamp(record_date)*1000 as tim,avg_glycemia FROM wegs.daily_glycemia_summary;" \
  --url {JDBC_URL} --username **** --password ***************\
   --threads 3 ts.add --keyspace cache:glycemia --timestamp tim --key patient_id \
   --value avg_glycemia

bin/riot db-import "SELECT CONCAT(patient_id,':eu') as patient_id,unix_timestamp(record_date)*1000 as tim,eu_percent FROM wegs.daily_glycemia_summary;" \
  --url {JDBC_URL} --username **** --password ***************\
   --threads 3 ts.add --keyspace cache:glycemia --timestamp tim --key patient_id \
   --value eu_percent

bin/riot db-import "SELECT CONCAT(patient_id,':hyper') as patient_id,unix_timestamp(record_date)*1000 as tim,hyper_percent FROM wegs.daily_glycemia_summary;" \
  --url {JDBC_URL} --username **** --password ***************\
   --threads 3 ts.add --keyspace cache:glycemia --timestamp tim --key patient_id \
   --value hyper_percent

bin/riot db-import "SELECT CONCAT(patient_id,':hypo') as patient_id,unix_timestamp(record_date)*1000 as tim,hypo_percent FROM wegs.daily_glycemia_summary;" \
  --url {JDBC_URL} --username **** --password ***************\
   --threads 3 ts.add --keyspace cache:glycemia --timestamp tim --key patient_id \
   --value hypo_percent
## More examples will be added soon
