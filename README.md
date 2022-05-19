# Postgres Custom EncryptionUDf

This is a custom postgres java code than  can be used in enecryption of data to meet data privacy requirements.<br/>
It implements  java AES encryption and decryption. It supports AES_CBC and AES_EBC algorithms with key sizes 128, 192 and 256.<br/>
This allows you to load custom java functions in postgres database.<br/>

The code has two functions:<br/>
i. Encrypt:
ii. Decrypt



## Requirements
- jdk-11


## Installation

git clone git@github.com:Kenchir/postgresql-encryption-UDF.git

```bash
mvn clean compile package
```


## Usage
Add the following property to postgresql.conf to jlibvm.so location  <br/>
pljava.libjvm_location='/usr/lib/jvm/java-11-openjdk-amd64/lib/server/libjvm.so'

Restart postgres server<br/>

Create pljava extension and load your packaged jar from your location.
```sql

create  extension if not EXISTS pljava;

SELECT sqlj.install_jar('file:///ext-configs/EcryptDecryptPostgres-1.0-SNAPSHOT.jar', 'Aes', true);	

SELECT sqlj.set_classpath('public', 'Aes');
```
#### Create your Encryption  function
```
CREATE OR REPLACE function ENCRYPT(column varchar ,key varchar ,algorithm varchar ) 
RETURNS "varchar" 
AS 
     'com.bigdata.postgres.Aes.encrypt' 
LANGUAGE 'java' VOLATILE;
```

#### Create your Decryption function
```
CREATE OR REPLACE function DECRYPT(column varchar ,key varchar ,algorithm varchar ) 
RETURNS "varchar" 
AS 
     'com.bigdata.postgres.Aes.decrypt' 
LANGUAGE 'java' VOLATILE;
```
## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)