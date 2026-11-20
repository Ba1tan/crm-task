CREATE EXTENSION IF NOT EXISTS btree_gist;

CREATE TABLE managers (
  id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  full_name VARCHAR(255) NOT NULL
);

CREATE TABLE companies (
  id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name       VARCHAR(255) NOT NULL,
  bin        VARCHAR(12)  NOT NULL,
  phone      VARCHAR(20)  NOT NULL,
  status     VARCHAR(20)  NOT NULL DEFAULT 'NEW',
  created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
  CONSTRAINT uq_companies_bin    UNIQUE (bin),
  CONSTRAINT ck_companies_bin    CHECK (bin ~ '^[0-9]{12}$'),
  CONSTRAINT ck_companies_status CHECK (status IN ('NEW', 'NO_ANSWER', 'REFUSED', 'MEETING_SET'))
);
CREATE INDEX idx_companies_status ON companies (status, id);

CREATE TABLE meetings (
  id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  company_id        BIGINT      NOT NULL REFERENCES companies (id),
  manager_id        BIGINT      NOT NULL REFERENCES managers (id),
  start_at          TIMESTAMPTZ NOT NULL,
  end_at            TIMESTAMPTZ NOT NULL,
  initial_start_at TIMESTAMPTZ NOT NULL,
  status            VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
  cancel_reason     VARCHAR(500),
  created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT ck_meetings_status   CHECK (status IN ('SCHEDULED', 'CANCELLED')),
  CONSTRAINT ck_meetings_duration CHECK (end_at = start_at + INTERVAL '60 minutes'),
  -- '[)': встречи 10:00-11:00 и 11:00-12:00 не конфликтуют
  CONSTRAINT no_manager_overlap EXCLUDE USING gist
    (manager_id WITH =, tstzrange(start_at, end_at, '[)') WITH &&)
    WHERE (status = 'SCHEDULED'),
  CONSTRAINT no_company_overlap EXCLUDE USING gist
    (company_id WITH =, tstzrange(start_at, end_at, '[)') WITH &&)
    WHERE (status = 'SCHEDULED')
);
CREATE INDEX idx_meetings_manager_start ON meetings (manager_id, start_at);

CREATE TABLE meeting_events (
  id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  meeting_id   BIGINT      NOT NULL REFERENCES meetings (id),
  type         VARCHAR(20) NOT NULL,
  old_start_at TIMESTAMPTZ,
  new_start_at TIMESTAMPTZ,
  occurred_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT ck_meeting_events_type CHECK (type IN ('CREATED', 'RESCHEDULED', 'CANCELLED'))
);
CREATE INDEX idx_meeting_events_meeting ON meeting_events (meeting_id, id);

CREATE TABLE calls (
  id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  company_id BIGINT      NOT NULL REFERENCES companies (id),
  result     VARCHAR(20) NOT NULL,
  meeting_id BIGINT REFERENCES meetings (id),
  comment    VARCHAR(1000),
  called_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT ck_calls_result  CHECK (result IN ('NO_ANSWER', 'REFUSED', 'MEETING_SET')),
  CONSTRAINT ck_calls_meeting CHECK ((result = 'MEETING_SET') = (meeting_id IS NOT NULL))
);
CREATE INDEX idx_calls_company ON calls (company_id);
