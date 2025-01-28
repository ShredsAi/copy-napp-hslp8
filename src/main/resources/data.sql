INSERT INTO menu_record (id, restaurant_id, created_at, updated_at, status)
VALUES (
    'e18f10f6-31c5-4a6a-960c-ff4b78ecf46a',
    '4c8fc4c0-5f8f-4ebe-93c4-52d47439c941',
    NOW(),
    NOW(),
    'ACTIVE'
);

INSERT INTO dish_item (id, menu_record_id, dish_name, price, description, additional_metadata)
VALUES (
    'd414acde-8007-4f75-9aaf-670ed15aacda',
    'e18f10f6-31c5-4a6a-960c-ff4b78ecf46a',
    'Spaghetti',
    12.50,
    'Classic pasta with marinara sauce',
    '{"vegan": false, "spicy": false}'
);
