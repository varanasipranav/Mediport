-- Runs once, when the MySQL volume is first created.
-- The app user manages the master database and one schema per clinic (t_<code>); it needs nothing else.
GRANT ALL PRIVILEGES ON `platform_master`.* TO 'mediport'@'%';
GRANT ALL PRIVILEGES ON `t\_%`.* TO 'mediport'@'%';
FLUSH PRIVILEGES;
