# Postgres Custom EncryptionUDf

This is a custom postgres java code than  can be used in encryption of data to meet data privacy requirements.<br/>
It implements  java AES encryption and decryption. It supports AES_CBC and AES_EBC algorithms with key sizes 128, 192 and 256.<br/>
This allows you to load custom java functions in postgres database.<br/>

The code has two functions:<br/>
i. Encrypt:
ii. Decrypt


## Requirements     
- jdk-11


## Installation
```git
git clone git@github.com:Kenchir/postgresql-encryption-UDF.git
```


```bash
mvn clean compile package
```


## Usage
Add the following property to postgresql.conf to jlibvm.so location  <br/>

```bash
pljava.libjvm_location='/usr/lib/jvm/java-11-openjdk-amd64/lib/server/libjvm.so'
```


Restart postgres server<br/>

Create pljava extension and load your packaged jar from your location.
```sql

create  extension if not EXISTS pljava;

Load the jar from your destination

SELECT sqlj.install_jar('file:///ext-configs/EcryptDecryptPostgres-1.0-SNAPSHOT.jar', 'Aes', true);	

SELECT sqlj.set_classpath('public', 'Aes');
```
#### Create your Encryption  function
```sql
CREATE OR REPLACE function ENCRYPT(col varchar ,aes_key varchar ) 
RETURNS "varchar" 
AS 
     'com.bigdata.postgres.Aes.encrypt' 
LANGUAGE 'java' VOLATILE;
```

#### Create your Decryption function
```sql
CREATE OR REPLACE function DECRYPT(col varchar ,aes_key varchar ,algorithm varchar ) 
RETURNS "varchar" 
AS 
     'com.bigdata.postgres.Aes.decrypt' 
LANGUAGE 'java' VOLATILE;
```


### Sample tests
```bash
create  table test_tbl(
 	name text,
  	msisdn text
);

insert  into test_tbl values('Ken', ENCRYPT('254727128043','n9Tp9+69gxNdUg9F632u1cCRuqcOuGmN'))
insert  into test_tbl values('Alen', ENCRYPT('727399473','n9Tp9+69gxNdUg9F632u1cCRuqcOuGmN'))

select  * from  test_tbl

```
|name|msisdn|
|----|------|
|Ken|Tywy7Y272MmuDlrewpOV9A==|
|Alen|i2v/RyXZHmfEUWZgcMx+XQ==|

Querying the table and Decrypting the col:

```bash
select  name, DECRYPT(msisdn,'n9Tp9+69gxNdUg9F632u1cCRuqcOuGmN')   from test_tbl tt ;

```
|name|decrypt|
|----|-------|
|Ken|254727128043|
|Alen|727399473|


## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)