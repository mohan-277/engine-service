-- Ensure the player table is structured to include 'is_out'
-- This statement might be different based on your actual table structure.
-- Uncomment and adjust if necessary:
-- ALTER TABLE testDb.player MODIFY COLUMN is_out TINYINT DEFAULT 0;

-- Team 1: Australia
INSERT INTO testDb.player (name, date_of_birth, specialization, gender, country, played_matches, runs, high_score, strike_rate, number_of50s, number_of100s, is_out)
VALUES
    ('Aaron Finch', '1986-11-17', 'Batsman', 'Male', 'Australia', 0, 0, 0, 0, 0, 0, 0),
    ('David Warner', '1986-10-27', 'Batsman', 'Male', 'Australia', 0, 0, 0, 0, 0, 0, 0),
    ('Pat Cummins', '1993-05-08', 'Bowler', 'Male', 'Australia', 0, 0, 0, 0, 0, 0, 0),
    ('Steve Smith', '1989-06-02', 'Batsman', 'Male', 'Australia', 0, 0, 0, 0, 0, 0, 0),
    ('Mitchell Starc', '1990-01-30', 'Bowler', 'Male', 'Australia', 0, 0, 0, 0, 0, 0, 0),
    ('Glenn Maxwell', '1988-10-14', 'All-Rounder', 'Male', 'Australia', 0, 0, 0, 0, 0, 0, 0),
    ('Kane Richardson', '1990-06-12', 'Bowler', 'Male', 'Australia', 0, 0, 0, 0, 0, 0, 0),
    ('Josh Hazlewood', '1990-01-08', 'Bowler', 'Male', 'Australia', 0, 0, 0, 0, 0, 0, 0),
    ('Marcus Stoinis', '1989-08-16', 'All-Rounder', 'Male', 'Australia', 0, 0, 0, 0, 0, 0, 0),
    ('Alex Carey', '1991-08-27', 'Wicketkeeper-Batsman', 'Male', 'Australia', 0, 0, 0, 0, 0, 0, 0),
    ('Ashton Agar', '1993-10-14', 'All-Rounder', 'Male', 'Australia', 0, 0, 0, 0, 0, 0, 0),
    ('Matthew Wade', '1988-12-26', 'Wicketkeeper-Batsman', 'Male', 'Australia', 0, 0, 0, 0, 0, 0, 0),
    ('James Pattinson', '1990-05-09', 'Bowler', 'Male', 'Australia', 0, 0, 0, 0, 0, 0, 0),
    ('Daniel Sams', '1993-11-27', 'All-Rounder', 'Male', 'Australia', 0, 0, 0, 0, 0, 0, 0),
    ('Josh Philippe', '1997-05-08', 'Wicketkeeper-Batsman', 'Male', 'Australia', 0, 0, 0, 0, 0, 0, 0);

