# Quoridor TDD Checklist

---

## Domain Objects

### Direction
- [x] NORTH has row delta +1, col delta 0
- [x] SOUTH has row delta -1, col delta 0
- [x] EAST has row delta 0, col delta +1
- [x] WEST has row delta 0, col delta -1

### Position
- [x] Create position with valid values (0-8, 0-8)
- [x] Reject position with negative row
- [x] Reject position with row > 8
- [x] Reject position with negative column
- [x] Reject position with column > 8
- [x] Move north from (4,4) gives (5,4)
- [x] Move south from (4,4) gives (3,4)
- [x] Move east from (4,4) gives (4,5)
- [x] Move west from (4,4) gives (4,3)
- [x] Reject move north from top row (row 8)
- [x] Reject move south from bottom row (row 0)
- [x] Reject move west from leftmost column (col 0)
- [x] Reject move east from rightmost column (col 8)
- [x] Two positions with same row and col are equal
- [x] Two positions with different row are not equal
- [x] Two positions with different col are not equal

### WallPosition
- [x] Create wall position with valid values (0-7, 0-7)
- [x] Reject wall position with negative row
- [x] Reject wall position with row > 7
- [x] Reject wall position with negative column
- [x] Reject wall position with column > 7
- [x] Two wall positions with same coordinates are equal
- [x] Two wall positions with different coordinates are not equal

### WallOrientation
- [x] HORIZONTAL orientation exists
- [x] VERTICAL orientation exists

### Wall
- [x] Create wall with position and orientation
- [x] Wall knows its position
- [x] Wall knows its orientation
- [x] Two walls with same position and orientation are equal
- [x] Two walls with same position but different orientation are not equal
- [x] Two walls with different position but same orientation are not equal

### BlockedEdge
- [x] Create blocked edge with position and direction
- [x] Two blocked edges with same position and direction are equal
- [x] Two blocked edges with different position are not equal
- [x] Two blocked edges with different direction are not equal

### Wall - Blocked Edges Behavior
- [x] Horizontal wall at (3,4) blocks (3,4) moving SOUTH
- [x] Horizontal wall at (3,4) blocks (3,5) moving SOUTH
- [x] Horizontal wall at (3,4) blocks (4,4) moving NORTH
- [x] Horizontal wall at (3,4) blocks (4,5) moving NORTH
- [x] Vertical wall at (3,4) blocks (3,4) moving EAST
- [x] Vertical wall at (3,4) blocks (3,5) moving WEST
- [x] Vertical wall at (3,4) blocks (4,4) moving EAST
- [x] Vertical wall at (3,4) blocks (4,5) moving WEST

### PlayerId
- [x] PLAYER_1 exists with ordinal 0
- [x] PLAYER_2 exists with ordinal 1
- [x] PLAYER_3 exists with ordinal 2
- [x] PLAYER_4 exists with ordinal 3

### Player (Refactored - Position moved to Board)
- [x] Create player with id, name, goal row, and initial walls
- [x] Player knows its id
- [x] Player knows its name
- [x] ~~Player knows its position~~ (MOVED TO BOARD)
- [x] Player knows its goal row
- [x] Player knows walls remaining
- ~~[x] Move player to new position updates position~~ (MOVED TO BOARD)
- [x] Use wall decrements walls remaining
- [x] Use wall with zero walls throws exception
- ~~[x] Player at goal row has reached goal~~ (MOVED TO LOGIC LAYER)
- ~~[x] Player not at goal row has not reached goal~~ (MOVED TO LOGIC LAYER)
- [x] **REFACTOR**: Remove position attribute from Player
- [x] **REFACTOR**: Remove move() method from Player
- [x] **REFACTOR**: Update constructor to not take startingPosition

### Board
- [x] Create empty board
- [x] Add wall to board
- [x] Get all walls from board
- [x] Check if board contains specific wall
- [x] Set player position on board
- [x] Get player position from board
- [x] Move player updates position on board
- [x] Get all blocked edges from all walls
- [x] Check if specific edge is blocked
- ~~[ ] Cannot place same wall twice~~ (MOVED TO LOGIC LAYER)

