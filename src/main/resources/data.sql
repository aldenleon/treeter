INSERT INTO comment (id, text_content, up, dw, parent_id, owner_id)
VALUES (1, 'root comment', 1, 0, NULL, NULL)
ON CONFLICT DO NOTHING;

SELECT SETVAL(PG_GET_SERIAL_SEQUENCE('comment', 'id'), (SELECT MAX(id) FROM comment));
