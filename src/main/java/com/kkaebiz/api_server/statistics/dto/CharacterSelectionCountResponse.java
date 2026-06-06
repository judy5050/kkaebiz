package com.kkaebiz.api_server.statistics.dto;

import java.util.List;

public record CharacterSelectionCountResponse(
        List<CharacterSelectionCountItem> selectionCountList
) {
}
