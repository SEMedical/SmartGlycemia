# Description: This script is used to import glycemia data per 15 minutes after downsampling
#into redis cache.
bin/riot db-import "SELECT UNIX_TIMESTAMP(rounded_time)*1000 AS record_ts,\
 patient_id, (SELECT glycemia FROM glycemia WHERE CONCAT(DATE_FORMAT(record_time\
 , '%Y-%m-%d %H:'), LPAD(FLOOR(MINUTE(record_time) / 15) * 15, 2, '0'), ':00') =\
  rt.rounded_time LIMIT 1) AS glycemia FROM (SELECT DISTINCT CONCAT(DATE_FORMAT\
  (record_time, '%Y-%m-%d %H:'), LPAD(FLOOR(MINUTE(record_time) / 15) * 15, 2, '0')\
  , ':00') AS rounded_time, patient_id FROM glycemia) rt ORDER BY rounded_time;" \
  --url {JDBC_URL} --username **** --password ***************\
   --threads 3 ts.add --keyspace cache:glycemia --timestamp record_ts --key patient_id \
   --value glycemia
## More examples will be added soon