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
- [x] Create ActionValidator
- [x] Create MoveResult (will be expanded later)

#### Game Engine Tests
For the _pawn_ part:
- [x] Engine holds the initial state
- [ ] Engine asks validator on a pawn move
- [ ] Invalid pawn moves are rejected and state is unchanged
- [ ] Valid moves should advance the turn
- [ ] Engine refuses moves from a non-current player

For the _walls_ part:
- [ ] Engine asks validator on wall placement
- [ ] Invalid wall placements are rejected and state unchanged (as with pawn!)
- [ ] Valid wall placements should advance the turn and "consume?" the wall
