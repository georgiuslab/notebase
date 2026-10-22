-- 1. ფაკულტეტები (Faculties)
INSERT INTO faculties (id, name) VALUES (1, 'ზუსტი და საბუნებისმეტყველო მეცნიერებები')
    ON CONFLICT (id) DO NOTHING;

INSERT INTO faculties (id, name) VALUES (2, 'ბიზნესი და ეკონომიკა')
    ON CONFLICT (id) DO NOTHING;

-- 2. საგნები (Subjects)
INSERT INTO subjects (id, name, faculty_id) VALUES (1, 'მათემატიკური ანალიზი', 1)
    ON CONFLICT (id) DO NOTHING;

INSERT INTO subjects (id, name, faculty_id) VALUES (2, 'პროგრამირების საფუძვლები', 1)
    ON CONFLICT (id) DO NOTHING;

INSERT INTO subjects (id, name, faculty_id) VALUES (3, 'მიკროეკონომიკა', 2)
    ON CONFLICT (id) DO NOTHING;