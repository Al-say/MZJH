-- V3: Add structured prompt fields to agent_seat table
ALTER TABLE agent_seats
  ADD COLUMN codename VARCHAR(64),
  ADD COLUMN role VARCHAR(128),
  ADD COLUMN goal TEXT,
  ADD COLUMN thinking_style VARCHAR(256),
  ADD COLUMN reasoning_logic VARCHAR(256),
  ADD COLUMN avatar_url TEXT,
  ADD COLUMN prompt_template_version INT NOT NULL DEFAULT 1;

-- Update existing records to use template version 1
UPDATE agent_seats SET prompt_template_version = 1 WHERE prompt_template_version IS NULL;

-- Optional: Add comments for documentation
COMMENT ON COLUMN agent_seats.codename IS 'Agent codename for display purposes';
COMMENT ON COLUMN agent_seats.role IS 'Professional role/identity of the agent';
COMMENT ON COLUMN agent_seats.goal IS 'Task objective for the agent';
COMMENT ON COLUMN agent_seats.thinking_style IS 'Thinking approach and style';
COMMENT ON COLUMN agent_seats.reasoning_logic IS 'Reasoning logic and analogy rules';
COMMENT ON COLUMN agent_seats.avatar_url IS 'Avatar image URL for UI display';
COMMENT ON COLUMN agent_seats.prompt_template_version IS 'Version of prompt template for backward compatibility';