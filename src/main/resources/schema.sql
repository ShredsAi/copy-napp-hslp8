-- Menu Record table with audit columns and constraints
CREATE TABLE IF NOT EXISTS menu_record (
    id UUID PRIMARY KEY,
    restaurant_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'INACTIVE')),
    version INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT unique_active_menu_per_restaurant UNIQUE (restaurant_id, status)
);

COMMENT ON TABLE menu_record IS 'Stores menu records for restaurants';
COMMENT ON COLUMN menu_record.id IS 'Unique identifier for the menu record';
COMMENT ON COLUMN menu_record.restaurant_id IS 'Reference to the restaurant this menu belongs to';
COMMENT ON COLUMN menu_record.status IS 'Current status of the menu record (ACTIVE/INACTIVE)';
COMMENT ON COLUMN menu_record.version IS 'Optimistic locking version number';

-- Dish Item table with constraints and validations
CREATE TABLE IF NOT EXISTS dish_item (
    id UUID PRIMARY KEY,
    menu_record_id UUID NOT NULL,
    dish_name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    description TEXT,
    additional_metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_menu_record 
        FOREIGN KEY (menu_record_id) 
        REFERENCES menu_record(id) 
        ON DELETE CASCADE,
    CONSTRAINT unique_dish_name_per_menu 
        UNIQUE (menu_record_id, dish_name)
);

COMMENT ON TABLE dish_item IS 'Stores individual dish items within a menu';
COMMENT ON COLUMN dish_item.id IS 'Unique identifier for the dish item';
COMMENT ON COLUMN dish_item.menu_record_id IS 'Reference to the parent menu record';
COMMENT ON COLUMN dish_item.dish_name IS 'Name of the dish';
COMMENT ON COLUMN dish_item.price IS 'Price of the dish (must be non-negative)';
COMMENT ON COLUMN dish_item.additional_metadata IS 'Additional dish information stored as JSON';

-- Indices for better query performance
CREATE INDEX IF NOT EXISTS idx_menu_record_restaurant_status ON menu_record (restaurant_id, status);
CREATE INDEX IF NOT EXISTS idx_menu_record_status ON menu_record (status);
CREATE INDEX IF NOT EXISTS idx_menu_record_created_at ON menu_record (created_at);

CREATE INDEX IF NOT EXISTS idx_dish_item_menu_record ON dish_item (menu_record_id);
CREATE INDEX IF NOT EXISTS idx_dish_item_name ON dish_item (dish_name);
CREATE INDEX IF NOT EXISTS idx_dish_item_price ON dish_item (price);

-- Function to update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Triggers to maintain updated_at columns
CREATE TRIGGER update_menu_record_modtime
    BEFORE UPDATE ON menu_record
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_dish_item_modtime
    BEFORE UPDATE ON dish_item
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
