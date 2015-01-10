# jdbc-ssh

JDBC driver over SSH tunnel


## Build

If you want to run the tests against your own database (no modifications are made in the database):

```
mvn -Durl="jdbc:ssh:mysql://127.0.0.1:3306/xxx?user=xxx&password=xxx&jdbc.ssh.host=xxx.xxx.xxx.xxx" install
```

NOTE: if your SSH server is running on the default port 22 the only additional parameter you have to assign is 
"jdbc.ssh.host" (IP or hostname of your SSH server).