-- Create the "uuid-ossp" extension in the "postgres" database
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create the "templateengine" database
CREATE DATABASE templateengine;

-- Connect to the "templateengine" database
\connect templateengine;

-- Create the "uuid-ossp" extension in the "templateengine" database
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create the table
CREATE TABLE stakeholder_template (
    id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    template TEXT NOT NULL,
    CONSTRAINT stakeholder_template_pkey PRIMARY KEY (id)
);

-- Insert data into the table
INSERT INTO stakeholder_template (id, name, template)
VALUES (
   'e289b474-3930-4a31-a915-bb69c9e420d2'::uuid,
   'stakeholder123',
   '{
     "targetEndpoint": "http://localhost:9090/api/target",
     "targetRequestType": "POST",
     "bodyMapping": {
       "name": "#firstName + '' '' + #middleName",
       "surname": "#lastName",
       "age": "#age"
     },
     "headerMapping": {
       "Authorization": "#authtoken",
       "Correlation-Id": "#correlationId"
     }
   }'
);