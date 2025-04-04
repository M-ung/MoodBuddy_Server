DELIMITER //
CREATE PROCEDURE GenerateTestData()
BEGIN
	DECLARE i INT DEFAULT 1;
    DECLARE j INT DEFAULT 1;
    DECLARE k INT DEFAULT 1;
    DECLARE user_id INT;
    DECLARE diary_id INT;
    DECLARE emotion VARCHAR(20);
    DECLARE subject VARCHAR(20);
    DECLARE weather VARCHAR(20);
    DECLARE font VARCHAR(20);
    DECLARE font_size VARCHAR(20);
    DECLARE content TEXT;
    DECLARE summary TEXT;
    DECLARE created DATETIME;
    DECLARE updated DATETIME;

    WHILE i <= 10000 DO
        INSERT INTO user (
            user_role, nickname, kakao_id, alarm, alarm_time, letter_alarm, phone_number, birthday, gender, deleted,
            user_cur_diary_nums, user_last_diary_nums, user_letter_nums, check_today_diary, created_time, updated_time
        ) VALUES (
            'USER',
            CONCAT('user_', i),
            1234567890 + i,
            TRUE,
            '08:00',
            FALSE,
            CONCAT('010-1234-', LPAD(i, 4, '0')),
            '1990-01-01',
            TRUE,
            FALSE,
            0,
            0,
            0,
            TRUE,
            NOW(),
            NOW()
        );
        SET user_id = LAST_INSERT_ID();
INSERT INTO profile (
    profile_comment, user_id, created_time, updated_time
) VALUES (
             CONCAT('This is a sample profile comment for user_', i),
             user_id,
             NOW(),
             NOW()
         );
INSERT INTO profile_image (
    user_id, profile_img_url, created_time, updated_time
) VALUES (
             user_id,
             CONCAT('https://example.com/profile_image_', i, '.jpg'),
             NOW(),
             NOW()
         );
SET j = 1;
        WHILE j <= 50 DO
			SET emotion = CASE j % 4
				WHEN 0 THEN 'HAPPINESS'
				WHEN 1 THEN 'SADNESS'
				WHEN 2 THEN 'ANGER'
				ELSE 'SURPRISE'
END;

			SET subject = CASE j % 4
				WHEN 0 THEN 'DAILY'
				WHEN 1 THEN 'GROWTH'
				WHEN 2 THEN 'TRAVEL'
				ELSE 'EMOTION'
END;

			SET weather = CASE j % 4
				WHEN 0 THEN 'CLEAR'
				WHEN 1 THEN 'CLOUDY'
				WHEN 2 THEN 'RAIN'
				ELSE 'SNOW'
END;

			SET font = CASE j % 2
				WHEN 0 THEN 'INTER'
				ELSE 'MEETME'
END;

			SET font_size = CASE j % 3
				WHEN 0 THEN 'PX24'
				WHEN 1 THEN 'PX28'
				ELSE 'PX30'
END;

			SET content = CONCAT('더미 내용_', j);
			SET summary = CONCAT('더미 요약_', j);
			SET created = NOW();
			SET updated = NOW();

INSERT INTO diary (
    title, date, content, weather, emotion,
    subject, summary, user_id,
    book_mark, font, font_size, thumbnail, mood_buddy_status, version, created_time, updated_time
) VALUES (
             CONCAT('더미 제목_', j),
             DATE_ADD('2025-01-25', INTERVAL j DAY),
             content,
             weather,
             emotion,
             subject,
             summary,
             user_id,
             FALSE,
             font,
             font_size,
             'thumbnail',
             'ACTIVE',
             0,
             created,
             updated
         );

SET diary_id = LAST_INSERT_ID();

INSERT INTO diary_query (
    diary_id, title, date, content, emotion, subject,
    user_id, thumbnail,
    mood_buddy_status
) VALUES (
             diary_id,
             CONCAT('더미 제목_', j),
             DATE_ADD('2025-01-25', INTERVAL j DAY),
             content,
             emotion,
             subject,
             user_id,
             'thumbnail',
             'ACTIVE'
         );

SET k = 1;
            WHILE k <= 10 DO
                INSERT INTO diary_image (
                    diary_id, image_url, mood_buddy_status, created_time, updated_time
                ) VALUES (
                    diary_id,
                    CONCAT('https://example.com/diary_image_', i, '_', j, '_', k, '.jpg'),
                    'ACTIVE',
                    NOW(),
                    NOW()
                );
                SET k = k + 1;
END WHILE;

            SET j = j + 1;
END WHILE;

        SET i = i + 1;
END WHILE;
END //
DELIMITER ;

CALL GenerateTestData();


INSERT INTO quddy_ti (
    user_id,
    quddy_ti_year,
    quddy_ti_month,
    diary_frequency,
    daily_count,
    growth_count,
    emotion_count,
    travel_count,
    happiness_count,
    anger_count,
    disgust_count,
    fear_count,
    neutral_count,
    sadness_count,
    surprise_count,
    quddy_ti_type,
    mood_buddy_status,
    created_time,
    updated_time
)
SELECT
    id AS user_id,
    '2025',
    '03',
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    NULL,
    'DIS_ACTIVE',
    NOW(),
    NOW()
FROM user
WHERE id BETWEEN 1 AND 100;