-- Create test database (run once)
CREATE DATABASE parkingdb_test WITH TEMPLATE parkingdb;

-- Grant permissions (run as admin)
GRANT ALL PRIVILEGES ON DATABASE parkingdb_test TO app;

-- Connect and set up test data
\c parkingdb_test

-- Insert minimal test data
INSERT INTO ParkingLots (name, location) VALUES
('Main Lot', '123 Test St'),
('North Lot', '456 Example Ave');

INSERT INTO ParkingSpaces (parking_lot_id, space_number) VALUES
(1, 101), (1, 102), (2, 201);

INSERT INTO users (email, user_type, status, password) VALUES
('client1@test.com', 'customer', 'active', 'pass123'),
('admin@test.com', 'admin', 'active', 'adminpass');