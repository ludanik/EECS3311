-- Create test database (run once as admin)
CREATE DATABASE parkingdb_test WITH TEMPLATE parkingdb;

-- Grant permissions (run as admin)
GRANT ALL PRIVILEGES ON DATABASE parkingdb_test TO app;

-- Connect to the test database
\c parkingdb_test

-- Create ParkingLots table
CREATE TABLE IF NOT EXISTS ParkingLots (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL
);

-- Create ParkingSpaces table
CREATE TABLE IF NOT EXISTS ParkingSpaces (
    id SERIAL PRIMARY KEY,
    parking_lot_id INT REFERENCES ParkingLots(id) ON DELETE CASCADE,
    space_number INT NOT NULL,
    status VARCHAR(50) DEFAULT 'AVAILABLE'
);

-- Create Users table
CREATE TABLE IF NOT EXISTS Users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    user_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Create Bookings table
CREATE TABLE IF NOT EXISTS Bookings (
    booking_id SERIAL PRIMARY KEY,
    client_id INT REFERENCES Users(id) ON DELETE CASCADE,
    space_id INT REFERENCES ParkingSpaces(id) ON DELETE CASCADE,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(50) DEFAULT 'active',
    payment_method VARCHAR(50),
    license_plate VARCHAR(20)
);

-- Insert test data
INSERT INTO ParkingLots (name, location) VALUES
('Main Lot', '123 Test St'),
('North Lot', '456 Example Ave');

INSERT INTO ParkingSpaces (parking_lot_id, space_number, status) VALUES
(1, 101, 'AVAILABLE'),
(1, 102, 'AVAILABLE'),
(2, 201, 'AVAILABLE');

INSERT INTO Users (email, user_type, status, password) VALUES
('client1@test.com', 'customer', 'active', 'pass123'),
('admin@test.com', 'admin', 'active', 'adminpass');

-- Insert sample booking (optional for testing)
INSERT INTO Bookings (client_id, space_id, start_time, end_time, status, payment_method, license_plate) VALUES
(1, 1, NOW(), NOW() + INTERVAL '2 hours', 'active', 'credit', 'ABC-123');