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

### Player
- [ ] Create player with id, name, start position, goal row, and initial walls
- [ ] Player knows its id
- [ ] Player knows its name
- [ ] Player knows its position
- [ ] Player knows its goal row
- [ ] Player knows walls remaining
- [ ] Move player to new position updates position
- [ ] Use wall decrements walls remaining
- [ ] Use wall with zero walls throws exception
- [ ] Return wall increments walls remaining
- [ ] Player at goal row has reached goal
- [ ] Player not at goal row has not reached goal

