-- 1. ფაკულტეტების ჩასწორება/განახლება ზუსტი სახელებით
INSERT INTO faculties (id, name) VALUES
                                     (1, 'ინფორმატიკა და IT'),
                                     (2, 'ბიზნესი და მართვა'),
                                     (3, 'ჰუმანიტარული და სოციალური'),
                                     (4, 'სამართლის სკოლა')
    ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name;

-- 2. ტეგები UI-ს სურათის მიხედვით
INSERT INTO material_tags (id, name) VALUES
                                         (1, 'შუალედური'),
                                         (2, 'ფინალური'),
                                         (3, 'სილაბუსი'),
                                         (4, 'კონსპექტი'),
                                         (5, 'წიგნი'),
                                         (6, 'პრეზენტაცია')
    ON CONFLICT (id) DO NOTHING;

-- 3. საგნები თითოეული ფაკულტეტისთვის (4-5 საგანი თითოეულზე)
INSERT INTO subjects (id, name, faculty_id) VALUES
-- ინფორმატიკა და IT (id: 1)
(1, 'პროგრამირების საფუძვლები', 1),
(2, 'მონაცემთა ბაზები', 1),
(3, 'ალგორითმები და სტრუქტურები', 1),
(4, 'ოპერაციული სისტემები', 1),
(5, 'ვებ ტექნოლოგიები', 1),

-- ბიზნესი და მართვა (id: 2)
(6, 'მიკროეკონომიკა', 2),
(7, 'მარკეტინგის საფუძვლები', 2),
(8, 'ფინანსური აღრიცხვა', 2),
(9, 'მენეჯმენტის პრინციპები', 2),
(10, 'ბიზნეს სტატისტიკა', 2),

-- ჰუმანიტარული და სოციალური (id: 3)
(11, 'ზოგადი ლინგვისტიკა', 3),
(12, 'მსოფლიო ისტორია', 3),
(13, 'კულტუროლოგია', 3),
(14, 'ფილოსოფიის საფუძვლები', 3),
(15, 'აკადემიური წერა', 3),

-- სამართლის სკოლა (id: 4)
(16, 'ზოგადი სამოქალაქო სამართალი', 4),
(17, 'სისხლის სამართლის ზოგადი ნაწილი', 4),
(18, 'კონსტიტუციური სამართალი', 4),
(19, 'ადმინისტრაციული სამართალი', 4),
(20, 'საერთაშორისო საჯარო სამართალი', 4)
    ON CONFLICT (id) DO NOTHING;

-- 4. სატესტო მასალები (created_at-ით)
INSERT INTO materials (id, title, description, file_url, subject_id, uploader_id, status, created_at) VALUES
                                                                                                          (1, 'პროგრამირების საფუძვლები - სილაბუსი და მოთხოვნები', 'ლექციების განრიგი და შესაფასებელი კრიტერიუმები.', 'https://notebase-storage.s3.amazonaws.com/syllabus.pdf', 1, '38381a93-0f54-464c-ab26-6aa94a82a7d8', 1, CURRENT_TIMESTAMP),
                                                                                                          (2, 'მონაცემთა ბაზები - შუალედური გამოცდის მასალა', 'SQL ქვერების მაგალითები და თეორიული საკითხები.', 'https://notebase-storage.s3.amazonaws.com/midterm_db.pdf', 2, '38381a93-0f54-464c-ab26-6aa94a82a7d8', 1, CURRENT_TIMESTAMP),
                                                                                                          (3, 'მიკროეკონომიკა - ფინალური კონსპექტი', 'მთლიანი სემესტრის შეჯამება და ფორმულები.', 'https://notebase-storage.s3.amazonaws.com/final_econ.pdf', 6, '38381a93-0f54-464c-ab26-6aa94a82a7d8', 1, CURRENT_TIMESTAMP),
                                                                                                          (4, 'ზოგადი სამოქალაქო სამართალი - სახელმძღვანელო', 'ძირითადი კოდექსის განხილვა და ქეისები.', 'https://notebase-storage.s3.amazonaws.com/civil_book.pdf', 16, '38381a93-0f54-464c-ab26-6aa94a82a7d8', 1, CURRENT_TIMESTAMP)
    ON CONFLICT (id) DO NOTHING;

-- 5. მასალებისა და ტეგების დაკავშირება
INSERT INTO material_tag_mapping (material_id, tag_id) VALUES
                                                           (1, 3), -- სილაბუსი
                                                           (2, 1), -- შუალედური
                                                           (2, 4), -- კონსპექტი
                                                           (3, 2), -- ფინალური
                                                           (3, 4), -- კონსპექტი
                                                           (4, 5)  -- წიგნი
    ON CONFLICT DO NOTHING;

-- 6. ლაიქები (created_at-ის დამატებით)
INSERT INTO likes (id, user_id, material_id, created_at) VALUES
                                                             (1, '38381a93-0f54-464c-ab26-6aa94a82a7d8', 1, CURRENT_TIMESTAMP),
                                                             (2, '38381a93-0f54-464c-ab26-6aa94a82a7d8', 2, CURRENT_TIMESTAMP)
    ON CONFLICT (id) DO NOTHING;

-- 7. ფავორიტები
INSERT INTO user_favorite_materials (user_id, material_id) VALUES
                                                               ('38381a93-0f54-464c-ab26-6aa94a82a7d8', 2),
                                                               ('38381a93-0f54-464c-ab26-6aa94a82a7d8', 3)
    ON CONFLICT (user_id, material_id) DO NOTHING;