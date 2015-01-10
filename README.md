# jdbc-ssh

JDBC driver over SSH tunnel


## Build

If you want to run the tests against your own database (no modifications are made in the database):

```
mvn -Durl="jdbc:ssh:mysql://127.0.0.1:3306/xxx?user=xxx&password=xxx&jdbc.ssh.host=xxx.xxx.xxx.xxx" install
```