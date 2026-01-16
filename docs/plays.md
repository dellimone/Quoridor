# Test Plays & Scenarios

Specific board states for unit testing.

## Basic Movement

### Play #1: Simple Move North
**Setup**: Player A at (8, 4), empty board  
**Action**: A moves NORTH  
**Result**: A at (7, 4)  

### Play #2: Simple Move All Directions
**Setup**: Player A at (4, 4), empty board  
**Actions**: Move in each direction  
**Results**:
- NORTH → (3, 4)
- SOUTH → (5, 4)
- EAST → (4, 5)
- WEST → (4, 3)

### Play #3: Move Blocked by Board Edge
**Setup**: Player A at (0, 4)  
**Action**: A attempts to move NORTH  
**Result**: Invalid move  

## Wall Blocking

### Play #4: Wall Blocks Movement
**Setup**:
- Player A at (4, 4)
- Horizontal wall at (3, 3) - blocks row 3/4 boundary at cols 3-4

**Action**: A attempts to move NORTH  
**Result**: Invalid move (wall blocks)  

```
       3   4   5
     ┌───┬───┬───┐
  3  │   │   │   │
     │═══════════│  ← wall at (3,3) horizontal
  4  │   │ A │   │
     └───┴───┴───┘
```

### Play #5: Wall Does Not Block Other Directions
**Setup**: Same as Play #4  
**Action**: A moves EAST  
**Result**: Valid, A at (4, 5)  

## Jump Scenarios

### Play #6: Straight Jump Over Opponent
**Setup**:
- Player A at (4, 4)
- Player B at (3, 4)
- No walls

**Action**: A moves NORTH (toward B)  
**Result**: A jumps to (2, 4)  

```
       3   4   5
     ┌───┬───┬───┐
  2  │   │ ✓ │   │  ← A lands here
     ├───┼───┼───┤
  3  │   │ B │   │
     ├───┼───┼───┤
  4  │   │ A │   │
     └───┴───┴───┘
```

### Play #7: Diagonal Jump (Wall Behind Opponent)
**Setup**:
- Player A at (4, 4)
- Player B at (3, 4)
- Horizontal wall at (2, 3) - blocks straight jump

**Action**: A moves toward B  
**Result**: A can move to (3, 3) or (3, 5)  

```
       3   4   5
     ┌───┬───┬───┐
  2  │   │   │   │
     │═══════════│  ← wall blocks straight jump
  3  │ ✓ │ B │ ✓ │  ← diagonal options
     ├───┼───┼───┤
  4  │   │ A │   │
     └───┴───┴───┘
```

### Play #8: Diagonal Jump (Board Edge Behind)
**Setup**:
- Player A at (1, 4)
- Player B at (0, 4) - at top edge

**Action**: A moves NORTH toward B  
**Result**: A can move to (0, 3) or (0, 5)  

### Play #9: Diagonal Jump Blocked by Wall
**Setup**:
- Player A at (4, 4)
- Player B at (3, 4)
- Horizontal wall at (2, 3)
- Vertical wall at (2, 3) - blocks left diagonal

**Action**: A attempts diagonal to (3, 3)  
**Result**: Invalid (wall blocks diagonal)  
**Valid**: A can still move to (3, 5)  

## Wall Placement

### Play #10: Valid Wall Placement
**Setup**: Empty board, Player A has 10 walls  
**Action**: Place horizontal wall at (4, 4)  
**Result**: Wall placed successfully  

### Play #11: Wall Overlap Rejected
**Setup**: Horizontal wall already at (4, 4)  
**Action**: Attempt horizontal wall at (4, 4)  
**Result**: Invalid (exact overlap)  

### Play #12: Adjacent Wall Overlap Rejected
**Setup**: Horizontal wall at (4, 4)  
**Action**: Attempt horizontal wall at (4, 3)  
**Result**: Invalid (walls would overlap)  

```
     ┌───┬───┬───┬───┬───┐
     │   │   │   │   │   │
     │   │═══════════════│  ← existing wall (4,4)
     │   │═══════════│   │  ← attempted wall (4,3) - OVERLAP!
     │   │   │   │   │   │
     └───┴───┴───┴───┴───┘
```

### Play #13: Perpendicular Wall Intersection Rejected
**Setup**: Horizontal wall at (4, 4)  
**Action**: Attempt vertical wall at (4, 4)  
**Result**: Invalid (walls intersect at same point)  

### Play #14: Wall Blocks All Paths (Rejected)
**Setup**:
- Player A at (8, 4) (bottom center)
- Walls forming almost complete horizontal barrier at row 7

**Action**: Place final wall completing the barrier  
**Result**: Invalid (blocks A's path to goal)  

```
     0   1   2   3   4   5   6   7   8
   ┌───┬───┬───┬───┬───┬───┬───┬───┬───┐
 7 │   │   │   │   │   │   │   │   │   │
   │═══════════════════════════════════│  ← complete barrier = INVALID
 8 │   │   │   │   │ A │   │   │   │   │
   └───┴───┴───┴───┴───┴───┴───┴───┴───┘
```

### Play #15: No Walls Remaining
**Setup**: Player A has 0 walls remaining  
**Action**: Attempt to place wall  
**Result**: Invalid (no walls left)  

## Win Conditions

### Play #16: Player Reaches Goal
**Setup**: Player A at (1, 4)  
**Action**: A moves NORTH to (0, 4)  
**Result**: Player A wins, game ends  

### Play #17: Win on Any Goal Column
**Setup**: Player A at (1, 0)  
**Action**: A moves NORTH to (0, 0)  
**Result**: Player A wins (corner is valid goal)  

## Path Finding

### Play #18: Path Exists Through Maze
**Setup**: Complex wall configuration with winding path  
**Query**: Does path exist from (8, 4) to row 0?  
**Result**: True (path exists via detour)  

### Play #19: No Path After Wall
**Setup**: Walls create complete barrier  
**Query**: Does path exist?  
**Result**: False  

## Undo Operations

### Play #20: Undo Pawn Move
**Setup**: A at (7, 4), was at (8, 4)  
**Action**: Undo  
**Result**: A back at (8, 4)  

### Play #21: Undo Wall Placement
**Setup**: Wall at (4, 4), A has 9 walls  
**Action**: Undo  
**Result**: Wall removed, A has 10 walls  

### Play #22: Undo After Win
**Setup**: Game finished, A won  
**Action**: Undo  
**Result**: Game resumes, status IN_PROGRESS  

## Edge Cases

### Play #23: Corner Movement
**Setup**: A at (0, 0)  
**Valid moves**: SOUTH, EAST only  
**Invalid**: NORTH (edge), WEST (edge)  

### Play #24: Three Pawns Adjacent (4-player)
**Setup**:
- A at (4, 4)
- B at (3, 4)
- C at (4, 5)

**Action**: A moves toward B  
**Result**: Normal jump rules apply to B  

### Play #25: Wall at Board Edge
**Setup**: Place horizontal wall at (0, 0)  
**Result**: Valid (wall along top edge)  
