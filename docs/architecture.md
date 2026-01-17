# Quoridor System

```
┌────────────────────────────────────────────────────────────────┐
│  USER INTERACTION                                              │
│  (clicks, key presses)                                         │
└──────────────────────────────┬─────────────────────────────────┘
                               ▼
┌────────────────────────────────────────────────────────────────┐
│  VIEW (Humble Dialog)                                          │
│  "I only know how to draw things and capture clicks"           │
│  - Zero game logic                                             │
│  - Receives view models, renders them                          │
│  - Forwards all events to listener                             │
└──────────────────────────────┬─────────────────────────────────┘
                               ▼
┌────────────────────────────────────────────────────────────────┐
│  CONTROLLER                                                    │
│  "I translate between the UI world and the game world"         │
│  - Converts UI events (cell clicks) to game concepts (moves)   │
│  - Converts game state to view models for display              │
│  - Handles UI flow (what to show when)                         │
└──────────────────────────────┬─────────────────────────────────┘
                               ▼
┌────────────────────────────────────────────────────────────────┐
│  ENGINE                                                        │
│  "I orchestrate the game flow and enforce turn order"          │
│  - Manages game lifecycle (start, reset, end)                  │
│  - Executes moves by delegating to validators                  │
│  - Determines winner                                           │
└──────────────────────────────┬─────────────────────────────────┘
                               ▼
┌────────────────────────────────────────────────────────────────┐
│  LOGIC (move and walls validators, path finder , rules)        │
│  "I know the rules of Quoridor"                                │
│  - Stateless services                                          │
│  - Pure functions: input → output                              │
└──────────────────────────────┬─────────────────────────────────┘
                               ▼
┌────────────────────────────────────────────────────────────────┐
│  DOMAIN                                                        │
│  "I am the vocabulary of the game"                             │
│  - Data structures representing game concepts                  │
│  - Minimal behavior, mostly data holders                       │
│  - Foundation for all other layers                             │
└────────────────────────────────────────────────────────────────┘
```