### GameState
- [x] Create game state with board and players
- [x] Get board from game state
- [x] Get current player
- [x] Get player by id
- [x] Get all players
- [x] Switch to next player (2-player game)
- [x] Switch to next player (4-player game)
- [x] Current player cycles correctly (P1 → P2 → P1)
- [x] Current player cycles correctly in 4-player (P1 → P2 → P3 → P4 → P1)
- [ ] Game state knows number of players

## View Layer

### View Models (Pure Data)
- [x] Create board view model record (playerPositions, walls)
- [x] Create player view model record (id, name, wallsRemaining, isCurrentPlayer)

### View Interfaces
- [x] Create GameView interface (renderBoard, highlightValidMoves, etc.)
- [x] Create ViewListener interface (onCellClicked, onWallPlacement, onUndo)

### Swing Components
- [x] Create SwingGameView skeleton (JFrame)
- [x] Create BoardPanel for 9x9 grid rendering
  - [x] Draw grid lines
  - [x] Handle cell clicks (forward to listener)
  - [x] Render player pawns at positions
  - [x] Render walls (horizontal and vertical)
  - [x] Highlight cells (for valid moves)
- [x] Create PlayerInfoPanel
  - [x] Display player name
  - [x] Display walls remaining
  - [x] Highlight current player
- [x] Create control buttons (Undo, New Game)
- [x] Wire up event forwarding to ViewListener
- [ ] Fix layout issues when window is resized (maybe disable resizing)

### Game Engine 
#### Engine Skeleton
- [x] Create GameEngine class (stores GameState and exposes getGameState())
- [x] ~~Create ActionValidator~~ -> became PawnMoveValidator and WallPlacementValidator
- [x] Create MoveResult (will be expanded later)

#### Game Engine Tests
For the _pawn_ part:
- [x] Engine holds the initial state
- [x] Engine asks validator on a pawn move
- [x] Invalid pawn moves are rejected and state is unchanged
- [x] Valid moves should advance the turn
- [x] Engine refuses moves from a non-current player
- [X] Engine handles validator checking whether the last move is winning and handles the aftermath (change internal state
and do not allow further moves, they should all be marked INVALID)
- [X] Valid move that is NOT winning should make the "win checker" to return false (prevent the always gameOver)
- [X] Valid pawn moves should actually move the current pawn position (update the board)

For the _walls_ part:
- [X] Engine asks validator on wall placement
- [X] Invalid wall placements are rejected and state unchanged (as with pawn!)
- [X] Valid wall placements should advance the turn 
- [X] Valid wall placements should update the board status
- [X] Valid wall placements should consume a wall
- [ ] Player with no walls remaining CANNOT place a wall

ResetGame tests:
- [X] reset() should restore initial state and change the flags
- [ ] reset() should clear history

Undo tests:
- [X] if we have no history (game has just started anew) we return false
- [X] after a valid pawn move, calling undo should return true (because we have a history)
- [X] after a valid pawn move, calling undo should bring the gamestate back to the previous "valid" one
- [ ] after a valid wall placement, undo should return true and restore walls remaining
- [ ] after winning pawn move, doing undo should clear the gameOver flag and the winner


#### Validator tests
For pawns:
- [X] If proposed pawn position is free, validator returns true
- [X] If proposed pawn position is blocked by a wall, validator should return false
- [X] If proposed pawn position is blocked by a player and the one behind is free, return true
- [X] ~~If proposed pawn position is blocked by a player and the one behind is blocked by either a player or a wall return false **UNTIL WE IMPLEMENT DIAGONALS**~~

For diagonal testing:
- [ ] Diagonal allowed when jump is blocked because of a wall behind BUT side is open
- [ ] Diagonal is denied if straight jump is not blocked
- [ ] Diagonal is denied if the adjacent square is empty
- [ ] Diagonal is denied if the diagonals are blocked by walls
- [ ] Diagonal is denied if the diagonal is occupied by another player


For walls:
- [X] If proposed wall is put is a free position, validator returns true
- [X] If proposed wall intersect with existing walls, validator returns false
- [X] If proposed wall _crosses_ (has the same anchor) as an existing wall, validator returns false
- [X] If proposed wall blocks every possible path of a player to their goalRow, validator returns false
Walls by definition cannot be place outside the grid.

