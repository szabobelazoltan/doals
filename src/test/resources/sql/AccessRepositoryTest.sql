-- test_findByActorAndEntry_whenExistsForActorAndEntry_returnsEntity
INSERT INTO "ACTOR" ("ID", "EXT_ID") VALUES (-5, '2173d97f-e0e4-4aa8-bdb5-55f5e8b37439');
INSERT INTO "DIRECTORY_ENTRY" ("ID", "TYPE", "EXT_ID", "STATUS") VALUES (-10000, 0, 'df174ea8-d805-453d-ae39-93daba466ceb', '0');
INSERT INTO "ACCESS" ("ID", "PERMISSION", "ROLE", "ACTOR_ID", "ENTRY_ID") VALUES (-1, 0, 0, -5, -10000);

-- test_findByActorAndEntry_whenMissesForActorAndEntry_returnsEmptyOptional
INSERT INTO "ACTOR" ("ID", "EXT_ID") VALUES (-6, '01d75f03-99a1-45ab-960b-e6f846ebc14f');
INSERT INTO "DIRECTORY_ENTRY" ("ID", "TYPE", "EXT_ID", "STATUS") VALUES (-10001, 0, '7d6aa986-5cd2-4b37-8224-f850ecad32cd', '0');