-- Team 2: India
INSERT INTO testDb.player (name, date_of_birth, specialization, gender, country, played_matches, runs, high_score, strike_rate, number_of50s, number_of100s, is_out)
VALUES
    ('Virat Kohli', '1988-11-05', 'Batsman', 'Male', 'India', 0, 0, 0, 0, 0, 0, 0),
    ('Rohit Sharma', '1987-04-30', 'Batsman', 'Male', 'India', 0, 0, 0, 0, 0, 0, 0),
    ('Jasprit Bumrah', '1993-12-06', 'Bowler', 'Male', 'India', 0, 0, 0, 0, 0, 0, 0),
    ('KL Rahul', '1992-04-18', 'Batsman', 'Male', 'India', 0, 0, 0, 0, 0, 0, 0),
    ('Hardik Pandya', '1993-10-11', 'All-Rounder', 'Male', 'India', 0, 0, 0, 0, 0, 0, 0),
    ('Rishabh Pant', '1997-10-04', 'Wicketkeeper-Batsman', 'Male', 'India', 0, 0, 0, 0, 0, 0, 0),
    ('Shreyas Iyer', '1994-09-06', 'Batsman', 'Male', 'India', 0, 0, 0, 0, 0, 0, 0),
    ('Yuzvendra Chahal', '1990-07-23', 'Bowler', 'Male', 'India', 0, 0, 0, 0, 0, 0, 0),
    ('Bhuvneshwar Kumar', '1990-02-05', 'Bowler', 'Male', 'India', 0, 0, 0, 0, 0, 0, 0),
    ('Mohammed Shami', '1990-09-03', 'Bowler', 'Male', 'India', 0, 0, 0, 0, 0, 0, 0),
    ('Ravindra Jadeja', '1988-12-06', 'All-Rounder', 'Male', 'India', 0, 0, 0, 0, 0, 0, 0),
    ('Navdeep Saini', '1992-11-23', 'Bowler', 'Male', 'India', 0, 0, 0, 0, 0, 0, 0),
    ('Suryakumar Yadav', '1990-09-14', 'Batsman', 'Male', 'India', 0, 0, 0, 0, 0, 0, 0),
    ('Shubman Gill', '1999-09-08', 'Batsman', 'Male', 'India', 0, 0, 0, 0, 0, 0, 0),
    ('Krunal Pandya', '1991-03-24', 'All-Rounder', 'Male', 'India', 0, 0, 0, 0, 0, 0, 0);

-- Team 3: England
INSERT INTO testDb.player (name, date_of_birth, specialization, gender, country, played_matches, runs, high_score, strike_rate, number_of50s, number_of100s, is_out)
VALUES
    ('Ben Stokes', '1991-06-04', 'All-Rounder', 'Male', 'England', 0, 0, 0, 0, 0, 0, 0),
    ('Joe Root', '1990-12-30', 'Batsman', 'Male', 'England', 0, 0, 0, 0, 0, 0, 0),
    ('Jos Buttler', '1990-09-08', 'Wicketkeeper-Batsman', 'Male', 'England', 0, 0, 0, 0, 0, 0, 0),
    ('Eoin Morgan', '1986-09-10', 'Batsman', 'Male', 'England', 0, 0, 0, 0, 0, 0, 0),
    ('Jofra Archer', '1994-04-01', 'Bowler', 'Male', 'England', 0, 0, 0, 0, 0, 0, 0),
    ('Chris Woakes', '1989-03-27', 'All-Rounder', 'Male', 'England', 0, 0, 0, 0, 0, 0, 0),
    ('Sam Curran', '1998-06-03', 'All-Rounder', 'Male', 'England', 0, 0, 0, 0, 0, 0, 0),
    ('Adil Rashid', '1988-02-17', 'Bowler', 'Male', 'England', 0, 0, 0, 0, 0, 0, 0),
    ('Mark Wood', '1989-01-11', 'Bowler', 'Male', 'England', 0, 0, 0, 0, 0, 0, 0),
    ('Moeen Ali', '1987-06-18', 'All-Rounder', 'Male', 'England', 0, 0, 0, 0, 0, 0, 0),
    ('Tom Banton', '1998-11-11', 'Batsman', 'Male', 'England', 0, 0, 0, 0, 0, 0, 0),
    ('James Vince', '1990-03-14', 'Batsman', 'Male', 'England', 0, 0, 0, 0, 0, 0, 0),
    ('Phil Salt', '1992-09-28', 'Batsman', 'Male', 'England', 0, 0, 0, 0, 0, 0, 0),
    ('Ollie Pope', '1998-08-02', 'Batsman', 'Male', 'England', 0, 0, 0, 0, 0, 0, 0),
    ('Reece Topley', '1994-07-21', 'Bowler', 'Male', 'England', 0, 0, 0, 0, 0, 0, 0);

