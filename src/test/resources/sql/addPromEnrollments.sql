INSERT INTO PROM_PERSON_ENTITY(PROM_PERSON_ID, EMAIL, FACULTY, FIELD, FROMAGH, INDEX, NAME, PHONE_NUMBER, SURNAME, YEAR)
VALUES
	('027fc173-de01-4ca2-92d5-1431e9967e60', 'first.user@gmail.com', 'WZ', 'IiE', true, 284266, 'firstName', 123456789, 'firstSurname', 2),
	('44482dd2-0298-49d6-86b1-a8e3808febc0', 'second.user@gmail.com', 'WZ', 'IiE', true, 284211, 'secondName', 234567890, 'secondSurname', 4),
	('86095baa-c75f-4c59-805f-bb0f38bb3904', 'third.user@gmail.com', 'WIMIP', 'IMiM', true, 301245, 'thirdName', 345678901, 'thirdSurname', 3),
	('89f48967-ac72-45f7-b1ec-7512662d4bda', 'fourth.user@gmail.com', null, null, false, null, 'fourthName', 456789012, 'fourthSurname', null);

INSERT INTO PROM_ENROLLMENT_ENTITY(PROM_ENROLLMENT_ID, MESSAGE, PAID, MAIN_PERSON_PROM_PERSON_ID, PARTNER_PROM_PERSON_ID, USER_ID)
VALUES
    ('0d7f96e5-1e06-4405-bf1a-c4c4a010fd27', 'firstMessage', false, '027fc173-de01-4ca2-92d5-1431e9967e60', '86095baa-c75f-4c59-805f-bb0f38bb3904', '068b6269-6c80-4046-98bb-801a88d10216'),
    ('14986394-03bc-4cb1-8381-4b559dad7f8d', 'secondMessage', false, '89f48967-ac72-45f7-b1ec-7512662d4bda', null, '068b6269-6c80-4046-98bb-801a88d10216'),
    ('355a5774-8ec6-4893-aef2-db98f7709ef3', 'thirdMessage', true, '44482dd2-0298-49d6-86b1-a8e3808febc0', null, '2e82418c-211e-42fe-bf7c-865020a3f8b2');

