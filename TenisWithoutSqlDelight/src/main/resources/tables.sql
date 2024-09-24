CREATE TABLE IF NOT EXISTS Tenist (
  id INTEGER PRIMARY KEY,
  name TEXT NOT NULL,
  country TEXT NOT NULL,
  weight INTEGER NOT NULL,
  height REAL NOT NULL,
  dominantHand TEXT NOT NULL,
  points INTEGER NOT NULL,
  birthDate TEXT NOT NULL,
  createdAt TEXT NOT NULL,
  updatedAt TEXT NOT NULL,
  isDeleted INTEGER NOT NULL DEFAULT 0
);