-- Team 4: Pakistan
INSERT INTO testDb.player (name, date_of_birth, specialization, gender, country, played_matches, runs, high_score, strike_rate, number_of50s, number_of100s, is_out)
VALUES
    ('Babar Azam', '1994-10-15', 'Batsman', 'Male', 'Pakistan', 0, 0, 0, 0, 0, 0, 0),
    ('Shaheen Afridi', '2000-04-06', 'Bowler', 'Male', 'Pakistan', 0, 0, 0, 0, 0, 0, 0),
    ('Mohammad Rizwan', '1992-06-01', 'Wicketkeeper-Batsman', 'Male', 'Pakistan', 0, 0, 0, 0, 0, 0, 0),
    ('Shadab Khan', '1998-10-04', 'All-Rounder', 'Male', 'Pakistan', 0, 0, 0, 0, 0, 0, 0),
    ('Fakhar Zaman', '1990-04-30', 'Batsman', 'Male', 'Pakistan', 0, 0, 0, 0, 0, 0, 0),
    ('Haris Rauf', '1993-06-07', 'Bowler', 'Male', 'Pakistan', 0, 0, 0, 0, 0, 0, 0),
    ('Iftikhar Ahmed', '1990-09-03', 'All-Rounder', 'Male', 'Pakistan', 0, 0, 0, 0, 0, 0, 0),
    ('Asif Ali', '1992-10-01', 'Batsman', 'Male', 'Pakistan', 0, 0, 0, 0, 0, 0, 0),
    ('Mohammad Nawaz', '1990-03-21', 'All-Rounder', 'Male', 'Pakistan', 0, 0, 0, 0, 0, 0, 0),
    ('Hasan Ali', '1993-02-07', 'Bowler', 'Male', 'Pakistan', 0, 0, 0, 0, 0, 0, 0),
    ('Usman Qadir', '1993-11-15', 'Bowler', 'Male', 'Pakistan', 0, 0, 0, 0, 0, 0, 0),
    ('Sohaib Maqsood', '1987-04-15', 'Batsman', 'Male', 'Pakistan', 0, 0, 0, 0, 0, 0, 0),
    ('Shoaib Malik', '1982-02-01', 'All-Rounder', 'Male', 'Pakistan', 0, 0, 0, 0, 0, 0, 0),
    ('Imam-ul-Haq', '1995-12-21', 'Batsman', 'Male', 'Pakistan', 0, 0, 0, 0, 0, 0, 0),
    ('Aamer Yamin', '1991-03-11', 'All-Rounder', 'Male', 'Pakistan', 0, 0, 0, 0, 0, 0, 0);

-- Team 5: South Africa
INSERT INTO testDb.player (name, date_of_birth, specialization, gender, country, played_matches, runs, high_score, strike_rate, number_of50s, number_of100s, is_out)
VALUES
    ('Quinton de Kock', '1992-12-17', 'Wicketkeeper-Batsman', 'Male', 'South Africa', 0, 0, 0, 0, 0, 0, 0),
    ('Kagiso Rabada', '1995-05-25', 'Bowler', 'Male', 'South Africa', 0, 0, 0, 0, 0, 0, 0),
    ('AB de Villiers', '1984-02-17', 'Batsman', 'Male', 'South Africa', 0, 0, 0, 0, 0, 0, 0),
    ('David Miller', '1989-06-10', 'Batsman', 'Male', 'South Africa', 0, 0, 0, 0, 0, 0, 0),
    ('Rassie van der Dussen', '1989-02-07', 'Batsman', 'Male', 'South Africa', 0, 0, 0, 0, 0, 0, 0),
    ('Andile Phehlukwayo', '1996-11-05', 'All-Rounder', 'Male', 'South Africa', 0, 0, 0, 0, 0, 0, 0),
    ('Lungi Ngidi', '1996-03-06', 'Bowler', 'Male', 'South Africa', 0, 0, 0, 0, 0, 0, 0),
    ('Temba Bavuma', '1990-06-17', 'Batsman', 'Male', 'South Africa', 0, 0, 0, 0, 0, 0, 0),
    ('Dwaine Pretorius', '1989-03-29', 'All-Rounder', 'Male', 'South Africa', 0, 0, 0, 0, 0, 0, 0),
    ('Tabraiz Shamsi', '1989-02-18', 'Bowler', 'Male', 'South Africa', 0, 0, 0, 0, 0, 0, 0),
    ('Beuran Hendricks', '1990-02-14', 'Bowler', 'Male', 'South Africa', 0, 0, 0, 0, 0, 0, 0),
    ('Wiaan Mulder', '1997-06-10', 'All-Rounder', 'Male', 'South Africa', 0, 0, 0, 0, 0, 0, 0),
    ('Aiden Markram', '1994-10-04', 'All-Rounder', 'Male', 'South Africa', 0, 0, 0, 0, 0, 0, 0),
    ('Heinrich Klaasen', '1991-07-30', 'Wicketkeeper-Batsman', 'Male', 'South Africa', 0, 0, 0, 0, 0, 0, 0),
    ('Keshav Maharaj', '1989-03-27', 'Bowler', 'Male', 'South Africa', 0, 0, 0, 0, 0, 0, 0);

