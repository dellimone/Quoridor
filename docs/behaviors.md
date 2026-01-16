# Behaviors

## Game Setup

- A new game initializes a 9x9 board
- Player 1 starts at center of bottom row (8, 4)
- Player 2 starts at center of top row (0, 4)
- Each player receives 10 walls
- Player 1 moves first

## Pawn Movement

- A player can move their pawn to an adjacent orthogonal square (not diagonal)
- A player cannot move through a wall
- A player cannot move outside the board boundaries
- A player cannot move to a square occupied by another pawn (except via jump)

## Jump Behavior

- When pawns are face-to-face (adjacent), a player can jump over the opponent
- **Straight jump**: If no wall behind opponent, jump to the square beyond them
- **Diagonal jump**: If wall or board edge behind opponent, move diagonally to either side of opponent
- Diagonal jump is only allowed when straight jump is blocked

## Wall Placement

- A player can place a wall horizontally or vertically
- Each wall covers exactly two cell edges (spans 2 squares)
- Walls are placed at intersections between four squares
- A player cannot place a wall if they have no walls remaining
- Walls cannot overlap with existing walls
- Walls cannot intersect perpendicular walls at the same intersection
- A wall placement must not block ALL paths for any player to reach their goal

## Turn Order

- Players alternate turns
- On each turn, a player must either:
    - Move their pawn, OR
    - Place a wall
- A player cannot skip their turn

## Winning

- First pawn to reach any square on the opposite baseline wins
- Player 1 wins by reaching row 0 (any column)
- Player 2 wins by reaching row 8 (any column)
- Game ends immediately when a pawn reaches the goal row