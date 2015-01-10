# jdbc-ssh
JDBC driver over SSH tunnel

If you want to run the tests against your own database (no modifications are made):

mvn -Durl="jdbc:ssh:mysql://127.0.0.1:13306/xxx?user=xxx&password=xxx" -Dhost=xxx.xxx.xxx.xxx -DremotePort=3306 install

