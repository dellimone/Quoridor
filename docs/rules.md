# Quoridor Rules Reference

## Board

- 9x9 grid of squares (81 total)
- Grooves between squares for wall placement

## Players

- 2 players (can extend to 4)
- Each player controls one pawn

## Starting Position

| Players | Player 1 | Player 2 | Player 3 | Player 4 |
|---------|----------|----------|----------|----------|
| 2       | (8, 4)   | (0, 4)   | -        | -        |
| 4       | (8, 4)   | (0, 4)   | (4, 0)   | (4, 8)   |

## Walls

- **2-player game**: Each player has 10 walls
- **4-player game**: Each player has 5 walls
- Wall length: Exactly 2 squares
- Walls are permanent once placed (cannot be moved)

## Movement Rules

### Basic Movement
- Pawns move one square orthogonally (up, down, left, right)
- Pawns cannot move diagonally (normally)
- Pawns cannot pass through walls
- Pawns cannot leave the board

### Face-to-Face (Jump) Rules

1. **Standard Jump**: When adjacent to opponent with clear path behind them:
    - Jump over opponent to the square directly beyond

2. **Diagonal Jump**: When adjacent to opponent BUT blocked behind them:
    - Move to either square diagonally adjacent to opponent
    - The diagonal square must not be blocked by a wall

```
Standard Jump:          Diagonal Jump (wall behind):
                        
   ┌───┬───┬───┐           ┌───┬───┬───┐
   │ ✓ │   │   │           │═══════════│  ← wall
   ├───┼───┼───┤           ├───┼───┼───┤
   │ B │   │   │           │ ✓ │ B │ ✓ │  ← diagonal options
   ├───┼───┼───┤           ├───┼───┼───┤
   │ A │   │   │           │   │ A │   │
   └───┴───┴───┘           └───┴───┴───┘
   
   A jumps to ✓            A moves diagonally to ✓
```

## Wall Placement Rules

1. Walls must be placed in grooves between squares
2. Walls span exactly 2 squares
3. Walls cannot overlap other walls
4. Perpendicular walls cannot cross at the same intersection
5. **Critical**: A wall cannot completely block a player's path to goal

### Valid Wall Orientations

```
HORIZONTAL (blocks up/down movement):

    ┌───┬───┬───┐
    │   │   │   │
    │ ═════════ │
    │   │   │   │
    └───┴───┴───┘

VERTICAL (blocks left/right movement):

    ┌───┬───┬───┐
    │   ║   │   │
    │   ║   │   │
    │   ║   │   │
    └───┴───┴───┘
```

## Winning Condition

- **Player 1**: Reach any square in row 0 (top row)
- **Player 2**: Reach any square in row 8 (bottom row)
- **4-player**: Each player aims for their opposite baseline

## Edge Cases

### Wall Placement at Board Edge
- Walls can be placed along the board edge
- Same rules apply (2 squares, no blocking all paths)

### Multiple Pawns Adjacent (4-player)
- Jump rules apply to each adjacent opponent independently
- Cannot jump over multiple pawns in one move

### Simultaneous Path Blocking
- If placing a wall would block ALL players, it's invalid
- Path check must verify each player has at least one path

### Corner Diagonal Jump
- If opponent is against corner and wall behind:
    - Only one diagonal option available
    - The other would be out of bounds