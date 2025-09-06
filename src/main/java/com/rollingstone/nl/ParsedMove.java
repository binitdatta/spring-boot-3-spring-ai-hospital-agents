package com.rollingstone.nl;



public record ParsedMove(
        Long staffId,
        Long roleId,
        Long fromUnitId,
        Long toUnitId,
        boolean splitFromAssignment,
        String note
) {}

