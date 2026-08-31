-- An equipment type can occur at most once for a given event.
-- Its requested amount is represented by the quantity column.
ALTER TABLE EVENT_EQUIPMENT
    ADD CONSTRAINT pk_event_equipment PRIMARY KEY (event_id, equipment_id);
