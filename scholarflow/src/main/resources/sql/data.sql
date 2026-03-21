INSERT INTO fields (id, name_en, name_sk) VALUES
    (gen_random_uuid(), 'Computer Science', 'Informatika'),
    (gen_random_uuid(), 'Mathematics', 'Matematika'),
    (gen_random_uuid(), 'Physics', 'Fyzika'),
    (gen_random_uuid(), 'Biology', 'Biologia');

INSERT INTO users (username, password_hash, email, full_name, role) VALUES
    ('admin', '$2a$12$pqT3G1BaXuDDvTwtVerMkOSbFSLBBBoyAYgGxIEPVIzJeQzHzjbGa', 
     'admin@scholarflow.com', 'System Administrator', 'ADMIN'),
    ('jsmith', '$2a$12$pqT3G1BaXuDDvTwtVerMkOSbFSLBBBoyAYgGxIEPVIzJeQzHzjbGa',
     'j.smith@scholarflow.com', 'John Smith', 'RESEARCHER'),
    ('mweber', '$2a$12$pqT3G1BaXuDDvTwtVerMkOSbFSLBBBoyAYgGxIEPVIzJeQzHzjbGa',
     'm.weber@scholarflow.com', 'Maria Weber', 'RESEARCHER'),
    ('rjones', '$2a$12$pqT3G1BaXuDDvTwtVerMkOSbFSLBBBoyAYgGxIEPVIzJeQzHzjbGa',
     'r.jones@scholarflow.com', 'Robert Jones', 'REVIEWER'),
    ('anovak', '$2a$12$pqT3G1BaXuDDvTwtVerMkOSbFSLBBBoyAYgGxIEPVIzJeQzHzjbGa',
     'a.novak@scholarflow.com', 'Anna Novak', 'REVIEWER');