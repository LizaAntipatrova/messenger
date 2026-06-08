-- ALTER KEYSPACE system_auth
--     WITH replication = {'class': 'NetworkTopologyStrategy', 'datacenter-1': 1};
--
-- CREATE USER IF NOT EXISTS user WITH PASSWORD '1234' NOSUPERUSER;
-- GRANT ALL PERMISSIONS ON KEYSPACE storage_service_keyspace TO user;
--

-- Создаем Keyspace
CREATE KEYSPACE IF NOT EXISTS storage_service_keyspace
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

-- В Cassandra 4.x правильный и поддерживаемый синтаксис — это CREATE ROLE с LOGIN
CREATE ROLE IF NOT EXISTS 'user' WITH PASSWORD = '1234' AND LOGIN = true;

-- Выдаем права
GRANT ALL PERMISSIONS ON KEYSPACE storage_service_keyspace TO 'user';