-- 1. Создаем Keyspace (используем SimpleStrategy для локальной разработки)
CREATE KEYSPACE IF NOT EXISTS storage_service_keyspace
    WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE storage_service_keyspace;

-- 2. Создаем роли (вместо ALTER дефолтного админа, просто создаем изолированного юзера)
CREATE ROLE IF NOT EXISTS 'user' WITH PASSWORD = '1234' AND LOGIN = true;
GRANT ALL PERMISSIONS ON KEYSPACE storage_service_keyspace TO 'user';

-- 3. Создаем таблицу сообщений
CREATE TABLE IF NOT EXISTS messages (
                                        id UUID,
                                        type TEXT,
                                        sender_id BIGINT,
                                        chat_id BIGINT,
                                        text TEXT,
                                        send_time TIMESTAMP,
                                        marked_to_delete BOOLEAN,
                                        PRIMARY KEY (id)
);

-- 4. Создаем таблицу сообщений по чатам с сортировкой времени
CREATE TABLE IF NOT EXISTS messages_by_chat (
                                                chat_id BIGINT,
                                                send_time TIMESTAMP,
                                                id UUID,
                                                text TEXT,
                                                sender_id BIGINT,
                                                type TEXT,
                                                marked_to_delete BOOLEAN,
                                                PRIMARY KEY ((chat_id), send_time, id)
) WITH CLUSTERING ORDER BY (send_time DESC);

-- 5. Создаем таблицу вложений
CREATE TABLE IF NOT EXISTS attachments (
                                           id UUID,
                                           message_id UUID,
                                           type TEXT,
                                           PRIMARY KEY ((message_id), id)
);