package it.units.quoridor.engine.moves;

import it.units.quoridor.domain.PlayerId;
import it.units.quoridor.domain.Position;

public record PawnMove(PlayerId playerId, Position to) {}
