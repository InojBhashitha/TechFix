-- TechFix Initial Seed Data

-- 1. Branches
MERGE INTO branches (id, name, address, phone, latitude, longitude, is_active) KEY (id) VALUES
(1, 'TechFix Colombo', 'No 45, Galle Road, Colombo 03', '+94 11 234 5678', 6.9271, 79.8612, true),
(2, 'TechFix Galle', 'No 12, Main Street, Galle Fort, Galle', '+94 91 223 4567', 6.0535, 80.2210, true);

-- 2. Device Categories
MERGE INTO device_categories (id, name, icon_name, description) KEY (id) VALUES
(1, 'Mobile Phone', 'ic_smartphone', 'Smartphones and mobile devices across Apple, Samsung, Xiaomi, and other brands.'),
(2, 'Laptop', 'ic_laptop', 'Laptops, MacBooks, Ultrabooks, and Gaming notebooks.'),
(3, 'Desktop Computer', 'ic_desktop_windows', 'Tower PCs, All-in-Ones, Custom rigs, and Workstations.'),
(4, 'Tablet', 'ic_tablet', 'iPads, Android tablets, and drawing surfaces.');

-- 3. Repair Services
MERGE INTO repair_services (id, category_id, name, description, estimated_price, estimated_duration_minutes, sample_image_url, is_active) KEY (id) VALUES
(1, 1, 'Screen Replacement', 'Complete replacement of cracked or damaged OLED / LCD display with genuine parts.', 12500.00, 60, 'https://images.unsplash.com/photo-1569842504407-b6014b2273ab?w=400', true),
(2, 1, 'Battery Replacement', 'Replacement of degraded battery with OEM high capacity lithium battery.', 6500.00, 45, 'https://images.unsplash.com/photo-1588508065123-287b28e013da?w=400', true),
(3, 1, 'Charging Port Repair', 'Fix loose, damaged, or unresponsive USB-C / Lightning charging ports.', 4500.00, 45, 'https://images.unsplash.com/photo-1584438784894-089d6a62b8fa?w=400', true),
(4, 1, 'Water Damage Treatment', 'Ultrasonic cleaning, corrosion removal, and logic board drying & diagnosis.', 5500.00, 120, 'https://images.unsplash.com/photo-1512499617640-c74ae3a79d37?w=400', true),
(5, 2, 'Laptop Screen Replacement', 'Panel replacement for broken, flickering, or line-glitched laptop screens.', 18500.00, 90, 'https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=400', true),
(6, 2, 'Keyboard Replacement', 'Full keyboard assembly replacement for sticky or dead keys.', 7500.00, 60, 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=400', true),
(7, 2, 'Motherboard Chip-Level Repair', 'Micro-soldering, short-circuit diagnostics, power IC and MOSFET repair.', 15000.00, 180, 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=400', true),
(8, 2, 'Thermal Servicing & Fan Cleanup', 'Deep dust cleaning, fan lubrication, and premium thermal paste application.', 3500.00, 45, 'https://images.unsplash.com/photo-1591488320449-011701bb6704?w=400', true),
(9, 3, 'Power Supply (PSU) Replacement', 'Diagnostic and replacement of faulty or blown computer power supplies.', 8500.00, 60, 'https://images.unsplash.com/photo-1591799264318-7e6ef8ddb7ea?w=400', true),
(10, 3, 'OS Reinstallation & Optimization', 'Clean installation of Windows / Linux / macOS with drivers and software setup.', 4000.00, 60, 'https://images.unsplash.com/photo-1618401471353-b98aedd04e11?w=400', true);

-- 4. Technicians
MERGE INTO technicians (id, branch_id, full_name, specialization, is_available, active_repairs_count) KEY (id) VALUES
(1, 1, 'Kasun Perera', 'Mobile Hardware & Display Specialist', true, 1),
(2, 1, 'Nuwan Silva', 'Laptop Motherboard & Micro-soldering', true, 0),
(3, 1, 'Dinesh Fernando', 'Desktop PCs & Power Diagnostics', true, 2),
(4, 2, 'Ruwan Jayawardena', 'Mobile & Tablet Hardware', true, 0),
(5, 2, 'Chaminda Bandara', 'Laptop Screen & Thermal Systems', true, 1);

-- 5. Spare Parts
MERGE INTO spare_parts (id, name, part_code, compatible_category_id, unit_cost) KEY (id) VALUES
(1, 'iPhone 13 OLED Display Assembly', 'PART-IP13-DISP', 1, 9500.00),
(2, 'Samsung Galaxy S22 AMOLED Panel', 'PART-S22-DISP', 1, 11000.00),
(3, 'Universal High Capacity Mobile Battery 5000mAh', 'PART-MOB-BAT50', 1, 3500.00),
(4, '15.6 Inch Full HD IPS Laptop Panel 144Hz', 'PART-LTP-156IPS', 2, 13500.00),
(5, 'Universal Backlit Laptop Keyboard Assembly', 'PART-LTP-KEY01', 2, 4500.00),
(6, '650W 80 Plus Bronze Power Supply Unit', 'PART-DT-PSU650', 3, 6200.00);

-- 6. Branch Inventory
MERGE INTO branch_inventory (id, branch_id, spare_part_id, quantity, minimum_stock_alert) KEY (id) VALUES
(1, 1, 1, 5, 2),  -- Colombo has 5 iPhone 13 screens
(2, 1, 2, 4, 2),  -- Colombo has 4 S22 screens
(3, 1, 3, 12, 3), -- Colombo has 12 mobile batteries
(4, 1, 4, 6, 2),  -- Colombo has 6 laptop screens
(5, 1, 5, 4, 1),  -- Colombo has 4 keyboards
(6, 1, 6, 8, 2),  -- Colombo has 8 PSUs
(7, 2, 1, 0, 2),  -- Galle has 0 iPhone 13 screens (demonstrates intelligent routing to Colombo!)
(8, 2, 2, 3, 1),  -- Galle has 3 S22 screens
(9, 2, 3, 8, 2),  -- Galle has 8 mobile batteries
(10, 2, 4, 2, 1), -- Galle has 2 laptop screens
(11, 2, 5, 2, 1), -- Galle has 2 keyboards
(12, 2, 6, 3, 1); -- Galle has 3 PSUs
