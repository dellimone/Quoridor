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

# 1. Overview
The system follows a **layered architecture** in which we separate:
- domain, which maintains the core data structures
- logic, which contains the actual Quoridor rules and the validators that make use of those rules
- engine, which orchestrates the **game flow only** (does not now about actual rules, only relies onto validators)
- controller, which is the bridge between the engine and the UI
- view, which is responsible for capturing actual user interactions, giving them to the controller

# 2. Main Components
## 2.1 Domain Layer
// TODO

## 2.2 Validation (Rules) Layer
- Its only role is checking whether a certain move is legal or not given the current GameState; it just has to return
boolean legality and **is not responsible** of modifying the actual state.
- It has to:
  - check walls blocking movement, intersections, crossing
  - check pawn occupancy
  - check jump and diagonal rules

## 2.3 Move Generation Layer
- Its purpose is determine where a pawn ends up after a move and provide legal destination positions for UI highlighting
- **It is not responsible for move legality**, that is delegated to the validator, it only resolves the destination square
- One of the reasons it was implemented was to avoid making the GameEngine a "god" class with too many functionalities

## 2.4 Engine Layer
- It's the game's **central coordinator**: it does not know or care about actual Quoridor Rules, it's only responsible for:
  - receiving user inputs from the controller
  - ask the generator for legality and destinations
  - update GameState if move is valid
  - expose read-only queries for UI (in particular, move highlights)

This is done for the UI to **never** interact with validators or board logic directly.

## 2.5 UI/Controller Layer
- This part handles:
  - user inputs
  - display updates
  - highlight allowed moves
- It communicates **only with the engine**: it does not know how jumps work, how walls are stored, how legality is computed.

# 3. An example

## Moves Highlight
1. Player1 turn
2. UI asks engine for legal destinations for such player 
3. Engine calls pawnMoveGenerator.legalDestinations(state, player)
4. Generator leverages the validator
5. UI highlights the returned positions

## Moves execution
1. Player clicks a cell
2. Controller asks engine: "is this proposed target cell valid?"
3. Engine, through the generator:
    - checks legality via validator
    - resolves destination via generator
    - by itself, updates the GameState
4. Returns an answer to the controller, which renders the new state

# 4. Design decisions - will be updated further
## Separation of legality vs destination
- The validator decides *if* a move is allowed
- The generator decides *where* the move lands, given the validator's indication

In this way:
- we do not duplicate logic
- UI does not compute rules, just asks the engine
- engine does not mix rule logic with state updates

## Engine is our single entry point!
Ui only interacts with engine, so that:
- UI does not depend on internal rule classes
- changes in rules do not affect UI!
- tests are also simpler