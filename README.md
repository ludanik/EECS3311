Before you run the application, you should recreate the tables. Here are the schemas:  

CREATE TABLE Bookings (  
    booking_id SERIAL PRIMARY KEY,  
    client_id INT NOT NULL,  	
    parking_space_id INT NOT NULL,    	
    license_plate VARCHAR(15) NOT NULL,  	
    start_time TIMESTAMP NOT NULL,  	
    end_time TIMESTAMP NOT NULL,  	
    deposit INT NOT NULL,  	
    total_cost INT NOT NULL,  
    status VARCHAR(50) CHECK (status IN('booked','cancelled','completed','extended')) NOT NULL DEFAULT 'booked',      
    payment_method VARCHAR(30) CHECK (payment_method IN('credit', 'debit', 'mobile')),      
    FOREIGN KEY (client_id) REFERENCES Users(id),      
    FOREIGN KEY (parking_space_id) REFERENCES ParkingSpaces(parking_space_id)      
);      
    
CREATE TABLE ParkingSpaces (    
    parking_space_id SERIAL PRIMARY KEY,        
    parking_lot_id INT NOT NULL,    
    space_number INT NOT NULL,     
    location_description VARCHAR(100),    
    status VARCHAR(50) CHECK (status IN('available','booked','occupied','maintenance')) NOT NULL DEFAULT 'available',    
    FOREIGN KEY (parking_lot_id) REFERENCES ParkingLots(parking_lot_id)        
);    

CREATE TABLE ParkingLots (    
    parking_lot_id SERIAL PRIMARY KEY,    
    name VARCHAR(50) NOT NULL,    
    location VARCHAR(100),    
    status TEXT CHECK (status IN('enabled','disabled')) NOT NULL DEFAULT 'enabled' 
);    

CREATE TABLE users (    
    id SERIAL PRIMARY KEY,    
    email VARCHAR(255) UNIQUE NOT NULL,    
    user_type VARCHAR(50) NOT NULL,    
    status VARCHAR(20) NOT NULL,    
    password VARCHAR(50) NOT NULL    
);    
