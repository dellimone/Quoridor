# Quoridor

A Java implementation of Quoridor board game for the Sofware Development Methods Exam project.

## Architecture

This project follows a **layered architecture** with clear separation of concerns:

- **Domain**: Data structures (player, wall, board, ...)
- **Logic**: Rule validators (pawn move, wall placement)
- **Engine**: Game orchestration (move execution, undo)
- **Controller**: UI-to-game translation
- **View**: Humble dialog pattern (rendering only)

For detailed architecture documentation, see [docs/architecture.md](docs/architecture.md).

## Building and Running



### Prerequisites

- Java
- Gradle

### Build Commands

>TODO

### Running the game

> TODO

##  Project Structure

```
it.units.quoridor/
├── domain/          # Position, Wall, Player, Board, GameState
├── logic/
├── engine/
├── controller/
└── view/
```

## Documentation

- [Architecture Details](docs/architecture.md) - Deep dive into each layer
- [Game Rules](docs/rules.md) - Complete Quoridor rules reference
- [Development Checklist](docs/todo.md) - Current progress and remaining tasks
- [Contributing Guide](docs/CONTRIBUTING.md) - How to contribute

## Technologies

- **Language**: Java 24
- **Build Tool**: Gradle 8.11
- **Testing**: JUnit 5, Mockito
- **GUI**: Swing