-- Team 6: New Zealand
INSERT INTO testDb.player (name, date_of_birth, specialization, gender, country, played_matches, runs, high_score, strike_rate, number_of50s, number_of100s, is_out)
VALUES
    ('Kane Williamson', '1990-08-08', 'Batsman', 'Male', 'New Zealand', 0, 0, 0, 0, 0, 0, 0),
    ('Ross Taylor', '1984-03-08', 'Batsman', 'Male', 'New Zealand', 0, 0, 0, 0, 0, 0, 0),
    ('Trent Boult', '1989-07-22', 'Bowler', 'Male', 'New Zealand', 0, 0, 0, 0, 0, 0, 0),
    ('Tim Southee', '1988-12-11', 'Bowler', 'Male', 'New Zealand', 0, 0, 0, 0, 0, 0, 0),
    ('James Neesham', '1990-09-17', 'All-Rounder', 'Male', 'New Zealand', 0, 0, 0, 0, 0, 0, 0),
    ('Martin Guptill', '1986-09-30', 'Batsman', 'Male', 'New Zealand', 0, 0, 0, 0, 0, 0, 0),
    ('Ish Sodhi', '1993-09-04', 'Bowler', 'Male', 'New Zealand', 0, 0, 0, 0, 0, 0, 0),
    ('Mitchell Santner', '1992-02-05', 'All-Rounder', 'Male', 'New Zealand', 0, 0, 0, 0, 0, 0, 0),
    ('Devdutt Padikkal', '2000-07-07', 'Batsman', 'Male', 'New Zealand', 0, 0, 0, 0, 0, 0, 0),
    ('Tom Latham', '1987-04-02', 'Wicketkeeper-Batsman', 'Male', 'New Zealand', 0, 0, 0, 0, 0, 0, 0),
    ('Kyle Jamieson', '1994-12-30', 'Bowler', 'Male', 'New Zealand', 0, 0, 0, 0, 0, 0, 0),
    ('Henry Nicholls', '1991-06-15', 'Batsman', 'Male', 'New Zealand', 0, 0, 0, 0, 0, 0, 0),
    ('Will Young', '1992-06-29', 'Batsman', 'Male', 'New Zealand', 0, 0, 0, 0, 0, 0, 0),
    ('Tom Blundell', '1990-12-06', 'Wicketkeeper-Batsman', 'Male', 'New Zealand', 0, 0, 0, 0, 0, 0, 0),
    ('Jimmy Neesham', '1990-09-17', 'All-Rounder', 'Male', 'New Zealand', 0, 0, 0, 0, 0, 0, 0);

-- The SQL script is complete with the specified players for each team and includes the 'is_out' column.
