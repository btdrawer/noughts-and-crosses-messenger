DELIMITER $$
CREATE PROCEDURE `get_leaderboard` (
	`limit` INT
)
BEGIN
	SELECT w.id,
		username, 
		gross, 
		gross - IF(COUNT(game.id) > 0, COUNT(game.id), 0) AS net 
	FROM (
		SELECT user.id AS id,
			username,
			COUNT(game.id) AS gross
		FROM user, game
		WHERE user.id = won
		GROUP BY username
	) w, game
	WHERE w.id = lost 
	GROUP BY username 
	ORDER BY gross - COUNT(game.id) DESC
    LIMIT `limit`;
END $$