-- OrbiFreight — Init PostgreSQL
-- Confirma que o banco está pronto (Hibernate cria as tabelas)
SELECT 'OrbiFreight DB inicializado' AS status;
SELECT current_database() AS banco, current_user AS usuario;
