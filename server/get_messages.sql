DELIMITER $$
CREATE PROCEDURE `get_messages` (
	user1 TEXT,
    user2 TEXT
)
BEGIN
	SET @u1 = (SELECT id FROM user WHERE username = user1);
	SET @u2 = (SELECT id FROM user WHERE username = user2);

	SELECT timestamp, username, message
	FROM message, user
	WHERE user.id = sender 
	AND ((sender = @u2 AND recipient = @u1)
	OR (sender = @u1 AND recipient = @u2));
END $$