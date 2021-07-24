CREATE TABLE guild_information
(guild_id BIGINT,
prefix TEXT,
moderator_role_id BIGINT DEFAULT 0,
twitch_channel_id BIGINT DEFAULT 0,
muted_role_id BIGINT DEFAULT 0,
live_notifications_role_id BIGINT DEFAULT 0,
notify_on_update TINYINT DEFAULT 1,
update_channel_id BIGINT DEFAULT 0,
counting_channel_id BIGINT DEFAULT 0,
poll_channel_id BIGINT DEFAULT 0,
raffle_channel_id BIGINT DEFAULT 0,
birthday_channel_id BIGINT DEFAULT 0,
welcome_channel_id BIGINT DEFAULT 0,
log_channel_id BIGINT DEFAULT 0,
venting_channel_id BIGINT DEFAULT 0,
ai_response TINYINT DEFAULT 0,
daily_channel_id BIGINT DEFAULT 0);