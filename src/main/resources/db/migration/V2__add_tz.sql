-- Reminders --------------------------------------------------------------
ALTER TABLE reminders
  ALTER COLUMN scheduled_at TYPE TIMESTAMPTZ
    USING scheduled_at AT TIME ZONE 'Asia/Tomsk',   -- <-- поправьте, где «как было»
  ALTER COLUMN created_at   TYPE TIMESTAMPTZ
    USING created_at   AT TIME ZONE 'Asia/Tomsk';

-- Outbox -----------------------------------------------------------------
ALTER TABLE outbox_notifications
  ALTER COLUMN created_at TYPE TIMESTAMPTZ
    USING created_at AT TIME ZONE 'Asia/Tomsk';